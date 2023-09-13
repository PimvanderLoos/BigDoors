package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vector3f;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityArmorStand extends EntityLiving {

    public static final int WOBBLE_TIME = 5;
    private static final boolean ENABLE_ARMS = true;
    private static final Vector3f DEFAULT_HEAD_POSE = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3f DEFAULT_BODY_POSE = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final Vector3f DEFAULT_LEFT_ARM_POSE = new Vector3f(-10.0F, 0.0F, -10.0F);
    private static final Vector3f DEFAULT_RIGHT_ARM_POSE = new Vector3f(-15.0F, 0.0F, 10.0F);
    private static final Vector3f DEFAULT_LEFT_LEG_POSE = new Vector3f(-1.0F, 0.0F, -1.0F);
    private static final Vector3f DEFAULT_RIGHT_LEG_POSE = new Vector3f(1.0F, 0.0F, 1.0F);
    private static final EntitySize MARKER_DIMENSIONS = new EntitySize(0.0F, 0.0F, true);
    private static final EntitySize BABY_DIMENSIONS = EntityTypes.ARMOR_STAND.m().a(0.5F);
    private static final double FEET_OFFSET = 0.1D;
    private static final double CHEST_OFFSET = 0.9D;
    private static final double LEGS_OFFSET = 0.4D;
    private static final double HEAD_OFFSET = 1.6D;
    public static final int DISABLE_TAKING_OFFSET = 8;
    public static final int DISABLE_PUTTING_OFFSET = 16;
    public static final int CLIENT_FLAG_SMALL = 1;
    public static final int CLIENT_FLAG_SHOW_ARMS = 4;
    public static final int CLIENT_FLAG_NO_BASEPLATE = 8;
    public static final int CLIENT_FLAG_MARKER = 16;
    public static final DataWatcherObject<Byte> DATA_CLIENT_FLAGS = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.BYTE);
    public static final DataWatcherObject<Vector3f> DATA_HEAD_POSE = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_BODY_POSE = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_LEFT_ARM_POSE = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_RIGHT_ARM_POSE = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_LEFT_LEG_POSE = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_RIGHT_LEG_POSE = DataWatcher.a(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    private static final Predicate<Entity> RIDABLE_MINECARTS = (entity) -> {
        return entity instanceof EntityMinecartAbstract && ((EntityMinecartAbstract) entity).getMinecartType() == EntityMinecartAbstract.EnumMinecartType.RIDEABLE;
    };
    private final NonNullList<ItemStack> handItems;
    private final NonNullList<ItemStack> armorItems;
    private boolean invisible;
    public long lastHit;
    public int disabledSlots;
    public Vector3f headPose;
    public Vector3f bodyPose;
    public Vector3f leftArmPose;
    public Vector3f rightArmPose;
    public Vector3f leftLegPose;
    public Vector3f rightLegPose;

    public EntityArmorStand(EntityTypes<? extends EntityArmorStand> entitytypes, World world) {
        super(entitytypes, world);
        this.handItems = NonNullList.a(2, ItemStack.EMPTY);
        this.armorItems = NonNullList.a(4, ItemStack.EMPTY);
        this.headPose = EntityArmorStand.DEFAULT_HEAD_POSE;
        this.bodyPose = EntityArmorStand.DEFAULT_BODY_POSE;
        this.leftArmPose = EntityArmorStand.DEFAULT_LEFT_ARM_POSE;
        this.rightArmPose = EntityArmorStand.DEFAULT_RIGHT_ARM_POSE;
        this.leftLegPose = EntityArmorStand.DEFAULT_LEFT_LEG_POSE;
        this.rightLegPose = EntityArmorStand.DEFAULT_RIGHT_LEG_POSE;
        this.maxUpStep = 0.0F;
    }

    public EntityArmorStand(World world, double d0, double d1, double d2) {
        this(EntityTypes.ARMOR_STAND, world);
        this.setPosition(d0, d1, d2);
    }

    @Override
    public void updateSize() {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();

        super.updateSize();
        this.setPosition(d0, d1, d2);
    }

    private boolean D() {
        return !this.isMarker() && !this.isNoGravity();
    }

    @Override
    public boolean doAITick() {
        return super.doAITick() && this.D();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityArmorStand.DATA_CLIENT_FLAGS, (byte) 0);
        this.entityData.register(EntityArmorStand.DATA_HEAD_POSE, EntityArmorStand.DEFAULT_HEAD_POSE);
        this.entityData.register(EntityArmorStand.DATA_BODY_POSE, EntityArmorStand.DEFAULT_BODY_POSE);
        this.entityData.register(EntityArmorStand.DATA_LEFT_ARM_POSE, EntityArmorStand.DEFAULT_LEFT_ARM_POSE);
        this.entityData.register(EntityArmorStand.DATA_RIGHT_ARM_POSE, EntityArmorStand.DEFAULT_RIGHT_ARM_POSE);
        this.entityData.register(EntityArmorStand.DATA_LEFT_LEG_POSE, EntityArmorStand.DEFAULT_LEFT_LEG_POSE);
        this.entityData.register(EntityArmorStand.DATA_RIGHT_LEG_POSE, EntityArmorStand.DEFAULT_RIGHT_LEG_POSE);
    }

    @Override
    public Iterable<ItemStack> bw() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        switch (enumitemslot.a()) {
            case HAND:
                return (ItemStack) this.handItems.get(enumitemslot.b());
            case ARMOR:
                return (ItemStack) this.armorItems.get(enumitemslot.b());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.f(itemstack);
        switch (enumitemslot.a()) {
            case HAND:
                this.playEquipSound(itemstack);
                this.handItems.set(enumitemslot.b(), itemstack);
                break;
            case ARMOR:
                this.playEquipSound(itemstack);
                this.armorItems.set(enumitemslot.b(), itemstack);
        }

    }

    @Override
    public boolean g(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return this.getEquipment(enumitemslot).isEmpty() && !this.d(enumitemslot);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.armorItems.iterator(); iterator.hasNext(); nbttaglist.add(nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.set("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.handItems.iterator(); iterator1.hasNext(); nbttaglist1.add(nbttagcompound2)) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();

            nbttagcompound2 = new NBTTagCompound();
            if (!itemstack1.isEmpty()) {
                itemstack1.save(nbttagcompound2);
            }
        }

        nbttagcompound.set("HandItems", nbttaglist1);
        nbttagcompound.setBoolean("Invisible", this.isInvisible());
        nbttagcompound.setBoolean("Small", this.isSmall());
        nbttagcompound.setBoolean("ShowArms", this.hasArms());
        nbttagcompound.setInt("DisabledSlots", this.disabledSlots);
        nbttagcompound.setBoolean("NoBasePlate", this.hasBasePlate());
        if (this.isMarker()) {
            nbttagcompound.setBoolean("Marker", this.isMarker());
        }

        nbttagcompound.set("Pose", this.F());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.hasKeyOfType("ArmorItems", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorItems", 10);

            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.handItems.size(); ++i) {
                this.handItems.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        this.setInvisible(nbttagcompound.getBoolean("Invisible"));
        this.setSmall(nbttagcompound.getBoolean("Small"));
        this.setArms(nbttagcompound.getBoolean("ShowArms"));
        this.disabledSlots = nbttagcompound.getInt("DisabledSlots");
        this.setBasePlate(nbttagcompound.getBoolean("NoBasePlate"));
        this.setMarker(nbttagcompound.getBoolean("Marker"));
        this.noPhysics = !this.D();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Pose");

        this.c(nbttagcompound1);
    }

    private void c(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.getList("Head", 5);

        this.setHeadPose(nbttaglist.isEmpty() ? EntityArmorStand.DEFAULT_HEAD_POSE : new Vector3f(nbttaglist));
        NBTTagList nbttaglist1 = nbttagcompound.getList("Body", 5);

        this.setBodyPose(nbttaglist1.isEmpty() ? EntityArmorStand.DEFAULT_BODY_POSE : new Vector3f(nbttaglist1));
        NBTTagList nbttaglist2 = nbttagcompound.getList("LeftArm", 5);

        this.setLeftArmPose(nbttaglist2.isEmpty() ? EntityArmorStand.DEFAULT_LEFT_ARM_POSE : new Vector3f(nbttaglist2));
        NBTTagList nbttaglist3 = nbttagcompound.getList("RightArm", 5);

        this.setRightArmPose(nbttaglist3.isEmpty() ? EntityArmorStand.DEFAULT_RIGHT_ARM_POSE : new Vector3f(nbttaglist3));
        NBTTagList nbttaglist4 = nbttagcompound.getList("LeftLeg", 5);

        this.setLeftLegPose(nbttaglist4.isEmpty() ? EntityArmorStand.DEFAULT_LEFT_LEG_POSE : new Vector3f(nbttaglist4));
        NBTTagList nbttaglist5 = nbttagcompound.getList("RightLeg", 5);

        this.setRightLegPose(nbttaglist5.isEmpty() ? EntityArmorStand.DEFAULT_RIGHT_LEG_POSE : new Vector3f(nbttaglist5));
    }

    private NBTTagCompound F() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (!EntityArmorStand.DEFAULT_HEAD_POSE.equals(this.headPose)) {
            nbttagcompound.set("Head", this.headPose.a());
        }

        if (!EntityArmorStand.DEFAULT_BODY_POSE.equals(this.bodyPose)) {
            nbttagcompound.set("Body", this.bodyPose.a());
        }

        if (!EntityArmorStand.DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
            nbttagcompound.set("LeftArm", this.leftArmPose.a());
        }

        if (!EntityArmorStand.DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
            nbttagcompound.set("RightArm", this.rightArmPose.a());
        }

        if (!EntityArmorStand.DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
            nbttagcompound.set("LeftLeg", this.leftLegPose.a());
        }

        if (!EntityArmorStand.DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
            nbttagcompound.set("RightLeg", this.rightLegPose.a());
        }

        return nbttagcompound;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void A(Entity entity) {}

    @Override
    protected void collideNearby() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), EntityArmorStand.RIDABLE_MINECARTS);

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (this.f(entity) <= 0.2D) {
                entity.collide(this);
            }
        }

    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isMarker() && !itemstack.a(Items.NAME_TAG)) {
            if (entityhuman.isSpectator()) {
                return EnumInteractionResult.SUCCESS;
            } else if (entityhuman.level.isClientSide) {
                return EnumInteractionResult.CONSUME;
            } else {
                EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

                if (itemstack.isEmpty()) {
                    EnumItemSlot enumitemslot1 = this.i(vec3d);
                    EnumItemSlot enumitemslot2 = this.d(enumitemslot1) ? enumitemslot : enumitemslot1;

                    if (this.a(enumitemslot2) && this.a(entityhuman, enumitemslot2, itemstack, enumhand)) {
                        return EnumInteractionResult.SUCCESS;
                    }
                } else {
                    if (this.d(enumitemslot)) {
                        return EnumInteractionResult.FAIL;
                    }

                    if (enumitemslot.a() == EnumItemSlot.Function.HAND && !this.hasArms()) {
                        return EnumInteractionResult.FAIL;
                    }

                    if (this.a(entityhuman, enumitemslot, itemstack, enumhand)) {
                        return EnumInteractionResult.SUCCESS;
                    }
                }

                return EnumInteractionResult.PASS;
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    private EnumItemSlot i(Vec3D vec3d) {
        EnumItemSlot enumitemslot = EnumItemSlot.MAINHAND;
        boolean flag = this.isSmall();
        double d0 = flag ? vec3d.y * 2.0D : vec3d.y;
        EnumItemSlot enumitemslot1 = EnumItemSlot.FEET;

        if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.a(enumitemslot1)) {
            enumitemslot = EnumItemSlot.FEET;
        } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.a(EnumItemSlot.CHEST)) {
            enumitemslot = EnumItemSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.a(EnumItemSlot.LEGS)) {
            enumitemslot = EnumItemSlot.LEGS;
        } else if (d0 >= 1.6D && this.a(EnumItemSlot.HEAD)) {
            enumitemslot = EnumItemSlot.HEAD;
        } else if (!this.a(EnumItemSlot.MAINHAND) && this.a(EnumItemSlot.OFFHAND)) {
            enumitemslot = EnumItemSlot.OFFHAND;
        }

        return enumitemslot;
    }

    private boolean d(EnumItemSlot enumitemslot) {
        return (this.disabledSlots & 1 << enumitemslot.getSlotFlag()) != 0 || enumitemslot.a() == EnumItemSlot.Function.HAND && !this.hasArms();
    }

    private boolean a(EntityHuman entityhuman, EnumItemSlot enumitemslot, ItemStack itemstack, EnumHand enumhand) {
        ItemStack itemstack1 = this.getEquipment(enumitemslot);

        if (!itemstack1.isEmpty() && (this.disabledSlots & 1 << enumitemslot.getSlotFlag() + 8) != 0) {
            return false;
        } else if (itemstack1.isEmpty() && (this.disabledSlots & 1 << enumitemslot.getSlotFlag() + 16) != 0) {
            return false;
        } else {
            ItemStack itemstack2;

            if (entityhuman.getAbilities().instabuild && itemstack1.isEmpty() && !itemstack.isEmpty()) {
                itemstack2 = itemstack.cloneItemStack();
                itemstack2.setCount(1);
                this.setSlot(enumitemslot, itemstack2);
                return true;
            } else if (!itemstack.isEmpty() && itemstack.getCount() > 1) {
                if (!itemstack1.isEmpty()) {
                    return false;
                } else {
                    itemstack2 = itemstack.cloneItemStack();
                    itemstack2.setCount(1);
                    this.setSlot(enumitemslot, itemstack2);
                    itemstack.subtract(1);
                    return true;
                }
            } else {
                this.setSlot(enumitemslot, itemstack);
                entityhuman.a(enumhand, itemstack1);
                return true;
            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            if (DamageSource.OUT_OF_WORLD.equals(damagesource)) {
                this.killEntity();
                return false;
            } else if (!this.isInvulnerable(damagesource) && !this.invisible && !this.isMarker()) {
                if (damagesource.isExplosion()) {
                    this.h(damagesource);
                    this.killEntity();
                    return false;
                } else if (DamageSource.IN_FIRE.equals(damagesource)) {
                    if (this.isBurning()) {
                        this.g(damagesource, 0.15F);
                    } else {
                        this.setOnFire(5);
                    }

                    return false;
                } else if (DamageSource.ON_FIRE.equals(damagesource) && this.getHealth() > 0.5F) {
                    this.g(damagesource, 4.0F);
                    return false;
                } else {
                    boolean flag = damagesource.k() instanceof EntityArrow;
                    boolean flag1 = flag && ((EntityArrow) damagesource.k()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(damagesource.u());

                    if (!flag2 && !flag) {
                        return false;
                    } else if (damagesource.getEntity() instanceof EntityHuman && !((EntityHuman) damagesource.getEntity()).getAbilities().mayBuild) {
                        return false;
                    } else if (damagesource.B()) {
                        this.H();
                        this.G();
                        this.killEntity();
                        return flag1;
                    } else {
                        long i = this.level.getTime();

                        if (i - this.lastHit > 5L && !flag) {
                            this.level.broadcastEntityEffect(this, (byte) 32);
                            this.a(GameEvent.ENTITY_DAMAGED, damagesource.getEntity());
                            this.lastHit = i;
                        } else {
                            this.g(damagesource);
                            this.G();
                            this.killEntity();
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void a(byte b0) {
        if (b0 == 32) {
            if (this.level.isClientSide) {
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
                this.lastHit = this.level.getTime();
            }
        } else {
            super.a(b0);
        }

    }

    @Override
    public boolean a(double d0) {
        double d1 = this.getBoundingBox().a() * 4.0D;

        if (Double.isNaN(d1) || d1 == 0.0D) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    private void G() {
        if (this.level instanceof WorldServer) {
            ((WorldServer) this.level).a(new ParticleParamBlock(Particles.BLOCK, Blocks.OAK_PLANKS.getBlockData()), this.locX(), this.e(0.6666666666666666D), this.locZ(), 10, (double) (this.getWidth() / 4.0F), (double) (this.getHeight() / 4.0F), (double) (this.getWidth() / 4.0F), 0.05D);
        }

    }

    private void g(DamageSource damagesource, float f) {
        float f1 = this.getHealth();

        f1 -= f;
        if (f1 <= 0.5F) {
            this.h(damagesource);
            this.killEntity();
        } else {
            this.setHealth(f1);
            this.a(GameEvent.ENTITY_DAMAGED, damagesource.getEntity());
        }

    }

    private void g(DamageSource damagesource) {
        Block.a(this.level, this.getChunkCoordinates(), new ItemStack(Items.ARMOR_STAND));
        this.h(damagesource);
    }

    private void h(DamageSource damagesource) {
        this.H();
        this.f(damagesource);

        ItemStack itemstack;
        int i;

        for (i = 0; i < this.handItems.size(); ++i) {
            itemstack = (ItemStack) this.handItems.get(i);
            if (!itemstack.isEmpty()) {
                Block.a(this.level, this.getChunkCoordinates().up(), itemstack);
                this.handItems.set(i, ItemStack.EMPTY);
            }
        }

        for (i = 0; i < this.armorItems.size(); ++i) {
            itemstack = (ItemStack) this.armorItems.get(i);
            if (!itemstack.isEmpty()) {
                Block.a(this.level, this.getChunkCoordinates().up(), itemstack);
                this.armorItems.set(i, ItemStack.EMPTY);
            }
        }

    }

    private void H() {
        this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
    }

    @Override
    protected float e(float f, float f1) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
        return 0.0F;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * (this.isBaby() ? 0.5F : 0.9F);
    }

    @Override
    public double bk() {
        return this.isMarker() ? 0.0D : 0.10000000149011612D;
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.D()) {
            super.g(vec3d);
        }
    }

    @Override
    public void m(float f) {
        this.yBodyRotO = this.yRotO = f;
        this.yHeadRotO = this.yHeadRot = f;
    }

    @Override
    public void setHeadRotation(float f) {
        this.yBodyRotO = this.yRotO = f;
        this.yHeadRotO = this.yHeadRot = f;
    }

    @Override
    public void tick() {
        super.tick();
        Vector3f vector3f = (Vector3f) this.entityData.get(EntityArmorStand.DATA_HEAD_POSE);

        if (!this.headPose.equals(vector3f)) {
            this.setHeadPose(vector3f);
        }

        Vector3f vector3f1 = (Vector3f) this.entityData.get(EntityArmorStand.DATA_BODY_POSE);

        if (!this.bodyPose.equals(vector3f1)) {
            this.setBodyPose(vector3f1);
        }

        Vector3f vector3f2 = (Vector3f) this.entityData.get(EntityArmorStand.DATA_LEFT_ARM_POSE);

        if (!this.leftArmPose.equals(vector3f2)) {
            this.setLeftArmPose(vector3f2);
        }

        Vector3f vector3f3 = (Vector3f) this.entityData.get(EntityArmorStand.DATA_RIGHT_ARM_POSE);

        if (!this.rightArmPose.equals(vector3f3)) {
            this.setRightArmPose(vector3f3);
        }

        Vector3f vector3f4 = (Vector3f) this.entityData.get(EntityArmorStand.DATA_LEFT_LEG_POSE);

        if (!this.leftLegPose.equals(vector3f4)) {
            this.setLeftLegPose(vector3f4);
        }

        Vector3f vector3f5 = (Vector3f) this.entityData.get(EntityArmorStand.DATA_RIGHT_LEG_POSE);

        if (!this.rightLegPose.equals(vector3f5)) {
            this.setRightLegPose(vector3f5);
        }

    }

    @Override
    protected void C() {
        this.setInvisible(this.invisible);
    }

    @Override
    public void setInvisible(boolean flag) {
        this.invisible = flag;
        super.setInvisible(flag);
    }

    @Override
    public boolean isBaby() {
        return this.isSmall();
    }

    @Override
    public void killEntity() {
        this.a(Entity.RemovalReason.KILLED);
    }

    @Override
    public boolean cx() {
        return this.isInvisible();
    }

    @Override
    public EnumPistonReaction getPushReaction() {
        return this.isMarker() ? EnumPistonReaction.IGNORE : super.getPushReaction();
    }

    public void setSmall(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.a((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 1, flag));
    }

    public boolean isSmall() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 1) != 0;
    }

    public void setArms(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.a((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 4, flag));
    }

    public boolean hasArms() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 4) != 0;
    }

    public void setBasePlate(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.a((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 8, flag));
    }

    public boolean hasBasePlate() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 8) != 0;
    }

    public void setMarker(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.a((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 16, flag));
    }

    public boolean isMarker() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 16) != 0;
    }

    private byte a(byte b0, int i, boolean flag) {
        if (flag) {
            b0 = (byte) (b0 | i);
        } else {
            b0 = (byte) (b0 & ~i);
        }

        return b0;
    }

    public void setHeadPose(Vector3f vector3f) {
        this.headPose = vector3f;
        this.entityData.set(EntityArmorStand.DATA_HEAD_POSE, vector3f);
    }

    public void setBodyPose(Vector3f vector3f) {
        this.bodyPose = vector3f;
        this.entityData.set(EntityArmorStand.DATA_BODY_POSE, vector3f);
    }

    public void setLeftArmPose(Vector3f vector3f) {
        this.leftArmPose = vector3f;
        this.entityData.set(EntityArmorStand.DATA_LEFT_ARM_POSE, vector3f);
    }

    public void setRightArmPose(Vector3f vector3f) {
        this.rightArmPose = vector3f;
        this.entityData.set(EntityArmorStand.DATA_RIGHT_ARM_POSE, vector3f);
    }

    public void setLeftLegPose(Vector3f vector3f) {
        this.leftLegPose = vector3f;
        this.entityData.set(EntityArmorStand.DATA_LEFT_LEG_POSE, vector3f);
    }

    public void setRightLegPose(Vector3f vector3f) {
        this.rightLegPose = vector3f;
        this.entityData.set(EntityArmorStand.DATA_RIGHT_LEG_POSE, vector3f);
    }

    public Vector3f v() {
        return this.headPose;
    }

    public Vector3f w() {
        return this.bodyPose;
    }

    public Vector3f x() {
        return this.leftArmPose;
    }

    public Vector3f z() {
        return this.rightArmPose;
    }

    public Vector3f A() {
        return this.leftLegPose;
    }

    public Vector3f B() {
        return this.rightLegPose;
    }

    @Override
    public boolean isInteractable() {
        return super.isInteractable() && !this.isMarker();
    }

    @Override
    public boolean r(Entity entity) {
        return entity instanceof EntityHuman && !this.level.a((EntityHuman) entity, this.getChunkCoordinates());
    }

    @Override
    public EnumMainHand getMainHand() {
        return EnumMainHand.RIGHT;
    }

    @Override
    protected SoundEffect getSoundFall(int i) {
        return SoundEffects.ARMOR_STAND_FALL;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ARMOR_STAND_HIT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ARMOR_STAND_BREAK;
    }

    @Override
    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {}

    @Override
    public boolean eQ() {
        return false;
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityArmorStand.DATA_CLIENT_FLAGS.equals(datawatcherobject)) {
            this.updateSize();
            this.blocksBuilding = !this.isMarker();
        }

        super.a(datawatcherobject);
    }

    @Override
    public boolean eR() {
        return false;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return this.u(this.isMarker());
    }

    private EntitySize u(boolean flag) {
        return flag ? EntityArmorStand.MARKER_DIMENSIONS : (this.isBaby() ? EntityArmorStand.BABY_DIMENSIONS : this.getEntityType().m());
    }

    @Override
    public Vec3D j(float f) {
        if (this.isMarker()) {
            AxisAlignedBB axisalignedbb = this.u(false).a(this.getPositionVector());
            BlockPosition blockposition = this.getChunkCoordinates();
            int i = Integer.MIN_VALUE;
            Iterator iterator = BlockPosition.a(new BlockPosition(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ), new BlockPosition(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int j = Math.max(this.level.getBrightness(EnumSkyBlock.BLOCK, blockposition1), this.level.getBrightness(EnumSkyBlock.SKY, blockposition1));

                if (j == 15) {
                    return Vec3D.a((BaseBlockPosition) blockposition1);
                }

                if (j > i) {
                    i = j;
                    blockposition = blockposition1.immutableCopy();
                }
            }

            return Vec3D.a((BaseBlockPosition) blockposition);
        } else {
            return super.j(f);
        }
    }

    @Override
    public ItemStack df() {
        return new ItemStack(Items.ARMOR_STAND);
    }

    @Override
    public boolean dO() {
        return !this.isInvisible() && !this.isMarker();
    }
}
