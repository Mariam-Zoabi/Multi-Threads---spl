package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.MI6Runner;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

    private Inventory inventory;
    private int time = 0;


    public Q() {
        super("The Q Subscriber");
        this.inventory = Inventory.getInstance();
    }

    private static class QHolder {
        private static final Q Instance = new Q();
    }

    public static Q getInstance() {
        return QHolder.Instance;
    }

    public Inventory printLeftInventory() {
        return null;
    }


    @Override
    protected void initialize() {
        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate) -> {
            this.terminate();
        });
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            time = tick.getTick();
        });

        this.subscribeEvent(GadgetAvailableEvent.class, (eventGadget) -> {
            boolean isGadget = inventory.getItem(eventGadget.getGadgetName());
            int qTime = isGadget ? time : -1;
            complete(eventGadget, qTime);
        });
        MI6Runner.getGate().countDown();
    }

}
