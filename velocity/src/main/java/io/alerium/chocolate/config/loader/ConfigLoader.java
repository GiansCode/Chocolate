package io.alerium.chocolate.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private final Class<?> aClass;
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private boolean newObjects = false;

    public ConfigLoader(Class<?> aClass, File file) {
        this.aClass = aClass;
        this.file = file;
        this.loadConfig();
    }

    public void loadConfig() {
        if (!this.file.getParentFile().exists()) this.file.getParentFile().mkdir();
        if (!this.file.exists()) {
            loadDefaultConfig();
            return;
        }
        try (Reader reader = new FileReader(this.file)) {
            Map<String, Object> map = this.gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
            }.getType());
            for (Field field : this.aClass.getFields()) {
                if (!field.isAccessible()) field.setAccessible(true);
                if (!map.containsKey(field.getName())) {
                    this.newObjects = true;
                    continue;
                }
                field.set(null, this.gson.fromJson(this.gson.toJson(map.get(field.getName())), field.getType()));
            }
            if (newObjects) loadNewObjects();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveConfig() {
        Map<String, Object> map = new HashMap<>();
        try (Writer writer = new FileWriter(this.file)) {
            for (Field field : this.aClass.getFields()) {
                if (!field.isAccessible()) field.setAccessible(true);
                map.put(field.getName(), field.get(null));
            }
            if (map.isEmpty())
                return;

            this.gson.toJson(map, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadNewObjects() {
        try (Reader reader = new FileReader(this.file)) {
            Map<String, Object> map = this.gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
            }.getType());
            for (Field field : this.aClass.getFields()) {
                if (map.containsKey(field.getName())) continue;
                if (!field.isAccessible()) field.setAccessible(true);
                map.put(field.getName(), field.get(null));
            }

            if (map.isEmpty()) return;

            Writer writer = new FileWriter(this.file);
            this.gson.toJson(map, writer);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadDefaultConfig() {
        try (Writer writer = new FileWriter(this.file)) {
            Map<String, Object> map = new HashMap<>();
            for (Field field : this.aClass.getFields()) {
                if (!field.isAccessible()) field.setAccessible(true);
                map.put(field.getName(), field.get(null));
            }
            this.gson.toJson(map, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
