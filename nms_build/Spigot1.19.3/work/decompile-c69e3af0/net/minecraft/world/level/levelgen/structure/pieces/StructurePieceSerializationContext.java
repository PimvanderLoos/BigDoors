package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record StructurePieceSerializationContext(IResourceManager resourceManager, IRegistryCustom registryAccess, StructureTemplateManager structureTemplateManager) {

    public static StructurePieceSerializationContext fromLevel(WorldServer worldserver) {
        MinecraftServer minecraftserver = worldserver.getServer();

        return new StructurePieceSerializationContext(minecraftserver.getResourceManager(), minecraftserver.registryAccess(), minecraftserver.getStructureManager());
    }
}
