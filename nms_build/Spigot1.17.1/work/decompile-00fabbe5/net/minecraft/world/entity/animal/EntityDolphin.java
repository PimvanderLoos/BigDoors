package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreath;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowBoat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomSwim;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWater;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWaterJump;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityGuardian;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;

public class EntityDolphin extends EntityWaterAnimal {

    private static final DataWatcherObject<BlockPosition> TREASURE_POS = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.BLOCK_POS);
    private static final DataWatcherObject<Boolean> GOT_FISH = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> MOISTNESS_LEVEL = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.INT);
    static final PathfinderTargetCondition SWIM_WITH_PLAYER_TARGETING = PathfinderTargetCondition.b().a(10.0D).d();
    public static final int TOTAL_AIR_SUPPLY = 4800;
    private static final int TOTAL_MOISTNESS_LEVEL = 2400;
    public static final Predicate<EntityItem> ALLOWED_ITEMS = (entityitem) -> {
        return !entityitem.q() && entityitem.isAlive() && entityitem.isInWater();
    };

    public EntityDolphin(EntityTypes<? extends EntityDolphin> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setCanPickupLoot(true);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setAirTicks(this.bS());
        this.setXRot(0.0F);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public boolean dr() {
        return false;
    }

    @Override
    protected void a(int i) {}

    public void setTreasurePos(BlockPosition blockposition) {
        this.entityData.set(EntityDolphin.TREASURE_POS, blockposition);
    }

    public BlockPosition getTreasurePos() {
        return (BlockPosition) this.entityData.get(EntityDolphin.TREASURE_POS);
    }

    public boolean gotFish() {
        return (Boolean) this.entityData.get(EntityDolphin.GOT_FISH);
    }

    public void setGotFish(boolean flag) {
        this.entityData.set(EntityDolphin.GOT_FISH, flag);
    }

    public int getMoistness() {
        return (Integer) this.entityData.get(EntityDolphin.MOISTNESS_LEVEL);
    }

    public void setMoistness(int i) {
        this.entityData.set(EntityDolphin.MOISTNESS_LEVEL, i);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityDolphin.TREASURE_POS, BlockPosition.ZERO);
        this.entityData.register(EntityDolphin.GOT_FISH, false);
        this.entityData.register(EntityDolphin.MOISTNESS_LEVEL, 2400);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("TreasurePosX", this.getTreasurePos().getX());
        nbttagcompound.setInt("TreasurePosY", this.getTreasurePos().getY());
        nbttagcompound.setInt("TreasurePosZ", this.getTreasurePos().getZ());
        nbttagcompound.setBoolean("GotFish", this.gotFish());
        nbttagcompound.setInt("Moistness", this.getMoistness());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("TreasurePosX");
        int j = nbttagcompound.getInt("TreasurePosY");
        int k = nbttagcompound.getInt("TreasurePosZ");

        this.setTreasurePos(new BlockPosition(i, j, k));
        super.loadData(nbttagcompound);
        this.setGotFish(nbttagcompound.getBoolean("GotFish"));
        this.setMoistness(nbttagcompound.getInt("Moistness"));
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalBreath(this));
        this.goalSelector.a(0, new PathfinderGoalWater(this));
        this.goalSelector.a(1, new EntityDolphin.a(this));
        this.goalSelector.a(2, new EntityDolphin.b(this, 4.0D));
        this.goalSelector.a(4, new PathfinderGoalRandomSwim(this, 1.0D, 10));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(5, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(5, new PathfinderGoalWaterJump(this, 10));
        this.goalSelector.a(6, new PathfinderGoalMeleeAttack(this, 1.2000000476837158D, true));
        this.goalSelector.a(8, new EntityDolphin.c());
        this.goalSelector.a(8, new PathfinderGoalFollowBoat(this));
        this.goalSelector.a(9, new PathfinderGoalAvoidTarget<>(this, EntityGuardian.class, 8.0F, 1.0D, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityGuardian.class})).a());
    }

    public static AttributeProvider.Builder fw() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.MOVEMENT_SPEED, 1.2000000476837158D).a(GenericAttributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.b(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.a((EntityLiving) this, entity);
            this.playSound(SoundEffects.DOLPHIN_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public int bS() {
        return 4800;
    }

    @Override
    protected int n(int i) {
        return this.bS();
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.3F;
    }

    @Override
    public int eZ() {
        return 1;
    }

    @Override
    public int fa() {
        return 1;
    }

    @Override
    protected boolean l(Entity entity) {
        return true;
    }

    @Override
    public boolean g(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.g(itemstack);
    }

    @Override
    protected void b(EntityItem entityitem) {
        if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
            ItemStack itemstack = entityitem.getItemStack();

            if (this.canPickup(itemstack)) {
                this.a(entityitem);
                this.setSlot(EnumItemSlot.MAINHAND, itemstack);
                this.handDropChances[EnumItemSlot.MAINHAND.b()] = 2.0F;
                this.receive(entityitem, itemstack.getCount());
                entityitem.die();
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isNoAI()) {
            this.setAirTicks(this.bS());
        } else {
            if (this.aN()) {
                this.setMoistness(2400);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0) {
                    this.damageEntity(DamageSource.DRY_OUT, 1.0F);
                }

                if (this.onGround) {
                    this.setMot(this.getMot().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
                    this.setYRot(this.random.nextFloat() * 360.0F);
                    this.onGround = false;
                    this.hasImpulse = true;
                }
            }

            if (this.level.isClientSide && this.isInWater() && this.getMot().g() > 0.03D) {
                Vec3D vec3d = this.e(0.0F);
                float f = MathHelper.cos(this.getYRot() * 0.017453292F) * 0.3F;
                float f1 = MathHelper.sin(this.getYRot() * 0.017453292F) * 0.3F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for (int i = 0; i < 2; ++i) {
                    this.level.addParticle(Particles.DOLPHIN, this.locX() - vec3d.x * (double) f2 + (double) f, this.locY() - vec3d.y, this.locZ() - vec3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(Particles.DOLPHIN, this.locX() - vec3d.x * (double) f2 - (double) f, this.locY() - vec3d.y, this.locZ() - vec3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
    }

    @Override
    public void a(byte b0) {
        if (b0 == 38) {
            this.a((ParticleParam) Particles.HAPPY_VILLAGER);
        } else {
            super.a(b0);
        }

    }

    private void a(ParticleParam particleparam) {
        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.01D;
            double d1 = this.random.nextGaussian() * 0.01D;
            double d2 = this.random.nextGaussian() * 0.01D;

            this.level.addParticle(particleparam, this.d(1.0D), this.da() + 0.2D, this.g(1.0D), d0, d1, d2);
        }

    }

    @Override
    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && itemstack.a((Tag) TagsItem.FISHES)) {
            if (!this.level.isClientSide) {
                this.playSound(SoundEffects.DOLPHIN_EAT, 1.0F, 1.0F);
            }

            this.setGotFish(true);
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.subtract(1);
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    public static boolean b(EntityTypes<EntityDolphin> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (blockposition.getY() > 45 && blockposition.getY() < generatoraccess.getSeaLevel()) {
            Optional<ResourceKey<BiomeBase>> optional = generatoraccess.j(blockposition);

            return (!Objects.equals(optional, Optional.of(Biomes.OCEAN)) || !Objects.equals(optional, Optional.of(Biomes.DEEP_OCEAN))) && generatoraccess.getFluid(blockposition).a((Tag) TagsFluid.WATER);
        } else {
            return false;
        }
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.DOLPHIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.DOLPHIN_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isInWater() ? SoundEffects.DOLPHIN_AMBIENT_WATER : SoundEffects.DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.DOLPHIN_SWIM;
    }

    protected boolean fx() {
        BlockPosition blockposition = this.getNavigation().h();

        return blockposition != null ? blockposition.a((IPosition) this.getPositionVector(), 12.0D) : false;
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater()) {
            this.a(this.ew(), vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (this.getGoalTarget() == null) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.g(vec3d);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    private static class a extends PathfinderGoal {

        private final EntityDolphin dolphin;
        private boolean stuck;

        a(EntityDolphin entitydolphin) {
            this.dolphin = entitydolphin;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean C_() {
            return false;
        }

        @Override
        public boolean a() {
            return this.dolphin.gotFish() && this.dolphin.getAirTicks() >= 100;
        }

        @Override
        public boolean b() {
            BlockPosition blockposition = this.dolphin.getTreasurePos();

            return !(new BlockPosition((double) blockposition.getX(), this.dolphin.locY(), (double) blockposition.getZ())).a((IPosition) this.dolphin.getPositionVector(), 4.0D) && !this.stuck && this.dolphin.getAirTicks() >= 100;
        }

        @Override
        public void c() {
            if (this.dolphin.level instanceof WorldServer) {
                WorldServer worldserver = (WorldServer) this.dolphin.level;

                this.stuck = false;
                this.dolphin.getNavigation().o();
                BlockPosition blockposition = this.dolphin.getChunkCoordinates();
                StructureGenerator<?> structuregenerator = (double) worldserver.random.nextFloat() >= 0.5D ? StructureGenerator.OCEAN_RUIN : StructureGenerator.SHIPWRECK;
                BlockPosition blockposition1 = worldserver.a(structuregenerator, blockposition, 50, false);

                if (blockposition1 == null) {
                    StructureGenerator<?> structuregenerator1 = structuregenerator.equals(StructureGenerator.OCEAN_RUIN) ? StructureGenerator.SHIPWRECK : StructureGenerator.OCEAN_RUIN;
                    BlockPosition blockposition2 = worldserver.a(structuregenerator1, blockposition, 50, false);

                    if (blockposition2 == null) {
                        this.stuck = true;
                        return;
                    }

                    this.dolphin.setTreasurePos(blockposition2);
                } else {
                    this.dolphin.setTreasurePos(blockposition1);
                }

                worldserver.broadcastEntityEffect(this.dolphin, (byte) 38);
            }
        }

        @Override
        public void d() {
            BlockPosition blockposition = this.dolphin.getTreasurePos();

            if ((new BlockPosition((double) blockposition.getX(), this.dolphin.locY(), (double) blockposition.getZ())).a((IPosition) this.dolphin.getPositionVector(), 4.0D) || this.stuck) {
                this.dolphin.setGotFish(false);
            }

        }

        @Override
        public void e() {
            World world = this.dolphin.level;

            if (this.dolphin.fx() || this.dolphin.getNavigation().m()) {
                Vec3D vec3d = Vec3D.a((BaseBlockPosition) this.dolphin.getTreasurePos());
                Vec3D vec3d1 = DefaultRandomPos.a(this.dolphin, 16, 1, vec3d, 0.39269909262657166D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.a(this.dolphin, 8, 4, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 != null) {
                    BlockPosition blockposition = new BlockPosition(vec3d1);

                    if (!world.getFluid(blockposition).a((Tag) TagsFluid.WATER) || !world.getType(blockposition).a((IBlockAccess) world, blockposition, PathMode.WATER)) {
                        vec3d1 = DefaultRandomPos.a(this.dolphin, 8, 5, vec3d, 1.5707963705062866D);
                    }
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.dolphin.getControllerLook().a(vec3d1.x, vec3d1.y, vec3d1.z, (float) (this.dolphin.fa() + 20), (float) this.dolphin.eZ());
                this.dolphin.getNavigation().a(vec3d1.x, vec3d1.y, vec3d1.z, 1.3D);
                if (world.random.nextInt(80) == 0) {
                    world.broadcastEntityEffect(this.dolphin, (byte) 38);
                }
            }

        }
    }

    private static class b extends PathfinderGoal {

        private final EntityDolphin dolphin;
        private final double speedModifier;
        private EntityHuman player;

        b(EntityDolphin entitydolphin, double d0) {
            this.dolphin = entitydolphin;
            this.speedModifier = d0;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            this.player = this.dolphin.level.a(EntityDolphin.SWIM_WITH_PLAYER_TARGETING, (EntityLiving) this.dolphin);
            return this.player == null ? false : this.player.isSwimming() && this.dolphin.getGoalTarget() != this.player;
        }

        @Override
        public boolean b() {
            return this.player != null && this.player.isSwimming() && this.dolphin.f((Entity) this.player) < 256.0D;
        }

        @Override
        public void c() {
            this.player.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
        }

        @Override
        public void d() {
            this.player = null;
            this.dolphin.getNavigation().o();
        }

        @Override
        public void e() {
            this.dolphin.getControllerLook().a(this.player, (float) (this.dolphin.fa() + 20), (float) this.dolphin.eZ());
            if (this.dolphin.f((Entity) this.player) < 6.25D) {
                this.dolphin.getNavigation().o();
            } else {
                this.dolphin.getNavigation().a((Entity) this.player, this.speedModifier);
            }

            if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
                this.player.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
            }

        }
    }

    private class c extends PathfinderGoal {

        private int cooldown;

        c() {}

        @Override
        public boolean a() {
            if (this.cooldown > EntityDolphin.this.tickCount) {
                return false;
            } else {
                List<EntityItem> list = EntityDolphin.this.level.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.ALLOWED_ITEMS);

                return !list.isEmpty() || !EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
            }
        }

        @Override
        public void c() {
            List<EntityItem> list = EntityDolphin.this.level.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.ALLOWED_ITEMS);

            if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
                EntityDolphin.this.playSound(SoundEffects.DOLPHIN_PLAY, 1.0F, 1.0F);
            }

            this.cooldown = 0;
        }

        @Override
        public void d() {
            ItemStack itemstack = EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                this.a(itemstack);
                EntityDolphin.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                this.cooldown = EntityDolphin.this.tickCount + EntityDolphin.this.random.nextInt(100);
            }

        }

        @Override
        public void e() {
            List<EntityItem> list = EntityDolphin.this.level.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.ALLOWED_ITEMS);
            ItemStack itemstack = EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                this.a(itemstack);
                EntityDolphin.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }

        private void a(ItemStack itemstack) {
            if (!itemstack.isEmpty()) {
                double d0 = EntityDolphin.this.getHeadY() - 0.30000001192092896D;
                EntityItem entityitem = new EntityItem(EntityDolphin.this.level, EntityDolphin.this.locX(), d0, EntityDolphin.this.locZ(), itemstack);

                entityitem.setPickupDelay(40);
                entityitem.setThrower(EntityDolphin.this.getUniqueID());
                float f = 0.3F;
                float f1 = EntityDolphin.this.random.nextFloat() * 6.2831855F;
                float f2 = 0.02F * EntityDolphin.this.random.nextFloat();

                entityitem.setMot((double) (0.3F * -MathHelper.sin(EntityDolphin.this.getYRot() * 0.017453292F) * MathHelper.cos(EntityDolphin.this.getXRot() * 0.017453292F) + MathHelper.cos(f1) * f2), (double) (0.3F * MathHelper.sin(EntityDolphin.this.getXRot() * 0.017453292F) * 1.5F), (double) (0.3F * MathHelper.cos(EntityDolphin.this.getYRot() * 0.017453292F) * MathHelper.cos(EntityDolphin.this.getXRot() * 0.017453292F) + MathHelper.sin(f1) * f2));
                EntityDolphin.this.level.addEntity(entityitem);
            }
        }
    }
}
