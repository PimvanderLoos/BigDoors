package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.server.packs.resources.IoSupplier;

public interface IResourcePack extends AutoCloseable {

    String METADATA_EXTENSION = ".mcmeta";
    String PACK_META = "pack.mcmeta";

    @Nullable
    IoSupplier<InputStream> getRootResource(String... astring);

    @Nullable
    IoSupplier<InputStream> getResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey);

    void listResources(EnumResourcePackType enumresourcepacktype, String s, String s1, IResourcePack.a iresourcepack_a);

    Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype);

    @Nullable
    <T> T getMetadataSection(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException;

    String packId();

    default boolean isBuiltin() {
        return false;
    }

    void close();

    @FunctionalInterface
    public interface a extends BiConsumer<MinecraftKey, IoSupplier<InputStream>> {}
}
