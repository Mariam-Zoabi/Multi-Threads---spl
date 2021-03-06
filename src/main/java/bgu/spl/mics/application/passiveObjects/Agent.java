package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */


public class Agent {
	private String name;
	private String serialNumber;
	private boolean isAvailable;
	/**
	 * Sets the serial number of an agent.
	 */
	public Agent(){
		this.isAvailable = true;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
     * Retrieves the serial number of an agent.
     * <p>
     * @return The serial number of an agent.
     */
	public String getSerialNumber() {
		return this.serialNumber;
	}

	/**
	 * Sets the name of the agent.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * Retrieves the name of the agent.
     * <p>
     * @return the name of the agent.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves if the agent is available.
     * <p>
     * @return if the agent is available.
     */
	public boolean isAvailable() {
		return this.isAvailable;
	}

	/**
	 * Acquires an agent.
	 */
	public void acquire(){
		this.isAvailable = false;
	}

	/**
	 * Releases an agent.
	 */
	public void release(){
		this.isAvailable = true;
	}
}
