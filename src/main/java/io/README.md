

## IO模型

* IO模型就是说用什么样的通道进行数据的发送和接收。Java共支持3中网络编程模式：BIO，NIO，AIO

### 一、BIO（Blocking IO）

* 同步阻塞模型，一个客户端连接对应一个处理线程

* 缺点：

  1. IO代码里read操作是阻塞操作，如果客户端不做写数据，会导致线程阻塞，浪费服务器资源
  2. （使用多线程的方式保证为每一个连接的客户端开启一个线程）如果线程很多，并且执行业务逻辑耗时非常长，最终会导致服务器的资源耗尽。像在linux系统下，句柄的大小是有限制的，当超过句柄限制后，会直接丢弃请求。

* 应用场景：

  BIO方式适用于连接数目比较小且比较固定的架构，这种方式对服务器资源要求比较高，但程序简单易理解。

* BIO缺点重现方案：

  ```txt
  重现上一个线程无法处理完逻辑导致后面的线程无法连接服务器
  1、在单线程BIO中，因上一个连接的客户端一直没有写数据，导致服务器卡在read方法处，此时我们来开启一个新客户端进行连接，会发现，新客户端无法连接服务器。
  
  重现BIO多线程模型中，一个客户端对应一个线程处理，导致服务器资源耗尽
  2、在服务端中，针对每一个连接的客户端都开启一个新线程去处理，达到一个客户端对应一个线程处理。若每一个线程处理的业务逻辑耗时非常长，而此时连接服务器的客户端又非常的多。此时就会一直不停的创建新线程，抛开创建新线程的要调用native方法的耗时不说，最终都会一直创建线程，导致服务器资源耗尽。
  ==> 当然，可以使用线程池来代替，但是使用线程池的话，我们线程池处理线程的能力是有限的，使用线程池的原因是避免每次创建线程而带来的上下文切换的损耗。
  
  ```

### 二、NIO（No Blocking IO）

* 同步非阻塞模型（**单线程情况下在accept和read方法非阻塞**），服务器实现的模式为：**一个线程可以处理多个请求（连接）**，`看到这句话，再回想下，BIO是否能一个线程处理多个连接？`。客户端发送的连接请求都会注册到多路复用器selector上，多路复用器轮训到连接有IO请求就进行处理。

  IO多路复用底层一般用的Linux API（select，poll，epoll）来实现，他们的区别如下表：

  |          |                 select                 |                  poll                  |                    epoll（jdk 1.5及以上）                    |
  | :------- | :------------------------------------: | :------------------------------------: | :----------------------------------------------------------: |
  | 操作方式 |                  遍历                  |                  遍历                  |                             回调                             |
  | 底层实现 |                  数组                  |                  链表                  |                            哈希表                            |
  | IO效率   | 每次调用都进行线性遍历，时间复杂度O(n) | 每次调用都进行线性遍历，时间复杂度O(n) | 事件通知方式，每当有IO事件就绪，系统注册的回调函数就会被调用，时间复杂度O(1) |
  |          |                 有上限                 |                 无上限                 |                            无上限                            |

* 应用场景：

  NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，弹幕系统，服务器间通讯。编程比较复杂，JDK1.4开始支持。
  
* 关于**同步非阻塞**这五个字的解读：

  ```txt
  同步：
    同步是我在处理客户端发送给服务端的请求时，我能不能接着往后面做其他的事情
    
  非阻塞：
    其实nio也是阻塞的，其阻塞的方法就是多路复用器的select方法。。但是为什么要称NIO是非阻塞的呢？因为这个方法阻塞的时候表明程序是“静止”的，什么叫静止？即无任何客户端来操作服务器，那既然没有任何客户端来连接服务器，那为什么不阻塞呢？难道还让他空跑浪费服务器资源？而只要有客户端对服务器有任何操作，这个方法就不会有阻塞的情况发生，因此，这是NIO非阻塞的一个点。
    
  相比于BIO的非阻塞：
    我们知道BIO的阻塞发生在accept和read这两个api处，而在NIO中，其实accept也是阻塞的，但是我们可以保证有客户端连接时再调用accept方法，这样不就不会阻塞了吗？其次，再NIO中，read方法是否阻塞，是根据Channel的配置有关，我们在敲代码的时候经常会出现channel.configuraBlocking(false); 这段代码，就是配置了非阻塞，只要这里配置了非阻塞，那么我们在read方法的执行过程中也是非阻塞的。
  ```

#### 2.1 单线程模型

