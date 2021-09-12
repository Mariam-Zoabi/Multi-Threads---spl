package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.MI6Runner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    private int currentTick;
    private int timeTick;

    public M() {
        super("M-" + M.ID_COUNTER.getAndIncrement());
        this.currentTick = 0;
        this.timeTick = -1;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {//not sure
            this.currentTick++;
        });

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminate) -> this.terminate());

        this.subscribeEvent(MissionReceivedEvent.class, (MissionReceivedEvent newMission) -> {
            Diary.getInstance().incrementTotal();
            MissionInfo mi = newMission.getMissionInfo();

            List<String> agentsSerialNum = mi.getSerialAgentsNumbers();
            AgentsAvailableEvent agentsE = new AgentsAvailableEvent(agentsSerialNum);
            Future<Object[]> futureAvailability = this.sendEvent(agentsE);

            if (futureAvailability == null) {
                return;
            }

            Object[] result = futureAvailability.get();
            if (result == null) {
                return;
            }
			Integer mp = (Integer) result[0];
            @SuppressWarnings("unchecked")
            List<String> agentsNames = (List<String>)result[1];
			if (mp==null||agentsNames == null||mp.equals(-1)) {
  				return;
			}
            String gadget = mi.getGadget();
            Future<Integer> gadgetAvailability = this.sendEvent(new GadgetAvailableEvent(gadget));
            if (gadgetAvailability == null) {
                agentsE.getFuture().resolve(0);
                return;
            }
            Integer result2 = gadgetAvailability.get();
            if (result2 == null || result2>=0) {
                agentsE.getFuture().resolve(0);
                return;
            }

            int timeExpired = mi.getTimeExpired();
            if (currentTick > timeExpired) {
                agentsE.getFuture().resolve(0);
                return;
            }

            agentsE.getFuture().resolve(mi.getDuration());
            Report report=new Report();
            report.setAgentsNames(agentsNames);
            report.setTimeIssued(mi.getTimeIssued());
            report.setTimeCreated(timeTick);
            report.setQTime(result2);
            report.setMoneypenny(mp);
            report.setMissionName(mi.getName());
            report.setGadgetName(mi.getGadget());
            report.setAgentsSerialNumbers(mi.getSerialAgentsNumbers());

            Diary.getInstance().addReport(report);
        });
        MI6Runner.getGate().countDown();

    }

}
