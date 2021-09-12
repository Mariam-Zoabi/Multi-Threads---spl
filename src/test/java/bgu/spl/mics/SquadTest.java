package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    private Squad testSquad;
    private Agent[] testAgentArr;
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;


    @BeforeEach
    public void setUp(){
        agent1 = new Agent();
        agent2 = new Agent();
        agent3 = new Agent();
        agent4 = new Agent();
        agent5 = new Agent();
        agent1.setSerialNumber("001");
        agent1.setName("Ariel");
        agent2.setSerialNumber("002");
        agent2.setName("Mariam");
        agent3.setSerialNumber("003");
        agent3.setName("James Bond");
        agent4.setSerialNumber("004");
        agent4.setName("Rick");
        agent5.setSerialNumber("000");
        agent5.setName("Morty");

        testAgentArr = new Agent[5];
        testAgentArr[0] = agent1;
        testAgentArr[1] = agent2;
        testAgentArr[2] = agent3;
        testAgentArr[3] = agent4;
        testAgentArr[4] = agent5;


    }


    @Test
    public void getAgentsNames_Test_Check_If_Works() {
        testSquad.load(testAgentArr);
        List<String> serielList = new ArrayList<String>();
        for(int i =0; i<testAgentArr.length; i++)
            serielList.add(testAgentArr[i].getSerialNumber());

        List<String> namesList = testSquad.getAgentsNames(serielList);
        //convert array to list
        List<String> testAgentNamesList = new ArrayList<String>();
        for(int i=0; i<testAgentArr.length; i++)
            testAgentNamesList.add(testAgentArr[i].getName());

        assertEquals(testAgentNamesList,namesList);
    }

    @Test
    public void getAgents_Test_Works_Right_With_Missing_Agents(){
        testSquad.load(testAgentArr);
        List<String> testSerirlNumList = new ArrayList<String>();
        testSerirlNumList.add(agent1.getSerialNumber());
        testSerirlNumList.add(agent4.getSerialNumber());
        testSerirlNumList.add(agent5.getSerialNumber());
        assertTrue(testSquad.getAgents(testSerirlNumList));
    }

    @Test
    public void getAgents_Test_Works_Right_With_Full_Agents(){

        List<String> testSerirlNumList = new ArrayList<String>();
        testAgentArr[0].acquire();
        testAgentArr[4].acquire();
        testAgentArr[0].release();
        testAgentArr[4].release();
        testSerirlNumList.add(testAgentArr[0].getSerialNumber());
        testSerirlNumList.add(agent2.getSerialNumber());
        testSerirlNumList.add(agent3.getSerialNumber());
        testSerirlNumList.add(agent4.getSerialNumber());
        testSerirlNumList.add(testAgentArr[4].getSerialNumber());
        testSquad.load(testAgentArr);
        assertTrue(testSquad.getAgents(testSerirlNumList));
    }

    @Test
    public void getAgents_Test_Fail_No_Such_Agent(){
        testSquad.load(testAgentArr);
        List<String> testSerirlNumList = new ArrayList<String>();
        testSerirlNumList.add(agent1.getSerialNumber());
        Agent notAgent = new Agent();
        notAgent.setSerialNumber("200");
        notAgent.setName("Tomer");
        testSerirlNumList.add(notAgent.getSerialNumber());
        assertTrue(!testSquad.getAgents(testSerirlNumList));
    }

    @Test
    public void getAgents_Test_Fail_Agent_Is_Acquired(){
        List<String> testSerirlNumList = new ArrayList<String>();
        testSerirlNumList.add(agent1.getSerialNumber());
        testAgentArr[3].acquire();
        testSerirlNumList.add(testAgentArr[3].getSerialNumber());
        testSquad.load(testAgentArr);
        assertFalse(testSquad.getAgents(testSerirlNumList));
    }

    @Test
    public void releaseAngents_Test_Works(){

        testAgentArr[0].acquire();
        testAgentArr[2].acquire();
        testSquad.load(testAgentArr);
        List<String> testSerielNum = new ArrayList<String>();
        testSerielNum.add(testAgentArr[0].getSerialNumber());
        testSerielNum.add(testAgentArr[2].getSerialNumber());
        testSquad.releaseAgents(testSerielNum);
        assertTrue(testSquad.getAgents(testSerielNum));

    }

    @Test
    public void sendAngents_Test_Works(){
        //in order to see if rndTime has passed in MilliSec
        //in other words: this test will check if the Agents are resolved and if the required time has passed
        System.out.println(java.time.LocalDateTime.now());

        Random rnd = new Random();
        testAgentArr[3].acquire();
        testAgentArr[2].acquire();
        List<String> testSerielNum = new ArrayList<String>();
        testSerielNum.add(testAgentArr[3].getSerialNumber());
        testSerielNum.add(testAgentArr[2].getSerialNumber());
        testSquad.load(testAgentArr);
        int rndTime = rnd.nextInt(10000) + 1000;
        testSquad.sendAgents(testSerielNum,rndTime);

        System.out.println(java.time.LocalDateTime.now()); //see if rndTime has passed

        List<String> testCompareSeriel = new ArrayList<String>();
        testCompareSeriel.add("002");
        testCompareSeriel.add("003");
        assertTrue(testSquad.getAgents(testCompareSeriel));

    }


}