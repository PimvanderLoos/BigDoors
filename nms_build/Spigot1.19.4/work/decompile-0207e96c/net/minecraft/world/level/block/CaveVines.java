package net.minecraft.world.level.block;

import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CaveVines {

    VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    BlockStateBoolean BERRIES = BlockProperties.BERRIES;

    static EnumInteractionResult use(@Nullable Entity entity, IBlockData iblockdata, World world, BlockPosition blockposition) {
        if ((Boolean) iblockdata.getValue(CaveVines.BERRIES)) {
            Block.popResource(world, blockposition, new ItemStack(Items.GLOW_BERRIES, 1));
            float f = MathHelper.randomBetween(world.random, 0.8F, 1.2F);

            world.playSound((EntityHuman) null, blockposition, SoundEffects.CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, f);
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(CaveVines.BERRIES, false);

            world.setBlock(blockposition, iblockdata1, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entity, iblockdata1));
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    static boolean hasGlowBerries(IBlockData iblockdata) {
        return iblockdata.hasProperty(CaveVines.BERRIES) && (Boolean) iblockdata.getValue(CaveVines.BERRIES);
    }

    static ToIntFunction<IBlockData> emission(int i) {
        return (iblockdata) -> {
            return (Boolean) iblockdata.getValue(BlockProperties.BERRIES) ? i : 0;
        };
    }
}
