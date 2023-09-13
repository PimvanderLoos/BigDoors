package net.minecraft.server;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityPigZombie extends EntityZombie implements IEntityAngerable {

    private static final UUID b = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier c = new AttributeModifier(EntityPigZombie.b, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
    private static final IntRange d = TimeRange.a(0, 1);
    private int bv;
    private static final IntRange bw = TimeRange.a(20, 39);
    private int bx;
    private UUID by;
    private static final IntRange bz = TimeRange.a(4, 6);
    private int bA;

    public EntityPigZombie(EntityTypes<? extends EntityPigZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.LAVA, 8.0F);
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.by = uuid;
    }

    @Override
    public double aX() {
        return this.isBaby() ? -0.16D : -0.45D;
    }

    @Override
    protected void m() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::b));
        this.targetSelector.a(3, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    public static AttributeProvider.Builder eX() {
        return EntityZombie.eT().a(GenericAttributes.SPAWN_REINFORCEMENTS, 0.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected boolean eO() {
        return false;
    }

    @Override
    protected void mobTick() {
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (this.isAngry()) {
            if (!this.isBaby() && !attributemodifiable.a(EntityPigZombie.c)) {
                attributemodifiable.b(EntityPigZombie.c);
            }

            this.eY();
        } else if (attributemodifiable.a(EntityPigZombie.c)) {
            attributemodifiable.removeModifier(EntityPigZombie.c);
        }

        this.a((WorldServer) this.world, true);
        if (this.getGoalTarget() != null) {
            this.eZ();
        }

        if (this.isAngry()) {
            this.lastDamageByPlayerTime = this.ticksLived;
        }

        super.mobTick();
    }

    private void eY() {
        if (this.bv > 0) {
            --this.bv;
            if (this.bv == 0) {
                this.fb();
            }
        }

    }

    private void eZ() {
        if (this.bA > 0) {
            --this.bA;
        } else {
            if (this.getEntitySenses().a(this.getGoalTarget())) {
                this.fa();
            }

            this.bA = EntityPigZombie.bz.a(this.random);
        }
    }

    private void fa() {
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

    private void fb() {
        this.playSound(SoundEffects.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.dG() * 1.8F);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        if (this.getGoalTarget() == null && entityliving != null) {
            this.bv = EntityPigZombie.d.a(this.random);
            this.bA = EntityPigZombie.bz.a(this.random);
        }

        if (entityliving instanceof EntityHuman) {
            this.e((EntityHuman) entityliving);
        }

        super.setGoalTarget(entityliving);
    }

    @Override
    public void anger() {
        this.setAnger(EntityPigZombie.bw.a(this.random));
    }

    public static boolean b(EntityTypes<EntityPigZombie> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && generatoraccess.getType(blockposition.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.i(this) && !iworldreader.containsLiquid(this.getBoundingBox());
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
        this.bx = i;
    }

    @Override
    public int getAnger() {
        return this.bx;
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
    protected ItemStack eN() {
        return ItemStack.b;
    }

    @Override
    protected void eW() {
        this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS).setValue(0.0D);
    }

    @Override
    public UUID getAngerTarget() {
        return this.by;
    }

    @Override
    public boolean f(EntityHuman entityhuman) {
        return this.b((EntityLiving) entityhuman);
    }
}
