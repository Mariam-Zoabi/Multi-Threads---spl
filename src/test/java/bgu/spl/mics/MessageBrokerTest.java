package bgu.spl.mics;//dt

import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.subscribers.M;
import org.junit.jupiter.api.BeforeEach;//dt
import org.junit.jupiter.api.Test;//dt

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageBrokerTest {

    /** Object under test. */
    private MessageBroker mb;

    @BeforeEach
    public void setUp(){
    }

    public Subscriber getinstanceSub(){
        return new M();
    }

    public TestEvent getEvent(){
        return  new TestEvent();
    }
    public TestBroadcast getBroadcast(){ return  new TestBroadcast(); }

    private static class TestEvent implements Event<Integer> {};
    private static class TestBroadcast implements Broadcast {};


    //test method for subsicribeEvent method
    @Test
    public void testSubscribeEvent(){
        try{
            Subscriber sub = getinstanceSub();
            mb.register(sub);
            mb.subscribeEvent(TestEvent.class ,sub);
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for subscribeBroadcast method
    @Test
    public void testSubscribeBroadcast(){
        try{
            Subscriber sub = getinstanceSub();
            mb.register(sub);
            mb.subscribeBroadcast(TestBroadcast.class ,sub);
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for complete method
    @Test
    public void testComplete(){
        try{
            Subscriber sub = getinstanceSub();
            TestEvent te = getEvent();
            mb.subscribeEvent(te.getClass() , sub);
            Future<Integer> object = mb.sendEvent(te);
            mb.complete(te , 8);
            if(!object.isDone()){
                fail("Complete should have set future object to be done");
            }
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for sendBroadcast method
    @Test
    public void testSendBroadcast(){
        try{
            Subscriber s1 = getinstanceSub();
            Subscriber s2 = getinstanceSub();

            TestBroadcast bc = getBroadcast();
            mb.subscribeBroadcast(bc.getClass() , s1);
            mb.subscribeBroadcast(bc.getClass() , s2);

            mb.sendBroadcast(bc);

            Thread t1 = new Thread(() -> {
                try{
                   Message b1 =  mb.awaitMessage(s1);
                    if(b1 != bc){
                        fail("did't return the broadcast");
                    }
                }
                 catch (InterruptedException e) {
                    e.printStackTrace();
                };
            });
            t1.start();
            t1.join(1000);
            if (t1.isAlive()) {
                t1.interrupt();
                if (t1.isAlive()) {
                    t1.stop();
                }
                fail("awaitMessage is stuck");
            }


            Thread t2 = new Thread(() -> {
                try{
                    Message b2 = mb.awaitMessage(s2);
                    if(b2 != bc){
                        fail("did't return the broadcast");
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                };
            });
            t2.start();
            t2.join(1000);
            if (t2.isAlive()) {
                t2.interrupt();
                if (t2.isAlive()) {
                    t2.stop();
                }
                fail("awaitMessage is stuck");
            }

        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for sendEvent method
    @Test
    public void testSendEvent1(){
        try{
            Subscriber sub = getinstanceSub();
            TestEvent te = getEvent();
            mb.subscribeEvent(te.getClass() , sub);
            Future<Integer> object = mb.sendEvent(te);
            if(object == null){
                fail("SendEvent should not return null");
            }
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }
    //test method for sendEvent method
    @Test
    public void testSendEvent2(){
        try{
            TestEvent te = getEvent();
            Future<Integer> object = mb.sendEvent(te);
            if(object != null){
                fail("SendEvent should return null");
            }
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for register method
    @Test
    public void testRegister(){
        try{
            Subscriber sub = getinstanceSub();
            mb.register(sub);
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for unRegister method
    @Test
    public void testUnregister(){
        try{
            Subscriber sub = getinstanceSub();
            mb.unregister(sub);
        }
        catch (Exception e){
            fail("Unexpected Exception" + e.getMessage());
        }
    }

    //test method for await method
    @Test
    public void testAwaitMessage() {
        try {
            Subscriber sub = getinstanceSub();
            mb.register(sub);
            mb.subscribeEvent(TestEvent.class ,sub);
            Future<Integer> fuOb = mb.sendEvent(new TestEvent());
            Thread t1 = new Thread(() -> {
                try {
                    mb.awaitMessage(sub);
                } catch (InterruptedException e) {

                } catch (Exception e) {
                    fail("Unexpected exception: " + e.getMessage());
                }
            });

            t1.start();
            t1.join(1000);
            if (t1.isAlive()) {
                t1.interrupt();
                if (t1.isAlive()) {
                    t1.stop();
                }
                fail("awaitMessage is stuck");
            }
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    //test method for await method
    @Test
    public void testAwaitMessage2() {
        try {
            Subscriber sub = getinstanceSub();
            mb.register(sub);
            mb.subscribeEvent(TestEvent.class ,sub);

            Thread t1 = new Thread(() -> {
                try {
                    mb.awaitMessage(sub);
                } catch (InterruptedException e) {

                } catch (Exception e) {
                    fail("Unexpected exception: " + e.getMessage());
                }
            });

            t1.start();
            long before = System.currentTimeMillis();
            t1.join(3000);
            long after = System.currentTimeMillis();
            if (after-before < 3000) {
                fail("awaitMessage is not blocking");
            } else {
                t1.interrupt();
                if (t1.isAlive()) {
                    t1.stop();
                }
            }
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

}
