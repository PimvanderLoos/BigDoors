package net.minecraft.server.packs.resources;

import java.io.IOException;
import net.minecraft.resources.MinecraftKey;

@FunctionalInterface
public interface ResourceProvider {

    IResource getResource(MinecraftKey minecraftkey) throws IOException;
}
