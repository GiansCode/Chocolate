package io.alerium.chocolate.velocity.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.minidigger.minimessage.text.MiniMessageParser;
import net.kyori.text.Component;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;

public class Config {
    
    private static final Gson gson = new Gson();
    
    private final Object plugin;
    private final File file;
    @Getter private JsonObject object;
    
    public Config(Object plugin, Path folder, String name) throws IOException {
        this.plugin = plugin;
        File folderFile = folder.toFile();
        if (!folderFile.exists())
            folderFile.mkdir();
        
        file = new File(folderFile, name + ".json");
        if (!file.exists())
            createFile();
        
        reload();
    }
    
    public void reload() throws FileNotFoundException {
        object = gson.fromJson(new FileReader(file), JsonObject.class);
    }
    
    public Component getMessage(String name, String... placeholders) {
        return MiniMessageParser.parseFormat(object.getAsJsonObject("messages").get(name).getAsString(), placeholders);
    }
    
    private void createFile() throws IOException {
        InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(file.getName());
        
        file.createNewFile();
        FileUtils.copyInputStreamToFile(stream, file);
    }
    
}
