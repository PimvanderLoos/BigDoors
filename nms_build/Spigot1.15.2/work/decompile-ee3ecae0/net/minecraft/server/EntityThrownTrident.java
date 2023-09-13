package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityThrownTrident extends EntityArrow {

    private static final DataWatcherObject<Byte> ap = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Boolean> aq = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.i);
    public ItemStack trident;
    private boolean as;
    public int ao;

    public EntityThrownTrident(EntityTypes<? extends EntityThrownTrident> entitytypes, World world) {
        super(entitytypes, world);
        this.trident = new ItemStack(Items.TRIDENT);
    }

    public EntityThrownTrident(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(EntityTypes.TRIDENT, entityliving, world);
        this.trident = new ItemStack(Items.TRIDENT);
        this.trident = itemstack.cloneItemStack();
        this.datawatcher.set(EntityThrownTrident.ap, (byte) EnchantmentManager.f(itemstack));
        this.datawatcher.set(EntityThrownTrident.aq, itemstack.u());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityThrownTrident.ap, (byte) 0);
        this.datawatcher.register(EntityThrownTrident.aq, false);
    }

    @Override
    public void tick() {
        if (this.d > 4) {
            this.as = true;
        }

        Entity entity = this.getShooter();

        if ((this.as || this.v()) && entity != null) {
            byte b0 = (Byte) this.datawatcher.get(EntityThrownTrident.ap);

            if (b0 > 0 && !this.z()) {
                if (!this.world.isClientSide && this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            } else if (b0 > 0) {
                this.n(true);
                Vec3D vec3d = new Vec3D(entity.locX() - this.locX(), entity.getHeadY() - this.locY(), entity.locZ() - this.locZ());

                this.setPositionRaw(this.locX(), this.locY() + vec3d.y * 0.015D * (double) b0, this.locZ());
                if (this.world.isClientSide) {
                    this.F = this.locY();
                }

                double d0 = 0.05D * (double) b0;

                this.setMot(this.getMot().a(0.95D).e(vec3d.d().a(d0)));
                if (this.ao == 0) {
                    this.a(SoundEffects.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.ao;
            }
        }

        super.tick();
    }

    private boolean z() {
        Entity entity = this.getShooter();

        return entity != null && entity.isAlive() ? !(entity instanceof EntityPlayer) || !entity.isSpectator() : false;
    }

    @Override
    protected ItemStack getItemStack() {
        return this.trident.cloneItemStack();
    }

    @Nullable
    @Override
    protected MovingObjectPositionEntity a(Vec3D vec3d, Vec3D vec3d1) {
        return this.as ? null : super.a(vec3d, vec3d1);
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        Entity entity = movingobjectpositionentity.getEntity();
        float f = 8.0F;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            f += EnchantmentManager.a(this.trident, entityliving.getMonsterType());
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource = DamageSource.a(this, (Entity) (entity1 == null ? this : entity1));

        this.as = true;
        SoundEffect soundeffect = SoundEffects.ITEM_TRIDENT_HIT;

        if (entity.damageEntity(damagesource, f)) {
            if (entity.getEntityType() == EntityTypes.ENDERMAN) {
                return;
            }

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving1 = (EntityLiving) entity;

                if (entity1 instanceof EntityLiving) {
                    EnchantmentManager.a(entityliving1, entity1);
                    EnchantmentManager.b((EntityLiving) entity1, (Entity) entityliving1);
                }

                this.a(entityliving1);
            }
        }

        this.setMot(this.getMot().d(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;

        if (this.world instanceof WorldServer && this.world.U() && EnchantmentManager.h(this.trident)) {
            BlockPosition blockposition = entity.getChunkCoordinates();

            if (this.world.f(blockposition)) {
                EntityLightning entitylightning = new EntityLightning(this.world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, false);

                entitylightning.d(entity1 instanceof EntityPlayer ? (EntityPlayer) entity1 : null);
                ((WorldServer) this.world).strikeLightning(entitylightning);
                soundeffect = SoundEffects.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.a(soundeffect, f1, 1.0F);
    }

    @Override
    protected SoundEffect k() {
        return SoundEffects.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        Entity entity = this.getShooter();

        if (entity == null || entity.getUniqueID() == entityhuman.getUniqueID()) {
            super.pickup(entityhuman);
        }
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Trident", 10)) {
            this.trident = ItemStack.a(nbttagcompound.getCompound("Trident"));
        }

        this.as = nbttagcompound.getBoolean("DealtDamage");
        this.datawatcher.set(EntityThrownTrident.ap, (byte) EnchantmentManager.f(this.trident));
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("Trident", this.trident.save(new NBTTagCompound()));
        nbttagcompound.setBoolean("DealtDamage", this.as);
    }

    @Override
    public void i() {
        byte b0 = (Byte) this.datawatcher.get(EntityThrownTrident.ap);

        if (this.fromPlayer != EntityArrow.PickupStatus.ALLOWED || b0 <= 0) {
            super.i();
        }

    }

    @Override
    protected float u() {
        return 0.99F;
    }
}
