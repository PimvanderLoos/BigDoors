package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class BlockCauldron extends AbstractCauldronBlock {

    private static final float RAIN_FILL_CHANCE = 0.05F;
    private static final float POWDER_SNOW_FILL_CHANCE = 0.1F;

    public BlockCauldron(BlockBase.Info blockbase_info) {
        super(blockbase_info, CauldronInteraction.EMPTY);
    }

    @Override
    public boolean isFull(IBlockData iblockdata) {
        return false;
    }

    protected static boolean shouldHandlePrecipitation(World world, BiomeBase.Precipitation biomebase_precipitation) {
        return biomebase_precipitation == BiomeBase.Precipitation.RAIN ? world.getRandom().nextFloat() < 0.05F : (biomebase_precipitation == BiomeBase.Precipitation.SNOW ? world.getRandom().nextFloat() < 0.1F : false);
    }

    @Override
    public void handlePrecipitation(IBlockData iblockdata, World world, BlockPosition blockposition, BiomeBase.Precipitation biomebase_precipitation) {
        if (shouldHandlePrecipitation(world, biomebase_precipitation)) {
            if (biomebase_precipitation == BiomeBase.Precipitation.RAIN) {
                world.setBlockAndUpdate(blockposition, Blocks.WATER_CAULDRON.defaultBlockState());
                world.gameEvent((Entity) null, GameEvent.BLOCK_CHANGE, blockposition);
            } else if (biomebase_precipitation == BiomeBase.Precipitation.SNOW) {
                world.setBlockAndUpdate(blockposition, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
                world.gameEvent((Entity) null, GameEvent.BLOCK_CHANGE, blockposition);
            }

        }
    }

    @Override
    protected boolean canReceiveStalactiteDrip(FluidType fluidtype) {
        return true;
    }

    @Override
    protected void receiveStalactiteDrip(IBlockData iblockdata, World world, BlockPosition blockposition, FluidType fluidtype) {
        IBlockData iblockdata1;

        if (fluidtype == FluidTypes.WATER) {
            iblockdata1 = Blocks.WATER_CAULDRON.defaultBlockState();
            world.setBlockAndUpdate(blockposition, iblockdata1);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(iblockdata1));
            world.levelEvent(1047, blockposition, 0);
        } else if (fluidtype == FluidTypes.LAVA) {
            iblockdata1 = Blocks.LAVA_CAULDRON.defaultBlockState();
            world.setBlockAndUpdate(blockposition, iblockdata1);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(iblockdata1));
            world.levelEvent(1046, blockposition, 0);
        }

    }
}
