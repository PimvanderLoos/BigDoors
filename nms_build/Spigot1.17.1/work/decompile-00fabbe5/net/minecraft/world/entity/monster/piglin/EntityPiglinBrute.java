package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityPiglinBrute extends EntityPiglinAbstract {

    private static final int MAX_HEALTH = 50;
    private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.35F;
    private static final int ATTACK_DAMAGE = 7;
    protected static final ImmutableList<SensorType<? extends Sensor<? super EntityPiglinBrute>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME});

    public EntityPiglinBrute(EntityTypes<? extends EntityPiglinBrute> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 20;
    }

    public static AttributeProvider.Builder fC() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 50.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3499999940395355D).a(GenericAttributes.ATTACK_DAMAGE, 7.0D);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        PiglinBruteAI.a(this);
        this.a(difficultydamagescaler);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
    }

    @Override
    protected BehaviorController.b<EntityPiglinBrute> dp() {
        return BehaviorController.a((Collection) EntityPiglinBrute.MEMORY_TYPES, (Collection) EntityPiglinBrute.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return PiglinBruteAI.a(this, this.dp().a(dynamic));
    }

    @Override
    public BehaviorController<EntityPiglinBrute> getBehaviorController() {
        return super.getBehaviorController();
    }

    @Override
    public boolean n() {
        return false;
    }

    @Override
    public boolean l(ItemStack itemstack) {
        return itemstack.a(Items.GOLDEN_AXE) ? super.l(itemstack) : false;
    }

    @Override
    protected void mobTick() {
        this.level.getMethodProfiler().enter("piglinBruteBrain");
        this.getBehaviorController().a((WorldServer) this.level, (EntityLiving) this);
        this.level.getMethodProfiler().exit();
        PiglinBruteAI.b(this);
        PiglinBruteAI.c(this);
        super.mobTick();
    }

    @Override
    public EntityPiglinArmPose fx() {
        return this.isAggressive() && this.fy() ? EntityPiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : EntityPiglinArmPose.DEFAULT;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean flag = super.damageEntity(damagesource, f);

        if (this.level.isClientSide) {
            return false;
        } else {
            if (flag && damagesource.getEntity() instanceof EntityLiving) {
                PiglinBruteAI.a(this, (EntityLiving) damagesource.getEntity());
            }

            return flag;
        }
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.PIGLIN_BRUTE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PIGLIN_BRUTE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PIGLIN_BRUTE_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
    }

    protected void fD() {
        this.playSound(SoundEffects.PIGLIN_BRUTE_ANGRY, 1.0F, this.ep());
    }

    @Override
    protected void fz() {
        this.playSound(SoundEffects.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.ep());
    }
}
