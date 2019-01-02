package com.thread;

/**
 * Created by 11790 on 2018/12/27.
 */
public class TestThread {
    public static void main(String[] args) {
        RunnableDemo runnableDemo=new RunnableDemo("Thread-1");//新建状态
        runnableDemo.start();//就绪状态
        RunnableDemo runnableDemo2=new RunnableDemo("Thread-2");
        runnableDemo2.start();
    }
}
