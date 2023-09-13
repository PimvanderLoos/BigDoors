package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public class ExplosionDamageCalculator {

    public ExplosionDamageCalculator() {}

    public Optional<Float> getBlockExplosionResistance(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return iblockdata.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(iblockdata.getBlock().getExplosionResistance(), fluid.getExplosionResistance()));
    }

    public boolean shouldBlockExplode(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }
}
