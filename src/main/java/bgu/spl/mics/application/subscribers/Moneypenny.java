package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.MI6Runner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    private Squad squad;
    private int id;

    public Moneypenny() {
        super("Moneypenny-" + Moneypenny.ID_COUNTER.get());
        id = Moneypenny.ID_COUNTER.getAndIncrement();
        this.squad = Squad.getInstance();
    }

    @Override//here this sub subscribes for the suit events
    protected void initialize() {
        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate) -> {
            this.terminate();
        });

        this.subscribeEvent(AgentsAvailableEvent.class, (AgentsAvailableEvent agentsAequire) -> {
            List<String> serials = agentsAequire.getAgentSerialNum();
            boolean isAgents = squad.getAgents(serials);
            int mpid = isAgents ? id : -1;
            Object[] objects = {mpid, squad.getAgentsNames(serials)};
            this.complete(agentsAequire, objects);

            if (isAgents) {
                int durOfMis = agentsAequire.getFuture().get();
                if (durOfMis != 0)
                    squad.sendAgents(serials, durOfMis);
                else
                    squad.releaseAgents(serials);
            }
        });

        MI6Runner.getGate().countDown();

    }

}
