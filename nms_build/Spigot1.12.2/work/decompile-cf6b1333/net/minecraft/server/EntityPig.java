package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;

public class EntityPig extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bx = DataWatcher.a(EntityPig.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> by = DataWatcher.a(EntityPig.class, DataWatcherRegistry.b);
    private static final Set<Item> bz = Sets.newHashSet(new Item[] { Items.CARROT, Items.POTATO, Items.BEETROOT});
    private boolean bB;
    private int bC;
    private int bD;

    public EntityPig(World world) {
        super(world);
        this.setSize(0.9F, 0.9F);
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, Items.CARROT_ON_A_STICK, false));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, false, EntityPig.bz));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Nullable
    public Entity bE() {
        return this.bF().isEmpty() ? null : (Entity) this.bF().get(0);
    }

    public boolean cV() {
        Entity entity = this.bE();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.CARROT_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPig.by.equals(datawatcherobject) && this.world.isClientSide) {
            this.bB = true;
            this.bC = 0;
            this.bD = ((Integer) this.datawatcher.get(EntityPig.by)).intValue();
        }

        super.a(datawatcherobject);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityPig.bx, Boolean.valueOf(false));
        this.datawatcher.register(EntityPig.by, Integer.valueOf(0));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityPig.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", this.hasSaddle());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSaddle(nbttagcompound.getBoolean("Saddle"));
    }

    protected SoundEffect F() {
        return SoundEffects.fo;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.fq;
    }

    protected SoundEffect cf() {
        return SoundEffects.fp;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.fs, 0.15F, 1.0F);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        if (!super.a(entityhuman, enumhand)) {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (itemstack.getItem() == Items.NAME_TAG) {
                itemstack.a(entityhuman, (EntityLiving) this, enumhand);
                return true;
            } else if (this.hasSaddle() && !this.isVehicle()) {
                if (!this.world.isClientSide) {
                    entityhuman.startRiding(this);
                }

                return true;
            } else if (itemstack.getItem() == Items.SADDLE) {
                itemstack.a(entityhuman, (EntityLiving) this, enumhand);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (!this.world.isClientSide) {
            if (this.hasSaddle()) {
                this.a(Items.SADDLE, 1);
            }

        }
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.E;
    }

    public boolean hasSaddle() {
        return ((Boolean) this.datawatcher.get(EntityPig.bx)).booleanValue();
    }

    public void setSaddle(boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityPig.bx, Boolean.valueOf(true));
        } else {
            this.datawatcher.set(EntityPig.bx, Boolean.valueOf(false));
        }

    }

    public void onLightningStrike(EntityLightning entitylightning) {
        if (!this.world.isClientSide && !this.dead) {
            EntityPigZombie entitypigzombie = new EntityPigZombie(this.world);

            entitypigzombie.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            entitypigzombie.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            entitypigzombie.setNoAI(this.isNoAI());
            if (this.hasCustomName()) {
                entitypigzombie.setCustomName(this.getCustomName());
                entitypigzombie.setCustomNameVisible(this.getCustomNameVisible());
            }

            this.world.addEntity(entitypigzombie);
            this.die();
        }
    }

    public void a(float f, float f1, float f2) {
        Entity entity = this.bF().isEmpty() ? null : (Entity) this.bF().get(0);

        if (this.isVehicle() && this.cV()) {
            this.yaw = entity.yaw;
            this.lastYaw = this.yaw;
            this.pitch = entity.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aN = this.yaw;
            this.aP = this.yaw;
            this.P = 1.0F;
            this.aR = this.cy() * 0.1F;
            if (this.bB && this.bC++ > this.bD) {
                this.bB = false;
            }

            if (this.bI()) {
                float f3 = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * 0.225F;

                if (this.bB) {
                    f3 += f3 * 1.15F * MathHelper.sin((float) this.bC / (float) this.bD * 3.1415927F);
                }

                this.k(f3);
                super.a(0.0F, 0.0F, 1.0F);
            } else {
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            this.aF = this.aG;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aG += (f4 - this.aG) * 0.4F;
            this.aH += this.aG;
        } else {
            this.P = 0.5F;
            this.aR = 0.02F;
            super.a(f, f1, f2);
        }
    }

    public boolean dm() {
        if (this.bB) {
            return false;
        } else {
            this.bB = true;
            this.bC = 0;
            this.bD = this.getRandom().nextInt(841) + 140;
            this.getDataWatcher().set(EntityPig.by, Integer.valueOf(this.bD));
            return true;
        }
    }

    public EntityPig b(EntityAgeable entityageable) {
        return new EntityPig(this.world);
    }

    public boolean e(ItemStack itemstack) {
        return EntityPig.bz.contains(itemstack.getItem());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
