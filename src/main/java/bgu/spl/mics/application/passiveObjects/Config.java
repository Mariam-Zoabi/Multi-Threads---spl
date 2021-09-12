package bgu.spl.mics.application.passiveObjects;


public class Config {
        private Agent[] squad;
        private String[] inventory;
        private Services services;

        public Agent[] getSquad() {
            return squad;
        }

        public void setSquad(Agent[] squad) {
            this.squad = squad;
        }

        public String[] getInventory() { return inventory; }

        public void setInventory(String[] inventory) {
            this.inventory = inventory;
        }

        public Services getServices() {
            return services;
        }

        public void setServices(Services services) {
            this.services = services;
        }
    }


