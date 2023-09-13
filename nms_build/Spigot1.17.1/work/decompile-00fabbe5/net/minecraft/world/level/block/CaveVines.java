package net.minecraft.world.level.block;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CaveVines {

    VoxelShape SHAPE = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    BlockStateBoolean BERRIES = BlockProperties.BERRIES;

    static EnumInteractionResult harvest(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if ((Boolean) iblockdata.get(CaveVines.BERRIES)) {
            Block.a(world, blockposition, new ItemStack(Items.GLOW_BERRIES, 1));
            float f = MathHelper.b(world.random, 0.8F, 1.2F);

            world.playSound((EntityHuman) null, blockposition, SoundEffects.CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, f);
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(CaveVines.BERRIES, false), 2);
            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    static boolean a(IBlockData iblockdata) {
        return iblockdata.b(CaveVines.BERRIES) && (Boolean) iblockdata.get(CaveVines.BERRIES);
    }

    static ToIntFunction<IBlockData> c_(int i) {
        return (iblockdata) -> {
            return (Boolean) iblockdata.get(BlockProperties.BERRIES) ? i : 0;
        };
    }
}
