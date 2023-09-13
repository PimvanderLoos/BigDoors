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
    public IBlockData r() {
        return Blocks.TNT.getBlockData();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fuse > 0) {
            --this.fuse;
            this.level.addParticle(Particles.SMOKE, this.locX(), this.locY() + 0.5D, this.locZ(), 0.0D, 0.0D, 0.0D);
        } else if (this.fuse == 0) {
            this.h(this.getMot().i());
        }

        if (this.horizontalCollision) {
            double d0 = this.getMot().i();

            if (d0 >= 0.009999999776482582D) {
                this.h(d0);
            }
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.k();

        if (entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.isBurning()) {
                this.h(entityarrow.getMot().g());
            }
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    public void a(DamageSource damagesource) {
        double d0 = this.getMot().i();

        if (!damagesource.isFire() && !damagesource.isExplosion() && d0 < 0.009999999776482582D) {
            super.a(damagesource);
            if (!damagesource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.a((IMaterial) Blocks.TNT);
            }

        } else {
            if (this.fuse < 0) {
                this.w();
                this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
            }

        }
    }

    protected void h(double d0) {
        if (!this.level.isClientSide) {
            double d1 = Math.sqrt(d0);

            if (d1 > 5.0D) {
                d1 = 5.0D;
            }

            this.level.explode(this, this.locX(), this.locY(), this.locZ(), (float) (4.0D + this.random.nextDouble() * 1.5D * d1), Explosion.Effect.BREAK);
            this.die();
        }

    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        if (f >= 3.0F) {
            float f2 = f / 10.0F;

            this.h((double) (f2 * f2));
        }

        return super.a(f, f1, damagesource);
    }

    @Override
    public void a(int i, int j, int k, boolean flag) {
        if (flag && this.fuse < 0) {
            this.w();
        }

    }

    @Override
    public void a(byte b0) {
        if (b0 == 10) {
            this.w();
        } else {
            super.a(b0);
        }

    }

    public void w() {
        this.fuse = 80;
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEffect(this, (byte) 10);
            if (!this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

    }

    public int x() {
        return this.fuse;
    }

    public boolean z() {
        return this.fuse > -1;
    }

    @Override
    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return this.z() && (iblockdata.a((Tag) TagsBlock.RAILS) || iblockaccess.getType(blockposition.up()).a((Tag) TagsBlock.RAILS)) ? 0.0F : super.a(explosion, iblockaccess, blockposition, iblockdata, fluid, f);
    }

    @Override
    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return this.z() && (iblockdata.a((Tag) TagsBlock.RAILS) || iblockaccess.getType(blockposition.up()).a((Tag) TagsBlock.RAILS)) ? false : super.a(explosion, iblockaccess, blockposition, iblockdata, f);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("TNTFuse", 99)) {
            this.fuse = nbttagcompound.getInt("TNTFuse");
        }

    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("TNTFuse", this.fuse);
    }
}
