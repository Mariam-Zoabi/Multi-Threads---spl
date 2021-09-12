package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;


public class MissionReceivedEvent implements Event<MissionInfo> {

    private MissionInfo mission;
    public MissionReceivedEvent(MissionInfo missionInfo) {
        this.mission = missionInfo;
    }

    public MissionInfo getMissionInfo() {
        return  this.mission;
    }
}
