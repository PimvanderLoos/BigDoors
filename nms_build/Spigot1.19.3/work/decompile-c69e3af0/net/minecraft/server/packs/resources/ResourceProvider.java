package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.MinecraftKey;

@FunctionalInterface
public interface ResourceProvider {

    Optional<IResource> getResource(MinecraftKey minecraftkey);

    default IResource getResourceOrThrow(MinecraftKey minecraftkey) throws FileNotFoundException {
        return (IResource) this.getResource(minecraftkey).orElseThrow(() -> {
            return new FileNotFoundException(minecraftkey.toString());
        });
    }

    default InputStream open(MinecraftKey minecraftkey) throws IOException {
        return this.getResourceOrThrow(minecraftkey).open();
    }

    default BufferedReader openAsReader(MinecraftKey minecraftkey) throws IOException {
        return this.getResourceOrThrow(minecraftkey).openAsReader();
    }

    static ResourceProvider fromMap(Map<MinecraftKey, IResource> map) {
        return (minecraftkey) -> {
            return Optional.ofNullable((IResource) map.get(minecraftkey));
        };
    }
}
