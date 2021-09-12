package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateBroadcast;
import javafx.util.Pair;

//import java.util.*; //1 SHAKED: this is not ok. you shouldn't import with '*', it will import EVERYTHING in that lib, only import what you need.
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {
    //fields
    private final Map<Class<? extends Message>, ConcurrentLinkedQueue<Subscriber>> messageToSub; // list of subscribers to relay messages that are received from publishers
    private final Map<Subscriber, LinkedBlockingQueue<Message>> messages;
    private final Map<Event, Future> eventFutureMap;

    private MessageBrokerImpl() {
        this.messageToSub = new ConcurrentHashMap<>(); // doesn't have to be concurrent
        this.eventFutureMap = new ConcurrentHashMap<>();
        this.messages = new ConcurrentHashMap<>();
    }

    private static class MessageBrokerHolder {
        private static final MessageBrokerImpl INSTANCE = new MessageBrokerImpl();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MessageBroker getInstance() {
        return MessageBrokerHolder.INSTANCE;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {

        subscribeMessage(type, m);
    }

    @Override // done ?
    public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {

        subscribeMessage(type, m);

    }

    private void subscribeMessage(Class<? extends Message> type, Subscriber m) {
        ConcurrentLinkedQueue<Subscriber> temp;
        messageToSub.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        temp = messageToSub.get(type);
        temp.add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        this.eventFutureMap.remove(e).resolve(result);
    }

    @Override
    //send the broadcast to all the subscribers
    public void sendBroadcast(Broadcast b) {
        Queue<Subscriber> listOfSubs = this.messageToSub.get(b.getClass());
        if (listOfSubs == null)
            return;

        for (Subscriber s : listOfSubs) {
            LinkedBlockingQueue<Message> q = messages.get(s);
            if (q == null)
                return;
            q.add(b);
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Queue<Subscriber> q; //the q of sub's that already registered to the event e
        Subscriber s;
        q = messageToSub.get(e.getClass());
        if (q == null) {
            return null;
        }

        synchronized (e.getClass()) {
            s = q.poll();
            if (s == null)
                return null;
            q.add(s);
        }


        LinkedBlockingQueue<Message> q2 = messages.get(s);
        if (q2 == null) {
            return null;
        }
        Future<T> futOb = new Future<>();
        eventFutureMap.put(e, futOb);
        q2.add(e);

        if (!messages.containsKey(s)) {//for no synchronizing
            futOb.resolve(null);
            return null;
        }

        return futOb;

    }


    @Override
    public void register(Subscriber m) {
        this.messages.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(Subscriber m) {
        messageToSub.forEach((c, q) -> {
            synchronized (c) {
                q.remove(m);
            }
        });


        BlockingQueue<Message> messageLoop = this.messages.remove(m);
        for (Message message : messageLoop) {
            if (message instanceof Event) {
                complete((Event<?>) message, null);
            }
        }
    }

    @Override
    public Message awaitMessage(Subscriber m) throws InterruptedException {
        BlockingQueue<Message> q = this.messages.get(m);
        if (q == null)
            throw new InterruptedException(m.getName() + " : await for Message before registering itself");
        return q.take();
    }

    /** SHAKED:
     * NO, nop, no, noooooo
     * You are literally emptying his list of messages into TEMPORARY list
     * and then giving him one message (if there was no message then, it will block FOREVER),
     * and after the "return" line, your temporary list is being deleted!!
     *
     * Solution? change the subToEvent's "Queue<Event>" to a BlockingQueue there,
     * so when you take out a message from there, it will block.
     * However, make sure that you config it that it won't block when adding a message to the queue!
     * (it could happen if you put a limit on the amount of items the queue can hold)
     *
     * Another point, how do you know if to give the user an event from subToEvent or a broadcast from subToBroad?
     * you don't know which one came first.
     * Which means, all messages needs to be in one queue.
     * I would merge it with subToEvent, have it Queue<Message> instead (and a BlockingQueue at the same time).
     */

}
