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

    private static final DataWatcherObject<Byte> g = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Boolean> ag = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.i);
    public ItemStack trident;
    private boolean ai;
    public int f;

    public EntityThrownTrident(EntityTypes<? extends EntityThrownTrident> entitytypes, World world) {
        super(entitytypes, world);
        this.trident = new ItemStack(Items.TRIDENT);
    }

    public EntityThrownTrident(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(EntityTypes.TRIDENT, entityliving, world);
        this.trident = new ItemStack(Items.TRIDENT);
        this.trident = itemstack.cloneItemStack();
        this.datawatcher.set(EntityThrownTrident.g, (byte) EnchantmentManager.f(itemstack));
        this.datawatcher.set(EntityThrownTrident.ag, itemstack.u());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityThrownTrident.g, (byte) 0);
        this.datawatcher.register(EntityThrownTrident.ag, false);
    }

    @Override
    public void tick() {
        if (this.c > 4) {
            this.ai = true;
        }

        Entity entity = this.getShooter();

        if ((this.ai || this.t()) && entity != null) {
            byte b0 = (Byte) this.datawatcher.get(EntityThrownTrident.g);

            if (b0 > 0 && !this.z()) {
                if (!this.world.isClientSide && this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            } else if (b0 > 0) {
                this.o(true);
                Vec3D vec3d = new Vec3D(entity.locX() - this.locX(), entity.getHeadY() - this.locY(), entity.locZ() - this.locZ());

                this.setPositionRaw(this.locX(), this.locY() + vec3d.y * 0.015D * (double) b0, this.locZ());
                if (this.world.isClientSide) {
                    this.E = this.locY();
                }

                double d0 = 0.05D * (double) b0;

                this.setMot(this.getMot().a(0.95D).e(vec3d.d().a(d0)));
                if (this.f == 0) {
                    this.playSound(SoundEffects.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.f;
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
        return this.ai ? null : super.a(vec3d, vec3d1);
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
        DamageSource damagesource = DamageSource.a((Entity) this, (Entity) (entity1 == null ? this : entity1));

        this.ai = true;
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

        if (this.world instanceof WorldServer && this.world.W() && EnchantmentManager.h(this.trident)) {
            BlockPosition blockposition = entity.getChunkCoordinates();

            if (this.world.e(blockposition)) {
                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.a(this.world);

                entitylightning.d(Vec3D.c((BaseBlockPosition) blockposition));
                entitylightning.d(entity1 instanceof EntityPlayer ? (EntityPlayer) entity1 : null);
                this.world.addEntity(entitylightning);
                soundeffect = SoundEffects.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundeffect, f1, 1.0F);
    }

    @Override
    protected SoundEffect i() {
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
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Trident", 10)) {
            this.trident = ItemStack.a(nbttagcompound.getCompound("Trident"));
        }

        this.ai = nbttagcompound.getBoolean("DealtDamage");
        this.datawatcher.set(EntityThrownTrident.g, (byte) EnchantmentManager.f(this.trident));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.set("Trident", this.trident.save(new NBTTagCompound()));
        nbttagcompound.setBoolean("DealtDamage", this.ai);
    }

    @Override
    public void h() {
        byte b0 = (Byte) this.datawatcher.get(EntityThrownTrident.g);

        if (this.fromPlayer != EntityArrow.PickupStatus.ALLOWED || b0 <= 0) {
            super.h();
        }

    }

    @Override
    protected float s() {
        return 0.99F;
    }
}
