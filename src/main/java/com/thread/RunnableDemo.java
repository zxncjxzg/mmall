package com.thread;

/**
 * Created by 11790 on 2018/12/27.
 */
public class RunnableDemo implements Runnable {
    private Thread thread;
    private String threadName;

    //构造器，为Runable命名
    RunnableDemo(String threadName){
        this.threadName=threadName;
        System.out.println("Creating "+threadName);
    }

    @Override
    public void run() {
        System.out.println("Running "+threadName);
        try {
            for(int i=4;i>0;i--){
                System.out.println("Thread:"+threadName+","+i);
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " +  threadName + " interrupted.");
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }

    public void start(){
        System.out.println("Starting " +  threadName );
        if(thread==null){
            thread=new Thread(this,threadName);
            thread.start();
        }
    }
}
