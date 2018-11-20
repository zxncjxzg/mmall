package com.mmall.test;

/**
 * Created by 11790 on 2018/11/17.
 */
public class SubClass extends SuperClass{
    int b;
    int c;
    //x=1,y=2,z=3
    public SubClass(int x,int y,int z){
        /**
         * 调用父类的构造函数，父类拥有属性a、b
         * 因为子类也拥有属性b,所以this.b=b+z  =>  this.b=0+3
         * this.c=a+b =>this.c=1+3
         */
        super(x,y);
        this.b=b+z;
        this.c=a+b;
    }
}
