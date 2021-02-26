package io.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 认识NIO中的channel
 *
 * channel在nio中用于源节点和目标节点的链接，eg：从起始站到终点站铁轨
 * channel内部不保存数据，其数据存储在byteBuffer(可以理解成在铁轨中运货的火车)中
 *
 * channel包含：
 * 本地IO和网络IO
 * 本地IO包括: FileInputStream/FileOutputStream/RandomAccessFile
 * 网络IO包括：Socket/ServerSocket/DatagramSocket
 *
 *
 * 本案例测试：
 * 使用FileChannel配置缓存区来实现文件的复制
 *
 */
public class ChannelTest {


    public static void main(String[] args) throws Exception {

        copyFileByByteBuffer();
//        copyFileByChannel();

    }

    /**
     * 直接通过channel交互数据
     * @throws IOException
     */
    private static void copyFileByChannel() throws IOException {
        FileChannel inChannel = new FileInputStream("D:\\demo.txt").getChannel();
        FileChannel outChannel = new FileOutputStream("D:\\tmp\\demo.txt").getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
    }

    private static void copyFileByByteBuffer() throws IOException {
        // D:\demo.txt 文件内容就是：avengerEug
        FileChannel inChannel = new FileInputStream("D:\\demo.txt").getChannel();
        FileChannel outChannel = new FileOutputStream("D:\\tmp\\demo.txt").getChannel();

        // 每次最大读取10个长度
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);

        /**
         * inChannel将文件流读取到byteBuffer缓存区中，大小为10位  --> 每一次循环从文件中读取10个字符
         * 类似于调用了byteBuffer.put方法，往缓存区写数据、
         *
         * 对于read方法而言，
         * 1、等于0         表示客户端没有发送任何数据
         * 2、小于0（-1）   表示客户端断开了连接
         * 3、大于0         表示客户端实际发送给服务的数据的大小
         */
        int length;
        while ((length = inChannel.read(byteBuffer)) != -1) {
            System.out.println(length);
            /**
             * 要进行写文件，我们需要从buffer中读取内容并写到文件中去，因此首先要对buffer进行读取数据.
             * 读取的数据内容是数组的那一部分内容呢？很简单，
             * 需要执行flip方法，即切换成读模式，然后缓存区position到limit的位置就是当前循环读取到的内容
             */
            byteBuffer.flip();

            /**
             * 经过上述的flip操作，我们的缓存区的position和limit（limit的变化取决于这次读取内容的长度，在此案例中，因为avengerEug长度本身就是10，因此
             * limit不会变化，若想测试limit会变化的情况，可以把avengerEug改成avenger，此时limit会变化成7）发生了变化，position和limit之间的内容
             * 就是当前循环读取到的内容。因此我们直接将缓存区参数传入到outChannel的write方法即可，
             * write方法内部会取position到limit之前的内容写入到文件中去
             */
            outChannel.write(byteBuffer);

            /**
             * 切换成写模式  --> 一定要这么做
             * 假设不这么做，此时的position为10，
             * 也就是说下一次读取是从下标为10的位置开始的。
             * 当进行下一次循环时，
             * inChannel.read(byteBuffer)操作发现position和limit的值相等了，即无法再继续往
             * 缓存区写数据，则直接返回原缓存区，而此时缓存区的内容还是"avengerEug"。
             * 经过了flip操作后，下一次写文件操作就还是写"avengerEug"这段话，于是，
             * 就这样死循环了。
             * 因此，程序运行的结果就是D:\tmp\demo.txt文件内容全是avengerEug这段话
             *
             * 如果切换成了写模式的话，缓存区的position变成了0
             * 即下一次读文件内容时，是从头（position = 0的位置）开始的，而不是从position下标为10的位置上开始读取的
             *
             */
            byteBuffer.clear();
        }
        System.out.println(length);
    }
}
