package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
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

    private static final DataWatcherObject<Byte> ID_LOYALTY = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Boolean> ID_FOIL = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.BOOLEAN);
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
        this.tridentItem = itemstack.cloneItemStack();
        this.entityData.set(EntityThrownTrident.ID_LOYALTY, (byte) EnchantmentManager.f(itemstack));
        this.entityData.set(EntityThrownTrident.ID_FOIL, itemstack.y());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityThrownTrident.ID_LOYALTY, (byte) 0);
        this.entityData.register(EntityThrownTrident.ID_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getShooter();
        byte b0 = (Byte) this.entityData.get(EntityThrownTrident.ID_LOYALTY);

        if (b0 > 0 && (this.dealtDamage || this.t()) && entity != null) {
            if (!this.B()) {
                if (!this.level.isClientSide && this.pickup == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            } else {
                this.p(true);
                Vec3D vec3d = entity.bb().d(this.getPositionVector());

                this.setPositionRaw(this.locX(), this.locY() + vec3d.y * 0.015D * (double) b0, this.locZ());
                if (this.level.isClientSide) {
                    this.yOld = this.locY();
                }

                double d0 = 0.05D * (double) b0;

                this.setMot(this.getMot().a(0.95D).e(vec3d.d().a(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEffects.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.clientSideReturnTridentTickCount;
            }
        }

        super.tick();
    }

    private boolean B() {
        Entity entity = this.getShooter();

        return entity != null && entity.isAlive() ? !(entity instanceof EntityPlayer) || !entity.isSpectator() : false;
    }

    @Override
    protected ItemStack getItemStack() {
        return this.tridentItem.cloneItemStack();
    }

    public boolean v() {
        return (Boolean) this.entityData.get(EntityThrownTrident.ID_FOIL);
    }

    @Nullable
    @Override
    protected MovingObjectPositionEntity a(Vec3D vec3d, Vec3D vec3d1) {
        return this.dealtDamage ? null : super.a(vec3d, vec3d1);
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        Entity entity = movingobjectpositionentity.getEntity();
        float f = 8.0F;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            f += EnchantmentManager.a(this.tridentItem, entityliving.getMonsterType());
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource = DamageSource.a((Entity) this, (Entity) (entity1 == null ? this : entity1));

        this.dealtDamage = true;
        SoundEffect soundeffect = SoundEffects.TRIDENT_HIT;

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

        if (this.level instanceof WorldServer && this.level.Y() && this.A()) {
            BlockPosition blockposition = entity.getChunkCoordinates();

            if (this.level.g(blockposition)) {
                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.a(this.level);

                entitylightning.d(Vec3D.c((BaseBlockPosition) blockposition));
                entitylightning.b(entity1 instanceof EntityPlayer ? (EntityPlayer) entity1 : null);
                this.level.addEntity(entitylightning);
                soundeffect = SoundEffects.TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundeffect, f1, 1.0F);
    }

    public boolean A() {
        return EnchantmentManager.h(this.tridentItem);
    }

    @Override
    protected boolean a(EntityHuman entityhuman) {
        return super.a(entityhuman) || this.t() && this.d((Entity) entityhuman) && entityhuman.getInventory().pickup(this.getItemStack());
    }

    @Override
    protected SoundEffect i() {
        return SoundEffects.TRIDENT_HIT_GROUND;
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (this.d((Entity) entityhuman) || this.getShooter() == null) {
            super.pickup(entityhuman);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Trident", 10)) {
            this.tridentItem = ItemStack.a(nbttagcompound.getCompound("Trident"));
        }

        this.dealtDamage = nbttagcompound.getBoolean("DealtDamage");
        this.entityData.set(EntityThrownTrident.ID_LOYALTY, (byte) EnchantmentManager.f(this.tridentItem));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.set("Trident", this.tridentItem.save(new NBTTagCompound()));
        nbttagcompound.setBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void h() {
        byte b0 = (Byte) this.entityData.get(EntityThrownTrident.ID_LOYALTY);

        if (this.pickup != EntityArrow.PickupStatus.ALLOWED || b0 <= 0) {
            super.h();
        }

    }

    @Override
    protected float s() {
        return 0.99F;
    }

    @Override
    public boolean j(double d0, double d1, double d2) {
        return true;
    }
}
