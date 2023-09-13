package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.IntRange;
import net.minecraft.util.TimeRange;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.IEntityAngerable;
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

    private static final UUID b = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier c = new AttributeModifier(EntityPigZombie.b, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
    private static final IntRange d = TimeRange.a(0, 1);
    private int bo;
    private static final IntRange bp = TimeRange.a(20, 39);
    private int bq;
    private UUID br;
    private static final IntRange bs = TimeRange.a(4, 6);
    private int bt;

    public EntityPigZombie(EntityTypes<? extends EntityPigZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.LAVA, 8.0F);
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.br = uuid;
    }

    @Override
    public double bb() {
        return this.isBaby() ? -0.05D : -0.45D;
    }

    @Override
    protected void m() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        this.targetSelector.a(3, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    public static AttributeProvider.Builder eW() {
        return EntityZombie.eS().a(GenericAttributes.SPAWN_REINFORCEMENTS, 0.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected boolean eN() {
        return false;
    }

    @Override
    protected void mobTick() {
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (this.isAngry()) {
            if (!this.isBaby() && !attributemodifiable.a(EntityPigZombie.c)) {
                attributemodifiable.b(EntityPigZombie.c);
            }

            this.eX();
        } else if (attributemodifiable.a(EntityPigZombie.c)) {
            attributemodifiable.removeModifier(EntityPigZombie.c);
        }

        this.a((WorldServer) this.world, true);
        if (this.getGoalTarget() != null) {
            this.eY();
        }

        if (this.isAngry()) {
            this.lastDamageByPlayerTime = this.ticksLived;
        }

        super.mobTick();
    }

    private void eX() {
        if (this.bo > 0) {
            --this.bo;
            if (this.bo == 0) {
                this.fa();
            }
        }

    }

    private void eY() {
        if (this.bt > 0) {
            --this.bt;
        } else {
            if (this.getEntitySenses().a(this.getGoalTarget())) {
                this.eZ();
            }

            this.bt = EntityPigZombie.bs.a(this.random);
        }
    }

    private void eZ() {
        double d0 = this.b(GenericAttributes.FOLLOW_RANGE);
        AxisAlignedBB axisalignedbb = AxisAlignedBB.a(this.getPositionVector()).grow(d0, 10.0D, d0);

        this.world.b(EntityPigZombie.class, axisalignedbb).stream().filter((entitypigzombie) -> {
            return entitypigzombie != this;
        }).filter((entitypigzombie) -> {
            return entitypigzombie.getGoalTarget() == null;
        }).filter((entitypigzombie) -> {
            return !entitypigzombie.r(this.getGoalTarget());
        }).forEach((entitypigzombie) -> {
            entitypigzombie.setGoalTarget(this.getGoalTarget());
        });
    }

    private void fa() {
        this.playSound(SoundEffects.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.dH() * 1.8F);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        if (this.getGoalTarget() == null && entityliving != null) {
            this.bo = EntityPigZombie.d.a(this.random);
            this.bt = EntityPigZombie.bs.a(this.random);
        }

        if (entityliving instanceof EntityHuman) {
            this.e((EntityHuman) entityliving);
        }

        super.setGoalTarget(entityliving);
    }

    @Override
    public void anger() {
        this.setAnger(EntityPigZombie.bp.a(this.random));
    }

    public static boolean b(EntityTypes<EntityPigZombie> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && generatoraccess.getType(blockposition.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.j((Entity) this) && !iworldreader.containsLiquid(this.getBoundingBox());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.a((WorldServer) this.world, nbttagcompound);
    }

    @Override
    public void setAnger(int i) {
        this.bq = i;
    }

    @Override
    public int getAnger() {
        return this.bq;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isAngry() ? SoundEffects.ENTITY_ZOMBIFIED_PIGLIN_ANGRY : SoundEffects.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected ItemStack eM() {
        return ItemStack.b;
    }

    @Override
    protected void eV() {
        this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS).setValue(0.0D);
    }

    @Override
    public UUID getAngerTarget() {
        return this.br;
    }

    @Override
    public boolean f(EntityHuman entityhuman) {
        return this.a_((EntityLiving) entityhuman);
    }
}
