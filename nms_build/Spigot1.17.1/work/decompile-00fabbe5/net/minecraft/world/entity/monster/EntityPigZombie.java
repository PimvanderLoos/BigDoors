package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AxisAlignedBB;

public class EntityPigZombie extends EntityZombie implements IEntityAngerable {

    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(EntityPigZombie.SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
    private static final UniformInt FIRST_ANGER_SOUND_DELAY = TimeRange.a(0, 1);
    private int playFirstAngerSoundIn;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.a(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;
    private static final int ALERT_RANGE_Y = 10;
    private static final UniformInt ALERT_INTERVAL = TimeRange.a(4, 6);
    private int ticksUntilNextAlert;

    public EntityPigZombie(EntityTypes<? extends EntityPigZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.LAVA, 8.0F);
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public double bk() {
        return this.isBaby() ? -0.05D : -0.45D;
    }

    @Override
    protected void n() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        this.targetSelector.a(3, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    public static AttributeProvider.Builder fG() {
        return EntityZombie.fC().a(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected boolean fx() {
        return false;
    }

    @Override
    protected void mobTick() {
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (this.isAngry()) {
            if (!this.isBaby() && !attributemodifiable.a(EntityPigZombie.SPEED_MODIFIER_ATTACKING)) {
                attributemodifiable.b(EntityPigZombie.SPEED_MODIFIER_ATTACKING);
            }

            this.fH();
        } else if (attributemodifiable.a(EntityPigZombie.SPEED_MODIFIER_ATTACKING)) {
            attributemodifiable.removeModifier(EntityPigZombie.SPEED_MODIFIER_ATTACKING);
        }

        this.a((WorldServer) this.level, true);
        if (this.getGoalTarget() != null) {
            this.fI();
        }

        if (this.isAngry()) {
            this.lastHurtByPlayerTime = this.tickCount;
        }

        super.mobTick();
    }

    private void fH() {
        if (this.playFirstAngerSoundIn > 0) {
            --this.playFirstAngerSoundIn;
            if (this.playFirstAngerSoundIn == 0) {
                this.fK();
            }
        }

    }

    private void fI() {
        if (this.ticksUntilNextAlert > 0) {
            --this.ticksUntilNextAlert;
        } else {
            if (this.getEntitySenses().a(this.getGoalTarget())) {
                this.fJ();
            }

            this.ticksUntilNextAlert = EntityPigZombie.ALERT_INTERVAL.a(this.random);
        }
    }

    private void fJ() {
        double d0 = this.b(GenericAttributes.FOLLOW_RANGE);
        AxisAlignedBB axisalignedbb = AxisAlignedBB.a(this.getPositionVector()).grow(d0, 10.0D, d0);

        this.level.a(EntityPigZombie.class, axisalignedbb, IEntitySelector.NO_SPECTATORS).stream().filter((entitypigzombie) -> {
            return entitypigzombie != this;
        }).filter((entitypigzombie) -> {
            return entitypigzombie.getGoalTarget() == null;
        }).filter((entitypigzombie) -> {
            return !entitypigzombie.p(this.getGoalTarget());
        }).forEach((entitypigzombie) -> {
            entitypigzombie.setGoalTarget(this.getGoalTarget());
        });
    }

    private void fK() {
        this.playSound(SoundEffects.ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.ep() * 1.8F);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        if (this.getGoalTarget() == null && entityliving != null) {
            this.playFirstAngerSoundIn = EntityPigZombie.FIRST_ANGER_SOUND_DELAY.a(this.random);
            this.ticksUntilNextAlert = EntityPigZombie.ALERT_INTERVAL.a(this.random);
        }

        if (entityliving instanceof EntityHuman) {
            this.e((EntityHuman) entityliving);
        }

        super.setGoalTarget(entityliving);
    }

    @Override
    public void anger() {
        this.setAnger(EntityPigZombie.PERSISTENT_ANGER_TIME.a(this.random));
    }

    public static boolean b(EntityTypes<EntityPigZombie> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && !generatoraccess.getType(blockposition.down()).a(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.f((Entity) this) && !iworldreader.containsLiquid(this.getBoundingBox());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.a(this.level, nbttagcompound);
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
    protected SoundEffect getSoundAmbient() {
        return this.isAngry() ? SoundEffects.ZOMBIFIED_PIGLIN_ANGRY : SoundEffects.ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected ItemStack fw() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void fF() {
        this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).setValue(0.0D);
    }

    @Override
    public UUID getAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public boolean f(EntityHuman entityhuman) {
        return this.a_((EntityLiving) entityhuman);
    }

    @Override
    public boolean l(ItemStack itemstack) {
        return this.canPickup(itemstack);
    }
}
