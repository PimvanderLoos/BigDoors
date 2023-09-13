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

    String METADATA_EXTENSION = ".mcmeta";
    String PACK_META = "pack.mcmeta";

    @Nullable
    InputStream getRootResource(String s) throws IOException;

    InputStream getResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) throws IOException;

    Collection<MinecraftKey> getResources(EnumResourcePackType enumresourcepacktype, String s, String s1, int i, Predicate<String> predicate);

    boolean hasResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey);

    Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype);

    @Nullable
    <T> T getMetadataSection(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException;

    String getName();

    void close();
}
