package net.minecraft.server;

import java.util.Optional;

public enum ExplosionDamageCalculatorBlock implements ExplosionDamageCalculator {

    INSTANCE;

    private ExplosionDamageCalculatorBlock() {}

    @Override
    public Optional<Float> a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return iblockdata.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(iblockdata.getBlock().getDurability(), fluid.i()));
    }

    @Override
    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }
}
