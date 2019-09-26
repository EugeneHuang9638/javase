package annotation.chapter1;

import java.lang.annotation.*;

/**
 * 定义UseCase注解, 存在两个属性: id和description(定义方式类似于方法的定义)
 * @author Eugene
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface UseCase {

    public int id();

    public String description() default "No description";
}
