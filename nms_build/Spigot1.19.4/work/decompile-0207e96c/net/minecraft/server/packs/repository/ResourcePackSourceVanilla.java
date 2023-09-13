package net.minecraft.server.packs.repository;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.ResourcePackVanilla;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.ResourcePackInfo;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.SavedFile;

public class ResourcePackSourceVanilla extends BuiltInPackSource {

    private static final ResourcePackInfo VERSION_METADATA_SECTION = new ResourcePackInfo(IChatBaseComponent.translatable("dataPack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(EnumResourcePackType.SERVER_DATA));
    private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(ResourcePackInfo.TYPE, ResourcePackSourceVanilla.VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, ResourcePackSourceVanilla.FEATURE_FLAGS_METADATA_SECTION);
    private static final IChatBaseComponent VANILLA_NAME = IChatBaseComponent.translatable("dataPack.vanilla.name");
    private static final MinecraftKey PACKS_DIR = new MinecraftKey("minecraft", "datapacks");

    public ResourcePackSourceVanilla() {
        super(EnumResourcePackType.SERVER_DATA, createVanillaPackSource(), ResourcePackSourceVanilla.PACKS_DIR);
    }

    private static ResourcePackVanilla createVanillaPackSource() {
        return (new VanillaPackResourcesBuilder()).setMetadata(ResourcePackSourceVanilla.BUILT_IN_METADATA).exposeNamespace("minecraft").applyDevelopmentConfig().pushJarResources().build();
    }

    @Override
    protected IChatBaseComponent getPackTitle(String s) {
        return IChatBaseComponent.literal(s);
    }

    @Nullable
    @Override
    protected ResourcePackLoader createVanillaPack(IResourcePack iresourcepack) {
        return ResourcePackLoader.readMetaAndCreate("vanilla", ResourcePackSourceVanilla.VANILLA_NAME, false, (s) -> {
            return iresourcepack;
        }, EnumResourcePackType.SERVER_DATA, ResourcePackLoader.Position.BOTTOM, PackSource.BUILT_IN);
    }

    @Nullable
    @Override
    protected ResourcePackLoader createBuiltinPack(String s, ResourcePackLoader.c resourcepackloader_c, IChatBaseComponent ichatbasecomponent) {
        return ResourcePackLoader.readMetaAndCreate(s, ichatbasecomponent, false, resourcepackloader_c, EnumResourcePackType.SERVER_DATA, ResourcePackLoader.Position.TOP, PackSource.FEATURE);
    }

    public static ResourcePackRepository createPackRepository(Path path) {
        return new ResourcePackRepository(new ResourcePackSource[]{new ResourcePackSourceVanilla(), new ResourcePackSourceFolder(path, EnumResourcePackType.SERVER_DATA, PackSource.WORLD)});
    }

    public static ResourcePackRepository createPackRepository(Convertable.ConversionSession convertable_conversionsession) {
        return createPackRepository(convertable_conversionsession.getLevelPath(SavedFile.DATAPACK_DIR));
    }
}
