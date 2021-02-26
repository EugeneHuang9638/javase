# 前言

* [上篇文档：IO系列学习总结二：认识NIO，从ByteBuffer开始](https://blog.csdn.net/avengerEug/article/details/113960479)，我们了解了NIO解决了BIO阻塞的问题来达到高并发的目的。其中，还用了三张图了解了NIO中的ByteBuffer及常用API。上文总结到NIO的几个核心组件：**Selector、SeletedKey、ByteBuffer、ServerSockerChannel、SocketChannel**，我们详细了解了ByteBuffer组件的底层原理。今天，我们以一个简陋的、用NIO技术编写的聊天应用程序来了解Selector、SelectedKey、ServerSocketChannel、SocketChannel。废话不多说，咱们直接进行今天旅程。

## 一、