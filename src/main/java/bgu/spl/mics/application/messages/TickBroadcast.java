package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
//Time: The termination tick will be given as a parameter for every publisher/subscriber page16
    private final int currTick;

    public TickBroadcast(int currTick){
        this.currTick = currTick;
    }

    public int getTick() {
        return this.currTick;
    }

}
