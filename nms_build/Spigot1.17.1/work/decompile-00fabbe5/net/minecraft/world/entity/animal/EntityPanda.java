package net.minecraft.world.entity.animal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class EntityPanda extends EntityAnimal {

    private static final DataWatcherObject<Integer> UNHAPPY_COUNTER = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> SNEEZE_COUNTER = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> EAT_COUNTER = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Byte> MAIN_GENE_ID = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Byte> HIDDEN_GENE_ID = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Byte> DATA_ID_FLAGS = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.BYTE);
    static final PathfinderTargetCondition BREED_TARGETING = PathfinderTargetCondition.b().a(8.0D);
    private static final int FLAG_SNEEZE = 2;
    private static final int FLAG_ROLL = 4;
    private static final int FLAG_SIT = 8;
    private static final int FLAG_ON_BACK = 16;
    private static final int EAT_TICK_INTERVAL = 5;
    public static final int TOTAL_ROLL_STEPS = 32;
    private static final int TOTAL_UNHAPPY_TIME = 32;
    boolean gotBamboo;
    boolean didBite;
    public int rollCounter;
    private Vec3D rollDelta;
    private float sitAmount;
    private float sitAmountO;
    private float onBackAmount;
    private float onBackAmountO;
    private float rollAmount;
    private float rollAmountO;
    EntityPanda.g lookAtPlayerGoal;
    static final Predicate<EntityItem> PANDA_ITEMS = (entityitem) -> {
        ItemStack itemstack = entityitem.getItemStack();

        return (itemstack.a(Blocks.BAMBOO.getItem()) || itemstack.a(Blocks.CAKE.getItem())) && entityitem.isAlive() && !entityitem.q();
    };

    public EntityPanda(EntityTypes<? extends EntityPanda> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new EntityPanda.h(this);
        if (!this.isBaby()) {
            this.setCanPickupLoot(true);
        }

    }

    @Override
    public boolean g(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.g(itemstack);
    }

    public int p() {
        return (Integer) this.entityData.get(EntityPanda.UNHAPPY_COUNTER);
    }

    public void u(int i) {
        this.entityData.set(EntityPanda.UNHAPPY_COUNTER, i);
    }

    public boolean t() {
        return this.x(2);
    }

    public boolean fw() {
        return this.x(8);
    }

    public void v(boolean flag) {
        this.d(8, flag);
    }

    public boolean fx() {
        return this.x(16);
    }

    public void w(boolean flag) {
        this.d(16, flag);
    }

    public boolean fy() {
        return (Integer) this.entityData.get(EntityPanda.EAT_COUNTER) > 0;
    }

    public void x(boolean flag) {
        this.entityData.set(EntityPanda.EAT_COUNTER, flag ? 1 : 0);
    }

    private int fS() {
        return (Integer) this.entityData.get(EntityPanda.EAT_COUNTER);
    }

    private void w(int i) {
        this.entityData.set(EntityPanda.EAT_COUNTER, i);
    }

    public void y(boolean flag) {
        this.d(2, flag);
        if (!flag) {
            this.v(0);
        }

    }

    public int fE() {
        return (Integer) this.entityData.get(EntityPanda.SNEEZE_COUNTER);
    }

    public void v(int i) {
        this.entityData.set(EntityPanda.SNEEZE_COUNTER, i);
    }

    public EntityPanda.Gene getMainGene() {
        return EntityPanda.Gene.a((Byte) this.entityData.get(EntityPanda.MAIN_GENE_ID));
    }

    public void setMainGene(EntityPanda.Gene entitypanda_gene) {
        if (entitypanda_gene.a() > 6) {
            entitypanda_gene = EntityPanda.Gene.a(this.random);
        }

        this.entityData.set(EntityPanda.MAIN_GENE_ID, (byte) entitypanda_gene.a());
    }

    public EntityPanda.Gene getHiddenGene() {
        return EntityPanda.Gene.a((Byte) this.entityData.get(EntityPanda.HIDDEN_GENE_ID));
    }

    public void setHiddenGene(EntityPanda.Gene entitypanda_gene) {
        if (entitypanda_gene.a() > 6) {
            entitypanda_gene = EntityPanda.Gene.a(this.random);
        }

        this.entityData.set(EntityPanda.HIDDEN_GENE_ID, (byte) entitypanda_gene.a());
    }

    public boolean fH() {
        return this.x(4);
    }

    public void z(boolean flag) {
        this.d(4, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityPanda.UNHAPPY_COUNTER, 0);
        this.entityData.register(EntityPanda.SNEEZE_COUNTER, 0);
        this.entityData.register(EntityPanda.MAIN_GENE_ID, (byte) 0);
        this.entityData.register(EntityPanda.HIDDEN_GENE_ID, (byte) 0);
        this.entityData.register(EntityPanda.DATA_ID_FLAGS, (byte) 0);
        this.entityData.register(EntityPanda.EAT_COUNTER, 0);
    }

    private boolean x(int i) {
        return ((Byte) this.entityData.get(EntityPanda.DATA_ID_FLAGS) & i) != 0;
    }

    private void d(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityPanda.DATA_ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityPanda.DATA_ID_FLAGS, (byte) (b0 | i));
        } else {
            this.entityData.set(EntityPanda.DATA_ID_FLAGS, (byte) (b0 & ~i));
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setString("MainGene", this.getMainGene().b());
        nbttagcompound.setString("HiddenGene", this.getHiddenGene().b());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setMainGene(EntityPanda.Gene.a(nbttagcompound.getString("MainGene")));
        this.setHiddenGene(EntityPanda.Gene.a(nbttagcompound.getString("HiddenGene")));
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityPanda entitypanda = (EntityPanda) EntityTypes.PANDA.a((World) worldserver);

        if (entityageable instanceof EntityPanda) {
            entitypanda.a(this, (EntityPanda) entityageable);
        }

        entitypanda.fQ();
        return entitypanda;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new EntityPanda.i(this, 2.0D));
        this.goalSelector.a(2, new EntityPanda.d(this, 1.0D));
        this.goalSelector.a(3, new EntityPanda.b(this, 1.2000000476837158D, true));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.0D, RecipeItemStack.a(Blocks.BAMBOO.getItem()), false));
        this.goalSelector.a(6, new EntityPanda.c<>(this, EntityHuman.class, 8.0F, 2.0D, 2.0D));
        this.goalSelector.a(6, new EntityPanda.c<>(this, EntityMonster.class, 4.0F, 2.0D, 2.0D));
        this.goalSelector.a(7, new EntityPanda.k());
        this.goalSelector.a(8, new EntityPanda.f(this));
        this.goalSelector.a(8, new EntityPanda.l(this));
        this.lookAtPlayerGoal = new EntityPanda.g(this, EntityHuman.class, 6.0F);
        this.goalSelector.a(9, this.lookAtPlayerGoal);
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(12, new EntityPanda.j(this));
        this.goalSelector.a(13, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(14, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new EntityPanda.e(this, new Class[0])).a(new Class[0]));
    }

    public static AttributeProvider.Builder fI() {
        return EntityInsentient.w().a(GenericAttributes.MOVEMENT_SPEED, 0.15000000596046448D).a(GenericAttributes.ATTACK_DAMAGE, 6.0D);
    }

    public EntityPanda.Gene getActiveGene() {
        return EntityPanda.Gene.a(this.getMainGene(), this.getHiddenGene());
    }

    public boolean isLazy() {
        return this.getActiveGene() == EntityPanda.Gene.LAZY;
    }

    public boolean isWorried() {
        return this.getActiveGene() == EntityPanda.Gene.WORRIED;
    }

    public boolean isPlayful() {
        return this.getActiveGene() == EntityPanda.Gene.PLAYFUL;
    }

    public boolean fN() {
        return this.getActiveGene() == EntityPanda.Gene.BROWN;
    }

    public boolean isWeak() {
        return this.getActiveGene() == EntityPanda.Gene.WEAK;
    }

    @Override
    public boolean isAggressive() {
        return this.getActiveGene() == EntityPanda.Gene.AGGRESSIVE;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public boolean attackEntity(Entity entity) {
        this.playSound(SoundEffects.PANDA_BITE, 1.0F, 1.0F);
        if (!this.isAggressive()) {
            this.didBite = true;
        }

        return super.attackEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isWorried()) {
            if (this.level.Y() && !this.isInWater()) {
                this.v(true);
                this.x(false);
            } else if (!this.fy()) {
                this.v(false);
            }
        }

        if (this.getGoalTarget() == null) {
            this.gotBamboo = false;
            this.didBite = false;
        }

        if (this.p() > 0) {
            if (this.getGoalTarget() != null) {
                this.a((Entity) this.getGoalTarget(), 90.0F, 90.0F);
            }

            if (this.p() == 29 || this.p() == 14) {
                this.playSound(SoundEffects.PANDA_CANT_BREED, 1.0F, 1.0F);
            }

            this.u(this.p() - 1);
        }

        if (this.t()) {
            this.v(this.fE() + 1);
            if (this.fE() > 20) {
                this.y(false);
                this.fZ();
            } else if (this.fE() == 1) {
                this.playSound(SoundEffects.PANDA_PRE_SNEEZE, 1.0F, 1.0F);
            }
        }

        if (this.fH()) {
            this.fY();
        } else {
            this.rollCounter = 0;
        }

        if (this.fw()) {
            this.setXRot(0.0F);
        }

        this.fV();
        this.fT();
        this.fW();
        this.fX();
    }

    public boolean fP() {
        return this.isWorried() && this.level.Y();
    }

    private void fT() {
        if (!this.fy() && this.fw() && !this.fP() && !this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.x(true);
        } else if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() || !this.fw()) {
            this.x(false);
        }

        if (this.fy()) {
            this.fU();
            if (!this.level.isClientSide && this.fS() > 80 && this.random.nextInt(20) == 1) {
                if (this.fS() > 100 && this.m(this.getEquipment(EnumItemSlot.MAINHAND))) {
                    if (!this.level.isClientSide) {
                        this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                        this.a(GameEvent.EAT, this.cT());
                    }

                    this.v(false);
                }

                this.x(false);
                return;
            }

            this.w(this.fS() + 1);
        }

    }

    private void fU() {
        if (this.fS() % 5 == 0) {
            this.playSound(SoundEffects.PANDA_EAT, 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

            for (int i = 0; i < 6; ++i) {
                Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);

                vec3d = vec3d.a(-this.getXRot() * 0.017453292F);
                vec3d = vec3d.b(-this.getYRot() * 0.017453292F);
                double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
                Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.8D, d0, 1.0D + ((double) this.random.nextFloat() - 0.5D) * 0.4D);

                vec3d1 = vec3d1.b(-this.yBodyRot * 0.017453292F);
                vec3d1 = vec3d1.add(this.locX(), this.getHeadY() + 1.0D, this.locZ());
                this.level.addParticle(new ParticleParamItem(Particles.ITEM, this.getEquipment(EnumItemSlot.MAINHAND)), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
        }

    }

    private void fV() {
        this.sitAmountO = this.sitAmount;
        if (this.fw()) {
            this.sitAmount = Math.min(1.0F, this.sitAmount + 0.15F);
        } else {
            this.sitAmount = Math.max(0.0F, this.sitAmount - 0.19F);
        }

    }

    private void fW() {
        this.onBackAmountO = this.onBackAmount;
        if (this.fx()) {
            this.onBackAmount = Math.min(1.0F, this.onBackAmount + 0.15F);
        } else {
            this.onBackAmount = Math.max(0.0F, this.onBackAmount - 0.19F);
        }

    }

    private void fX() {
        this.rollAmountO = this.rollAmount;
        if (this.fH()) {
            this.rollAmount = Math.min(1.0F, this.rollAmount + 0.15F);
        } else {
            this.rollAmount = Math.max(0.0F, this.rollAmount - 0.19F);
        }

    }

    public float z(float f) {
        return MathHelper.h(f, this.sitAmountO, this.sitAmount);
    }

    public float A(float f) {
        return MathHelper.h(f, this.onBackAmountO, this.onBackAmount);
    }

    public float B(float f) {
        return MathHelper.h(f, this.rollAmountO, this.rollAmount);
    }

    private void fY() {
        ++this.rollCounter;
        if (this.rollCounter > 32) {
            this.z(false);
        } else {
            if (!this.level.isClientSide) {
                Vec3D vec3d = this.getMot();

                if (this.rollCounter == 1) {
                    float f = this.getYRot() * 0.017453292F;
                    float f1 = this.isBaby() ? 0.1F : 0.2F;

                    this.rollDelta = new Vec3D(vec3d.x + (double) (-MathHelper.sin(f) * f1), 0.0D, vec3d.z + (double) (MathHelper.cos(f) * f1));
                    this.setMot(this.rollDelta.add(0.0D, 0.27D, 0.0D));
                } else if ((float) this.rollCounter != 7.0F && (float) this.rollCounter != 15.0F && (float) this.rollCounter != 23.0F) {
                    this.setMot(this.rollDelta.x, vec3d.y, this.rollDelta.z);
                } else {
                    this.setMot(0.0D, this.onGround ? 0.27D : vec3d.y, 0.0D);
                }
            }

        }
    }

    private void fZ() {
        Vec3D vec3d = this.getMot();

        this.level.addParticle(Particles.SNEEZE, this.locX() - (double) (this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(this.yBodyRot * 0.017453292F), this.getHeadY() - 0.10000000149011612D, this.locZ() + (double) (this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(this.yBodyRot * 0.017453292F), vec3d.x, 0.0D, vec3d.z);
        this.playSound(SoundEffects.PANDA_SNEEZE, 1.0F, 1.0F);
        List<EntityPanda> list = this.level.a(EntityPanda.class, this.getBoundingBox().g(10.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityPanda entitypanda = (EntityPanda) iterator.next();

            if (!entitypanda.isBaby() && entitypanda.onGround && !entitypanda.isInWater() && entitypanda.fR()) {
                entitypanda.jump();
            }
        }

        if (!this.level.isClientSide() && this.random.nextInt(700) == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.a((IMaterial) Items.SLIME_BALL);
        }

    }

    @Override
    protected void b(EntityItem entityitem) {
        if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() && EntityPanda.PANDA_ITEMS.test(entityitem)) {
            this.a(entityitem);
            ItemStack itemstack = entityitem.getItemStack();

            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
            this.handDropChances[EnumItemSlot.MAINHAND.b()] = 2.0F;
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        this.v(false);
        return super.damageEntity(damagesource, f);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setMainGene(EntityPanda.Gene.a(this.random));
        this.setHiddenGene(EntityPanda.Gene.a(this.random));
        this.fQ();
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(0.2F);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public void a(EntityPanda entitypanda, @Nullable EntityPanda entitypanda1) {
        if (entitypanda1 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(entitypanda.ga());
                this.setHiddenGene(EntityPanda.Gene.a(this.random));
            } else {
                this.setMainGene(EntityPanda.Gene.a(this.random));
                this.setHiddenGene(entitypanda.ga());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene(entitypanda.ga());
            this.setHiddenGene(entitypanda1.ga());
        } else {
            this.setMainGene(entitypanda1.ga());
            this.setHiddenGene(entitypanda.ga());
        }

        if (this.random.nextInt(32) == 0) {
            this.setMainGene(EntityPanda.Gene.a(this.random));
        }

        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(EntityPanda.Gene.a(this.random));
        }

    }

    private EntityPanda.Gene ga() {
        return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
    }

    public void fQ() {
        if (this.isWeak()) {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        }

        if (this.isLazy()) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.07000000029802322D);
        }

    }

    void gb() {
        if (!this.isInWater()) {
            this.u(0.0F);
            this.getNavigation().o();
            this.v(true);
        }

    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (this.fP()) {
            return EnumInteractionResult.PASS;
        } else if (this.fx()) {
            this.w(false);
            return EnumInteractionResult.a(this.level.isClientSide);
        } else if (this.isBreedItem(itemstack)) {
            if (this.getGoalTarget() != null) {
                this.gotBamboo = true;
            }

            if (this.isBaby()) {
                this.a(entityhuman, enumhand, itemstack);
                this.setAge((int) ((float) (-this.getAge() / 20) * 0.1F), true);
                this.a(GameEvent.MOB_INTERACT, this.cT());
            } else if (!this.level.isClientSide && this.getAge() == 0 && this.fz()) {
                this.a(entityhuman, enumhand, itemstack);
                this.g(entityhuman);
                this.a(GameEvent.MOB_INTERACT, this.cT());
            } else {
                if (this.level.isClientSide || this.fw() || this.isInWater()) {
                    return EnumInteractionResult.PASS;
                }

                this.gb();
                this.x(true);
                ItemStack itemstack1 = this.getEquipment(EnumItemSlot.MAINHAND);

                if (!itemstack1.isEmpty() && !entityhuman.getAbilities().instabuild) {
                    this.b(itemstack1);
                }

                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(itemstack.getItem(), 1));
                this.a(entityhuman, enumhand, itemstack);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isAggressive() ? SoundEffects.PANDA_AGGRESSIVE_AMBIENT : (this.isWorried() ? SoundEffects.PANDA_WORRIED_AMBIENT : SoundEffects.PANDA_AMBIENT);
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PANDA_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return itemstack.a(Blocks.BAMBOO.getItem());
    }

    private boolean m(ItemStack itemstack) {
        return this.isBreedItem(itemstack) || itemstack.a(Blocks.CAKE.getItem());
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PANDA_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PANDA_HURT;
    }

    public boolean fR() {
        return !this.fx() && !this.fP() && !this.fy() && !this.fH() && !this.fw();
    }

    private static class h extends ControllerMove {

        private final EntityPanda panda;

        public h(EntityPanda entitypanda) {
            super(entitypanda);
            this.panda = entitypanda;
        }

        @Override
        public void a() {
            if (this.panda.fR()) {
                super.a();
            }
        }
    }

    public static enum Gene {

        NORMAL(0, "normal", false), LAZY(1, "lazy", false), WORRIED(2, "worried", false), PLAYFUL(3, "playful", false), BROWN(4, "brown", true), WEAK(5, "weak", true), AGGRESSIVE(6, "aggressive", false);

        private static final EntityPanda.Gene[] BY_ID = (EntityPanda.Gene[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EntityPanda.Gene::a)).toArray((i) -> {
            return new EntityPanda.Gene[i];
        });
        private static final int MAX_GENE = 6;
        private final int id;
        private final String name;
        private final boolean isRecessive;

        private Gene(int i, String s, boolean flag) {
            this.id = i;
            this.name = s;
            this.isRecessive = flag;
        }

        public int a() {
            return this.id;
        }

        public String b() {
            return this.name;
        }

        public boolean isRecessive() {
            return this.isRecessive;
        }

        static EntityPanda.Gene a(EntityPanda.Gene entitypanda_gene, EntityPanda.Gene entitypanda_gene1) {
            return entitypanda_gene.isRecessive() ? (entitypanda_gene == entitypanda_gene1 ? entitypanda_gene : EntityPanda.Gene.NORMAL) : entitypanda_gene;
        }

        public static EntityPanda.Gene a(int i) {
            if (i < 0 || i >= EntityPanda.Gene.BY_ID.length) {
                i = 0;
            }

            return EntityPanda.Gene.BY_ID[i];
        }

        public static EntityPanda.Gene a(String s) {
            EntityPanda.Gene[] aentitypanda_gene = values();
            int i = aentitypanda_gene.length;

            for (int j = 0; j < i; ++j) {
                EntityPanda.Gene entitypanda_gene = aentitypanda_gene[j];

                if (entitypanda_gene.name.equals(s)) {
                    return entitypanda_gene;
                }
            }

            return EntityPanda.Gene.NORMAL;
        }

        public static EntityPanda.Gene a(Random random) {
            int i = random.nextInt(16);

            return i == 0 ? EntityPanda.Gene.LAZY : (i == 1 ? EntityPanda.Gene.WORRIED : (i == 2 ? EntityPanda.Gene.PLAYFUL : (i == 4 ? EntityPanda.Gene.AGGRESSIVE : (i < 9 ? EntityPanda.Gene.WEAK : (i < 11 ? EntityPanda.Gene.BROWN : EntityPanda.Gene.NORMAL)))));
        }
    }

    private static class i extends PathfinderGoalPanic {

        private final EntityPanda panda;

        public i(EntityPanda entitypanda, double d0) {
            super(entitypanda, d0);
            this.panda = entitypanda;
        }

        @Override
        public boolean a() {
            if (!this.panda.isBurning()) {
                return false;
            } else {
                BlockPosition blockposition = this.a(this.mob.level, this.mob, 5, 4);

                if (blockposition != null) {
                    this.posX = (double) blockposition.getX();
                    this.posY = (double) blockposition.getY();
                    this.posZ = (double) blockposition.getZ();
                    return true;
                } else {
                    return this.g();
                }
            }
        }

        @Override
        public boolean b() {
            if (this.panda.fw()) {
                this.panda.getNavigation().o();
                return false;
            } else {
                return super.b();
            }
        }
    }

    private class d extends PathfinderGoalBreed {

        private final EntityPanda panda;
        private int unhappyCooldown;

        public d(EntityPanda entitypanda, double d0) {
            super(entitypanda, d0);
            this.panda = entitypanda;
        }

        @Override
        public boolean a() {
            if (super.a() && this.panda.p() == 0) {
                if (!this.h()) {
                    if (this.unhappyCooldown <= this.panda.tickCount) {
                        this.panda.u(32);
                        this.unhappyCooldown = this.panda.tickCount + 600;
                        if (this.panda.doAITick()) {
                            EntityHuman entityhuman = this.level.a(EntityPanda.BREED_TARGETING, (EntityLiving) this.panda);

                            this.panda.lookAtPlayerGoal.a((EntityLiving) entityhuman);
                        }
                    }

                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        private boolean h() {
            BlockPosition blockposition = this.panda.getChunkCoordinates();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 8; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, k, i, l);
                            if (this.level.getType(blockposition_mutableblockposition).a(Blocks.BAMBOO)) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    private static class b extends PathfinderGoalMeleeAttack {

        private final EntityPanda panda;

        public b(EntityPanda entitypanda, double d0, boolean flag) {
            super(entitypanda, d0, flag);
            this.panda = entitypanda;
        }

        @Override
        public boolean a() {
            return this.panda.fR() && super.a();
        }
    }

    private static class c<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityPanda panda;

        public c(EntityPanda entitypanda, Class<T> oclass, float f, double d0, double d1) {
            Predicate predicate = IEntitySelector.NO_SPECTATORS;

            Objects.requireNonNull(predicate);
            super(entitypanda, oclass, f, d0, d1, predicate::test);
            this.panda = entitypanda;
        }

        @Override
        public boolean a() {
            return this.panda.isWorried() && this.panda.fR() && super.a();
        }
    }

    private class k extends PathfinderGoal {

        private int cooldown;

        public k() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (this.cooldown <= EntityPanda.this.tickCount && !EntityPanda.this.isBaby() && !EntityPanda.this.isInWater() && EntityPanda.this.fR() && EntityPanda.this.p() <= 0) {
                List<EntityItem> list = EntityPanda.this.level.a(EntityItem.class, EntityPanda.this.getBoundingBox().grow(6.0D, 6.0D, 6.0D), EntityPanda.PANDA_ITEMS);

                return !list.isEmpty() || !EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
            } else {
                return false;
            }
        }

        @Override
        public boolean b() {
            return !EntityPanda.this.isInWater() && (EntityPanda.this.isLazy() || EntityPanda.this.random.nextInt(600) != 1) ? EntityPanda.this.random.nextInt(2000) != 1 : false;
        }

        @Override
        public void e() {
            if (!EntityPanda.this.fw() && !EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.gb();
            }

        }

        @Override
        public void c() {
            List<EntityItem> list = EntityPanda.this.level.a(EntityItem.class, EntityPanda.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityPanda.PANDA_ITEMS);

            if (!list.isEmpty() && EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            } else if (!EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.gb();
            }

            this.cooldown = 0;
        }

        @Override
        public void d() {
            ItemStack itemstack = EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                EntityPanda.this.b(itemstack);
                EntityPanda.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                int i = EntityPanda.this.isLazy() ? EntityPanda.this.random.nextInt(50) + 10 : EntityPanda.this.random.nextInt(150) + 10;

                this.cooldown = EntityPanda.this.tickCount + i * 20;
            }

            EntityPanda.this.v(false);
        }
    }

    private static class f extends PathfinderGoal {

        private final EntityPanda panda;
        private int cooldown;

        public f(EntityPanda entitypanda) {
            this.panda = entitypanda;
        }

        @Override
        public boolean a() {
            return this.cooldown < this.panda.tickCount && this.panda.isLazy() && this.panda.fR() && this.panda.random.nextInt(400) == 1;
        }

        @Override
        public boolean b() {
            return !this.panda.isInWater() && (this.panda.isLazy() || this.panda.random.nextInt(600) != 1) ? this.panda.random.nextInt(2000) != 1 : false;
        }

        @Override
        public void c() {
            this.panda.w(true);
            this.cooldown = 0;
        }

        @Override
        public void d() {
            this.panda.w(false);
            this.cooldown = this.panda.tickCount + 200;
        }
    }

    private static class l extends PathfinderGoal {

        private final EntityPanda panda;

        public l(EntityPanda entitypanda) {
            this.panda = entitypanda;
        }

        @Override
        public boolean a() {
            return this.panda.isBaby() && this.panda.fR() ? (this.panda.isWeak() && this.panda.random.nextInt(500) == 1 ? true : this.panda.random.nextInt(6000) == 1) : false;
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void c() {
            this.panda.y(true);
        }
    }

    private static class g extends PathfinderGoalLookAtPlayer {

        private final EntityPanda panda;

        public g(EntityPanda entitypanda, Class<? extends EntityLiving> oclass, float f) {
            super(entitypanda, oclass, f);
            this.panda = entitypanda;
        }

        public void a(EntityLiving entityliving) {
            this.lookAt = entityliving;
        }

        @Override
        public boolean b() {
            return this.lookAt != null && super.b();
        }

        @Override
        public boolean a() {
            if (this.mob.getRandom().nextFloat() >= this.probability) {
                return false;
            } else {
                if (this.lookAt == null) {
                    if (this.lookAtType == EntityHuman.class) {
                        this.lookAt = this.mob.level.a(this.lookAtContext, this.mob, this.mob.locX(), this.mob.getHeadY(), this.mob.locZ());
                    } else {
                        this.lookAt = this.mob.level.a(this.mob.level.a(this.lookAtType, this.mob.getBoundingBox().grow((double) this.lookDistance, 3.0D, (double) this.lookDistance), (entityliving) -> {
                            return true;
                        }), this.lookAtContext, (EntityLiving) this.mob, this.mob.locX(), this.mob.getHeadY(), this.mob.locZ());
                    }
                }

                return this.panda.fR() && this.lookAt != null;
            }
        }

        @Override
        public void e() {
            if (this.lookAt != null) {
                super.e();
            }

        }
    }

    private static class j extends PathfinderGoal {

        private final EntityPanda panda;

        public j(EntityPanda entitypanda) {
            this.panda = entitypanda;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
        }

        @Override
        public boolean a() {
            if ((this.panda.isBaby() || this.panda.isPlayful()) && this.panda.onGround) {
                if (!this.panda.fR()) {
                    return false;
                } else {
                    float f = this.panda.getYRot() * 0.017453292F;
                    int i = 0;
                    int j = 0;
                    float f1 = -MathHelper.sin(f);
                    float f2 = MathHelper.cos(f);

                    if ((double) Math.abs(f1) > 0.5D) {
                        i = (int) ((float) i + f1 / Math.abs(f1));
                    }

                    if ((double) Math.abs(f2) > 0.5D) {
                        j = (int) ((float) j + f2 / Math.abs(f2));
                    }

                    return this.panda.level.getType(this.panda.getChunkCoordinates().c(i, -1, j)).isAir() ? true : (this.panda.isPlayful() && this.panda.random.nextInt(60) == 1 ? true : this.panda.random.nextInt(500) == 1);
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void c() {
            this.panda.z(true);
        }

        @Override
        public boolean C_() {
            return false;
        }
    }

    private static class e extends PathfinderGoalHurtByTarget {

        private final EntityPanda panda;

        public e(EntityPanda entitypanda, Class<?>... aclass) {
            super(entitypanda, aclass);
            this.panda = entitypanda;
        }

        @Override
        public boolean b() {
            if (!this.panda.gotBamboo && !this.panda.didBite) {
                return super.b();
            } else {
                this.panda.setGoalTarget((EntityLiving) null);
                return false;
            }
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPanda && ((EntityPanda) entityinsentient).isAggressive()) {
                entityinsentient.setGoalTarget(entityliving);
            }

        }
    }
}
