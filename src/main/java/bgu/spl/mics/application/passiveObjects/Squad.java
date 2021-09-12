package bgu.spl.mics.application.passiveObjects;

import java.util.*;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

    private Map<String, Agent> agents;

    /**
     * Retrieves the single instance of this class.
     */

    public static Squad getInstance() {
        return Squad.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static Squad INSTANCE = new Squad();
    }

    private Squad() {
        agents = new HashMap<>();
    }

    /**
     * Initializes the squad. This method adds all the agents to the squad.
     * <p>
     *
     * @param agents Data structure containing all data necessary for initialization
     *               of the squad.
     */
    public void load(Agent[] agents) {
        for (Agent agent : agents)
            this.agents.put(agent.getSerialNumber(), agent);
    }

    /**
     * Releases agents.
     */
    public void releaseAgents(List<String> serials) {
        for (String temp : serials) {
            if (agents.get(temp) != null) {
                agents.get(temp).release();
                synchronized (agents.get(temp)) {
                    agents.get(temp).notify();
                }
            }
        }
    }

    /**
     * simulates executing a mission by calling sleep.
     *
     * @param time ticks to sleep
     */
    public void sendAgents(List<String> serials, int time) {
        try {
            Thread.sleep(time * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        releaseAgents(serials);
    }

    /**
     * acquires an agent, i.e. holds the agent until the caller is done with it
     *
     * @param serials the serial numbers of the agents
     * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
     */
    public boolean getAgents(List<String> serials) {
        try {
            for (String temp : serials) {
                if (agents.get(temp) == null)
                    return false;
            }

            serials.sort(String::compareTo);
            for (String s : serials) {
                Agent agent = agents.get(s);
                synchronized (agent) {
                    while (!agent.isAvailable())
                        agent.wait();
                    agent.acquire();
                }
            }
            return true;

        } catch (InterruptedException ignored) {
        }
        return false;
    }

    /**
     * gets the agents names
     *
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */

    public List<String> getAgentsNames(List<String> serials) {
        Iterator<String> iterator = serials.iterator();
        List<String> agentNames = new LinkedList<>();
        while (iterator.hasNext()) {
            String temp = iterator.next();
            if (agents.get(temp) != null) {
                agentNames.add(agents.get(temp).getName());
            }
        }
        return agentNames;
    }

}
