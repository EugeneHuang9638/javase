package com.eugene.basic.thread.myNative;

public class ExecMyNativeMethod {

    /**
     * 加载本地方法类库，注意这个名字，后面会用到
     */
    static {
        System.loadLibrary("MyNative");
    }

    public static void main(String[] args) {
        ExecMyNativeMethod execMyNativeMethod = new ExecMyNativeMethod();
        execMyNativeMethod.start0();
    }

    private native void start0();

    public void run() {
        System.out.println("I'm run method..........");
    }
}
