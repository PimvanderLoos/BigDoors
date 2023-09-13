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
    public boolean c(IBlockData iblockdata) {
        return false;
    }

    protected static boolean a(World world, BiomeBase.Precipitation biomebase_precipitation) {
        return biomebase_precipitation == BiomeBase.Precipitation.RAIN ? world.getRandom().nextFloat() < 0.05F : (biomebase_precipitation == BiomeBase.Precipitation.SNOW ? world.getRandom().nextFloat() < 0.1F : false);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, BiomeBase.Precipitation biomebase_precipitation) {
        if (a(world, biomebase_precipitation)) {
            if (biomebase_precipitation == BiomeBase.Precipitation.RAIN) {
                world.setTypeUpdate(blockposition, Blocks.WATER_CAULDRON.getBlockData());
                world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
            } else if (biomebase_precipitation == BiomeBase.Precipitation.SNOW) {
                world.setTypeUpdate(blockposition, Blocks.POWDER_SNOW_CAULDRON.getBlockData());
                world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
            }

        }
    }

    @Override
    protected boolean a(FluidType fluidtype) {
        return true;
    }

    @Override
    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, FluidType fluidtype) {
        if (fluidtype == FluidTypes.WATER) {
            world.setTypeUpdate(blockposition, Blocks.WATER_CAULDRON.getBlockData());
            world.triggerEffect(1047, blockposition, 0);
            world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
        } else if (fluidtype == FluidTypes.LAVA) {
            world.setTypeUpdate(blockposition, Blocks.LAVA_CAULDRON.getBlockData());
            world.triggerEffect(1046, blockposition, 0);
            world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
        }

    }
}
