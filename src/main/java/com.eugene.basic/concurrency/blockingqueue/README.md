## 阻塞队列核心api

#### 一、阻塞队列核心api预览

| 方法类型 | 抛出异常  |  特殊值  |  阻塞  |         超时         |
| :------: | :-------: | :------: | :----: | :------------------: |
|   插入   |  add(e)   | offer(e) | put(e) | offer(e, time, unit) |
|   移除   | remove()  |  poll()  | take() |   poll(time, unit)   |
|   检查   | element() |  peek()  | 不可用 |        不可用        |

* 抛出异常：(参考**BlockingQueueDemo1.java**文件)

  ```txt
  1、当阻塞队列满了后，再往队列里add插入元素，会抛illegalStateException: Queue Full
  2、当阻塞队列空了后，再往队列里remove移除元素，会抛NoSuchElementException
  ```

* 特殊值：（参考**BlockingQueueDemo2.java**文件）

  ```txt
  1、插入方法，成功返回true，失败返回false
  2、移除方法，成功返回出队列的元素，队列里没有就返回null
  ```

* 一直阻塞：(参考**BlockingQueueDemo3.java**文件)

  ```txt
  1、当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产线程，直到put数据or响应中断退出
  2、当阻塞队列空时，消费者线程试图从队列take元素，队列会一直阻塞消费者线程，直到队列可用。
  ```

* 超时：(参考**BlockingQueueDemo4.java**文件)

  ```txt
  在“一直阻塞”的情形下，比较温柔的版本。配置一下阻塞的时间，不让线程一直阻塞到那里
  ```

  