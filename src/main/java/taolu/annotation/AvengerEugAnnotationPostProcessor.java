package taolu.annotation;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * @author muyang
 * @create 2023/12/22 10:20
 */
@Component
public class AvengerEugAnnotationPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 获取目标类是否有自定义注解
        AvengerEug annotation = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(bean),
                AvengerEug.class);

        if (annotation == null) {
            return bean;
        }

        // 处理想要的扩展
        System.out.println(beanName + " has AvengerEug" + " beanName: " + beanName + ", avengerEug annotation's type is : " + annotation.type());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
