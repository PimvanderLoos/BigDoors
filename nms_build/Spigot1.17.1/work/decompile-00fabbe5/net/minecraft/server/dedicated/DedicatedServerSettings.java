package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class DedicatedServerSettings {

    private final Path source;
    private DedicatedServerProperties properties;

    public DedicatedServerSettings(Path path) {
        this.source = path;
        this.properties = DedicatedServerProperties.load(path);
    }

    public DedicatedServerProperties getProperties() {
        return this.properties;
    }

    public void save() {
        this.properties.savePropertiesFile(this.source);
    }

    public DedicatedServerSettings setProperty(UnaryOperator<DedicatedServerProperties> unaryoperator) {
        (this.properties = (DedicatedServerProperties) unaryoperator.apply(this.properties)).savePropertiesFile(this.source);
        return this;
    }
}
