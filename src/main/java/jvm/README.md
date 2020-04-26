## 一、类加载器类型

* 表格

  |          类加载器类型          |                      作用                       |                           获取方式                           |                   备注                   |
  | :----------------------------: | :---------------------------------------------: | :----------------------------------------------------------: | :--------------------------------------: |
  |   系统加载器(AppClassLoader)   | 加载当前应用`classpath`下的class文件至jvm内存中 | ClassLoader appClassLoader = ClassLoader.getSystemClassLoader() | 线程上下文获取的类加载器就是`系统加载器` |
  |   扩展加载器(ExtClassLoader)   |   加载`%JAVA_HOME%/bin/ext`路径下的所有jar包    |   ClassLoader extClassLoader = appClassLoader.getParent()    |                    无                    |
  | 根加载器(BootstrapClassLoader) | 加载`%JAVA_HOME%/bin/jre/lib`路径下的所有jar包  |   ClassLoader rootClassLoader = extClassLoader.getParent()   |  在java中获取的为null, 因为是由C++写的   |

## 二、如何将一个java文件加载到jvm内存 -- Class.forName() -- "全盘委派机制"

* 大范围就是：将java文件编译成class文件，再使用类加载器将class文件加载到jvm内存中。在这一般会用到Class.forName("全限定名")的api。但是使用这个api有一个特点，就是内部有这么一段代码：

  ```java
  public static Class<?> forName(String className)
                  throws ClassNotFoundException {
      // 拿到调用方的Class类，假设在类A的main方法中调用了Class.forName("com.xxx.xxx");
      // 那么获取到的调用方的Class类就是类A。
      // 最终会使用类A的类加载器将com.xxx.xxx类加载到jvm中
      Class<?> caller = Reflection.getCallerClass();
      return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
  }
  ```

## 三、双亲委派机制

* 如图

  ![双亲委派机制.png](https://github.com/AvengerEug/javase/tree/develop/src/main/java/jvm/双亲委派机制.png)

* 双亲委派机制有一个规则：`每一个类加载器只能做自己的工作，假设我们想让BootstrapClassLoader来加载classpath路径下的某个类，这是行不通的(正常情况下)`

## 四、JDBC破坏双亲委派机制案例

* 情况一：（`未破坏双亲委派机制`）

  ```java
  Class.forName("com.mysql.jdbc.Driver");
  Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true", "root", "");
  System.out.println(connection);
  ```

  解析：

  ```txt
  上面有说道，全局委派机制。因为在我们自定义写的类A中调用了Class.forName("com.mysql.jdbc.Driver");方法，
  所以com.mysql.jdbc.Driver的加载是由AppClassLoader完成的。而且在执行Class.forName("com.mysql.jdbc.Driver");代码时，做了一件事，就是把mysql的驱动添加到DriverManager的registeredDrivers属性中去了。最后是直接从registeredDrivers属性中拿驱动获取连接的。
  ```

* 情况二：(`破坏双亲委派机制`)

  ```java
  Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true", "root", "");
  System.out.println(connection);
  ```

  解析：

  ```txt
  可以看到，我们并没有手动将com.mysql.jdbc.Driver类记载到jvm中去。而DriverManager是java.sql包下的，位于rt.jar包。所以DriverManager肯定是由根加载器加载到jvm的。而在记载DriverManager类时，内部的静态代码会使用java的spi技术。读取classpath下META-INF/services/java.sql.Driver文件，里面存储的就是一些实现了java.sql.Driver接口的实现类。其中就包括com.mysql.jdbc.Driver。我们拿到的还只是字符串，我们要把它加载到jvm中，会用到Class.forName()的api。由上述的全盘委托机制可知，在DriverManager内部执行Class.forName()方法，最终肯定使用的是根加载器。但是com.mysql.jdbc.Driver并不在%JAVA_HOME%/bin/jre/lib路径下，所以肯定是不能加载到的。此时就用到了ClassLoader cl = Thread.currentThread().getContextClassLoader()代码来获取当前线程的类加载器, 此段代码获取的是系统加载器，即AppClassLoader，然后使用Class.forName的另外一个能指定类加载器的api去加载类。所以最终是使用系统加载器来加载com.mysql.jdbc.Driver类
  ```

## 五、JVM内存模型

* 结构图

  ![jvm内存模型.png](https://github.com/AvengerEug/javase/tree/develop/src/main/java/jvm/jvm内存模型.png)

* 整理下jvm线程私用的内存结构

  |      类目       |                        作用                        |                             备注                             |
  | :-------------: | :------------------------------------------------: | :----------------------------------------------------------: |
  |   程序计数器    |   类似于pc寄存器，用来存储下一步jvm要处理的指令    |                              无                              |
  |   本地方法栈    | jvm中(eg: hotspot)的原生方法，eg: UNSAFE类中的方法 |                              无                              |
  |      栈帧       |    线程每调用一个方法都会以栈帧的方式存储在栈中    |                              无                              |
  | 栈帧-局部变量表 |               存储方法内部定义的变量               | 1. 方法中具体定义的变量名，在内部都不会存在，jvm不在乎你的变量名是什么<br>2. 当执行store相关指令时，会将变量存储到局部变量表中 |
  |  栈帧-操作数栈  |             临时存储方法内部定义的变量             | 1. 当执行const相关的指令时，会将变量临时存储到操作数栈中<br>2. 当执行load相关指令时，会将局部变量表中的变量移动到操作数栈中 |
  |  栈帧-动态链接  |          java中多态的机制就是靠它来完成的          |                                                              |
  |                 |                 栈帧执行结束的出口                 | 方法结束的出口一共有两个: <br>1. 正常return<br>2.方法出异常  |

  

## 六、JVM堆内存结构与测试

* 结构图

  ![jvm堆内存结构.png](https://github.com/AvengerEug/javase/tree/develop/src/main/java/jvm/jvm堆内存结构.png)

* 测试JVM堆内存

  * 测试类:

    ```java
    package jvm;
    
    import java.util.ArrayList;
    import java.util.List;
    
    public class HeapMemory {
    
        // 表示DumpMemory对象占用内存至少 1024 * 100 kb
        private char[] chars = new char[1024 * 100];
    
        public static void main(String[] args) throws InterruptedException {
            System.out.println("Starting");
            List<HeapMemory> list = new ArrayList();
            for (int i = 0; i < 1000; i++) {
                // 循环1000次，每隔100毫秒往list添加一个DumpMemory对象， 最后使用jconsole来定位main线程，来查看堆内存的变化
                Thread.sleep(100);
                list.add(new HeapMemory());
            }
            System.out.println("end");
        }
    }
    ```

  * 使用`jdk`自带的**jconsole**工具进行查看堆内存

    * 整体堆内存

      ![堆内存飙升.png](https://github.com/AvengerEug/javase/tree/develop/src/main/java/jvm/堆内存飙升.png)

    * Eden区

      ![eden区内存情况.png](https://github.com/AvengerEug/javase/tree/develop/src/main/java/jvm/eden区内存情况.png)

    * 老年代

      ![老年代.png](https://github.com/AvengerEug/javase/tree/develop/src/main/java/jvm/老年代.png)

## 七、以jvm的角度来查看常见的几个面试题

* Q: String str = "a" + "b"; 一共创建了几个对象？
* A:

---

* Q: 泛型擦除
* A: 

---

* Q: try catch时，finally块一定会执行么？
* A: 

## jvm工具

### Jinfo

* `jinfo -flags java进程ID`， 可以看到java的运行时环境(jvm的一些参数) , 与java中`System.getProperties()`一致



