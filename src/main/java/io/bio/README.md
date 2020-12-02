## BIO

### 一、BIO缺点

* BIO有一个很大的缺点：整个服务端有两个地方会阻塞。第一个是**socket.accept()**处，第二个是获取输入流socket.getInputStream()并使用输入流进行读**取数据(inputStream.read(bytes))**时，也会进行阻塞。

  如下所示：

  ```java
  public static void startCommonServer() throws IOException {
      ServerSocket socket = new ServerSocket(8800);
      while (true) {
          try{
              System.out.println("服务器启动，等待连接, 第一次阻塞");
              Socket clientSocket = socket.accept();
  			System.out.println("客户端连接成功： " + clientSocket);
              InputStream inputStream = clientSocket.getInputStream();
              byte[] bytes = new byte[1024];
              int length;
              // read 方法在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞
              // 所以无法将"Hi avengerEug"写入到socket，进而客户端收不到消息
              System.out.println("连接成功，准备读数据, 第二次阻塞");
              while ((length = inputStream.read(bytes)) != -1) {
                  System.out.println(length);
                  System.out.println(new String(bytes, 0, length));
              }
              System.out.println("数据读取成功，继续等待下一个客户端连接");
  
          } catch (IOException e) {
              e.printStackTrace();
          } finally {
              // 关闭input流代码省略
          }
      }
  }
  ```

  由上述代码可知：在第二次阻塞过程(`inputStream.read(bytes)`)中，如果客户端一直没有断开连接，此时的服务端只服务于当前客户端，无法处理其他客户端的请求。进而说明**BIO**无法处理高并发请求。


### 二、如何解决BIO的并发场景？

* 其实也很简单：就是针对每个客户端的accept操作都开启一个线程来处理，就可以处理并发了，为了避免线程的上下文切换，可以使用线程池来解决创建线程耗资源的问题。代码如下所示：

  ```java
  /**
   * 创建一个任务长度为10的线程池，同时工作的线程数为5, 最大线程数为10
   * 空闲时间为60s，拒绝策略为抛异常
   */
  private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
      5,
      10,
      60,
      TimeUnit.SECONDS,
      new ArrayBlockingQueue<>(20),
      Executors.defaultThreadFactory(),
      new ThreadPoolExecutor.AbortPolicy()
  );
  
  public static void startConcurrencyServer() throws IOException {
      ServerSocket socket = new ServerSocket(8800);
      while (true) {
  
          Socket clientSocket = socket.accept();
  
          threadPoolExecutor.execute(() -> {
              try{
                  System.out.println("accepted connection from " + clientSocket);
                  InputStream inputStream = clientSocket.getInputStream();
                  byte[] bytes = new byte[1024];
                  int length;
                  // read 方法在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞
                  // 所以无法将"Hi avengerEug"写入到socket，进而客户端收不到消息
                  while ((length = inputStream.read(bytes)) != -1) {
                      System.out.println(new String(bytes, 0, length));
                      System.out.println(length);
                  }
  
              } catch (IOException e) {
                  e.printStackTrace();
              } finally {
                  // 关闭input流代码省略
              }
          });
      }
  }
  ```

  这里提一下，使用线程池的**submit**和**execute**方法执行任务的区别

  | 线程池提交类别 |                             原理                             |
  | :------------: | :----------------------------------------------------------: |
  |     submit     | 内部封装成FutureTask并调用execute方法，可以获取到线程的返回值以及抛出的异常 |
  |    execute     | 正常的线程执行流程，完全异步，感知不到异步线程的异常和返回值 |

* 使用多线程解决BIO并发场景问题：

  ```txt
  因为每次连接服务器都会开启一个线程连接，但是有可能我也仅仅只是连接，并还没发送数据，此时这个线程是不是比较浪费，单单是为了连接而开启的。
  ```

  

  

