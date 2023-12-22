package taolu.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author muyang
 * @create 2023/12/22 10:23
 */
public class Entry {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
