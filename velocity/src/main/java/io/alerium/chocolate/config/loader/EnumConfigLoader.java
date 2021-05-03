package io.alerium.chocolate.config.loader;

import eu.vertcode.vertconfig.VertConfigs;
import eu.vertcode.vertconfig.object.VertConfig;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class EnumConfigLoader {

    private final File file;
    private final Class<?> clazz;
    private VertConfig infusedConfig;

    @SneakyThrows
    public EnumConfigLoader(File file, Class<?> clazz) {
        this.file = file;
        this.clazz = clazz;
        if (!clazz.isEnum()) throw new NullPointerException("The class must be a enum to use EnumConfigLoader");
        if (!EnumConfig.class.isAssignableFrom(clazz))
            throw new NullPointerException("The class must implement EnumConfig");
        reload();
    }

    public void reload() {
        this.infusedConfig = VertConfigs.getInstance().getConfig(file, new ByteArrayInputStream("{}".getBytes()));
        setValues();
    }

    public void save() {
        try {
            this.infusedConfig.save();
        } catch (IOException e) {
            System.out.println("Couldn't save " + this.file.getName());
            e.printStackTrace();
        }
    }

    private void setValues() {
        for (Object enumConst : this.clazz.getEnumConstants()) {
            EnumConfig enumConfig = (EnumConfig) enumConst;
            Object object = this.infusedConfig.get(enumConfig.getPath(), enumConfig.getObject().getClass());
            if (object == null) this.infusedConfig.set(enumConfig.getPath(), enumConfig.getObject());
            else enumConfig.setObject(object);
        }
        save();
    }

    public interface EnumConfig {
        Object getObject();

        void setObject(Object value);

        String getPath();
    }

}
