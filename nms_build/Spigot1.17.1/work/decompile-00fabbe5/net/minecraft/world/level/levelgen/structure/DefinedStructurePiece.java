package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyStructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DefinedStructurePiece extends StructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    protected final String templateName;
    protected DefinedStructure template;
    protected DefinedStructureInfo placeSettings;
    protected BlockPosition templatePosition;

    public DefinedStructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, String s, DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        super(worldgenfeaturestructurepiecetype, i, definedstructuremanager.a(minecraftkey).b(definedstructureinfo, blockposition));
        this.a(EnumDirection.NORTH);
        this.templateName = s;
        this.templatePosition = blockposition;
        this.template = definedstructuremanager.a(minecraftkey);
        this.placeSettings = definedstructureinfo;
    }

    public DefinedStructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound, WorldServer worldserver, Function<MinecraftKey, DefinedStructureInfo> function) {
        super(worldgenfeaturestructurepiecetype, nbttagcompound);
        this.a(EnumDirection.NORTH);
        this.templateName = nbttagcompound.getString("Template");
        this.templatePosition = new BlockPosition(nbttagcompound.getInt("TPX"), nbttagcompound.getInt("TPY"), nbttagcompound.getInt("TPZ"));
        MinecraftKey minecraftkey = this.a();

        this.template = worldserver.p().a(minecraftkey);
        this.placeSettings = (DefinedStructureInfo) function.apply(minecraftkey);
        this.boundingBox = this.template.b(this.placeSettings, this.templatePosition);
    }

    protected MinecraftKey a() {
        return new MinecraftKey(this.templateName);
    }

    @Override
    protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("TPX", this.templatePosition.getX());
        nbttagcompound.setInt("TPY", this.templatePosition.getY());
        nbttagcompound.setInt("TPZ", this.templatePosition.getZ());
        nbttagcompound.setString("Template", this.templateName);
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        this.placeSettings.a(structureboundingbox);
        this.boundingBox = this.template.b(this.placeSettings, this.templatePosition);
        if (this.template.a(generatoraccessseed, this.templatePosition, blockposition, this.placeSettings, random, 2)) {
            List<DefinedStructure.BlockInfo> list = this.template.a(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();

                if (definedstructure_blockinfo.nbt != null) {
                    BlockPropertyStructureMode blockpropertystructuremode = BlockPropertyStructureMode.valueOf(definedstructure_blockinfo.nbt.getString("mode"));

                    if (blockpropertystructuremode == BlockPropertyStructureMode.DATA) {
                        this.a(definedstructure_blockinfo.nbt.getString("metadata"), definedstructure_blockinfo.pos, generatoraccessseed, random, structureboundingbox);
                    }
                }
            }

            List<DefinedStructure.BlockInfo> list1 = this.template.a(this.templatePosition, this.placeSettings, Blocks.JIGSAW);
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo1 = (DefinedStructure.BlockInfo) iterator1.next();

                if (definedstructure_blockinfo1.nbt != null) {
                    String s = definedstructure_blockinfo1.nbt.getString("final_state");
                    ArgumentBlock argumentblock = new ArgumentBlock(new StringReader(s), false);
                    IBlockData iblockdata = Blocks.AIR.getBlockData();

                    try {
                        argumentblock.a(true);
                        IBlockData iblockdata1 = argumentblock.getBlockData();

                        if (iblockdata1 != null) {
                            iblockdata = iblockdata1;
                        } else {
                            DefinedStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, definedstructure_blockinfo1.pos);
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        DefinedStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, definedstructure_blockinfo1.pos);
                    }

                    generatoraccessseed.setTypeAndData(definedstructure_blockinfo1.pos, iblockdata, 3);
                }
            }
        }

        return true;
    }

    protected abstract void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox);

    @Override
    public void a(int i, int j, int k) {
        super.a(i, j, k);
        this.templatePosition = this.templatePosition.c(i, j, k);
    }

    @Override
    public EnumBlockRotation ac_() {
        return this.placeSettings.d();
    }
}
