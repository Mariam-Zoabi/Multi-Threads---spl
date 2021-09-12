package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.MI6Runner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {

    //i need the num of instances of this publisher
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    private int timeTick;
    private int lastSentMessage;
    private MissionInfo[] missions;


    public Intelligence() {
        super("Intelligence-" + Intelligence.ID_COUNTER.getAndIncrement());
        this.timeTick = 0;
        this.lastSentMessage = -1;

    }

    public Intelligence(int lastSentMessage, MissionInfo[] mission) {
        super("");
        this.lastSentMessage = lastSentMessage;
        this.missions = mission;
        Arrays.sort(missions, Comparator.comparingInt(MissionInfo::getTimeIssued));
    }


    @Override
    protected void initialize() {

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate) -> {
            this.terminate();
        });

        subscribeBroadcast(TickBroadcast.class, (x) -> {
            timeTick = x.getTick();
            while (missions.length - 1 > lastSentMessage && missions[lastSentMessage + 1].getTimeIssued() <= timeTick) {
                MissionReceivedEvent missionReceived = new MissionReceivedEvent(missions[++lastSentMessage]);
                this.getSimplePublisher().sendEvent(missionReceived);
            }
        });
        MI6Runner.getGate().countDown();
    }

}
