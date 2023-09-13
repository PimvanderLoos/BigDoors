package net.minecraft.server.packs.repository;

import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.ResourcePackVanilla;
import net.minecraft.server.packs.metadata.pack.ResourcePackInfo;

public class ResourcePackSourceVanilla implements ResourcePackSource {

    public static final ResourcePackInfo BUILT_IN_METADATA = new ResourcePackInfo(new ChatMessage("dataPack.vanilla.description"), EnumResourcePackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
    public static final String VANILLA_ID = "vanilla";
    private final ResourcePackVanilla vanillaPack;

    public ResourcePackSourceVanilla() {
        this.vanillaPack = new ResourcePackVanilla(ResourcePackSourceVanilla.BUILT_IN_METADATA, new String[]{"minecraft"});
    }

    @Override
    public void loadPacks(Consumer<ResourcePackLoader> consumer, ResourcePackLoader.a resourcepackloader_a) {
        ResourcePackLoader resourcepackloader = ResourcePackLoader.create("vanilla", false, () -> {
            return this.vanillaPack;
        }, resourcepackloader_a, ResourcePackLoader.Position.BOTTOM, PackSource.BUILT_IN);

        if (resourcepackloader != null) {
            consumer.accept(resourcepackloader);
        }

    }
}
