package com.mmall.test;

/**
 * Created by 11790 on 2018/11/19.
 */
public class SubSubClass extends SubClass {

    int a;
    //x=1,y=2,z=3
    public SubSubClass(int x,int y,int z){
        super(x,y,z);
        this.a=a+b+c;
    }

    public void show(){
        System.out.println("a=" + a + ";b=" + b + ";c=" + c);
    }
}
