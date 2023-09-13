package net.minecraft.world.entity.animal;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalOfferFlower;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalStrollVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalStrollVillageGolem;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalDefendVillage;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.Vec3D;

public class EntityIronGolem extends EntityGolem implements IEntityAngerable {

    protected static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntityIronGolem.class, DataWatcherRegistry.BYTE);
    private static final int IRON_INGOT_HEAL_AMOUNT = 25;
    private int attackAnimationTick;
    private int offerFlowerTick;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.a(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    public EntityIronGolem(EntityTypes<? extends EntityIronGolem> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(2, new PathfinderGoalStrollVillage(this, 0.6D, false));
        this.goalSelector.a(4, new PathfinderGoalStrollVillageGolem(this, 0.6D));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 5, false, false, (entityliving) -> {
            return entityliving instanceof IMonster && !(entityliving instanceof EntityCreeper);
        }));
        this.targetSelector.a(4, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityIronGolem.DATA_FLAGS_ID, (byte) 0);
    }

    public static AttributeProvider.Builder n() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 100.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 1.0D).a(GenericAttributes.ATTACK_DAMAGE, 15.0D);
    }

    @Override
    protected int m(int i) {
        return i;
    }

    @Override
    protected void A(Entity entity) {
        if (entity instanceof IMonster && !(entity instanceof EntityCreeper) && this.getRandom().nextInt(20) == 0) {
            this.setGoalTarget((EntityLiving) entity);
        }

        super.A(entity);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        if (this.offerFlowerTick > 0) {
            --this.offerFlowerTick;
        }

        if (this.getMot().i() > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
            int i = MathHelper.floor(this.locX());
            int j = MathHelper.floor(this.locY() - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ());
            IBlockData iblockdata = this.level.getType(new BlockPosition(i, j, k));

            if (!iblockdata.isAir()) {
                this.level.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getWidth(), this.locY() + 0.1D, this.locZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

        if (!this.level.isClientSide) {
            this.a((WorldServer) this.level, true);
        }

    }

    @Override
    public boolean a(EntityTypes<?> entitytypes) {
        return this.isPlayerCreated() && entitytypes == EntityTypes.PLAYER ? false : (entitytypes == EntityTypes.CREEPER ? false : super.a(entitytypes));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("PlayerCreated", this.isPlayerCreated());
        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setPlayerCreated(nbttagcompound.getBoolean("PlayerCreated"));
        this.a(this.level, nbttagcompound);
    }

    @Override
    public void anger() {
        this.setAnger(EntityIronGolem.PERSISTENT_ANGER_TIME.a(this.random));
    }

    @Override
    public void setAnger(int i) {
        this.remainingPersistentAngerTime = i;
    }

    @Override
    public int getAnger() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public UUID getAngerTarget() {
        return this.persistentAngerTarget;
    }

    private float fy() {
        return (float) this.b(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEffect(this, (byte) 4);
        float f = this.fy();
        float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f1);

        if (flag) {
            entity.setMot(entity.getMot().add(0.0D, 0.4000000059604645D, 0.0D));
            this.a((EntityLiving) this, entity);
        }

        this.playSound(SoundEffects.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        EntityIronGolem.CrackLevel entityirongolem_cracklevel = this.p();
        boolean flag = super.damageEntity(damagesource, f);

        if (flag && this.p() != entityirongolem_cracklevel) {
            this.playSound(SoundEffects.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }

        return flag;
    }

    public EntityIronGolem.CrackLevel p() {
        return EntityIronGolem.CrackLevel.a(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void a(byte b0) {
        if (b0 == 4) {
            this.attackAnimationTick = 10;
            this.playSound(SoundEffects.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else if (b0 == 11) {
            this.offerFlowerTick = 400;
        } else if (b0 == 34) {
            this.offerFlowerTick = 0;
        } else {
            super.a(b0);
        }

    }

    public int t() {
        return this.attackAnimationTick;
    }

    public void v(boolean flag) {
        if (flag) {
            this.offerFlowerTick = 400;
            this.level.broadcastEntityEffect(this, (byte) 11);
        } else {
            this.offerFlowerTick = 0;
            this.level.broadcastEntityEffect(this, (byte) 34);
        }

    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.IRON_GOLEM_DEATH;
    }

    @Override
    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.a(Items.IRON_INGOT)) {
            return EnumInteractionResult.PASS;
        } else {
            float f = this.getHealth();

            this.heal(25.0F);
            if (this.getHealth() == f) {
                return EnumInteractionResult.PASS;
            } else {
                float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;

                this.playSound(SoundEffects.IRON_GOLEM_REPAIR, 1.0F, f1);
                this.a(GameEvent.MOB_INTERACT, this.cT());
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                return EnumInteractionResult.a(this.level.isClientSide);
            }
        }
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public int fw() {
        return this.offerFlowerTick;
    }

    public boolean isPlayerCreated() {
        return ((Byte) this.entityData.get(EntityIronGolem.DATA_FLAGS_ID) & 1) != 0;
    }

    public void setPlayerCreated(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityIronGolem.DATA_FLAGS_ID);

        if (flag) {
            this.entityData.set(EntityIronGolem.DATA_FLAGS_ID, (byte) (b0 | 1));
        } else {
            this.entityData.set(EntityIronGolem.DATA_FLAGS_ID, (byte) (b0 & -2));
        }

    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        BlockPosition blockposition = this.getChunkCoordinates();
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = iworldreader.getType(blockposition1);

        if (!iblockdata.a((IBlockAccess) iworldreader, blockposition1, (Entity) this)) {
            return false;
        } else {
            for (int i = 1; i < 3; ++i) {
                BlockPosition blockposition2 = blockposition.up(i);
                IBlockData iblockdata1 = iworldreader.getType(blockposition2);

                if (!SpawnerCreature.a((IBlockAccess) iworldreader, blockposition2, iblockdata1, iblockdata1.getFluid(), EntityTypes.IRON_GOLEM)) {
                    return false;
                }
            }

            return SpawnerCreature.a((IBlockAccess) iworldreader, blockposition, iworldreader.getType(blockposition), FluidTypes.EMPTY.h(), EntityTypes.IRON_GOLEM) && iworldreader.f((Entity) this);
        }
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.875F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }

    public static enum CrackLevel {

        NONE(1.0F), LOW(0.75F), MEDIUM(0.5F), HIGH(0.25F);

        private static final List<EntityIronGolem.CrackLevel> BY_DAMAGE = (List) Stream.of(values()).sorted(Comparator.comparingDouble((entityirongolem_cracklevel) -> {
            return (double) entityirongolem_cracklevel.fraction;
        })).collect(ImmutableList.toImmutableList());
        private final float fraction;

        private CrackLevel(float f) {
            this.fraction = f;
        }

        public static EntityIronGolem.CrackLevel a(float f) {
            Iterator iterator = EntityIronGolem.CrackLevel.BY_DAMAGE.iterator();

            EntityIronGolem.CrackLevel entityirongolem_cracklevel;

            do {
                if (!iterator.hasNext()) {
                    return EntityIronGolem.CrackLevel.NONE;
                }

                entityirongolem_cracklevel = (EntityIronGolem.CrackLevel) iterator.next();
            } while (f >= entityirongolem_cracklevel.fraction);

            return entityirongolem_cracklevel;
        }
    }
}
