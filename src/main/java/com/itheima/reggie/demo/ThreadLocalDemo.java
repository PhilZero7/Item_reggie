package com.itheima.reggie.demo;

/**
 * @Author Vsunks.v
 * @Date 2022/6/9 16:16
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description:
 */
public class ThreadLocalDemo {

    /**
     * 两个要求：
     * 1. 保证是在同一个线程内
     * 2. 保证使用的是同一个ThreadLocal对象
     *
     * 每个线程维护了一个map（变量的容器），map的key就是这个Threadlocal对象，存的值就是tl.set(str)的值str
     *
     * 泛型的作用：限制容器中存储的数据类型
     *
     * 问题：
     *  多个类之间，也能共享吗？
     *  与是否同一个类无关，和是否是在同一个线程中有关。
     *
     */
    public static ThreadLocal<String> tl = new ThreadLocal();

    public static void main(String[] args) {
        String str = "暗号*****";
        // 通过ThreadLocal  线程变量容器对象实现
        // map.put(tl,str)
        tl.set(str);
        //test1();

        // 主线程调用test3
        new Demo2().test3();

        // Demo中main方法中，开一个新线程调用test3
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Demo2().test3();
            }
        }).start();


    }

    public static void test1() {
        tes2();
    }

    public static void tes2() {
        // 在test中使用main方法中定义的字符串
        // map.get(tl)
        String s = tl.get();
        System.out.println("s = " + s);

    }
}

class Demo2 {

    void test3(){
        // 获取tl对象

        // tl.get()
        String str = ThreadLocalDemo.tl.get();
        System.out.println("Demo2中test3中的str= " + str);

    }
}


