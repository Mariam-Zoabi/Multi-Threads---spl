package bgu.spl.mics.application.publishers;

import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;


/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

    private int currTick;//the current time tick
    private final int timeTick; //the time tick which sent every 100 millisecond (the time-tick in which the Publisher is supposed to send a corresponding event)
    private final int duration;//the time duration before termination = the number of ticks before termination.

    public TimeService(int duration) {
        super("TimeService");
        this.timeTick = 100;
        this.duration = duration;
        this.currTick = 0;
    }

    @Override
    protected void initialize() {
    }

    @Override
    public void run() {
		MessageBroker mb_in = MessageBrokerImpl.getInstance();
		try {

            for (int i = 0; i <= duration; i++, currTick++) {
                Thread.sleep(this.timeTick);
                mb_in.sendBroadcast(new TickBroadcast(currTick));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		mb_in.sendBroadcast(new TerminateBroadcast());
    }

}
