package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 使用NIO构建服务端思路：
 * 主要从如下三个方向出发
 * 1. 解决bio accept时的阻塞
 * 2. 解决bio read时的阻塞
 * 3. list保存旧连接的客户端。
 *
 *
 * 使用此种方式缺点：
 *   有个list, 若客户端仅仅是连接了服务器，并没有传输任何数据，
 *   此时就会做很多空轮询，浪费了当前应用程序jvm的性能。
 *
 *   解决方案：
 *     将list中连接socket的客户端的遍历交由操作系统去处理，
 *     调用操作系统的selector/epoll函数
 *     ps: 在windows和linux的jdk中，获取SelectorProvider的逻辑有一点不同，
 *     在windows下，是直接获取sun.nio.ch.WindowsSelectorProvider这个类，
 *     而在linux下，会根据操作系统的不同而使用不同的SelectorProvider
 *     具体参考：
 *   =========================================================================
 *   |  windows下获取SelectorProvider的逻辑：
 *   |    https://gitee.com/huan4j/jdk8/blob/master/jdk/src/windows/classes/sun/nio/ch/DefaultSelectorProvider.java
 *   |    ===>
 *   |     public static SelectorProvider create() {
 *   |         return new sun.nio.ch.WindowsSelectorProvider();
 *   |     }
 *
 *   =========================================================================
 *   |  linux获取SelectorProvider的逻辑：
 *   |    https://gitee.com/huan4j/jdk8/blob/master/jdk/src/solaris/classes/sun/nio/ch/DefaultSelectorProvider.java
 *   |    ==>
 *   |     public static SelectorProvider create() {
 *   |          String osname = AccessController
 *   |              .doPrivileged(new GetPropertyAction("os.name"));
 *   |          if (osname.equals("SunOS"))
 *   |              return createProvider("sun.nio.ch.DevPollSelectorProvider");
 *   |          if (osname.equals("Linux"))
 *   |              return createProvider("sun.nio.ch.EPollSelectorProvider");
 *   |          return new sun.nio.ch.PollSelectorProvider();
 *   |     }
 *
 *
 *   这也证明了，在linux下，nio默认使用的是epoll，而在windows下，使用的selector，
 *   同时，我们也可以从openjdk中的源码中找到答案：
 *   window中：
 *     https://gitee.com/huan4j/jdk8/blob/master/jdk/src/windows/native/sun/nio/ch/WindowsSelectorImpl.c
 *     搜索epoll0,可以看到内部调用了os的select方法
 *
 *   linux中：
 *     从源码(https://gitee.com/huan4j/jdk8/blob/master/jdk/src/solaris/classes/sun/nio/ch/EPollSelectorProvider.java)
 *     中可知，最终使用的SelectorProvider为EPollSelectorImpl,
 *     由EPollSelectorImpl的doSelect可知，最终会调用EPollArrayWrapper的poll方法，
 *     由EPollArrayWrapper的poll方法可知，调用了内部的native方法：epollWait
 *     进而在https://gitee.com/huan4j/jdk8/blob/master/jdk/src/solaris/native/sun/nio/ch/EPollArrayWrapper.c中
 *     找到了epollWait的源码，进而查看到了调用了iepoll方法。至此可以证明：在linux下默认使用的是epoll
 *
 *   因为epoll的性能比selector高，你会发现在redis官网中并没有windows的安装包，就是因为redis中默认调用的是linux中的epoll，
 *   并没有调用selector，至于为什么会有windows的redis，那是因为有一些大佬对redis的代码进行了二次开发，将epoll的调用改成了
 *   selector
 *
 */
public class MyNIOServer {

    /**
     *
     * 上述说的第三个方向： list保存旧连接的客户端。
     *
     * 如果不使用list来存储连接上服务的客户端，试想下如下情景：
     * 客户端A连接服务端，但是不发送数据。由于nio没有阻塞，所以会立刻进入第二次循环，
     * 那么此时accept拿到的客户端已经不是上一次连接的了，此时拿到的是null。
     * 如果在这期间有第二个客户端B进行链接，那么此时拿到的客户端就是客户端B的SocketChannel
     */
    static List<SocketChannel> channels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("127.0.0.1", 8080));
        // 上述说的第一个方向：设置客户端连接为非阻塞  --> 当调用ssc.accept方法时，此时不会阻塞
        ssc.configureBlocking(false);
        ByteBuffer bf = ByteBuffer.allocate(1024);

        while (true) {
            boolean facebook = false;
            SocketChannel socketChannel = ssc.accept();
            if (socketChannel != null) {
                System.out.println("连接成功：" + socketChannel);

                // 上述说的第二个方向：读取数据时解阻塞
                socketChannel.configureBlocking(false);
                channels.add(socketChannel);
                System.out.println("连接数: " + channels.size());
            }

            Iterator<SocketChannel> iterator = channels.iterator();

            // 使用迭代器遍历list，便于删除
            while (iterator.hasNext()) {
                SocketChannel channel = iterator.next();
                int length = 0;
                bf.clear();

                /**
                 * channel.read(bf) ==> 这段代码的返回值有三种情况
                 * 1、等于0         表示客户端没有发送任何数据
                 * 2、小于0（-1）   表示客户端断开了连接
                 * 3、大于0         表示客户端实际发送给服务的数据的大小
                 */
                while ((length = channel.read(bf)) > 0) {
                    // 切换成读模式
                    bf.flip();
                    // byte数组的长度取决于读取数据时 byteBuffer的limit属性(表示byteBuffer中存储数据的大小)
                    byte[] bytes = new byte[bf.limit()];
                    // 从缓存区读取数据到bytes数组中
                    bf.get(bytes);
                    System.out.println(new String(bytes));
                    facebook = true;
                }

                // 小于0, 读取不到客户端发送的信息了 --> 代表客户端执行了close方法
                if (length < 0) {
                    iterator.remove();
                    System.out.println("客户端退出：" + channel);
                    System.out.println("连接数: " + channels.size());
                    continue;
                }

                if (facebook) {
                    // 将客户端的信息读取完成，告知客户端我接收完毕

                    // 清除缓冲区，将模式切换成写模式
                    bf.clear();
                    bf.put("我已收到你的消息, 消息内容, 请求状态：200".getBytes());
                    bf.flip();
                    channel.write(bf);
                    facebook = false;
                }

            }
        }

    }
}
