package com.eugene.basic.singletonpattern;

/**
 * Holder模式实现单例模式: 主要利用了静态内部类的懒加载机制
 */
public class HolderSingleton {

    private HolderSingleton() {

    }

    private static class Hodler {
        private static HolderSingleton holderSingleton = new HolderSingleton();

        public static HolderSingleton getInstance() {
            return holderSingleton;
        }
    }

    public static HolderSingleton getInstance() {
        return Hodler.getInstance();
    }
}
