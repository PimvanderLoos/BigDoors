package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

public class EntityPig extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityPig.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityPig.class, DataWatcherRegistry.b);
    private static final Set<Item> by = Sets.newHashSet(new Item[] { Items.CARROT, Items.POTATO, Items.BEETROOT});
    private boolean bA;
    private int bB;
    private int bC;

    public EntityPig(World world) {
        super(world);
        this.setSize(0.9F, 0.9F);
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, Items.CARROT_ON_A_STICK, false));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, false, EntityPig.by));
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
    public Entity bw() {
        return this.bx().isEmpty() ? null : (Entity) this.bx().get(0);
    }

    public boolean cR() {
        Entity entity = this.bw();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.CARROT_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPig.bx.equals(datawatcherobject) && this.world.isClientSide) {
            this.bA = true;
            this.bB = 0;
            this.bC = ((Integer) this.datawatcher.get(EntityPig.bx)).intValue();
        }

        super.a(datawatcherobject);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityPig.bw, Boolean.valueOf(false));
        this.datawatcher.register(EntityPig.bx, Integer.valueOf(0));
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

    protected SoundEffect G() {
        return SoundEffects.ep;
    }

    protected SoundEffect bW() {
        return SoundEffects.er;
    }

    protected SoundEffect bX() {
        return SoundEffects.eq;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.et, 0.15F, 1.0F);
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
        return ((Boolean) this.datawatcher.get(EntityPig.bw)).booleanValue();
    }

    public void setSaddle(boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityPig.bw, Boolean.valueOf(true));
        } else {
            this.datawatcher.set(EntityPig.bw, Boolean.valueOf(false));
        }

    }

    public void onLightningStrike(EntityLightning entitylightning) {
        if (!this.world.isClientSide && !this.dead) {
            EntityPigZombie entitypigzombie = new EntityPigZombie(this.world);

            entitypigzombie.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            entitypigzombie.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            entitypigzombie.setAI(this.hasAI());
            if (this.hasCustomName()) {
                entitypigzombie.setCustomName(this.getCustomName());
                entitypigzombie.setCustomNameVisible(this.getCustomNameVisible());
            }

            this.world.addEntity(entitypigzombie);
            this.die();
        }
    }

    public void e(float f, float f1) {
        super.e(f, f1);
        if (f > 5.0F) {
            Iterator iterator = this.b(EntityHuman.class).iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                entityhuman.b((Statistic) AchievementList.u);
            }
        }

    }

    public void g(float f, float f1) {
        Entity entity = this.bx().isEmpty() ? null : (Entity) this.bx().get(0);

        if (this.isVehicle() && this.cR()) {
            this.yaw = entity.yaw;
            this.lastYaw = this.yaw;
            this.pitch = entity.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aN = this.yaw;
            this.aP = this.yaw;
            this.P = 1.0F;
            this.aR = this.cq() * 0.1F;
            if (this.bA && this.bB++ > this.bC) {
                this.bA = false;
            }

            if (this.bA()) {
                float f2 = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * 0.225F;

                if (this.bA) {
                    f2 += f2 * 1.15F * MathHelper.sin((float) this.bB / (float) this.bC * 3.1415927F);
                }

                this.l(f2);
                super.g(0.0F, 1.0F);
            } else {
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            this.aF = this.aG;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f3 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f3 > 1.0F) {
                f3 = 1.0F;
            }

            this.aG += (f3 - this.aG) * 0.4F;
            this.aH += this.aG;
        } else {
            this.P = 0.5F;
            this.aR = 0.02F;
            super.g(f, f1);
        }
    }

    public boolean di() {
        if (this.bA) {
            return false;
        } else {
            this.bA = true;
            this.bB = 0;
            this.bC = this.getRandom().nextInt(841) + 140;
            this.getDataWatcher().set(EntityPig.bx, Integer.valueOf(this.bC));
            return true;
        }
    }

    public EntityPig b(EntityAgeable entityageable) {
        return new EntityPig(this.world);
    }

    public boolean e(ItemStack itemstack) {
        return EntityPig.by.contains(itemstack.getItem());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
