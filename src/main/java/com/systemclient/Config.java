package com.systemclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DIR = Minecraft.getInstance().gameDirectory.toPath().resolve("config/systemclient");
    public static final Map<String, Module> MODULES = new LinkedHashMap<>();
    public static String category = "Combat";
    static {
        MODULES.put("Aim Assist", new Module());
        MODULES.put("Trigger Bot", new Module());
    }
    public static final class Module {
        public boolean enabled = false;
        public String targetMode = "Players";
        public double speed = 50, prediction = 0, delay = 100, triggerPrediction = 0;
        public boolean fpsAdaptive = true;
        public int bind = -1;
    }
    public static void save() {
        try { Files.createDirectories(DIR); try (Writer w = Files.newBufferedWriter(DIR.resolve("default.json"))) { GSON.toJson(MODULES, w); } }
        catch (Exception e) { e.printStackTrace(); }
    }
    public static void load() {
        try {
            Files.createDirectories(DIR); Path p = DIR.resolve("default.json");
            if (!Files.exists(p)) { save(); return; }
            try (Reader r = Files.newBufferedReader(p)) {
                Map<String, Module> loaded = GSON.fromJson(r, new com.google.gson.reflect.TypeToken<Map<String, Module>>(){}.getType());
                if (loaded != null) MODULES.putAll(loaded);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    public static void saveAs(String name) {
        if (!name.matches("[A-Za-z0-9_-]{1,24}")) return;
        try { Files.createDirectories(DIR); try (Writer w = Files.newBufferedWriter(DIR.resolve(name + ".json"))) { GSON.toJson(MODULES, w); } }
        catch (Exception e) { e.printStackTrace(); }
    }
    public static void loadAs(String name) {
        if (!name.matches("[A-Za-z0-9_-]{1,24}")) return;
        Path p = DIR.resolve(name + ".json");
        try (Reader r = Files.newBufferedReader(p)) {
            Map<String, Module> loaded = GSON.fromJson(r, new com.google.gson.reflect.TypeToken<Map<String, Module>>(){}.getType());
            if (loaded != null) { MODULES.clear(); MODULES.putAll(loaded); save(); }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
