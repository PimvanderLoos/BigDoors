package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DefinedStructurePiece extends StructurePiece {

    private static final Logger LOGGER = LogManager.getLogger();
    protected DefinedStructure a;
    protected DefinedStructureInfo b;
    protected BlockPosition c;

    public DefinedStructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i) {
        super(worldgenfeaturestructurepiecetype, i);
    }

    public DefinedStructurePiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
        super(worldgenfeaturestructurepiecetype, nbttagcompound);
        this.c = new BlockPosition(nbttagcompound.getInt("TPX"), nbttagcompound.getInt("TPY"), nbttagcompound.getInt("TPZ"));
    }

    protected void a(DefinedStructure definedstructure, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        this.a = definedstructure;
        this.a(EnumDirection.NORTH);
        this.c = blockposition;
        this.b = definedstructureinfo;
        this.n = definedstructure.b(definedstructureinfo, blockposition);
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("TPX", this.c.getX());
        nbttagcompound.setInt("TPY", this.c.getY());
        nbttagcompound.setInt("TPZ", this.c.getZ());
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        this.b.a(structureboundingbox);
        this.n = this.a.b(this.b, this.c);
        if (this.a.a(generatoraccessseed, this.c, blockposition, this.b, random, 2)) {
            List<DefinedStructure.BlockInfo> list = this.a.a(this.c, this.b, Blocks.STRUCTURE_BLOCK);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();

                if (definedstructure_blockinfo.c != null) {
                    BlockPropertyStructureMode blockpropertystructuremode = BlockPropertyStructureMode.valueOf(definedstructure_blockinfo.c.getString("mode"));

                    if (blockpropertystructuremode == BlockPropertyStructureMode.DATA) {
                        this.a(definedstructure_blockinfo.c.getString("metadata"), definedstructure_blockinfo.a, generatoraccessseed, random, structureboundingbox);
                    }
                }
            }

            List<DefinedStructure.BlockInfo> list1 = this.a.a(this.c, this.b, Blocks.JIGSAW);
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo1 = (DefinedStructure.BlockInfo) iterator1.next();

                if (definedstructure_blockinfo1.c != null) {
                    String s = definedstructure_blockinfo1.c.getString("final_state");
                    ArgumentBlock argumentblock = new ArgumentBlock(new StringReader(s), false);
                    IBlockData iblockdata = Blocks.AIR.getBlockData();

                    try {
                        argumentblock.a(true);
                        IBlockData iblockdata1 = argumentblock.getBlockData();

                        if (iblockdata1 != null) {
                            iblockdata = iblockdata1;
                        } else {
                            DefinedStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, definedstructure_blockinfo1.a);
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        DefinedStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, definedstructure_blockinfo1.a);
                    }

                    generatoraccessseed.setTypeAndData(definedstructure_blockinfo1.a, iblockdata, 3);
                }
            }
        }

        return true;
    }

    protected abstract void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox);

    @Override
    public void a(int i, int j, int k) {
        super.a(i, j, k);
        this.c = this.c.b(i, j, k);
    }

    @Override
    public EnumBlockRotation ap_() {
        return this.b.d();
    }
}
