package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.BlockStepAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorBlackstoneReplace extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorBlackstoneReplace> a = Codec.unit(() -> {
        return DefinedStructureProcessorBlackstoneReplace.b;
    });
    public static final DefinedStructureProcessorBlackstoneReplace b = new DefinedStructureProcessorBlackstoneReplace();
    private final Map<Block, Block> c = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
        hashmap.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
        hashmap.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
        hashmap.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        hashmap.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        hashmap.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        hashmap.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
        hashmap.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
        hashmap.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        hashmap.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        hashmap.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        hashmap.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
        hashmap.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        hashmap.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
        hashmap.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        hashmap.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        hashmap.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        hashmap.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        hashmap.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        hashmap.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
        hashmap.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
        hashmap.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        hashmap.put(Blocks.IRON_BARS, Blocks.CHAIN);
    });

    private DefinedStructureProcessorBlackstoneReplace() {}

    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        Block block = (Block) this.c.get(definedstructure_blockinfo1.b.getBlock());

        if (block == null) {
            return definedstructure_blockinfo1;
        } else {
            IBlockData iblockdata = definedstructure_blockinfo1.b;
            IBlockData iblockdata1 = block.getBlockData();

            if (iblockdata.b(BlockStairs.FACING)) {
                iblockdata1 = (IBlockData) iblockdata1.set(BlockStairs.FACING, iblockdata.get(BlockStairs.FACING));
            }

            if (iblockdata.b(BlockStairs.HALF)) {
                iblockdata1 = (IBlockData) iblockdata1.set(BlockStairs.HALF, iblockdata.get(BlockStairs.HALF));
            }

            if (iblockdata.b(BlockStepAbstract.a)) {
                iblockdata1 = (IBlockData) iblockdata1.set(BlockStepAbstract.a, iblockdata.get(BlockStepAbstract.a));
            }

            return new DefinedStructure.BlockInfo(definedstructure_blockinfo1.a, iblockdata1, definedstructure_blockinfo1.c);
        }
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.h;
    }
}