* 示例代码（其实就是redis的单线程模型）

  ```java
  /**
   * NIO服务端
   */
  public class NIOServer {
  
  
      public static void main(String[] args) throws IOException {
          startServer();
      }
  
      /**
       * 启动服务器
       * @throws IOException
       */
      private static void startServer() throws IOException {
          ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
          serverSocketChannel.configureBlocking(false);
          serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
  
          // 打开多路复用器
          Selector selector = Selector.open();
  
          /**
           * 将服务端的channel注册到多路复用器上，并设置感兴趣的事件为 “接收连接” 事件
           * 当多路复用器轮训（无事件时，会阻塞）时，只要有客户端连接到了服务器，就会解除阻塞
           */
          SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  
          while (!Thread.interrupted()) {
              System.out.println("等待注册到多路复用器的channel对应绑定的事件发生");
              /**
               * 轮询多路复用器，若注册到多路复用器的channel中无感兴趣的事件发生，则会阻塞在这里.r
               * 当有感兴趣的事件发生时，就会解除阻塞，继续往下执行。
               *
               * 其实这个方法也是阻塞的。但是为什么要称NIO是非阻塞的呢？
               * 因为这个方法阻塞的时候表明程序是“静止”的，什么叫静止？
               * 即无任何客户端来操作服务器，那既然没有任何客户端来连接服务器，那为什么不阻塞呢？难道还让他空跑浪费
               * 服务器资源？而只要有客户端对服务器有任何操作，这个方法就不会有阻塞的情况发生，
               * 因此，这是NIO非阻塞的一个点。
               */
              selector.select();
  
              /**
               * 执行到这里则说明，一定有感兴趣的事件发生了，此时我们直接
               * 拿到对应的selectionKey，此key就是当初我们注册到多路复用器时返回的key
               * @see selectionKey
               * 我们可以通过这个key拿到当初注册的到多路复用器的channel
               */
              Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
              while (iterator.hasNext()) {
                  SelectionKey next = iterator.next();
                  handle(next);
                  iterator.remove();;
              }
          }
      }
  
      private static void handle(SelectionKey next) throws IOException {
          if (next.isAcceptable()) {
              System.out.println("注册到多路复用器的服务器的channel有感兴趣的事件发生了，事件为：客户端连接服务器的连接事件");
  
              /**
               * 此处发生的是客户端连接服务器的事件，我们要做的事情就是把连接到客户端的channel也注册到多路复用器中统一管理
               * 1、拿到selectionKey对应的服务器channel
               */
              ServerSocketChannel channel = (ServerSocketChannel) next.channel();
  
              /**
               * 2、拿到连接到服务器的客户端channel
               *
               * accept其实是阻塞的，那他什么时候会解阻塞呢？
               * 当有客户端连接时，就会变成非阻塞。而在此，我们有个前提条件，就是有客户端连接了，我们
               * 才调用了这个accept方法，那此时是不是就变成了非阻塞了呢？
               * 这是NIO非阻塞的另外一个点。
               */
              SocketChannel accept = channel.accept();
              System.out.println("有客户端连接了：" + accept.getRemoteAddress());
              // 3、配置非阻塞
              accept.configureBlocking(false);
              // 4、客户端channel也注册到多路复用器，并对读取事件感兴趣
              accept.register(next.selector(), SelectionKey.OP_READ);
          } else if (next.isReadable()) {
              System.out.println("注册到多路复用器的客户端的channel向服务端写数据了，触发了channel的读事件了");
              ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
              SocketChannel channel = (SocketChannel) next.channel();
              channel.read(byteBuffer);
  
              byteBuffer.flip();
              byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
              byteBuffer.get(bytes);
              System.out.println("接收到客户端传来的消息：" + new String(bytes));
  
              // 写数据，告诉客户端
              channel.write(ByteBuffer.wrap("我收到你的消息了".getBytes()));
          } else if (next.isValid()) {
              System.out.println("服务端端口连接");
          }
      }
  }
  ```

* 单线程模型可能会出现的问题：

  ```txt
  假设select方法已经解除阻塞了，此时我们在遍历selectedKeys并处理对应的业务逻辑，若业务逻辑处理时间非常久，还没有处理完，此时若有客户端来连接了，此时不会立马连接上的。因为这是单线程模型，我们的线程还在处理业务逻辑，没法及时的处理客户端的连接以及其他的客户端操作，在NIO中，可能会存在一个队列来保存客户端的操作，待多路复用器下次轮训时，再从队列中去取。（若队列满了， 后面的请求则直接拒绝客户端的请求）
  ```

* 如何解决：解决问题从他源头开始，出现此问题的原因就是因为处理业务逻辑花费了太多的时间，导致selector一直无法进行下一次轮训，那我们直接把处理业务逻辑的操作**异步处理**，是不是就能让selector立马得到轮训呢？





