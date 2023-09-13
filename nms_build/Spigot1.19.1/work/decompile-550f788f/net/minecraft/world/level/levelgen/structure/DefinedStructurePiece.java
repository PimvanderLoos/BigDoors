package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyStructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public abstract class DefinedStructurePiece extends StructurePiece {

    private static final Logger LOGGER = LogUtils.getLogger();
    protected final String templateName;
    protected DefinedStructure template;
    protected DefinedStructureInfo placeSettings;
    protected BlockPosition templatePosition;

    public DefinedStructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, StructureTemplateManager structuretemplatemanager, MinecraftKey minecraftkey, String s, DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        super(worldgenfeaturestructurepiecetype, i, structuretemplatemanager.getOrCreate(minecraftkey).getBoundingBox(definedstructureinfo, blockposition));
        this.setOrientation(EnumDirection.NORTH);
        this.templateName = s;
        this.templatePosition = blockposition;
        this.template = structuretemplatemanager.getOrCreate(minecraftkey);
        this.placeSettings = definedstructureinfo;
    }

    public DefinedStructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound, StructureTemplateManager structuretemplatemanager, Function<MinecraftKey, DefinedStructureInfo> function) {
        super(worldgenfeaturestructurepiecetype, nbttagcompound);
        this.setOrientation(EnumDirection.NORTH);
        this.templateName = nbttagcompound.getString("Template");
        this.templatePosition = new BlockPosition(nbttagcompound.getInt("TPX"), nbttagcompound.getInt("TPY"), nbttagcompound.getInt("TPZ"));
        MinecraftKey minecraftkey = this.makeTemplateLocation();

        this.template = structuretemplatemanager.getOrCreate(minecraftkey);
        this.placeSettings = (DefinedStructureInfo) function.apply(minecraftkey);
        this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
    }

    protected MinecraftKey makeTemplateLocation() {
        return new MinecraftKey(this.templateName);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("TPX", this.templatePosition.getX());
        nbttagcompound.putInt("TPY", this.templatePosition.getY());
        nbttagcompound.putInt("TPZ", this.templatePosition.getZ());
        nbttagcompound.putString("Template", this.templateName);
    }

    @Override
    public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        this.placeSettings.setBoundingBox(structureboundingbox);
        this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        if (this.template.placeInWorld(generatoraccessseed, this.templatePosition, blockposition, this.placeSettings, randomsource, 2)) {
            List<DefinedStructure.BlockInfo> list = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();

                if (definedstructure_blockinfo.nbt != null) {
                    BlockPropertyStructureMode blockpropertystructuremode = BlockPropertyStructureMode.valueOf(definedstructure_blockinfo.nbt.getString("mode"));

                    if (blockpropertystructuremode == BlockPropertyStructureMode.DATA) {
                        this.handleDataMarker(definedstructure_blockinfo.nbt.getString("metadata"), definedstructure_blockinfo.pos, generatoraccessseed, randomsource, structureboundingbox);
                    }
                }
            }

            List<DefinedStructure.BlockInfo> list1 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW);
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo1 = (DefinedStructure.BlockInfo) iterator1.next();

                if (definedstructure_blockinfo1.nbt != null) {
                    String s = definedstructure_blockinfo1.nbt.getString("final_state");
                    IBlockData iblockdata = Blocks.AIR.defaultBlockState();

                    try {
                        iblockdata = ArgumentBlock.parseForBlock((IRegistry) IRegistry.BLOCK, s, true).blockState();
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        DefinedStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, definedstructure_blockinfo1.pos);
                    }

                    generatoraccessseed.setBlock(definedstructure_blockinfo1.pos, iblockdata, 3);
                }
            }
        }

    }

    protected abstract void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, RandomSource randomsource, StructureBoundingBox structureboundingbox);

    /** @deprecated */
    @Deprecated
    @Override
    public void move(int i, int j, int k) {
        super.move(i, j, k);
        this.templatePosition = this.templatePosition.offset(i, j, k);
    }

    @Override
    public EnumBlockRotation getRotation() {
        return this.placeSettings.getRotation();
    }

    public DefinedStructure template() {
        return this.template;
    }

    public BlockPosition templatePosition() {
        return this.templatePosition;
    }

    public DefinedStructureInfo placeSettings() {
        return this.placeSettings;
    }
}
