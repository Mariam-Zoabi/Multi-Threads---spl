package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.Gson;


import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    // Member - shared object
    public static CountDownLatch gate; // Will help to coordinate the services,//copy this
    // so TimeService will initialize after everyone else initialized.

    public static CountDownLatch getGate() {
        return MI6Runner.gate;
    }


    public static void main(String[] args) throws IOException {
        Config config = new Gson().fromJson(new FileReader(args[0]), Config.class);
        Inventory.getInstance().load(config.getInventory());
        Squad.getInstance().load(config.getSquad());
        List<String> serials = new LinkedList<>();
        for (int i = 0; i < config.getSquad().length; i++) {
            serials.add(config.getSquad()[i].getSerialNumber());
        }
        Squad.getInstance().releaseAgents(serials);
        Services services = config.getServices();
        int M = services.M;
        int MoneyPenny = services.Moneypenny;
        int intelligence = services.intelligence.length;
        int Time = services.time;

        try {
            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < M; i++) {
                threads.add(new Thread(new M(), "M" + i));
            }
            for (int i = 0; i < MoneyPenny; i++) {
                threads.add(new Thread(new Moneypenny(), "MoneyPenny" + i));
            }
            for (int i = 0; i < intelligence; i++) {
                threads.add(new Thread(services.intelligence[i], "Int" + i));
            }
            TimeService timeService;
            threads.add(new Thread(new Q(), "Q"));
            gate = new CountDownLatch(threads.size());
            for (int i = 0; i < threads.size(); i++) {
                threads.get(i).start();
            }

            gate.await();

            Thread timeTh = new Thread(new TimeService(Time), "Tick");
            timeTh.start();
            timeTh.join();
            for (int i = 0; i < threads.size(); i++) {
                threads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Inventory k = Inventory.getInstance();
        k.printToFile(args[1]);
        Diary d = Diary.getInstance();
        d.printToFile(args[2]);
    }
}
