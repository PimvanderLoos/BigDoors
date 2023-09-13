package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public class EntityMinecartTNT extends EntityMinecartAbstract {

    private static final byte EVENT_PRIME = 10;
    private int fuse = -1;

    public EntityMinecartTNT(EntityTypes<? extends EntityMinecartTNT> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMinecartTNT(World world, double d0, double d1, double d2) {
        super(EntityTypes.TNT_MINECART, world, d0, d1, d2);
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.TNT;
    }

    @Override
    public IBlockData getDefaultDisplayBlockState() {
        return Blocks.TNT.defaultBlockState();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fuse > 0) {
            --this.fuse;
            this.level.addParticle(Particles.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
        } else if (this.fuse == 0) {
            this.explode(this.getDeltaMovement().horizontalDistanceSqr());
        }

        if (this.horizontalCollision) {
            double d0 = this.getDeltaMovement().horizontalDistanceSqr();

            if (d0 >= 0.009999999776482582D) {
                this.explode(d0);
            }
        }

    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        Entity entity = damagesource.getDirectEntity();

        if (entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.isOnFire()) {
                this.explode(entityarrow.getDeltaMovement().lengthSqr());
            }
        }

        return super.hurt(damagesource, f);
    }

    @Override
    public void destroy(DamageSource damagesource) {
        double d0 = this.getDeltaMovement().horizontalDistanceSqr();

        if (!damagesource.isFire() && !damagesource.isExplosion() && d0 < 0.009999999776482582D) {
            super.destroy(damagesource);
            if (!damagesource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtLocation((IMaterial) Blocks.TNT);
            }

        } else {
            if (this.fuse < 0) {
                this.primeFuse();
                this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
            }

        }
    }

    protected void explode(double d0) {
        if (!this.level.isClientSide) {
            double d1 = Math.sqrt(d0);

            if (d1 > 5.0D) {
                d1 = 5.0D;
            }

            this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float) (4.0D + this.random.nextDouble() * 1.5D * d1), Explosion.Effect.BREAK);
            this.discard();
        }

    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        if (f >= 3.0F) {
            float f2 = f / 10.0F;

            this.explode((double) (f2 * f2));
        }

        return super.causeFallDamage(f, f1, damagesource);
    }

    @Override
    public void activateMinecart(int i, int j, int k, boolean flag) {
        if (flag && this.fuse < 0) {
            this.primeFuse();
        }

    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 10) {
            this.primeFuse();
        } else {
            super.handleEntityEvent(b0);
        }

    }

    public void primeFuse() {
        this.fuse = 80;
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 10);
            if (!this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

    }

    public int getFuse() {
        return this.fuse;
    }

    public boolean isPrimed() {
        return this.fuse > -1;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return this.isPrimed() && (iblockdata.is((Tag) TagsBlock.RAILS) || iblockaccess.getBlockState(blockposition.above()).is((Tag) TagsBlock.RAILS)) ? 0.0F : super.getBlockExplosionResistance(explosion, iblockaccess, blockposition, iblockdata, fluid, f);
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return this.isPrimed() && (iblockdata.is((Tag) TagsBlock.RAILS) || iblockaccess.getBlockState(blockposition.above()).is((Tag) TagsBlock.RAILS)) ? false : super.shouldBlockExplode(explosion, iblockaccess, blockposition, iblockdata, f);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("TNTFuse", 99)) {
            this.fuse = nbttagcompound.getInt("TNTFuse");
        }

    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("TNTFuse", this.fuse);
    }
}
