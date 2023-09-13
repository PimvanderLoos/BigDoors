package net.minecraft.server.packs.resources;

import java.io.IOException;
import net.minecraft.resources.MinecraftKey;

public interface ResourceProvider {

    IResource getResource(MinecraftKey minecraftkey) throws IOException;
}
