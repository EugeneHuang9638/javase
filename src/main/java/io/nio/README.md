## NIO

### 一、NIO设计初衷

* **使用单线程来处理并发**，调用底层os中epoll函数来实现。它主要是想解决**客户端连接**和**bio中读数据**时的阻塞问题。

### 二、使用java远程api构建一个支持高并发的server

* 服务端：

  ```java
  /**
   * 使用NIO构建服务端思路：
   * 主要从如下三个方向出发
   * 1. 解决bio accept时的阻塞
   * 2. 解决bio read时的阻塞
   * 3. list保存旧连接的客户端。
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
       * 若果在这期间有第二个客户端B进行链接，那么此时拿到的客户端就是客户端B的SocketChannel
       */
      static List<SocketChannel> channels = new ArrayList<>();
  
      public static void main(String[] args) throws IOException {
          ServerSocketChannel ssc = ServerSocketChannel.open();
          ssc.bind(new InetSocketAddress("127.0.0.1", 8080));
          // 上述说的第一个方向：设置客户端连接为非阻塞
          ssc.configureBlocking(false);
          ByteBuffer bf = ByteBuffer.allocate(1024);
  
          while (true) {
              SocketChannel accept = ssc.accept();
              if (accept != null) {
                  System.out.println("连接成功：" + accept);
  
                  // 上述说的第二个方向：读取数据时解阻塞
                  accept.configureBlocking(false);
                  channels.add(accept);
                  System.out.println("连接数: " + channels.size());
              }
  
              Iterator<SocketChannel> iterator = channels.iterator();
  
              // 使用迭代器遍历list，便于删除
              while (iterator.hasNext()) {
                  SocketChannel channel = iterator.next();
                  int length = 0;
                  while ((length = channel.read(bf)) > 0) {
                      // 有数据，打印数据
                      bf.clear();
                      byte[] bytes = bf.array();
                      System.out.println(new String(bytes, 0, length));
                  }
  
                  // 小于0, 代表客户端执行了close方法
                  if (length < 0) {
                      iterator.remove();
                      continue;
                  }
  
              }
          }
  
      }
  }
  ```

* 客户端

  ```java
  public class MyNIOClient1 {
  
      public static void main(String[] args) throws IOException {
          Socket socket = new Socket("127.0.0.1", 8080);
          OutputStream outputStream = socket.getOutputStream();
          Scanner scanner = null;
          while (true) {
              scanner = new Scanner(System.in);
              String next = scanner.next();
  
              if ("exit".equals(next)) {
                  break;
              }
  
              outputStream.write(next.getBytes());
              outputStream.flush();
          }
  
          scanner.close();
          outputStream.close();
          socket.close();
  
      }
  }
  ```

