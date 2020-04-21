package annotation;

import annotation.chapter1.TestAnnotation;

public class Test {

    public static void main(String[] args) {
        Class clazz = TestAnnotation.class;
        annotation.chapter1.Test test = (annotation.chapter1.Test) clazz.getAnnotation(annotation.chapter1.Test.class);
        System.out.println(test.id());

    }
}
