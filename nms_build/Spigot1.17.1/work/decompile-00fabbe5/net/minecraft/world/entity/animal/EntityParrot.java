package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMoveFlying;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowOwner;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPerch;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomFly;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSit;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityParrot extends EntityPerchable implements EntityBird {

    private static final DataWatcherObject<Integer> DATA_VARIANT_ID = DataWatcher.a(EntityParrot.class, DataWatcherRegistry.INT);
    private static final Predicate<EntityInsentient> NOT_PARROT_PREDICATE = new Predicate<EntityInsentient>() {
        public boolean test(@Nullable EntityInsentient entityinsentient) {
            return entityinsentient != null && EntityParrot.MOB_SOUND_MAP.containsKey(entityinsentient.getEntityType());
        }
    };
    private static final Item POISONOUS_FOOD = Items.COOKIE;
    private static final Set<Item> TAME_FOOD = Sets.newHashSet(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    private static final int VARIANTS = 5;
    static final Map<EntityTypes<?>, SoundEffect> MOB_SOUND_MAP = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(EntityTypes.BLAZE, SoundEffects.PARROT_IMITATE_BLAZE);
        hashmap.put(EntityTypes.CAVE_SPIDER, SoundEffects.PARROT_IMITATE_SPIDER);
        hashmap.put(EntityTypes.CREEPER, SoundEffects.PARROT_IMITATE_CREEPER);
        hashmap.put(EntityTypes.DROWNED, SoundEffects.PARROT_IMITATE_DROWNED);
        hashmap.put(EntityTypes.ELDER_GUARDIAN, SoundEffects.PARROT_IMITATE_ELDER_GUARDIAN);
        hashmap.put(EntityTypes.ENDER_DRAGON, SoundEffects.PARROT_IMITATE_ENDER_DRAGON);
        hashmap.put(EntityTypes.ENDERMITE, SoundEffects.PARROT_IMITATE_ENDERMITE);
        hashmap.put(EntityTypes.EVOKER, SoundEffects.PARROT_IMITATE_EVOKER);
        hashmap.put(EntityTypes.GHAST, SoundEffects.PARROT_IMITATE_GHAST);
        hashmap.put(EntityTypes.GUARDIAN, SoundEffects.PARROT_IMITATE_GUARDIAN);
        hashmap.put(EntityTypes.HOGLIN, SoundEffects.PARROT_IMITATE_HOGLIN);
        hashmap.put(EntityTypes.HUSK, SoundEffects.PARROT_IMITATE_HUSK);
        hashmap.put(EntityTypes.ILLUSIONER, SoundEffects.PARROT_IMITATE_ILLUSIONER);
        hashmap.put(EntityTypes.MAGMA_CUBE, SoundEffects.PARROT_IMITATE_MAGMA_CUBE);
        hashmap.put(EntityTypes.PHANTOM, SoundEffects.PARROT_IMITATE_PHANTOM);
        hashmap.put(EntityTypes.PIGLIN, SoundEffects.PARROT_IMITATE_PIGLIN);
        hashmap.put(EntityTypes.PIGLIN_BRUTE, SoundEffects.PARROT_IMITATE_PIGLIN_BRUTE);
        hashmap.put(EntityTypes.PILLAGER, SoundEffects.PARROT_IMITATE_PILLAGER);
        hashmap.put(EntityTypes.RAVAGER, SoundEffects.PARROT_IMITATE_RAVAGER);
        hashmap.put(EntityTypes.SHULKER, SoundEffects.PARROT_IMITATE_SHULKER);
        hashmap.put(EntityTypes.SILVERFISH, SoundEffects.PARROT_IMITATE_SILVERFISH);
        hashmap.put(EntityTypes.SKELETON, SoundEffects.PARROT_IMITATE_SKELETON);
        hashmap.put(EntityTypes.SLIME, SoundEffects.PARROT_IMITATE_SLIME);
        hashmap.put(EntityTypes.SPIDER, SoundEffects.PARROT_IMITATE_SPIDER);
        hashmap.put(EntityTypes.STRAY, SoundEffects.PARROT_IMITATE_STRAY);
        hashmap.put(EntityTypes.VEX, SoundEffects.PARROT_IMITATE_VEX);
        hashmap.put(EntityTypes.VINDICATOR, SoundEffects.PARROT_IMITATE_VINDICATOR);
        hashmap.put(EntityTypes.WITCH, SoundEffects.PARROT_IMITATE_WITCH);
        hashmap.put(EntityTypes.WITHER, SoundEffects.PARROT_IMITATE_WITHER);
        hashmap.put(EntityTypes.WITHER_SKELETON, SoundEffects.PARROT_IMITATE_WITHER_SKELETON);
        hashmap.put(EntityTypes.ZOGLIN, SoundEffects.PARROT_IMITATE_ZOGLIN);
        hashmap.put(EntityTypes.ZOMBIE, SoundEffects.PARROT_IMITATE_ZOMBIE);
        hashmap.put(EntityTypes.ZOMBIE_VILLAGER, SoundEffects.PARROT_IMITATE_ZOMBIE_VILLAGER);
    });
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private boolean partyParrot;
    private BlockPosition jukebox;

    public EntityParrot(EntityTypes<? extends EntityParrot> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new ControllerMoveFlying(this, 10, false);
        this.a(PathType.DANGER_FIRE, -1.0F);
        this.a(PathType.DAMAGE_FIRE, -1.0F);
        this.a(PathType.COCOA, -1.0F);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setVariant(this.random.nextInt(5));
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(false);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(2, new PathfinderGoalSit(this));
        this.goalSelector.a(2, new PathfinderGoalFollowOwner(this, 1.0D, 5.0F, 1.0F, true));
        this.goalSelector.a(2, new PathfinderGoalRandomFly(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalPerch(this));
        this.goalSelector.a(3, new PathfinderGoalFollowEntity(this, 1.0D, 3.0F, 7.0F));
    }

    public static AttributeProvider.Builder fE() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 6.0D).a(GenericAttributes.FLYING_SPEED, 0.4000000059604645D).a(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    @Override
    protected NavigationAbstract a(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world);

        navigationflying.a(false);
        navigationflying.d(true);
        navigationflying.b(true);
        return navigationflying;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.6F;
    }

    @Override
    public void movementTick() {
        if (this.jukebox == null || !this.jukebox.a((IPosition) this.getPositionVector(), 3.46D) || !this.level.getType(this.jukebox).a(Blocks.JUKEBOX)) {
            this.partyParrot = false;
            this.jukebox = null;
        }

        if (this.level.random.nextInt(400) == 0) {
            a(this.level, (Entity) this);
        }

        super.movementTick();
        this.fI();
    }

    @Override
    public void a(BlockPosition blockposition, boolean flag) {
        this.jukebox = blockposition;
        this.partyParrot = flag;
    }

    public boolean fF() {
        return this.partyParrot;
    }

    private void fI() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
        this.flapSpeed = MathHelper.a(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping = (float) ((double) this.flapping * 0.9D);
        Vec3D vec3d = this.getMot();

        if (!this.onGround && vec3d.y < 0.0D) {
            this.setMot(vec3d.d(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;
    }

    public static boolean a(World world, Entity entity) {
        if (entity.isAlive() && !entity.isSilent() && world.random.nextInt(2) == 0) {
            List<EntityInsentient> list = world.a(EntityInsentient.class, entity.getBoundingBox().g(20.0D), EntityParrot.NOT_PARROT_PREDICATE);

            if (!list.isEmpty()) {
                EntityInsentient entityinsentient = (EntityInsentient) list.get(world.random.nextInt(list.size()));

                if (!entityinsentient.isSilent()) {
                    SoundEffect soundeffect = b(entityinsentient.getEntityType());

                    world.playSound((EntityHuman) null, entity.locX(), entity.locY(), entity.locZ(), soundeffect, entity.getSoundCategory(), 0.7F, a(world.random));
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isTamed() && EntityParrot.TAME_FOOD.contains(itemstack.getItem())) {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.subtract(1);
            }

            if (!this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.PARROT_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.level.isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame(entityhuman);
                    this.level.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else if (itemstack.a(EntityParrot.POISONOUS_FOOD)) {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.subtract(1);
            }

            this.addEffect(new MobEffect(MobEffects.POISON, 900));
            if (entityhuman.isCreative() || !this.isInvulnerable()) {
                this.damageEntity(DamageSource.playerAttack(entityhuman), Float.MAX_VALUE);
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else if (!this.fK() && this.isTamed() && this.j((EntityLiving) entityhuman)) {
            if (!this.level.isClientSide) {
                this.setWillSit(!this.isWillSit());
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return false;
    }

    public static boolean c(EntityTypes<EntityParrot> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        IBlockData iblockdata = generatoraccess.getType(blockposition.down());

        return (iblockdata.a((Tag) TagsBlock.LEAVES) || iblockdata.a(Blocks.GRASS_BLOCK) || iblockdata.a((Tag) TagsBlock.LOGS) || iblockdata.a(Blocks.AIR)) && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return null;
    }

    @Override
    public boolean attackEntity(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
    }

    @Nullable
    @Override
    public SoundEffect getSoundAmbient() {
        return a(this.level, this.level.random);
    }

    public static SoundEffect a(World world, Random random) {
        if (world.getDifficulty() != EnumDifficulty.PEACEFUL && random.nextInt(1000) == 0) {
            List<EntityTypes<?>> list = Lists.newArrayList(EntityParrot.MOB_SOUND_MAP.keySet());

            return b((EntityTypes) list.get(random.nextInt(list.size())));
        } else {
            return SoundEffects.PARROT_AMBIENT;
        }
    }

    private static SoundEffect b(EntityTypes<?> entitytypes) {
        return (SoundEffect) EntityParrot.MOB_SOUND_MAP.getOrDefault(entitytypes, SoundEffects.PARROT_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PARROT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PARROT_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PARROT_STEP, 0.15F, 1.0F);
    }

    @Override
    protected boolean aF() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void aE() {
        this.playSound(SoundEffects.PARROT_FLY, 0.15F, 1.0F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public float ep() {
        return a(this.random);
    }

    public static float a(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void A(Entity entity) {
        if (!(entity instanceof EntityHuman)) {
            super.A(entity);
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.setWillSit(false);
            return super.damageEntity(damagesource, f);
        }
    }

    public int getVariant() {
        return MathHelper.clamp((Integer) this.entityData.get(EntityParrot.DATA_VARIANT_ID), 0, 4);
    }

    public void setVariant(int i) {
        this.entityData.set(EntityParrot.DATA_VARIANT_ID, i);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityParrot.DATA_VARIANT_ID, 0);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
    }

    @Override
    public boolean fK() {
        return !this.onGround;
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.5F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }
}
