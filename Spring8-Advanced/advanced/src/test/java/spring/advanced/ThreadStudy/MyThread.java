package spring.advanced.ThreadStudy;

import lombok.extern.slf4j.Slf4j;

/**
 * MyThread extends Thread
 */
public class MyThread extends Thread{
    String name;
    public MyThread(String name) {
        this.name = name;
    }
    public void run(){
        for (int i = 0; i < 50; i++) {
            System.out.println(name + " " + i);
        }
    }
}
