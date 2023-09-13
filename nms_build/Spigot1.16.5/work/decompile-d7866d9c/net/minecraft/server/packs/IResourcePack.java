package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;

public interface IResourcePack extends AutoCloseable {

    InputStream a(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) throws IOException;

    Collection<MinecraftKey> a(EnumResourcePackType enumresourcepacktype, String s, String s1, int i, Predicate<String> predicate);

    boolean b(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey);

    Set<String> a(EnumResourcePackType enumresourcepacktype);

    @Nullable
    <T> T a(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException;

    String a();

    void close();
}
