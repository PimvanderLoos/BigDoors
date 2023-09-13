package net.minecraft.world.entity.animal.horse;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.players.NameReferencingFileConverter;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.IInventoryListener;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IJumpable;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTame;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.DismountUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityHorseAbstract extends EntityAnimal implements IInventoryListener, IJumpable, ISaddleable {

    public static final int EQUIPMENT_SLOT_OFFSET = 400;
    public static final int CHEST_SLOT_OFFSET = 499;
    public static final int INVENTORY_SLOT_OFFSET = 500;
    private static final Predicate<EntityLiving> PARENT_HORSE_SELECTOR = (entityliving) -> {
        return entityliving instanceof EntityHorseAbstract && ((EntityHorseAbstract) entityliving).hasReproduced();
    };
    private static final PathfinderTargetCondition MOMMY_TARGETING = PathfinderTargetCondition.b().a(16.0D).d().a(EntityHorseAbstract.PARENT_HORSE_SELECTOR);
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.a(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.getItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final DataWatcherObject<Byte> DATA_ID_FLAGS = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Optional<UUID>> DATA_ID_OWNER_UUID = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.OPTIONAL_UUID);
    private static final int FLAG_TAME = 2;
    private static final int FLAG_SADDLE = 4;
    private static final int FLAG_BRED = 8;
    private static final int FLAG_EATING = 16;
    private static final int FLAG_STANDING = 32;
    private static final int FLAG_OPEN_MOUTH = 64;
    public static final int INV_SLOT_SADDLE = 0;
    public static final int INV_SLOT_ARMOR = 1;
    public static final int INV_BASE_COUNT = 2;
    private int eatingCounter;
    private int mouthCounter;
    private int standCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean isJumping;
    public InventorySubcontainer inventory;
    protected int temper;
    protected float playerJumpPendingScale;
    private boolean allowStandSliding;
    private float eatAnim;
    private float eatAnimO;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected boolean canGallop = true;
    protected int gallopSoundCounter;

    protected EntityHorseAbstract(EntityTypes<? extends EntityHorseAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.loadChest();
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D, EntityHorseAbstract.class));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.fF();
    }

    protected void fF() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityHorseAbstract.DATA_ID_FLAGS, (byte) 0);
        this.entityData.register(EntityHorseAbstract.DATA_ID_OWNER_UUID, Optional.empty());
    }

    protected boolean u(int i) {
        return ((Byte) this.entityData.get(EntityHorseAbstract.DATA_ID_FLAGS) & i) != 0;
    }

    protected void d(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityHorseAbstract.DATA_ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityHorseAbstract.DATA_ID_FLAGS, (byte) (b0 | i));
        } else {
            this.entityData.set(EntityHorseAbstract.DATA_ID_FLAGS, (byte) (b0 & ~i));
        }

    }

    public boolean isTamed() {
        return this.u(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(EntityHorseAbstract.DATA_ID_OWNER_UUID)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(EntityHorseAbstract.DATA_ID_OWNER_UUID, Optional.ofNullable(uuid));
    }

    public boolean fI() {
        return this.isJumping;
    }

    public void setTamed(boolean flag) {
        this.d(2, flag);
    }

    public void x(boolean flag) {
        this.isJumping = flag;
    }

    @Override
    protected void y(float f) {
        if (f > 6.0F && this.fJ()) {
            this.z(false);
        }

    }

    public boolean fJ() {
        return this.u(16);
    }

    public boolean fK() {
        return this.u(32);
    }

    public boolean hasReproduced() {
        return this.u(8);
    }

    public void y(boolean flag) {
        this.d(8, flag);
    }

    @Override
    public boolean canSaddle() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void saddle(@Nullable SoundCategory soundcategory) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (soundcategory != null) {
            this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.HORSE_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    public boolean hasSaddle() {
        return this.u(4);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int i) {
        this.temper = i;
    }

    public int w(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxDomestication());

        this.setTemper(j);
        return j;
    }

    @Override
    public boolean isCollidable() {
        return !this.isVehicle();
    }

    private void t() {
        this.fy();
        if (!this.isSilent()) {
            SoundEffect soundeffect = this.fQ();

            if (soundeffect != null) {
                this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), soundeffect, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        if (f > 1.0F) {
            this.playSound(SoundEffects.HORSE_LAND, 0.4F, 1.0F);
        }

        int i = this.d(f, f1);

        if (i <= 0) {
            return false;
        } else {
            this.damageEntity(damagesource, (float) i);
            if (this.isVehicle()) {
                Iterator iterator = this.getAllPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.damageEntity(damagesource, (float) i);
                }
            }

            this.playBlockStepSound();
            return true;
        }
    }

    @Override
    protected int d(float f, float f1) {
        return MathHelper.f((f * 0.5F - 3.0F) * f1);
    }

    protected int getChestSlots() {
        return 2;
    }

    public void loadChest() {
        InventorySubcontainer inventorysubcontainer = this.inventory;

        this.inventory = new InventorySubcontainer(this.getChestSlots());
        if (inventorysubcontainer != null) {
            inventorysubcontainer.b((IInventoryListener) this);
            int i = Math.min(inventorysubcontainer.getSize(), this.inventory.getSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventorysubcontainer.getItem(j);

                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.cloneItemStack());
                }
            }
        }

        this.inventory.a((IInventoryListener) this);
        this.fO();
    }

    protected void fO() {
        if (!this.level.isClientSide) {
            this.d(4, !this.inventory.getItem(0).isEmpty());
        }
    }

    @Override
    public void a(IInventory iinventory) {
        boolean flag = this.hasSaddle();

        this.fO();
        if (this.tickCount > 20 && !flag && this.hasSaddle()) {
            this.playSound(SoundEffects.HORSE_SADDLE, 0.5F, 1.0F);
        }

    }

    public double getJumpStrength() {
        return this.b(GenericAttributes.JUMP_STRENGTH);
    }

    @Nullable
    protected SoundEffect fQ() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        if (this.random.nextInt(3) == 0) {
            this.fE();
        }

        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        if (this.random.nextInt(10) == 0 && !this.isFrozen()) {
            this.fE();
        }

        return null;
    }

    @Nullable
    protected SoundEffect getSoundAngry() {
        this.fE();
        return null;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            IBlockData iblockdata1 = this.level.getType(blockposition.up());
            SoundEffectType soundeffecttype = iblockdata.getStepSound();

            if (iblockdata1.a(Blocks.SNOW)) {
                soundeffecttype = iblockdata1.getStepSound();
            }

            if (this.isVehicle() && this.canGallop) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                    this.a(soundeffecttype);
                } else if (this.gallopSoundCounter <= 5) {
                    this.playSound(SoundEffects.HORSE_STEP_WOOD, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
                }
            } else if (soundeffecttype == SoundEffectType.WOOD) {
                this.playSound(SoundEffects.HORSE_STEP_WOOD, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
            } else {
                this.playSound(SoundEffects.HORSE_STEP, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
            }

        }
    }

    protected void a(SoundEffectType soundeffecttype) {
        this.playSound(SoundEffects.HORSE_GALLOP, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
    }

    public static AttributeProvider.Builder fS() {
        return EntityInsentient.w().a(GenericAttributes.JUMP_STRENGTH).a(GenericAttributes.MAX_HEALTH, 53.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.22499999403953552D);
    }

    @Override
    public int getMaxSpawnGroup() {
        return 6;
    }

    public int getMaxDomestication() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int J() {
        return 400;
    }

    public void f(EntityHuman entityhuman) {
        if (!this.level.isClientSide && (!this.isVehicle() || this.u(entityhuman)) && this.isTamed()) {
            entityhuman.openHorseInventory(this, this.inventory);
        }

    }

    public EnumInteractionResult a(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = this.b(entityhuman, itemstack);

        if (!entityhuman.getAbilities().instabuild) {
            itemstack.subtract(1);
        }

        return this.level.isClientSide ? EnumInteractionResult.CONSUME : (flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS);
    }

    protected boolean b(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = false;
        float f = 0.0F;
        short short0 = 0;
        byte b0 = 0;

        if (itemstack.a(Items.WHEAT)) {
            f = 2.0F;
            short0 = 20;
            b0 = 3;
        } else if (itemstack.a(Items.SUGAR)) {
            f = 1.0F;
            short0 = 30;
            b0 = 3;
        } else if (itemstack.a(Blocks.HAY_BLOCK.getItem())) {
            f = 20.0F;
            short0 = 180;
        } else if (itemstack.a(Items.APPLE)) {
            f = 3.0F;
            short0 = 60;
            b0 = 3;
        } else if (itemstack.a(Items.GOLDEN_CARROT)) {
            f = 4.0F;
            short0 = 60;
            b0 = 5;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.g(entityhuman);
            }
        } else if (itemstack.a(Items.GOLDEN_APPLE) || itemstack.a(Items.ENCHANTED_GOLDEN_APPLE)) {
            f = 10.0F;
            short0 = 240;
            b0 = 10;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.g(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && short0 > 0) {
            this.level.addParticle(Particles.HAPPY_VILLAGER, this.d(1.0D), this.da() + 0.5D, this.g(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
                this.setAge(short0);
            }

            flag = true;
        }

        if (b0 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxDomestication()) {
            flag = true;
            if (!this.level.isClientSide) {
                this.w(b0);
            }
        }

        if (flag) {
            this.t();
            this.a(GameEvent.EAT, this.cT());
        }

        return flag;
    }

    protected void h(EntityHuman entityhuman) {
        this.z(false);
        this.setStanding(false);
        if (!this.level.isClientSide) {
            entityhuman.setYRot(this.getYRot());
            entityhuman.setXRot(this.getXRot());
            entityhuman.startRiding(this);
        }

    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() && this.isVehicle() && this.hasSaddle() || this.fJ() || this.fK();
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return EntityHorseAbstract.FOOD_ITEMS.test(itemstack);
    }

    private void fw() {
        this.tailCounter = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null) {
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty() && !EnchantmentManager.shouldNotDrop(itemstack)) {
                    this.b(itemstack);
                }
            }

        }
    }

    @Override
    public void movementTick() {
        if (this.random.nextInt(200) == 0) {
            this.fw();
        }

        super.movementTick();
        if (!this.level.isClientSide && this.isAlive()) {
            if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }

            if (this.fV()) {
                if (!this.fJ() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level.getType(this.getChunkCoordinates().down()).a(Blocks.GRASS_BLOCK)) {
                    this.z(true);
                }

                if (this.fJ() && ++this.eatingCounter > 50) {
                    this.eatingCounter = 0;
                    this.z(false);
                }
            }

            this.fU();
        }
    }

    protected void fU() {
        if (this.hasReproduced() && this.isBaby() && !this.fJ()) {
            EntityLiving entityliving = this.level.a(EntityHorseAbstract.class, EntityHorseAbstract.MOMMY_TARGETING, this, this.locX(), this.locY(), this.locZ(), this.getBoundingBox().g(16.0D));

            if (entityliving != null && this.f((Entity) entityliving) > 4.0D) {
                this.navigation.a((Entity) entityliving, 0);
            }
        }

    }

    public boolean fV() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
            this.mouthCounter = 0;
            this.d(64, false);
        }

        if ((this.cH() || this.doAITick()) && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }

        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }

        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }

        this.eatAnimO = this.eatAnim;
        if (this.fJ()) {
            this.eatAnim += (1.0F - this.eatAnim) * 0.4F + 0.05F;
            if (this.eatAnim > 1.0F) {
                this.eatAnim = 1.0F;
            }
        } else {
            this.eatAnim += (0.0F - this.eatAnim) * 0.4F - 0.05F;
            if (this.eatAnim < 0.0F) {
                this.eatAnim = 0.0F;
            }
        }

        this.standAnimO = this.standAnim;
        if (this.fK()) {
            this.eatAnim = 0.0F;
            this.eatAnimO = this.eatAnim;
            this.standAnim += (1.0F - this.standAnim) * 0.4F + 0.05F;
            if (this.standAnim > 1.0F) {
                this.standAnim = 1.0F;
            }
        } else {
            this.allowStandSliding = false;
            this.standAnim += (0.8F * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6F - 0.05F;
            if (this.standAnim < 0.0F) {
                this.standAnim = 0.0F;
            }
        }

        this.mouthAnimO = this.mouthAnim;
        if (this.u(64)) {
            this.mouthAnim += (1.0F - this.mouthAnim) * 0.7F + 0.05F;
            if (this.mouthAnim > 1.0F) {
                this.mouthAnim = 1.0F;
            }
        } else {
            this.mouthAnim += (0.0F - this.mouthAnim) * 0.7F - 0.05F;
            if (this.mouthAnim < 0.0F) {
                this.mouthAnim = 0.0F;
            }
        }

    }

    private void fy() {
        if (!this.level.isClientSide) {
            this.mouthCounter = 1;
            this.d(64, true);
        }

    }

    public void z(boolean flag) {
        this.d(16, flag);
    }

    public void setStanding(boolean flag) {
        if (flag) {
            this.z(false);
        }

        this.d(32, flag);
    }

    private void fE() {
        if (this.cH() || this.doAITick()) {
            this.standCounter = 1;
            this.setStanding(true);
        }

    }

    public void fW() {
        if (!this.fK()) {
            this.fE();
            SoundEffect soundeffect = this.getSoundAngry();

            if (soundeffect != null) {
                this.playSound(soundeffect, this.getSoundVolume(), this.ep());
            }
        }

    }

    public boolean i(EntityHuman entityhuman) {
        this.setOwnerUUID(entityhuman.getUniqueID());
        this.setTamed(true);
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.TAME_ANIMAL.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

        this.level.broadcastEntityEffect(this, (byte) 7);
        return true;
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.fd() && this.hasSaddle()) {
                EntityLiving entityliving = (EntityLiving) this.getRidingPassenger();

                this.setYRot(entityliving.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(entityliving.getXRot() * 0.5F);
                this.setYawPitch(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float f = entityliving.xxa * 0.5F;
                float f1 = entityliving.zza;

                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                    this.gallopSoundCounter = 0;
                }

                if (this.onGround && this.playerJumpPendingScale == 0.0F && this.fK() && !this.allowStandSliding) {
                    f = 0.0F;
                    f1 = 0.0F;
                }

                if (this.playerJumpPendingScale > 0.0F && !this.fI() && this.onGround) {
                    double d0 = this.getJumpStrength() * (double) this.playerJumpPendingScale * (double) this.getBlockJumpFactor();
                    double d1 = d0 + this.es();
                    Vec3D vec3d1 = this.getMot();

                    this.setMot(vec3d1.x, d1, vec3d1.z);
                    this.x(true);
                    this.hasImpulse = true;
                    if (f1 > 0.0F) {
                        float f2 = MathHelper.sin(this.getYRot() * 0.017453292F);
                        float f3 = MathHelper.cos(this.getYRot() * 0.017453292F);

                        this.setMot(this.getMot().add((double) (-0.4F * f2 * this.playerJumpPendingScale), 0.0D, (double) (0.4F * f3 * this.playerJumpPendingScale)));
                    }

                    this.playerJumpPendingScale = 0.0F;
                }

                this.flyingSpeed = this.ew() * 0.1F;
                if (this.cH()) {
                    this.r((float) this.b(GenericAttributes.MOVEMENT_SPEED));
                    super.g(new Vec3D((double) f, vec3d.y, (double) f1));
                } else if (entityliving instanceof EntityHuman) {
                    this.setMot(Vec3D.ZERO);
                }

                if (this.onGround) {
                    this.playerJumpPendingScale = 0.0F;
                    this.x(false);
                }

                this.a((EntityLiving) this, false);
                this.as();
            } else {
                this.flyingSpeed = 0.02F;
                super.g(vec3d);
            }
        }
    }

    protected void fX() {
        this.playSound(SoundEffects.HORSE_JUMP, 0.4F, 1.0F);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("EatingHaystack", this.fJ());
        nbttagcompound.setBoolean("Bred", this.hasReproduced());
        nbttagcompound.setInt("Temper", this.getTemper());
        nbttagcompound.setBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            nbttagcompound.a("Owner", this.getOwnerUUID());
        }

        if (!this.inventory.getItem(0).isEmpty()) {
            nbttagcompound.set("SaddleItem", this.inventory.getItem(0).save(new NBTTagCompound()));
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.z(nbttagcompound.getBoolean("EatingHaystack"));
        this.y(nbttagcompound.getBoolean("Bred"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTamed(nbttagcompound.getBoolean("Tame"));
        UUID uuid;

        if (nbttagcompound.b("Owner")) {
            uuid = nbttagcompound.a("Owner");
        } else {
            String s = nbttagcompound.getString("Owner");

            uuid = NameReferencingFileConverter.a(this.getMinecraftServer(), s);
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
        }

        if (nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("SaddleItem"));

            if (itemstack.a(Items.SADDLE)) {
                this.inventory.setItem(0, itemstack);
            }
        }

        this.fO();
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    protected boolean fY() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return null;
    }

    protected void a(EntityAgeable entityageable, EntityHorseAbstract entityhorseabstract) {
        double d0 = this.c(GenericAttributes.MAX_HEALTH) + entityageable.c(GenericAttributes.MAX_HEALTH) + (double) this.fZ();

        entityhorseabstract.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(d0 / 3.0D);
        double d1 = this.c(GenericAttributes.JUMP_STRENGTH) + entityageable.c(GenericAttributes.JUMP_STRENGTH) + this.ga();

        entityhorseabstract.getAttributeInstance(GenericAttributes.JUMP_STRENGTH).setValue(d1 / 3.0D);
        double d2 = this.c(GenericAttributes.MOVEMENT_SPEED) + entityageable.c(GenericAttributes.MOVEMENT_SPEED) + this.gb();

        entityhorseabstract.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(d2 / 3.0D);
    }

    @Override
    public boolean fd() {
        return this.getRidingPassenger() instanceof EntityLiving;
    }

    public float z(float f) {
        return MathHelper.h(f, this.eatAnimO, this.eatAnim);
    }

    public float A(float f) {
        return MathHelper.h(f, this.standAnimO, this.standAnim);
    }

    public float B(float f) {
        return MathHelper.h(f, this.mouthAnimO, this.mouthAnim);
    }

    @Override
    public void a(int i) {
        if (this.hasSaddle()) {
            if (i < 0) {
                i = 0;
            } else {
                this.allowStandSliding = true;
                this.fE();
            }

            if (i >= 90) {
                this.playerJumpPendingScale = 1.0F;
            } else {
                this.playerJumpPendingScale = 0.4F + 0.4F * (float) i / 90.0F;
            }

        }
    }

    @Override
    public boolean a() {
        return this.hasSaddle();
    }

    @Override
    public void b(int i) {
        this.allowStandSliding = true;
        this.fE();
        this.fX();
    }

    @Override
    public void b() {}

    protected void B(boolean flag) {
        ParticleType particletype = flag ? Particles.HEART : Particles.SMOKE;

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particletype, this.d(1.0D), this.da() + 0.5D, this.g(1.0D), d0, d1, d2);
        }

    }

    @Override
    public void a(byte b0) {
        if (b0 == 7) {
            this.B(true);
        } else if (b0 == 6) {
            this.B(false);
        } else {
            super.a(b0);
        }

    }

    @Override
    public void i(Entity entity) {
        super.i(entity);
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            this.yBodyRot = entityinsentient.yBodyRot;
        }

        if (this.standAnimO > 0.0F) {
            float f = MathHelper.sin(this.yBodyRot * 0.017453292F);
            float f1 = MathHelper.cos(this.yBodyRot * 0.017453292F);
            float f2 = 0.7F * this.standAnimO;
            float f3 = 0.15F * this.standAnimO;

            entity.setPosition(this.locX() + (double) (f2 * f), this.locY() + this.bl() + entity.bk() + (double) f3, this.locZ() - (double) (f2 * f1));
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).yBodyRot = this.yBodyRot;
            }
        }

    }

    protected float fZ() {
        return 15.0F + (float) this.random.nextInt(8) + (float) this.random.nextInt(9);
    }

    protected double ga() {
        return 0.4000000059604645D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    protected double gb() {
        return (0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.95F;
    }

    public boolean gc() {
        return false;
    }

    public boolean gd() {
        return !this.getEquipment(EnumItemSlot.CHEST).isEmpty();
    }

    public boolean m(ItemStack itemstack) {
        return false;
    }

    private SlotAccess a(final int i, final Predicate<ItemStack> predicate) {
        return new SlotAccess() {
            @Override
            public ItemStack a() {
                return EntityHorseAbstract.this.inventory.getItem(i);
            }

            @Override
            public boolean a(ItemStack itemstack) {
                if (!predicate.test(itemstack)) {
                    return false;
                } else {
                    EntityHorseAbstract.this.inventory.setItem(i, itemstack);
                    EntityHorseAbstract.this.fO();
                    return true;
                }
            }
        };
    }

    @Override
    public SlotAccess k(int i) {
        int j = i - 400;

        if (j >= 0 && j < 2 && j < this.inventory.getSize()) {
            if (j == 0) {
                return this.a(j, (itemstack) -> {
                    return itemstack.isEmpty() || itemstack.a(Items.SADDLE);
                });
            }

            if (j == 1) {
                if (!this.gc()) {
                    return SlotAccess.NULL;
                }

                return this.a(j, (itemstack) -> {
                    return itemstack.isEmpty() || this.m(itemstack);
                });
            }
        }

        int k = i - 500 + 2;

        return k >= 2 && k < this.inventory.getSize() ? SlotAccess.a(this.inventory, k) : super.k(i);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.cB();
    }

    @Nullable
    private Vec3D a(Vec3D vec3d, EntityLiving entityliving) {
        double d0 = this.locX() + vec3d.x;
        double d1 = this.getBoundingBox().minY;
        double d2 = this.locZ() + vec3d.z;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        UnmodifiableIterator unmodifiableiterator = entityliving.eS().iterator();

        while (unmodifiableiterator.hasNext()) {
            EntityPose entitypose = (EntityPose) unmodifiableiterator.next();

            blockposition_mutableblockposition.c(d0, d1, d2);
            double d3 = this.getBoundingBox().maxY + 0.75D;

            while (true) {
                double d4 = this.level.i(blockposition_mutableblockposition);

                if ((double) blockposition_mutableblockposition.getY() + d4 > d3) {
                    break;
                }

                if (DismountUtil.a(d4)) {
                    AxisAlignedBB axisalignedbb = entityliving.f(entitypose);
                    Vec3D vec3d1 = new Vec3D(d0, (double) blockposition_mutableblockposition.getY() + d4, d2);

                    if (DismountUtil.a(this.level, entityliving, axisalignedbb.c(vec3d1))) {
                        entityliving.setPose(entitypose);
                        return vec3d1;
                    }
                }

                blockposition_mutableblockposition.c(EnumDirection.UP);
                if ((double) blockposition_mutableblockposition.getY() >= d3) {
                    break;
                }
            }
        }

        return null;
    }

    @Override
    public Vec3D b(EntityLiving entityliving) {
        Vec3D vec3d = a((double) this.getWidth(), (double) entityliving.getWidth(), this.getYRot() + (entityliving.getMainHand() == EnumMainHand.RIGHT ? 90.0F : -90.0F));
        Vec3D vec3d1 = this.a(vec3d, entityliving);

        if (vec3d1 != null) {
            return vec3d1;
        } else {
            Vec3D vec3d2 = a((double) this.getWidth(), (double) entityliving.getWidth(), this.getYRot() + (entityliving.getMainHand() == EnumMainHand.LEFT ? 90.0F : -90.0F));
            Vec3D vec3d3 = this.a(vec3d2, entityliving);

            return vec3d3 != null ? vec3d3 : this.getPositionVector();
        }
    }

    protected void p() {}

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(0.2F);
        }

        this.p();
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public boolean b(IInventory iinventory) {
        return this.inventory != iinventory;
    }
}
