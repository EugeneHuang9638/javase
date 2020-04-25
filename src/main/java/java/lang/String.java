package java.lang;

public class String {

    public static void main(String[] args) {
        /**
         * 错误: 在类 java.lang.String 中找不到 main 方法, 请将 main 方法定义为:
         *    public static void main(String[] args)
         * 否则 JavaFX 应用程序类必须扩展javafx.application.Application
         *
         * 原因： 因为双亲委派机制, 在执行此java文件时，首先得把它编译成class文件，
         * 然后jvm需要把它(java.lang.String)加载到内存中，这一步骤会使用到类加载器，因为这是咱们自己写
         * 的程序，所以首先会使用AppClassLoader进行加载class文件，但是呢，因为
         * 双亲委派的机制，它并不会先自己执行，而是依次找ExtClassLoader > BootstrapClassLoader
         * 其中BootstrapClassLoader为底层用c++写的，所以在java中获取的为null,
         * 按照这样的顺序，首先找到ExtClassLoader，然后呢它又委托给BootstrapClassLoader去加载,
         * 但是BootstrapClassLoader为根加载器，它的作用就是加载 %JAVA_HOME%/bin/jre/lib目录下
         * 的一些jar包，欸，发现在rt.jar包中有java.lang.String类，于是去执行它内部的main方法，
         * 但是在jdk提供的String源码总，压根就没有main方法，所以会报错
         */
        System.out.println("Hell RootClassLoader");
    }
}
