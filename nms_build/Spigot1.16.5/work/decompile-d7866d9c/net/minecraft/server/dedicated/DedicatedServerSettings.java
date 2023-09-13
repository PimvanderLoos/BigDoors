package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.core.IRegistryCustom;

public class DedicatedServerSettings {

    private final Path path;
    private DedicatedServerProperties properties;

    public DedicatedServerSettings(IRegistryCustom iregistrycustom, Path path) {
        this.path = path;
        this.properties = DedicatedServerProperties.load(iregistrycustom, path);
    }

    public DedicatedServerProperties getProperties() {
        return this.properties;
    }

    public void save() {
        this.properties.savePropertiesFile(this.path);
    }

    public DedicatedServerSettings setProperty(UnaryOperator<DedicatedServerProperties> unaryoperator) {
        (this.properties = (DedicatedServerProperties) unaryoperator.apply(this.properties)).savePropertiesFile(this.path);
        return this;
    }
}
