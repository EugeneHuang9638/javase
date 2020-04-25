package jvm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 在正常的双亲委派机制中，
 * 是一层一层的向上委派工作，在向上委派的过程中，每个类加载器不会做其他事情，单纯的就是
 * 委派给上一级类加载器。当委派到根加载器时(BootstrapClassloader)，它是根加载器了，
 * 不需要委派了，于是开始做自己的事情(加载jdk环境变量中的%JAVA_HOME%/bin/jre/lib下面的所有jar包)
 * 然后再处理委托给我的事情，比如加载jvm.HitParentDelegationModel，因为它不在我的工作范围内，
 * 所以它需要将工作传递给ExtClassLoader, 但是ExtClassLoader发现这个jvm.HitParentDelegationModel
 * 文件也不是自己的工作范围(ExtClassLoader的工作是加载: %JAVA_HOME%/bin/jre/ext目录下的jar包),于是把它
 * 交给下一级AppClassLoader, 而AppClassLoader的事情就是加载当前应用程序下的classpath下的jar包，
 * 于是就交由它来加载jvm.HitParentDelegationModel了
 *
 */
public class HitParentDelegationModel {


    /**
     * 又因为全盘委派机制(A类中加载了B类，那么B类使用的类加载器和A的一样)。
     * 最开始是通过AppClassLoader来加载HitParentDelegationModel.class了，
     * 现在要执行Class.forName("com.mysql.jdbc.Driver");代码，因为HitParentDelegationModel.class
     * 是AppClassLoader来加载的，所以最后也会将由AppClassLoader来加载com.mysql.jdbc.Driver类。
     *
     * 加载完了之后，因为com.mysql.jdbc.Driver类中的静态块做了一件事，就是new了一个自己，并注册到
     * java.sql.DriverManager的registeredDrivers属性中去了。
     * 然后在DriverManager.getConnection()逻辑中，发现registeredDrivers属性中有值，
     * 于是就直接使用这个Driver去连接数据库了
     *
     * @throws Exception
     */
    public static void allDelegate() throws Exception {
        // 其实是因为这个Class.forName方法的内部，会去获取这段代码的调用方(出现在那个类中)
        // 然后根据这个类去获取类加载器，最终使用这个加载器来把一个类加载到内存中
        // 这就是全盘委派机制的原因
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true", "root", "");
        System.out.println(connection);
    }

    /**
     * 为什么删除Class.forName("com.mysql.jdbc.Driver");就变成了破坏了双亲委派机制了呢？
     *
     * 首先我们先了解下把一个类加载到内存中的流程:
     * 1. 把java类编译成class文件
     * 2. 获取类的全限定名, eg: com.mysql.jdbc.Driver
     * 3. 使用Class.forName("com.mysql.jdbc.Driver")
     *    => 但是此方法内部会有这么一段代码
     *    ClassLoader.getClassLoader(Reflection.getCallerClass())
     *    它会获取到调用Class.forName代码的类，比如在执行上述allDelegate()
     *    方法的Class.forName("com.mysql.jdbc.Driver");代码时，
     *    Reflection.getCallerClass() 获取的类就是jvm.HitParentDelegationModel
     *    最终会调用forName0(String name, boolean initialize, ClassLoader loader, Class<?> caller)方法
     *    来把class文件加载到jvm内存中，所以会指定使用的是哪个类加载器
     *
     * 我们了解类加载到内存的流程后，来说明下此方法为什么打破了双亲委派机制，
     * 双亲委派机制上面也介绍了，再来总结一下，大致的意思就是(在从根加载器开始工作时):
     * 每个类加载器只负责做自己的事情，当涉及到自己能力范围之外的事情后，再让别人做。
     * 当我们执行DriverManager.getConnection()方法时，因为DriverManager类是在rt.jar包下，
     * 所以DriverManager类肯定是由根类加载器加载的。又因为Class.forName的特性，所以在DriverManager
     * 类中调用的任何Class.forName方法使用的类加载器都是根加载器。(全盘委托机制)
     * 因为我们没有显示的执行Class.forName("com.mysql.jdbc.Driver");代码，所以它会使用spi机制
     * 在classpath下去找java.sql.Drivers的实现类，最终找到了com.mysql.jdbc.Driver类，那么问题来了,
     * 我要去加载这个类，肯定会使用Class.forName() api, 此时使用的类加载器肯定是根类加载器，而
     * com.mysql.jdbc.Driver不属于rt.jar包内。所以此时在内部使用了
     * ClassLoader cl = Thread.currentThread().getContextClassLoader();代码来获取当前线程的类加载器,
     * 此默认加载器为AppClassLoader, 最终使用此加载器来讲com.mysql.jdbc.Driver加载到jvm内存中，
     * 至此，双亲委派机制被打破了
     *
     * @throws SQLException
     */
    public static void hitParentDelegationModel() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true", "root", "");
        System.out.println(connection);
    }

    public static void main(String[] args) throws Exception {

        allDelegate();
        hitParentDelegationModel();
        // 当前应用程序类加载器(系统类加载器)
        System.out.println(ClassLoader.getSystemClassLoader());
        // 类加载器上面一级, 扩展类加载器
        System.out.println(ClassLoader.getSystemClassLoader().getParent());
        // 根加载器，BootstrapClassLoader
        System.out.println(ClassLoader.getSystemClassLoader().getParent().getParent());
    }
}
