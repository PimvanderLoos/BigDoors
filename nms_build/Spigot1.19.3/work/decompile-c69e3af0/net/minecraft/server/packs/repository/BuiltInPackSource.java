package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.ResourcePackVanilla;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BuiltInPackSource implements ResourcePackSource {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_ID = "vanilla";
    private final EnumResourcePackType packType;
    private final ResourcePackVanilla vanillaPack;
    private final MinecraftKey packDir;

    public BuiltInPackSource(EnumResourcePackType enumresourcepacktype, ResourcePackVanilla resourcepackvanilla, MinecraftKey minecraftkey) {
        this.packType = enumresourcepacktype;
        this.vanillaPack = resourcepackvanilla;
        this.packDir = minecraftkey;
    }

    @Override
    public void loadPacks(Consumer<ResourcePackLoader> consumer) {
        ResourcePackLoader resourcepackloader = this.createVanillaPack(this.vanillaPack);

        if (resourcepackloader != null) {
            consumer.accept(resourcepackloader);
        }

        this.listBundledPacks(consumer);
    }

    @Nullable
    protected abstract ResourcePackLoader createVanillaPack(IResourcePack iresourcepack);

    protected abstract IChatBaseComponent getPackTitle(String s);

    public ResourcePackVanilla getVanillaPack() {
        return this.vanillaPack;
    }

    private void listBundledPacks(Consumer<ResourcePackLoader> consumer) {
        Map<String, Function<String, ResourcePackLoader>> map = new HashMap();

        Objects.requireNonNull(map);
        this.populatePackList(map::put);
        map.forEach((s, function) -> {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) function.apply(s);

            if (resourcepackloader != null) {
                consumer.accept(resourcepackloader);
            }

        });
    }

    protected void populatePackList(BiConsumer<String, Function<String, ResourcePackLoader>> biconsumer) {
        this.vanillaPack.listRawPaths(this.packType, this.packDir, (path) -> {
            this.discoverPacksInPath(path, biconsumer);
        });
    }

    protected void discoverPacksInPath(@Nullable Path path, BiConsumer<String, Function<String, ResourcePackLoader>> biconsumer) {
        if (path != null && Files.isDirectory(path, new LinkOption[0])) {
            try {
                ResourcePackSourceFolder.discoverPacks(path, true, (path1, resourcepackloader_c) -> {
                    biconsumer.accept(pathToId(path1), (s) -> {
                        return this.createBuiltinPack(s, resourcepackloader_c, this.getPackTitle(s));
                    });
                });
            } catch (IOException ioexception) {
                BuiltInPackSource.LOGGER.warn("Failed to discover packs in {}", path, ioexception);
            }
        }

    }

    private static String pathToId(Path path) {
        return StringUtils.removeEnd(path.getFileName().toString(), ".zip");
    }

    @Nullable
    protected abstract ResourcePackLoader createBuiltinPack(String s, ResourcePackLoader.c resourcepackloader_c, IChatBaseComponent ichatbasecomponent);
}
