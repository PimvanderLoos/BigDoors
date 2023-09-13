package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public class ExplosionDamageCalculatorEntity extends ExplosionDamageCalculator {

    private final Entity source;

    public ExplosionDamageCalculatorEntity(Entity entity) {
        this.source = entity;
    }

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return super.getBlockExplosionResistance(explosion, iblockaccess, blockposition, iblockdata, fluid).map((ofloat) -> {
            return this.source.getBlockExplosionResistance(explosion, iblockaccess, blockposition, iblockdata, fluid, ofloat);
        });
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return this.source.shouldBlockExplode(explosion, iblockaccess, blockposition, iblockdata, f);
    }
}
