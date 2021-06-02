## 使用代码模拟Future模式

### future模式的主要参与者

| 参与者     | 作用                                                         |
| ---------- | ------------------------------------------------------------ |
| Main       | 系统启动，调用Client发出请求                                 |
| Client     | 返回Data对象，立即返回FutureData，并开启ClientThread线程装配RealData |
| Data       | 返回数据的接口                                               |
| FutureData | Future数据构造很快，但是是一个虚拟的数据，需要装配RealData   |
| ReadData   | 真实数据，其构造是比较慢的                                   |

### future模式的简单实现

* 在这个实现中，有一个核心接口Data，这就是客户端希望获取的数据。在Future模式中，这个Data接口有两个重要的实现，一个是RealData，也就是真实数据，这就是我们最终需要获得的、有价值的信息。另外一个就是FutureData，它是用来提取RealData的一个**身份标识**，后续要通过它来获取真正的realData。
* 查看代码顺序：**Data.java -> RealData.java -> FutureData.java -> Client.java -> Main.java**