package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.List;

public class AgentsAvailableEvent implements Event<Object[]> {
    //this event checks the availability of the specific agent
    private List<String> agentSerilNum;
    private Future<Integer> future = new Future<>();

    public Future<Integer> getFuture() {
        return future;
    }

    public AgentsAvailableEvent(List<String> g) {
        this.agentSerilNum = g;
    }


    public List<String> getAgentSerialNum() {
        return agentSerilNum;
    }
}



