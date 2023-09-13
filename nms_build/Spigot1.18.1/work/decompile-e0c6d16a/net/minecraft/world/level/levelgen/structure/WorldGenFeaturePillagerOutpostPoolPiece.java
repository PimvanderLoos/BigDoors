package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructureJigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeaturePillagerOutpostPoolPiece extends StructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    protected final WorldGenFeatureDefinedStructurePoolStructure element;
    protected BlockPosition position;
    private final int groundLevelDelta;
    protected final EnumBlockRotation rotation;
    private final List<WorldGenFeatureDefinedStructureJigsawJunction> junctions = Lists.newArrayList();
    private final DefinedStructureManager structureManager;

    public WorldGenFeaturePillagerOutpostPoolPiece(DefinedStructureManager definedstructuremanager, WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, BlockPosition blockposition, int i, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox) {
        super(WorldGenFeatureStructurePieceType.JIGSAW, 0, structureboundingbox);
        this.structureManager = definedstructuremanager;
        this.element = worldgenfeaturedefinedstructurepoolstructure;
        this.position = blockposition;
        this.groundLevelDelta = i;
        this.rotation = enumblockrotation;
    }

    public WorldGenFeaturePillagerOutpostPoolPiece(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.JIGSAW, nbttagcompound);
        this.structureManager = structurepieceserializationcontext.structureManager();
        this.position = new BlockPosition(nbttagcompound.getInt("PosX"), nbttagcompound.getInt("PosY"), nbttagcompound.getInt("PosZ"));
        this.groundLevelDelta = nbttagcompound.getInt("ground_level_delta");
        RegistryReadOps<NBTBase> registryreadops = RegistryReadOps.create(DynamicOpsNBT.INSTANCE, structurepieceserializationcontext.resourceManager(), structurepieceserializationcontext.registryAccess());
        DataResult dataresult = WorldGenFeatureDefinedStructurePoolStructure.CODEC.parse(registryreadops, nbttagcompound.getCompound("pool_element"));
        Logger logger = WorldGenFeaturePillagerOutpostPoolPiece.LOGGER;

        Objects.requireNonNull(logger);
        this.element = (WorldGenFeatureDefinedStructurePoolStructure) dataresult.resultOrPartial(logger::error).orElseThrow(() -> {
            return new IllegalStateException("Invalid pool element found");
        });
        this.rotation = EnumBlockRotation.valueOf(nbttagcompound.getString("rotation"));
        this.boundingBox = this.element.getBoundingBox(this.structureManager, this.position, this.rotation);
        NBTTagList nbttaglist = nbttagcompound.getList("junctions", 10);

        this.junctions.clear();
        nbttaglist.forEach((nbtbase) -> {
            this.junctions.add(WorldGenFeatureDefinedStructureJigsawJunction.deserialize(new Dynamic(registryreadops, nbtbase)));
        });
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("PosX", this.position.getX());
        nbttagcompound.putInt("PosY", this.position.getY());
        nbttagcompound.putInt("PosZ", this.position.getZ());
        nbttagcompound.putInt("ground_level_delta", this.groundLevelDelta);
        RegistryWriteOps<NBTBase> registrywriteops = RegistryWriteOps.create(DynamicOpsNBT.INSTANCE, structurepieceserializationcontext.registryAccess());
        DataResult dataresult = WorldGenFeatureDefinedStructurePoolStructure.CODEC.encodeStart(registrywriteops, this.element);
        Logger logger = WorldGenFeaturePillagerOutpostPoolPiece.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("pool_element", nbtbase);
        });
        nbttagcompound.putString("rotation", this.rotation.name());
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.junctions.iterator();

        while (iterator.hasNext()) {
            WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) iterator.next();

            nbttaglist.add((NBTBase) worldgenfeaturedefinedstructurejigsawjunction.serialize(registrywriteops).getValue());
        }

        nbttagcompound.put("junctions", nbttaglist);
    }

    @Override
    public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        this.place(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, blockposition, false);
    }

    public void place(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, BlockPosition blockposition, boolean flag) {
        this.element.place(this.structureManager, generatoraccessseed, structuremanager, chunkgenerator, this.position, blockposition, this.rotation, structureboundingbox, random, flag);
    }

    @Override
    public void move(int i, int j, int k) {
        super.move(i, j, k);
        this.position = this.position.offset(i, j, k);
    }

    @Override
    public EnumBlockRotation getRotation() {
        return this.rotation;
    }

    public String toString() {
        return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.position, this.rotation, this.element);
    }

    public WorldGenFeatureDefinedStructurePoolStructure getElement() {
        return this.element;
    }

    public BlockPosition getPosition() {
        return this.position;
    }

    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }

    public void addJunction(WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction) {
        this.junctions.add(worldgenfeaturedefinedstructurejigsawjunction);
    }

    public List<WorldGenFeatureDefinedStructureJigsawJunction> getJunctions() {
        return this.junctions;
    }
}
