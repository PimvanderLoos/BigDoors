package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityThrownTrident extends EntityArrow {

    private static final DataWatcherObject<Byte> ID_LOYALTY = DataWatcher.defineId(EntityThrownTrident.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Boolean> ID_FOIL = DataWatcher.defineId(EntityThrownTrident.class, DataWatcherRegistry.BOOLEAN);
    public ItemStack tridentItem;
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    public EntityThrownTrident(EntityTypes<? extends EntityThrownTrident> entitytypes, World world) {
        super(entitytypes, world);
        this.tridentItem = new ItemStack(Items.TRIDENT);
    }

    public EntityThrownTrident(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(EntityTypes.TRIDENT, entityliving, world);
        this.tridentItem = new ItemStack(Items.TRIDENT);
        this.tridentItem = itemstack.copy();
        this.entityData.set(EntityThrownTrident.ID_LOYALTY, (byte) EnchantmentManager.getLoyalty(itemstack));
        this.entityData.set(EntityThrownTrident.ID_FOIL, itemstack.hasFoil());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityThrownTrident.ID_LOYALTY, (byte) 0);
        this.entityData.define(EntityThrownTrident.ID_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        byte b0 = (Byte) this.entityData.get(EntityThrownTrident.ID_LOYALTY);

        if (b0 > 0 && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level.isClientSide && this.pickup == EntityArrow.PickupStatus.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3D vec3d = entity.getEyePosition().subtract(this.position());

                this.setPosRaw(this.getX(), this.getY() + vec3d.y * 0.015D * (double) b0, this.getZ());
                if (this.level.isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * (double) b0;

                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3d.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEffects.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.clientSideReturnTridentTickCount;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();

        return entity != null && entity.isAlive() ? !(entity instanceof EntityPlayer) || !entity.isSpectator() : false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    public boolean isFoil() {
        return (Boolean) this.entityData.get(EntityThrownTrident.ID_FOIL);
    }

    @Nullable
    @Override
    protected MovingObjectPositionEntity findHitEntity(Vec3D vec3d, Vec3D vec3d1) {
        return this.dealtDamage ? null : super.findHitEntity(vec3d, vec3d1);
    }

    @Override
    protected void onHitEntity(MovingObjectPositionEntity movingobjectpositionentity) {
        Entity entity = movingobjectpositionentity.getEntity();
        float f = 8.0F;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            f += EnchantmentManager.getDamageBonus(this.tridentItem, entityliving.getMobType());
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource = DamageSource.trident(this, (Entity) (entity1 == null ? this : entity1));

        this.dealtDamage = true;
        SoundEffect soundeffect = SoundEffects.TRIDENT_HIT;

        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityTypes.ENDERMAN) {
                return;
            }

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving1 = (EntityLiving) entity;

                if (entity1 instanceof EntityLiving) {
                    EnchantmentManager.doPostHurtEffects(entityliving1, entity1);
                    EnchantmentManager.doPostDamageEffects((EntityLiving) entity1, entityliving1);
                }

                this.doPostHurtEffects(entityliving1);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;

        if (this.level instanceof WorldServer && this.level.isThundering() && this.isChanneling()) {
            BlockPosition blockposition = entity.blockPosition();

            if (this.level.canSeeSky(blockposition)) {
                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.create(this.level);

                entitylightning.moveTo(Vec3D.atBottomCenterOf(blockposition));
                entitylightning.setCause(entity1 instanceof EntityPlayer ? (EntityPlayer) entity1 : null);
                this.level.addFreshEntity(entitylightning);
                soundeffect = SoundEffects.TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundeffect, f1, 1.0F);
    }

    public boolean isChanneling() {
        return EnchantmentManager.hasChanneling(this.tridentItem);
    }

    @Override
    protected boolean tryPickup(EntityHuman entityhuman) {
        return super.tryPickup(entityhuman) || this.isNoPhysics() && this.ownedBy(entityhuman) && entityhuman.getInventory().add(this.getPickupItem());
    }

    @Override
    protected SoundEffect getDefaultHitGroundSoundEvent() {
        return SoundEffects.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(EntityHuman entityhuman) {
        if (this.ownedBy(entityhuman) || this.getOwner() == null) {
            super.playerTouch(entityhuman);
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of(nbttagcompound.getCompound("Trident"));
        }

        this.dealtDamage = nbttagcompound.getBoolean("DealtDamage");
        this.entityData.set(EntityThrownTrident.ID_LOYALTY, (byte) EnchantmentManager.getLoyalty(this.tridentItem));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.put("Trident", this.tridentItem.save(new NBTTagCompound()));
        nbttagcompound.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tickDespawn() {
        byte b0 = (Byte) this.entityData.get(EntityThrownTrident.ID_LOYALTY);

        if (this.pickup != EntityArrow.PickupStatus.ALLOWED || b0 <= 0) {
            super.tickDespawn();
        }

    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double d0, double d1, double d2) {
        return true;
    }
}
