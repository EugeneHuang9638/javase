package com.eugene.basic.singletonpattern;

/**
 * 测试Hodler模式与枚举特性实现单例模式
 */
public class HolderEnumSingleton {

    private HolderEnumSingleton() {

    }

    private enum Holder {
        ;
        private static HolderEnumSingleton holderEnumSingleton = new HolderEnumSingleton();

        public static HolderEnumSingleton getInstance() {
            return Holder.holderEnumSingleton;
        }
    }

    public static HolderEnumSingleton getInstance() {
        return HolderEnumSingleton.Holder.getInstance();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            System.out.println(HolderEnumSingleton.getInstance());
        }
    }
}
