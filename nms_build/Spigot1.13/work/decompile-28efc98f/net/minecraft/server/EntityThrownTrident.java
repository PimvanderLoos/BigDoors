package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityThrownTrident extends EntityArrow {

    private static final DataWatcherObject<Byte> g = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.a);
    private ItemStack h;
    private boolean aw;
    public int f;

    public EntityThrownTrident(World world) {
        super(EntityTypes.TRIDENT, world);
        this.h = new ItemStack(Items.TRIDENT);
    }

    public EntityThrownTrident(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(EntityTypes.TRIDENT, entityliving, world);
        this.h = new ItemStack(Items.TRIDENT);
        this.h = itemstack.cloneItemStack();
        this.datawatcher.set(EntityThrownTrident.g, Byte.valueOf((byte) EnchantmentManager.f(itemstack)));
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityThrownTrident.g, Byte.valueOf((byte) 0));
    }

    public void tick() {
        if (this.b > 4) {
            this.aw = true;
        }

        if ((this.aw || this.p()) && this.shooter != null) {
            byte b0 = ((Byte) this.datawatcher.get(EntityThrownTrident.g)).byteValue();

            if (b0 > 0 && !this.q()) {
                if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            } else if (b0 > 0) {
                this.o(true);
                Vec3D vec3d = new Vec3D(this.shooter.locX - this.locX, this.shooter.locY + (double) this.shooter.getHeadHeight() - this.locY, this.shooter.locZ - this.locZ);

                this.locY += vec3d.y * 0.015D * (double) b0;
                if (this.world.isClientSide) {
                    this.O = this.locY;
                }

                vec3d = vec3d.a();
                double d0 = 0.05D * (double) b0;

                this.motX += vec3d.x * d0 - this.motX * 0.05D;
                this.motY += vec3d.y * d0 - this.motY * 0.05D;
                this.motZ += vec3d.z * d0 - this.motZ * 0.05D;
                if (this.f == 0) {
                    this.a(SoundEffects.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.f;
            }
        }

        super.tick();
    }

    private boolean q() {
        return this.shooter != null && this.shooter.isAlive() ? !(this.shooter instanceof EntityPlayer) || !((EntityPlayer) this.shooter).isSpectator() : false;
    }

    protected ItemStack getItemStack() {
        return this.h.cloneItemStack();
    }

    @Nullable
    protected Entity a(Vec3D vec3d, Vec3D vec3d1) {
        return this.aw ? null : super.a(vec3d, vec3d1);
    }

    protected void b(MovingObjectPosition movingobjectposition) {
        Entity entity = movingobjectposition.entity;
        float f = 8.0F;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            f += EnchantmentManager.a(this.h, entityliving.getMonsterType());
        }

        DamageSource damagesource = DamageSource.a(this, (Entity) (this.shooter == null ? this : this.shooter));

        this.aw = true;
        SoundEffect soundeffect = SoundEffects.ITEM_TRIDENT_HIT;

        if (entity.damageEntity(damagesource, f) && entity instanceof EntityLiving) {
            EntityLiving entityliving1 = (EntityLiving) entity;

            if (this.shooter instanceof EntityLiving) {
                EnchantmentManager.a(entityliving1, this.shooter);
                EnchantmentManager.b((EntityLiving) this.shooter, (Entity) entityliving1);
            }

            this.a(entityliving1);
        }

        this.motX *= -0.009999999776482582D;
        this.motY *= -0.10000000149011612D;
        this.motZ *= -0.009999999776482582D;
        float f1 = 1.0F;

        if (this.world.X() && EnchantmentManager.h(this.h)) {
            BlockPosition blockposition = entity.getChunkCoordinates();

            if (this.world.e(blockposition)) {
                EntityLightning entitylightning = new EntityLightning(this.world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), false);

                entitylightning.d(this.shooter instanceof EntityPlayer ? (EntityPlayer) this.shooter : null);
                this.world.strikeLightning(entitylightning);
                soundeffect = SoundEffects.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.a(soundeffect, f1, 1.0F);
    }

    protected SoundEffect i() {
        return SoundEffects.ITEM_TRIDENT_HIT_GROUND;
    }

    public void d(EntityHuman entityhuman) {
        if (this.shooter == null || this.shooter == entityhuman) {
            super.d(entityhuman);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Trident", 10)) {
            this.h = ItemStack.a(nbttagcompound.getCompound("Trident"));
        }

        this.aw = nbttagcompound.getBoolean("DealtDamage");
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("Trident", this.h.save(new NBTTagCompound()));
        nbttagcompound.setBoolean("DealtDamage", this.aw);
    }

    protected void f() {
        if (this.fromPlayer != EntityArrow.PickupStatus.ALLOWED) {
            super.f();
        }

    }

    protected float o() {
        return 0.99F;
    }
}
