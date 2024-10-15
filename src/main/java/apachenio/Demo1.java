package apachenio;

import org.apache.http.*;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultClientIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.BufferingHttpClientHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.protocol.HttpRequestExecutionHandler;
import org.apache.http.nio.reactor.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * @author muyang
 * @create 2023/5/22 17:45
 */
public class Demo1 {


    public static void main(String[] args) throws IOException, InterruptedException {
        HttpParams params = new BasicHttpParams();
        // see: https://www.cnblogs.com/hym-pcitc/p/5681192.html
        // 一个http请求会经历三个阶段，分别是建立链接、数据传输、断开链接
        // 而socket timeout 就是传输数据时的超时
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                // connect timeout 是链接对方的超时
                .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000)
                // socket传输数据的buffer大小
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.USER_AGENT, "HttpComponents/1.1");

        // 创建一个reactor，其中有两个工作线程
        final ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(2, params);

        // 创建一个http的处理器，可以配置http的拦截器
        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                new RequestContent(),
                new RequestTargetHost(),
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        // 使用countDownLatch，当三个请求结束后，再执行requestCount.await() 后面的方法
        CountDownLatch requestCount = new CountDownLatch(1);

        BufferingHttpClientHandler handler = new BufferingHttpClientHandler(
                httpproc,
                new MyHttpRequestExecutionHandler(requestCount),
                new DefaultConnectionReuseStrategy(),
                params);

        handler.setEventListener(new EventLogger());

        final IOEventDispatch ioEventDispatch = new DefaultClientIOEventDispatch(handler, params);

        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    ioReactor.execute(ioEventDispatch);
                } catch (InterruptedIOException ex) {
                    System.err.println("Interrupted");
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                }
                System.out.println("Shutdown");
            }

        });
        t.start();


        // SessionRequestCallback中添加一个HttpRequest后，可以支持请求到具体的服务。如果没有这段代码，则仅仅是与对方建立连接
        HttpRequest reqeust = new BasicHttpEntityEnclosingRequest("post",
                "/handler/datacenter/topv/mockQimen.json?data={\"hello\":\"world2\"}");

        Long start = System.currentTimeMillis();
        ioReactor.connect(
                new InetSocketAddress("www.baidu.com", 80),
                null,
                new HttpHost("www.baidu.com"),
                new MySessionRequestCallback(requestCount, reqeust));
        System.out.println("Thread: " + Thread.currentThread().getName() + ". 耗时：" + (System.currentTimeMillis() - start));


        // Block until all connections signal
        // completion of the request execution
        requestCount.await();

        System.out.println("Shutting down I/O reactor");

        ioReactor.shutdown();

        System.out.println("Done");
    }

    /**
     * 执行request的handler
     */
    static class MyHttpRequestExecutionHandler implements HttpRequestExecutionHandler {

        private final static String REQUEST_SENT       = "request-sent";
        private final static String RESPONSE_RECEIVED  = "response-received";

        private final CountDownLatch requestCount;

        public MyHttpRequestExecutionHandler(final CountDownLatch requestCount) {
            super();
            this.requestCount = requestCount;
        }

        public void initalizeContext(final HttpContext context, final Object attachment) {
            HttpHost targetHost = (HttpHost) attachment; // onConnected client和server链接上了，客户端向服务端发起握手请求
            context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, targetHost);
            System.out.println("Thread: " + Thread.currentThread().getName() +"initalizeContext");
        }

        public void finalizeContext(final HttpContext context) {
            Object flag = context.getAttribute(RESPONSE_RECEIVED);
            if (flag instanceof Boolean && (Boolean) flag) {
                // Signal completion of the request execution
                this.requestCount.countDown();
            }
            System.out.println("Thread: " + Thread.currentThread().getName() +"finalizeContext");
        }

        public HttpRequest submitRequest(final HttpContext context) {
            System.out.println(Thread.currentThread().getId() + ": submitRequest");
            HttpHost targetHost = (HttpHost) context.getAttribute(
                    ExecutionContext.HTTP_TARGET_HOST);
            Object token = context.getAttribute(REQUEST_SENT);
            if (token == null) {
                // Stick some object into the context
                context.setAttribute(REQUEST_SENT, Boolean.TRUE);

                System.out.println(Thread.currentThread().getId() + "--------------");
                System.out.println(Thread.currentThread().getId() + "Sending request to " + targetHost);
                System.out.println(Thread.currentThread().getId() + "--------------");

                return new BasicHttpRequest("GET", "/");
            } else {
                // No new request to submit
                return null;
            }
        }

        /**
         * 处理响应
         * @param response the HTTP response to be processed
         * @param context the actual HTTP context
         */
        public void handleResponse(final HttpResponse response, final HttpContext context) {
            HttpEntity entity = response.getEntity();
            try {
                String content = EntityUtils.toString(entity);

                System.out.println("--------------");
                System.out.println(response.getStatusLine());
                System.out.println("--------------");
                System.out.println("Thread: " + Thread.currentThread().getName() +"Document length: " + content.length());
                System.out.println("--------------");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            }

            context.setAttribute(RESPONSE_RECEIVED, Boolean.TRUE);

            // Signal completion of the request execution
            this.requestCount.countDown();
        }

    }

    /**
     * 与对方建立链接后的请求回调、
     * 比如数据传输结束时回调completed方法
     *
     */
    static class MySessionRequestCallback implements SessionRequestCallback {

        private final CountDownLatch requestCount;

        protected HttpRequest httpRequest;

        public MySessionRequestCallback(final CountDownLatch requestCount) {
            super();
            this.requestCount = requestCount;
        }

        public MySessionRequestCallback(final CountDownLatch requestCount, HttpRequest httpRequest) {
            super();
            this.requestCount = requestCount;
            this.httpRequest = httpRequest;
        }

        public void cancelled(final SessionRequest request) {
            System.out.println("Thread: " + Thread.currentThread().getName() +  "Connect request cancelled: " + request.getRemoteAddress());
            this.requestCount.countDown();
        }

        public void completed(final SessionRequest request) {
            System.out.println("Thread: " + Thread.currentThread().getName() +"：Connect request completed. \n");
        }

        public void failed(final SessionRequest request) { // 链接失败的回调。eg：找不到host、connect refused（端口没有相关服务）等等
            System.out.println("Thread: " + Thread.currentThread().getName() +"Connect request failed: " + request.getRemoteAddress());
            this.requestCount.countDown();
        }

        public void timeout(final SessionRequest request) { // 链接超时，根据connectTime的设置来定
            System.out.println("Thread: " + Thread.currentThread().getName() + "Connect request timed out: " + request.getRemoteAddress());
            this.requestCount.countDown();
        }

    }

    /**
     * 与对方建立链接的事件监听着
     */
    static class EventLogger implements EventListener {

        /**
         * 要请求到对方，会产生一个链接事件，最终会回调connectionOpen方法
         * @param conn the connection.
         */
        public void connectionOpen(final NHttpConnection conn) {
            System.out.println("Thread: " + Thread.currentThread().getName() +"Connection open: " + conn);
        }

        /**
         * 链接超时的回调
         * @param conn the connection.
         */
        public void connectionTimeout(final NHttpConnection conn) {
            System.out.println("Thread: " + Thread.currentThread().getName() +"Connection timed out: " + conn);
        }

        /**
         * 关闭链接时的回调方法
         * @param conn the connection.
         */
        public void connectionClosed(final NHttpConnection conn) {
            System.out.println("Thread: " + Thread.currentThread().getName() +"Connection closed: " + conn);
        }

        public void fatalIOException(final IOException ex, final NHttpConnection conn) {
            System.err.println("Thread: " + Thread.currentThread().getName() +"I/O error: " + ex.getMessage());
        }

        public void fatalProtocolException(final HttpException ex, final NHttpConnection conn) {
            System.err.println("Thread: " + Thread.currentThread().getName() +"HTTP error: " + ex.getMessage());
        }

    }

}
