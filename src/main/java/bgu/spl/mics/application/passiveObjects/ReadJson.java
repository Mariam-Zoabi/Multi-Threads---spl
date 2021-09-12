package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ReadJson {
    public static Config configJson(String filePath)  {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Config.class);
        } catch (IOException ignored) {
        }
        return null;
    }
}

