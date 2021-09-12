package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;



public class FutureTest {

    private Future<Integer> testFuture;
    Random rnd;
    @BeforeEach
    public void setUp(){
        testFuture = new Future<>();
        rnd = new Random();
    }

    @Test
    public void get1_Test_Check_If_Works_When_Null() {
        Thread a = new Thread(() -> testFuture.get());
        a.start();

        //try to excute
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(Thread.State.WAITING,a.getState());
    }

    @Test
    public void get1_resolve_Test_Check_If_Works() {
        Integer num = rnd.nextInt();
        testFuture.resolve(num);
        assertEquals(num,testFuture.get());
    }
    @Test
    public void get1_Test_If_Wait_And_Work() {

        Thread a = new Thread (new Runnable() {
            Integer num = null;

            @Override
            public void run() {
                num = testFuture.get();

                //This will maintain the state where it is runnable
                while (num!=null);
            }
        }
        );
        //execute get() if possible
        a.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());

        }
        Integer result = rnd.nextInt();
        assertEquals(Thread.State.WAITING,a.getState());
        testFuture.resolve(result);

        //Try to get a result from Thread  a
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());

        }
        assertEquals(Thread.State.RUNNABLE,a.getState());
    }



    @Test
    public void isDone_Test_False_At_Init() {
        assertFalse(testFuture.isDone());
    }

    @Test
    public void isDone_Test_True_When_Resolved() {
        Integer num = rnd.nextInt();
        testFuture.resolve(num);
        assertTrue(testFuture.isDone());
    }

    @Test
    public void get2_Test_Return_Null() {
        assertNull(testFuture.get(10,TimeUnit.SECONDS));

    }
    @Test
    public void get2_Test_Check_If_Works() {
        Integer temp = rnd.nextInt();
        Thread a = new Thread(() ->
        {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            testFuture.resolve(temp);
        });
        a.start();
        Integer test = testFuture.get(10,TimeUnit.SECONDS);
        assertEquals(temp,test);
    }
}