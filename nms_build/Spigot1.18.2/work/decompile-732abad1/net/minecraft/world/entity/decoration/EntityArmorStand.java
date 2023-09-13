package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
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
    private static final EntitySize BABY_DIMENSIONS = EntityTypes.ARMOR_STAND.getDimensions().scale(0.5F);
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
    public static final DataWatcherObject<Byte> DATA_CLIENT_FLAGS = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.BYTE);
    public static final DataWatcherObject<Vector3f> DATA_HEAD_POSE = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_BODY_POSE = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_LEFT_ARM_POSE = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_RIGHT_ARM_POSE = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_LEFT_LEG_POSE = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
    public static final DataWatcherObject<Vector3f> DATA_RIGHT_LEG_POSE = DataWatcher.defineId(EntityArmorStand.class, DataWatcherRegistry.ROTATIONS);
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
        this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
        this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
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
        this.setPos(d0, d1, d2);
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();

        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    private boolean hasPhysics() {
        return !this.isMarker() && !this.isNoGravity();
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && this.hasPhysics();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityArmorStand.DATA_CLIENT_FLAGS, (byte) 0);
        this.entityData.define(EntityArmorStand.DATA_HEAD_POSE, EntityArmorStand.DEFAULT_HEAD_POSE);
        this.entityData.define(EntityArmorStand.DATA_BODY_POSE, EntityArmorStand.DEFAULT_BODY_POSE);
        this.entityData.define(EntityArmorStand.DATA_LEFT_ARM_POSE, EntityArmorStand.DEFAULT_LEFT_ARM_POSE);
        this.entityData.define(EntityArmorStand.DATA_RIGHT_ARM_POSE, EntityArmorStand.DEFAULT_RIGHT_ARM_POSE);
        this.entityData.define(EntityArmorStand.DATA_LEFT_LEG_POSE, EntityArmorStand.DEFAULT_LEFT_LEG_POSE);
        this.entityData.define(EntityArmorStand.DATA_RIGHT_LEG_POSE, EntityArmorStand.DEFAULT_RIGHT_LEG_POSE);
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EnumItemSlot enumitemslot) {
        switch (enumitemslot.getType()) {
            case HAND:
                return (ItemStack) this.handItems.get(enumitemslot.getIndex());
            case ARMOR:
                return (ItemStack) this.armorItems.get(enumitemslot.getIndex());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItemSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.verifyEquippedItem(itemstack);
        switch (enumitemslot.getType()) {
            case HAND:
                this.equipEventAndSound(itemstack);
                this.handItems.set(enumitemslot.getIndex(), itemstack);
                break;
            case ARMOR:
                this.equipEventAndSound(itemstack);
                this.armorItems.set(enumitemslot.getIndex(), itemstack);
        }

    }

    @Override
    public boolean canTakeItem(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return this.getItemBySlot(enumitemslot).isEmpty() && !this.isDisabled(enumitemslot);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.armorItems.iterator(); iterator.hasNext(); nbttaglist.add(nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.put("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.handItems.iterator(); iterator1.hasNext(); nbttaglist1.add(nbttagcompound2)) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();

            nbttagcompound2 = new NBTTagCompound();
            if (!itemstack1.isEmpty()) {
                itemstack1.save(nbttagcompound2);
            }
        }

        nbttagcompound.put("HandItems", nbttaglist1);
        nbttagcompound.putBoolean("Invisible", this.isInvisible());
        nbttagcompound.putBoolean("Small", this.isSmall());
        nbttagcompound.putBoolean("ShowArms", this.isShowArms());
        nbttagcompound.putInt("DisabledSlots", this.disabledSlots);
        nbttagcompound.putBoolean("NoBasePlate", this.isNoBasePlate());
        if (this.isMarker()) {
            nbttagcompound.putBoolean("Marker", this.isMarker());
        }

        nbttagcompound.put("Pose", this.writePose());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.contains("ArmorItems", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorItems", 10);

            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.of(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.contains("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.handItems.size(); ++i) {
                this.handItems.set(i, ItemStack.of(nbttaglist.getCompound(i)));
            }
        }

        this.setInvisible(nbttagcompound.getBoolean("Invisible"));
        this.setSmall(nbttagcompound.getBoolean("Small"));
        this.setShowArms(nbttagcompound.getBoolean("ShowArms"));
        this.disabledSlots = nbttagcompound.getInt("DisabledSlots");
        this.setNoBasePlate(nbttagcompound.getBoolean("NoBasePlate"));
        this.setMarker(nbttagcompound.getBoolean("Marker"));
        this.noPhysics = !this.hasPhysics();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Pose");

        this.readPose(nbttagcompound1);
    }

    private void readPose(NBTTagCompound nbttagcompound) {
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

    private NBTTagCompound writePose() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (!EntityArmorStand.DEFAULT_HEAD_POSE.equals(this.headPose)) {
            nbttagcompound.put("Head", this.headPose.save());
        }

        if (!EntityArmorStand.DEFAULT_BODY_POSE.equals(this.bodyPose)) {
            nbttagcompound.put("Body", this.bodyPose.save());
        }

        if (!EntityArmorStand.DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
            nbttagcompound.put("LeftArm", this.leftArmPose.save());
        }

        if (!EntityArmorStand.DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
            nbttagcompound.put("RightArm", this.rightArmPose.save());
        }

        if (!EntityArmorStand.DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
            nbttagcompound.put("LeftLeg", this.leftLegPose.save());
        }

        if (!EntityArmorStand.DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
            nbttagcompound.put("RightLeg", this.rightLegPose.save());
        }

        return nbttagcompound;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    protected void pushEntities() {
        List<Entity> list = this.level.getEntities((Entity) this, this.getBoundingBox(), EntityArmorStand.RIDABLE_MINECARTS);

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (this.distanceToSqr(entity) <= 0.2D) {
                entity.push(this);
            }
        }

    }

    @Override
    public EnumInteractionResult interactAt(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!this.isMarker() && !itemstack.is(Items.NAME_TAG)) {
            if (entityhuman.isSpectator()) {
                return EnumInteractionResult.SUCCESS;
            } else if (entityhuman.level.isClientSide) {
                return EnumInteractionResult.CONSUME;
            } else {
                EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

                if (itemstack.isEmpty()) {
                    EnumItemSlot enumitemslot1 = this.getClickedSlot(vec3d);
                    EnumItemSlot enumitemslot2 = this.isDisabled(enumitemslot1) ? enumitemslot : enumitemslot1;

                    if (this.hasItemInSlot(enumitemslot2) && this.swapItem(entityhuman, enumitemslot2, itemstack, enumhand)) {
                        return EnumInteractionResult.SUCCESS;
                    }
                } else {
                    if (this.isDisabled(enumitemslot)) {
                        return EnumInteractionResult.FAIL;
                    }

                    if (enumitemslot.getType() == EnumItemSlot.Function.HAND && !this.isShowArms()) {
                        return EnumInteractionResult.FAIL;
                    }

                    if (this.swapItem(entityhuman, enumitemslot, itemstack, enumhand)) {
                        return EnumInteractionResult.SUCCESS;
                    }
                }

                return EnumInteractionResult.PASS;
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    private EnumItemSlot getClickedSlot(Vec3D vec3d) {
        EnumItemSlot enumitemslot = EnumItemSlot.MAINHAND;
        boolean flag = this.isSmall();
        double d0 = flag ? vec3d.y * 2.0D : vec3d.y;
        EnumItemSlot enumitemslot1 = EnumItemSlot.FEET;

        if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(enumitemslot1)) {
            enumitemslot = EnumItemSlot.FEET;
        } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EnumItemSlot.CHEST)) {
            enumitemslot = EnumItemSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EnumItemSlot.LEGS)) {
            enumitemslot = EnumItemSlot.LEGS;
        } else if (d0 >= 1.6D && this.hasItemInSlot(EnumItemSlot.HEAD)) {
            enumitemslot = EnumItemSlot.HEAD;
        } else if (!this.hasItemInSlot(EnumItemSlot.MAINHAND) && this.hasItemInSlot(EnumItemSlot.OFFHAND)) {
            enumitemslot = EnumItemSlot.OFFHAND;
        }

        return enumitemslot;
    }

    private boolean isDisabled(EnumItemSlot enumitemslot) {
        return (this.disabledSlots & 1 << enumitemslot.getFilterFlag()) != 0 || enumitemslot.getType() == EnumItemSlot.Function.HAND && !this.isShowArms();
    }

    private boolean swapItem(EntityHuman entityhuman, EnumItemSlot enumitemslot, ItemStack itemstack, EnumHand enumhand) {
        ItemStack itemstack1 = this.getItemBySlot(enumitemslot);

        if (!itemstack1.isEmpty() && (this.disabledSlots & 1 << enumitemslot.getFilterFlag() + 8) != 0) {
            return false;
        } else if (itemstack1.isEmpty() && (this.disabledSlots & 1 << enumitemslot.getFilterFlag() + 16) != 0) {
            return false;
        } else {
            ItemStack itemstack2;

            if (entityhuman.getAbilities().instabuild && itemstack1.isEmpty() && !itemstack.isEmpty()) {
                itemstack2 = itemstack.copy();
                itemstack2.setCount(1);
                this.setItemSlot(enumitemslot, itemstack2);
                return true;
            } else if (!itemstack.isEmpty() && itemstack.getCount() > 1) {
                if (!itemstack1.isEmpty()) {
                    return false;
                } else {
                    itemstack2 = itemstack.copy();
                    itemstack2.setCount(1);
                    this.setItemSlot(enumitemslot, itemstack2);
                    itemstack.shrink(1);
                    return true;
                }
            } else {
                this.setItemSlot(enumitemslot, itemstack);
                entityhuman.setItemInHand(enumhand, itemstack1);
                return true;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            if (DamageSource.OUT_OF_WORLD.equals(damagesource)) {
                this.kill();
                return false;
            } else if (!this.isInvulnerableTo(damagesource) && !this.invisible && !this.isMarker()) {
                if (damagesource.isExplosion()) {
                    this.brokenByAnything(damagesource);
                    this.kill();
                    return false;
                } else if (DamageSource.IN_FIRE.equals(damagesource)) {
                    if (this.isOnFire()) {
                        this.causeDamage(damagesource, 0.15F);
                    } else {
                        this.setSecondsOnFire(5);
                    }

                    return false;
                } else if (DamageSource.ON_FIRE.equals(damagesource) && this.getHealth() > 0.5F) {
                    this.causeDamage(damagesource, 4.0F);
                    return false;
                } else {
                    boolean flag = damagesource.getDirectEntity() instanceof EntityArrow;
                    boolean flag1 = flag && ((EntityArrow) damagesource.getDirectEntity()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(damagesource.getMsgId());

                    if (!flag2 && !flag) {
                        return false;
                    } else if (damagesource.getEntity() instanceof EntityHuman && !((EntityHuman) damagesource.getEntity()).getAbilities().mayBuild) {
                        return false;
                    } else if (damagesource.isCreativePlayer()) {
                        this.playBrokenSound();
                        this.showBreakingParticles();
                        this.kill();
                        return flag1;
                    } else {
                        long i = this.level.getGameTime();

                        if (i - this.lastHit > 5L && !flag) {
                            this.level.broadcastEntityEvent(this, (byte) 32);
                            this.gameEvent(GameEvent.ENTITY_DAMAGED, damagesource.getEntity());
                            this.lastHit = i;
                        } else {
                            this.brokenByPlayer(damagesource);
                            this.showBreakingParticles();
                            this.kill();
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
    public void handleEntityEvent(byte b0) {
        if (b0 == 32) {
            if (this.level.isClientSide) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEffects.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
                this.lastHit = this.level.getGameTime();
            }
        } else {
            super.handleEntityEvent(b0);
        }

    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        double d1 = this.getBoundingBox().getSize() * 4.0D;

        if (Double.isNaN(d1) || d1 == 0.0D) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    private void showBreakingParticles() {
        if (this.level instanceof WorldServer) {
            ((WorldServer) this.level).sendParticles(new ParticleParamBlock(Particles.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double) (this.getBbWidth() / 4.0F), (double) (this.getBbHeight() / 4.0F), (double) (this.getBbWidth() / 4.0F), 0.05D);
        }

    }

    private void causeDamage(DamageSource damagesource, float f) {
        float f1 = this.getHealth();

        f1 -= f;
        if (f1 <= 0.5F) {
            this.brokenByAnything(damagesource);
            this.kill();
        } else {
            this.setHealth(f1);
            this.gameEvent(GameEvent.ENTITY_DAMAGED, damagesource.getEntity());
        }

    }

    private void brokenByPlayer(DamageSource damagesource) {
        Block.popResource(this.level, this.blockPosition(), new ItemStack(Items.ARMOR_STAND));
        this.brokenByAnything(damagesource);
    }

    private void brokenByAnything(DamageSource damagesource) {
        this.playBrokenSound();
        this.dropAllDeathLoot(damagesource);

        ItemStack itemstack;
        int i;

        for (i = 0; i < this.handItems.size(); ++i) {
            itemstack = (ItemStack) this.handItems.get(i);
            if (!itemstack.isEmpty()) {
                Block.popResource(this.level, this.blockPosition().above(), itemstack);
                this.handItems.set(i, ItemStack.EMPTY);
            }
        }

        for (i = 0; i < this.armorItems.size(); ++i) {
            itemstack = (ItemStack) this.armorItems.get(i);
            if (!itemstack.isEmpty()) {
                Block.popResource(this.level, this.blockPosition().above(), itemstack);
                this.armorItems.set(i, ItemStack.EMPTY);
            }
        }

    }

    private void playBrokenSound() {
        this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    protected float tickHeadTurn(float f, float f1) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
        return 0.0F;
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * (this.isBaby() ? 0.5F : 0.9F);
    }

    @Override
    public double getMyRidingOffset() {
        return this.isMarker() ? 0.0D : 0.10000000149011612D;
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.hasPhysics()) {
            super.travel(vec3d);
        }
    }

    @Override
    public void setYBodyRot(float f) {
        this.yBodyRotO = this.yRotO = f;
        this.yHeadRotO = this.yHeadRot = f;
    }

    @Override
    public void setYHeadRot(float f) {
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
    protected void updateInvisibilityStatus() {
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
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isInvisible();
    }

    @Override
    public EnumPistonReaction getPistonPushReaction() {
        return this.isMarker() ? EnumPistonReaction.IGNORE : super.getPistonPushReaction();
    }

    public void setSmall(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.setBit((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 1, flag));
    }

    public boolean isSmall() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 1) != 0;
    }

    public void setShowArms(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.setBit((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 4, flag));
    }

    public boolean isShowArms() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 4) != 0;
    }

    public void setNoBasePlate(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.setBit((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 8, flag));
    }

    public boolean isNoBasePlate() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 8) != 0;
    }

    public void setMarker(boolean flag) {
        this.entityData.set(EntityArmorStand.DATA_CLIENT_FLAGS, this.setBit((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS), 16, flag));
    }

    public boolean isMarker() {
        return ((Byte) this.entityData.get(EntityArmorStand.DATA_CLIENT_FLAGS) & 16) != 0;
    }

    private byte setBit(byte b0, int i, boolean flag) {
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

    public Vector3f getHeadPose() {
        return this.headPose;
    }

    public Vector3f getBodyPose() {
        return this.bodyPose;
    }

    public Vector3f getLeftArmPose() {
        return this.leftArmPose;
    }

    public Vector3f getRightArmPose() {
        return this.rightArmPose;
    }

    public Vector3f getLeftLegPose() {
        return this.leftLegPose;
    }

    public Vector3f getRightLegPose() {
        return this.rightLegPose;
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isMarker();
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return entity instanceof EntityHuman && !this.level.mayInteract((EntityHuman) entity, this.blockPosition());
    }

    @Override
    public EnumMainHand getMainArm() {
        return EnumMainHand.RIGHT;
    }

    @Override
    public EntityLiving.a getFallSounds() {
        return new EntityLiving.a(SoundEffects.ARMOR_STAND_FALL, SoundEffects.ARMOR_STAND_FALL);
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ARMOR_STAND_HIT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.ARMOR_STAND_BREAK;
    }

    @Override
    public void thunderHit(WorldServer worldserver, EntityLightning entitylightning) {}

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityArmorStand.DATA_CLIENT_FLAGS.equals(datawatcherobject)) {
            this.refreshDimensions();
            this.blocksBuilding = !this.isMarker();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return this.getDimensionsMarker(this.isMarker());
    }

    private EntitySize getDimensionsMarker(boolean flag) {
        return flag ? EntityArmorStand.MARKER_DIMENSIONS : (this.isBaby() ? EntityArmorStand.BABY_DIMENSIONS : this.getType().getDimensions());
    }

    @Override
    public Vec3D getLightProbePosition(float f) {
        if (this.isMarker()) {
            AxisAlignedBB axisalignedbb = this.getDimensionsMarker(false).makeBoundingBox(this.position());
            BlockPosition blockposition = this.blockPosition();
            int i = Integer.MIN_VALUE;
            Iterator iterator = BlockPosition.betweenClosed(new BlockPosition(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ), new BlockPosition(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int j = Math.max(this.level.getBrightness(EnumSkyBlock.BLOCK, blockposition1), this.level.getBrightness(EnumSkyBlock.SKY, blockposition1));

                if (j == 15) {
                    return Vec3D.atCenterOf(blockposition1);
                }

                if (j > i) {
                    i = j;
                    blockposition = blockposition1.immutable();
                }
            }

            return Vec3D.atCenterOf(blockposition);
        } else {
            return super.getLightProbePosition(f);
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.ARMOR_STAND);
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return !this.isInvisible() && !this.isMarker();
    }
}
