package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public class ExplosionDamageCalculator {

    public ExplosionDamageCalculator() {}

    public Optional<Float> a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return iblockdata.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(iblockdata.getBlock().getDurability(), fluid.i()));
    }

    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }
}
