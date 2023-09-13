package net.minecraft.server.packs.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;

public interface IResource extends Closeable {

    MinecraftKey getLocation();

    InputStream getInputStream();

    boolean hasMetadata();

    @Nullable
    <T> T getMetadata(ResourcePackMetaParser<T> resourcepackmetaparser);

    String getSourceName();
}
