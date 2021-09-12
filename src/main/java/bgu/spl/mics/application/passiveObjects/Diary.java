package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
    private List<Report> reports;
    private int total;

    /**
     * Retrieves the single instance of this class.
     */
    public static Diary getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Diary() {
        reports = new LinkedList<>();
        total = 0;
    }

    private static class SingletonHolder {
        private static Diary INSTANCE = new Diary();
    }

    public List<Report> getReports() {
        return reports;
    }

    public synchronized void incrementTotal() {
        total++;
    }

    /**
     * adds a report to the diary
     *
     * @param reportToAdd - the report to add
     */
    public void addReport(Report reportToAdd) {
        synchronized (reports) {
            reports.add(reportToAdd);
        }
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object List<Report> which is a
     * List of all the reports in the diary.
     * This method is called by the main method in order to generate the output.
     */
    @SuppressWarnings("unchecked")
    public void printToFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            new Gson().toJson(this, fileWriter);
        } catch (IOException ignored) {
        }
    }


    /**
     * Gets the total number of received missions (executed / aborted) be all the M-instances.
     *
     * @return the total number of received missions (executed / aborted) be all the M-instances.
     */
    public int getTotal() {
        return total;
    }


}
