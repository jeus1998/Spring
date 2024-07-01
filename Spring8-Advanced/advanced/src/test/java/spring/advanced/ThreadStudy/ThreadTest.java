package spring.advanced.ThreadStudy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ThreadTest {
    @Test
    void threadBasicRunTest(){
        MyThread myThread1 = new MyThread("myThread1");
        MyThread myThread2 = new MyThread("myThread2");

        myThread1.run();
        myThread2.run();
    }
    @Test
    void threadBasicStartTest(){
        MyThread myThread1 = new MyThread("myThread1");
        MyThread myThread2 = new MyThread("myThread2");

        myThread1.start();
        myThread2.start();
    }

    @Test
    void runnableTest1(){
        MyRunnable myRunnable1 = new MyRunnable("myRunnable1");
        MyRunnable myRunnable2 = new MyRunnable("myRunnable2");

        Thread thread1 = new Thread(myRunnable1);
        Thread thread2 = new Thread(myRunnable2);
        thread1.start();
        thread2.start();
    }

    @Test
    void runnableTest2(){

        Runnable myRunnable1 = ()-> {
            for (int i = 0; i < 50; i++) {
                System.out.println("myRunnable1 " + i);
            }
        };

        MyRunnable myRunnable2 = new MyRunnable("myRunnable2");

        Thread thread1 = new Thread(myRunnable1);
        Thread thread2 = new Thread(myRunnable2);
        thread1.start();
        thread2.start();
    }

}
