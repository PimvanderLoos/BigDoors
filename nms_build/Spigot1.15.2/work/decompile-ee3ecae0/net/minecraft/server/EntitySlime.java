package net.minecraft.server;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

public class EntitySlime extends EntityInsentient implements IMonster {

    private static final DataWatcherObject<Integer> bw = DataWatcher.a(EntitySlime.class, DataWatcherRegistry.b);
    public float b;
    public float c;
    public float d;
    private boolean bx;

    public EntitySlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntitySlime.ControllerMoveSlime(this);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.a(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.a(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.a(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.locY() - this.locY()) <= 4.0D;
        }));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntitySlime.bw, 1);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
    }

    public void setSize(int i, boolean flag) {
        this.datawatcher.set(EntitySlime.bw, i);
        this.Z();
        this.updateSize();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) (i * i));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double) (0.2F + 0.1F * (float) i));
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double) i);
        if (flag) {
            this.setHealth(this.getMaxHealth());
        }

        this.f = i;
    }

    public int getSize() {
        return (Integer) this.datawatcher.get(EntitySlime.bw);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Size", this.getSize() - 1);
        nbttagcompound.setBoolean("wasOnGround", this.bx);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("Size");

        if (i < 0) {
            i = 0;
        }

        this.setSize(i + 1, false);
        super.a(nbttagcompound);
        this.bx = nbttagcompound.getBoolean("wasOnGround");
    }

    public boolean ev() {
        return this.getSize() <= 1;
    }

    protected ParticleParam l() {
        return Particles.ITEM_SLIME;
    }

    @Override
    protected boolean J() {
        return this.getSize() > 0;
    }

    @Override
    public void tick() {
        this.c += (this.b - this.c) * 0.5F;
        this.d = this.c;
        super.tick();
        if (this.onGround && !this.bx) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;

                this.world.addParticle(this.l(), this.locX() + (double) f2, this.locY(), this.locZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.a(this.getSoundSquish(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.b = -0.5F;
        } else if (!this.onGround && this.bx) {
            this.b = 1.0F;
        }

        this.bx = this.onGround;
        this.ep();
    }

    protected void ep() {
        this.b *= 0.6F;
    }

    protected int eo() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void updateSize() {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();

        super.updateSize();
        this.setPosition(d0, d1, d2);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntitySlime.bw.equals(datawatcherobject)) {
            this.updateSize();
            this.yaw = this.aK;
            this.aI = this.aK;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.aD();
            }
        }

        super.a(datawatcherobject);
    }

    @Override
    public EntityTypes<? extends EntitySlime> getEntityType() {
        return super.getEntityType();
    }

    @Override
    public void die() {
        int i = this.getSize();

        if (!this.world.isClientSide && i > 1 && this.getHealth() <= 0.0F) {
            int j = 2 + this.random.nextInt(3);

            for (int k = 0; k < j; ++k) {
                float f = ((float) (k % 2) - 0.5F) * (float) i / 4.0F;
                float f1 = ((float) (k / 2) - 0.5F) * (float) i / 4.0F;
                EntitySlime entityslime = (EntitySlime) this.getEntityType().a(this.world);

                if (this.hasCustomName()) {
                    entityslime.setCustomName(this.getCustomName());
                }

                if (this.isPersistent()) {
                    entityslime.setPersistent();
                }

                entityslime.setInvulnerable(this.isInvulnerable());
                entityslime.setSize(i / 2, true);
                entityslime.setPositionRotation(this.locX() + (double) f, this.locY() + 0.5D, this.locZ() + (double) f1, this.random.nextFloat() * 360.0F, 0.0F);
                this.world.addEntity(entityslime);
            }
        }

        super.die();
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof EntityIronGolem && this.eq()) {
            this.i((EntityLiving) entity);
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (this.eq()) {
            this.i(entityhuman);
        }

    }

    protected void i(EntityLiving entityliving) {
        if (this.isAlive()) {
            int i = this.getSize();

            if (this.h((Entity) entityliving) < 0.6D * (double) i * 0.6D * (double) i && this.hasLineOfSight(entityliving) && entityliving.damageEntity(DamageSource.mobAttack(this), this.er())) {
                this.a(SoundEffects.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.a((EntityLiving) this, (Entity) entityliving);
            }
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.625F * entitysize.height;
    }

    protected boolean eq() {
        return !this.ev() && this.doAITick();
    }

    protected float er() {
        return (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.ev() ? SoundEffects.ENTITY_SLIME_HURT_SMALL : SoundEffects.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.ev() ? SoundEffects.ENTITY_SLIME_DEATH_SMALL : SoundEffects.ENTITY_SLIME_DEATH;
    }

    protected SoundEffect getSoundSquish() {
        return this.ev() ? SoundEffects.ENTITY_SLIME_SQUISH_SMALL : SoundEffects.ENTITY_SLIME_SQUISH;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return this.getSize() == 1 ? this.getEntityType().h() : LootTables.a;
    }

    public static boolean c(EntityTypes<EntitySlime> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (generatoraccess.getWorldData().getType() == WorldType.FLAT && random.nextInt(4) != 1) {
            return false;
        } else {
            if (generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL) {
                BiomeBase biomebase = generatoraccess.getBiome(blockposition);

                if (biomebase == Biomes.SWAMP && blockposition.getY() > 50 && blockposition.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < generatoraccess.Y() && generatoraccess.getLightLevel(blockposition) <= random.nextInt(8)) {
                    return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
                }

                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
                boolean flag = SeededRandom.a(chunkcoordintpair.x, chunkcoordintpair.z, generatoraccess.getSeed(), 987234911L).nextInt(10) == 0;

                if (random.nextInt(10) == 0 && flag && blockposition.getY() < 40) {
                    return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
                }
            }

            return false;
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    @Override
    public int dU() {
        return 0;
    }

    protected boolean ew() {
        return this.getSize() > 0;
    }

    @Override
    protected void jump() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, (double) this.dp(), vec3d.z);
        this.impulse = true;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.random.nextInt(3);

        if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.d()) {
            ++i;
        }

        int j = 1 << i;

        this.setSize(j, true);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    protected SoundEffect getSoundJump() {
        return this.ev() ? SoundEffects.ENTITY_SLIME_JUMP_SMALL : SoundEffects.ENTITY_SLIME_JUMP;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return super.a(entitypose).a(0.255F * (float) this.getSize());
    }

    static class PathfinderGoalSlimeIdle extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return !this.a.isPassenger();
        }

        @Override
        public void e() {
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.0D);
        }
    }

    static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
            entityslime.getNavigation().d(true);
        }

        @Override
        public boolean a() {
            return (this.a.isInWater() || this.a.aH()) && this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (this.a.getRandom().nextFloat() < 0.8F) {
                this.a.getControllerJump().jump();
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.2D);
        }
    }

    static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {

        private final EntitySlime a;
        private float b;
        private int c;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return this.a.getGoalTarget() == null && (this.a.onGround || this.a.isInWater() || this.a.aH() || this.a.hasEffect(MobEffects.LEVITATION)) && this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (--this.c <= 0) {
                this.c = 40 + this.a.getRandom().nextInt(60);
                this.b = (float) this.a.getRandom().nextInt(360);
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.b, false);
        }
    }

    static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {

        private final EntitySlime a;
        private int b;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable ? false : this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime));
        }

        @Override
        public void c() {
            this.b = 300;
            super.c();
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable ? false : --this.b > 0));
        }

        @Override
        public void e() {
            this.a.a((Entity) this.a.getGoalTarget(), 10.0F, 10.0F);
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.a.yaw, this.a.eq());
        }
    }

    static class ControllerMoveSlime extends ControllerMove {

        private float i;
        private int j;
        private final EntitySlime k;
        private boolean l;

        public ControllerMoveSlime(EntitySlime entityslime) {
            super(entityslime);
            this.k = entityslime;
            this.i = 180.0F * entityslime.yaw / 3.1415927F;
        }

        public void a(float f, boolean flag) {
            this.i = f;
            this.l = flag;
        }

        public void a(double d0) {
            this.e = d0;
            this.h = ControllerMove.Operation.MOVE_TO;
        }

        @Override
        public void a() {
            this.a.yaw = this.a(this.a.yaw, this.i, 90.0F);
            this.a.aK = this.a.yaw;
            this.a.aI = this.a.yaw;
            if (this.h != ControllerMove.Operation.MOVE_TO) {
                this.a.r(0.0F);
            } else {
                this.h = ControllerMove.Operation.WAIT;
                if (this.a.onGround) {
                    this.a.o((float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                    if (this.j-- <= 0) {
                        this.j = this.k.eo();
                        if (this.l) {
                            this.j /= 3;
                        }

                        this.k.getControllerJump().jump();
                        if (this.k.ew()) {
                            this.k.a(this.k.getSoundJump(), this.k.getSoundVolume(), ((this.k.getRandom().nextFloat() - this.k.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        }
                    } else {
                        this.k.aZ = 0.0F;
                        this.k.bb = 0.0F;
                        this.a.o(0.0F);
                    }
                } else {
                    this.a.o((float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                }

            }
        }
    }
}
