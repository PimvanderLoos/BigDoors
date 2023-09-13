package net.minecraft.server.packs.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;

public interface IResource extends Closeable {

    MinecraftKey a();

    InputStream b();

    boolean c();

    @Nullable
    <T> T a(ResourcePackMetaParser<T> resourcepackmetaparser);

    String d();
}
