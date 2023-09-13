package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class ItemSpade extends ItemTool {

    private static final Set<Block> e = Sets.newHashSet(new Block[] { Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.dS});

    public ItemSpade(Item.EnumToolMaterial item_enumtoolmaterial) {
        super(1.5F, -3.0F, item_enumtoolmaterial, ItemSpade.e);
    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        return block == Blocks.SNOW_LAYER ? true : block == Blocks.SNOW;
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (enumdirection != EnumDirection.DOWN && world.getType(blockposition.up()).getMaterial() == Material.AIR && block == Blocks.GRASS) {
                IBlockData iblockdata1 = Blocks.GRASS_PATH.getBlockData();

                world.a(entityhuman, blockposition, SoundEffects.gz, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClientSide) {
                    world.setTypeAndData(blockposition, iblockdata1, 11);
                    itemstack.damage(1, entityhuman);
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }
}
