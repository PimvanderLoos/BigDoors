package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public record StructurePieceSerializationContext(IResourceManager a, IRegistryCustom b, DefinedStructureManager c) {

    private final IResourceManager resourceManager;
    private final IRegistryCustom registryAccess;
    private final DefinedStructureManager structureManager;

    public StructurePieceSerializationContext(IResourceManager iresourcemanager, IRegistryCustom iregistrycustom, DefinedStructureManager definedstructuremanager) {
        this.resourceManager = iresourcemanager;
        this.registryAccess = iregistrycustom;
        this.structureManager = definedstructuremanager;
    }

    public static StructurePieceSerializationContext fromLevel(WorldServer worldserver) {
        MinecraftServer minecraftserver = worldserver.getServer();

        return new StructurePieceSerializationContext(minecraftserver.getResourceManager(), minecraftserver.registryAccess(), minecraftserver.getStructureManager());
    }

    public IResourceManager resourceManager() {
        return this.resourceManager;
    }

    public IRegistryCustom registryAccess() {
        return this.registryAccess;
    }

    public DefinedStructureManager structureManager() {
        return this.structureManager;
    }
}
