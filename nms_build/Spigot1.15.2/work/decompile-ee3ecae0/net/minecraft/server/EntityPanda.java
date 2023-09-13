package net.minecraft.server;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPanda extends EntityAnimal {

    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> by = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bz = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Byte> bA = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Byte> bB = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Byte> bC = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.a);
    private static final PathfinderTargetCondition bD = (new PathfinderTargetCondition()).a(8.0D).b().a();
    private boolean bE;
    private boolean bF;
    public int bw;
    private Vec3D bG;
    private float bH;
    private float bI;
    private float bJ;
    private float bK;
    private float bL;
    private float bM;
    private EntityPanda.g bN;
    private static final Predicate<EntityItem> PICKUP_PREDICATE = (entityitem) -> {
        Item item = entityitem.getItemStack().getItem();

        return (item == Blocks.BAMBOO.getItem() || item == Blocks.CAKE.getItem()) && entityitem.isAlive() && !entityitem.p();
    };

    public EntityPanda(EntityTypes<? extends EntityPanda> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntityPanda.h(this);
        if (!this.isBaby()) {
            this.setCanPickupLoot(true);
        }

    }

    @Override
    public boolean e(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.h(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.e(itemstack);
    }

    public int eq() {
        return (Integer) this.datawatcher.get(EntityPanda.bx);
    }

    public void t(int i) {
        this.datawatcher.set(EntityPanda.bx, i);
    }

    public boolean er() {
        return this.w(2);
    }

    public boolean es() {
        return this.w(8);
    }

    public void r(boolean flag) {
        this.d(8, flag);
    }

    public boolean et() {
        return this.w(16);
    }

    public void s(boolean flag) {
        this.d(16, flag);
    }

    public boolean eu() {
        return (Integer) this.datawatcher.get(EntityPanda.bz) > 0;
    }

    public void t(boolean flag) {
        this.datawatcher.set(EntityPanda.bz, flag ? 1 : 0);
    }

    private int eO() {
        return (Integer) this.datawatcher.get(EntityPanda.bz);
    }

    private void v(int i) {
        this.datawatcher.set(EntityPanda.bz, i);
    }

    public void u(boolean flag) {
        this.d(2, flag);
        if (!flag) {
            this.u(0);
        }

    }

    public int ez() {
        return (Integer) this.datawatcher.get(EntityPanda.by);
    }

    public void u(int i) {
        this.datawatcher.set(EntityPanda.by, i);
    }

    public EntityPanda.Gene getMainGene() {
        return EntityPanda.Gene.a((Byte) this.datawatcher.get(EntityPanda.bA));
    }

    public void setMainGene(EntityPanda.Gene entitypanda_gene) {
        if (entitypanda_gene.a() > 6) {
            entitypanda_gene = EntityPanda.Gene.a(this.random);
        }

        this.datawatcher.set(EntityPanda.bA, (byte) entitypanda_gene.a());
    }

    public EntityPanda.Gene getHiddenGene() {
        return EntityPanda.Gene.a((Byte) this.datawatcher.get(EntityPanda.bB));
    }

    public void setHiddenGene(EntityPanda.Gene entitypanda_gene) {
        if (entitypanda_gene.a() > 6) {
            entitypanda_gene = EntityPanda.Gene.a(this.random);
        }

        this.datawatcher.set(EntityPanda.bB, (byte) entitypanda_gene.a());
    }

    public boolean eC() {
        return this.w(4);
    }

    public void v(boolean flag) {
        this.d(4, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPanda.bx, 0);
        this.datawatcher.register(EntityPanda.by, 0);
        this.datawatcher.register(EntityPanda.bA, (byte) 0);
        this.datawatcher.register(EntityPanda.bB, (byte) 0);
        this.datawatcher.register(EntityPanda.bC, (byte) 0);
        this.datawatcher.register(EntityPanda.bz, 0);
    }

    private boolean w(int i) {
        return ((Byte) this.datawatcher.get(EntityPanda.bC) & i) != 0;
    }

    private void d(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityPanda.bC);

        if (flag) {
            this.datawatcher.set(EntityPanda.bC, (byte) (b0 | i));
        } else {
            this.datawatcher.set(EntityPanda.bC, (byte) (b0 & ~i));
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setString("MainGene", this.getMainGene().b());
        nbttagcompound.setString("HiddenGene", this.getHiddenGene().b());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setMainGene(EntityPanda.Gene.a(nbttagcompound.getString("MainGene")));
        this.setHiddenGene(EntityPanda.Gene.a(nbttagcompound.getString("HiddenGene")));
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        EntityPanda entitypanda = (EntityPanda) EntityTypes.PANDA.a(this.world);

        if (entityageable instanceof EntityPanda) {
            entitypanda.a(this, (EntityPanda) entityageable);
        }

        entitypanda.eK();
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
        this.bN = new EntityPanda.g(this, EntityHuman.class, 6.0F);
        this.goalSelector.a(9, this.bN);
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(12, new EntityPanda.j(this));
        this.goalSelector.a(13, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(14, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new EntityPanda.e(this, new Class[0])).a(new Class[0]));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.15000000596046448D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
    }

    public EntityPanda.Gene eD() {
        return EntityPanda.Gene.b(this.getMainGene(), this.getHiddenGene());
    }

    public boolean eE() {
        return this.eD() == EntityPanda.Gene.LAZY;
    }

    public boolean eF() {
        return this.eD() == EntityPanda.Gene.WORRIED;
    }

    public boolean eG() {
        return this.eD() == EntityPanda.Gene.PLAYFUL;
    }

    public boolean eI() {
        return this.eD() == EntityPanda.Gene.WEAK;
    }

    @Override
    public boolean em() {
        return this.eD() == EntityPanda.Gene.AGGRESSIVE;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public boolean B(Entity entity) {
        this.a(SoundEffects.ENTITY_PANDA_BITE, 1.0F, 1.0F);
        if (!this.em()) {
            this.bF = true;
        }

        return super.B(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eF()) {
            if (this.world.U() && !this.isInWater()) {
                this.r(true);
                this.t(false);
            } else if (!this.eu()) {
                this.r(false);
            }
        }

        if (this.getGoalTarget() == null) {
            this.bE = false;
            this.bF = false;
        }

        if (this.eq() > 0) {
            if (this.getGoalTarget() != null) {
                this.a((Entity) this.getGoalTarget(), 90.0F, 90.0F);
            }

            if (this.eq() == 29 || this.eq() == 14) {
                this.a(SoundEffects.ENTITY_PANDA_CANT_BREED, 1.0F, 1.0F);
            }

            this.t(this.eq() - 1);
        }

        if (this.er()) {
            this.u(this.ez() + 1);
            if (this.ez() > 20) {
                this.u(false);
                this.eV();
            } else if (this.ez() == 1) {
                this.a(SoundEffects.ENTITY_PANDA_PRE_SNEEZE, 1.0F, 1.0F);
            }
        }

        if (this.eC()) {
            this.eU();
        } else {
            this.bw = 0;
        }

        if (this.es()) {
            this.pitch = 0.0F;
        }

        this.eR();
        this.eP();
        this.eS();
        this.eT();
    }

    public boolean eJ() {
        return this.eF() && this.world.U();
    }

    private void eP() {
        if (!this.eu() && this.es() && !this.eJ() && !this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.t(true);
        } else if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() || !this.es()) {
            this.t(false);
        }

        if (this.eu()) {
            this.eQ();
            if (!this.world.isClientSide && this.eO() > 80 && this.random.nextInt(20) == 1) {
                if (this.eO() > 100 && this.j(this.getEquipment(EnumItemSlot.MAINHAND))) {
                    if (!this.world.isClientSide) {
                        this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                    }

                    this.r(false);
                }

                this.t(false);
                return;
            }

            this.v(this.eO() + 1);
        }

    }

    private void eQ() {
        if (this.eO() % 5 == 0) {
            this.a(SoundEffects.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

            for (int i = 0; i < 6; ++i) {
                Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);

                vec3d = vec3d.a(-this.pitch * 0.017453292F);
                vec3d = vec3d.b(-this.yaw * 0.017453292F);
                double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
                Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.8D, d0, 1.0D + ((double) this.random.nextFloat() - 0.5D) * 0.4D);

                vec3d1 = vec3d1.b(-this.aI * 0.017453292F);
                vec3d1 = vec3d1.add(this.locX(), this.getHeadY() + 1.0D, this.locZ());
                this.world.addParticle(new ParticleParamItem(Particles.ITEM, this.getEquipment(EnumItemSlot.MAINHAND)), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
        }

    }

    private void eR() {
        this.bI = this.bH;
        if (this.es()) {
            this.bH = Math.min(1.0F, this.bH + 0.15F);
        } else {
            this.bH = Math.max(0.0F, this.bH - 0.19F);
        }

    }

    private void eS() {
        this.bK = this.bJ;
        if (this.et()) {
            this.bJ = Math.min(1.0F, this.bJ + 0.15F);
        } else {
            this.bJ = Math.max(0.0F, this.bJ - 0.19F);
        }

    }

    private void eT() {
        this.bM = this.bL;
        if (this.eC()) {
            this.bL = Math.min(1.0F, this.bL + 0.15F);
        } else {
            this.bL = Math.max(0.0F, this.bL - 0.19F);
        }

    }

    private void eU() {
        ++this.bw;
        if (this.bw > 32) {
            this.v(false);
        } else {
            if (!this.world.isClientSide) {
                Vec3D vec3d = this.getMot();

                if (this.bw == 1) {
                    float f = this.yaw * 0.017453292F;
                    float f1 = this.isBaby() ? 0.1F : 0.2F;

                    this.bG = new Vec3D(vec3d.x + (double) (-MathHelper.sin(f) * f1), 0.0D, vec3d.z + (double) (MathHelper.cos(f) * f1));
                    this.setMot(this.bG.add(0.0D, 0.27D, 0.0D));
                } else if ((float) this.bw != 7.0F && (float) this.bw != 15.0F && (float) this.bw != 23.0F) {
                    this.setMot(this.bG.x, vec3d.y, this.bG.z);
                } else {
                    this.setMot(0.0D, this.onGround ? 0.27D : vec3d.y, 0.0D);
                }
            }

        }
    }

    private void eV() {
        Vec3D vec3d = this.getMot();

        this.world.addParticle(Particles.SNEEZE, this.locX() - (double) (this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(this.aI * 0.017453292F), this.getHeadY() - 0.10000000149011612D, this.locZ() + (double) (this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(this.aI * 0.017453292F), vec3d.x, 0.0D, vec3d.z);
        this.a(SoundEffects.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);
        List<EntityPanda> list = this.world.a(EntityPanda.class, this.getBoundingBox().g(10.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityPanda entitypanda = (EntityPanda) iterator.next();

            if (!entitypanda.isBaby() && entitypanda.onGround && !entitypanda.isInWater() && entitypanda.eL()) {
                entitypanda.jump();
            }
        }

        if (!this.world.p_() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.a((IMaterial) Items.SLIME_BALL);
        }

    }

    @Override
    protected void a(EntityItem entityitem) {
        if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() && EntityPanda.PICKUP_PREDICATE.test(entityitem)) {
            ItemStack itemstack = entityitem.getItemStack();

            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
            this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 2.0F;
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        this.r(false);
        return super.damageEntity(damagesource, f);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setMainGene(EntityPanda.Gene.a(this.random));
        this.setHiddenGene(EntityPanda.Gene.a(this.random));
        this.eK();
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
            ((EntityAgeable.a) groupdataentity).a(0.2F);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public void a(EntityPanda entitypanda, @Nullable EntityPanda entitypanda1) {
        if (entitypanda1 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(entitypanda.eW());
                this.setHiddenGene(EntityPanda.Gene.a(this.random));
            } else {
                this.setMainGene(EntityPanda.Gene.a(this.random));
                this.setHiddenGene(entitypanda.eW());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene(entitypanda.eW());
            this.setHiddenGene(entitypanda1.eW());
        } else {
            this.setMainGene(entitypanda1.eW());
            this.setHiddenGene(entitypanda.eW());
        }

        if (this.random.nextInt(32) == 0) {
            this.setMainGene(EntityPanda.Gene.a(this.random));
        }

        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(EntityPanda.Gene.a(this.random));
        }

    }

    private EntityPanda.Gene eW() {
        return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
    }

    public void eK() {
        if (this.eI()) {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        }

        if (this.eE()) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.07000000029802322D);
        }

    }

    private void eX() {
        if (!this.isInWater()) {
            this.r(0.0F);
            this.getNavigation().o();
            this.r(true);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else if (this.eJ()) {
            return false;
        } else if (this.et()) {
            this.s(false);
            return true;
        } else if (this.i(itemstack)) {
            if (this.getGoalTarget() != null) {
                this.bE = true;
            }

            if (this.isBaby()) {
                this.a(entityhuman, itemstack);
                this.setAge((int) ((float) (-this.getAge() / 20) * 0.1F), true);
            } else if (!this.world.isClientSide && this.getAge() == 0 && this.ev()) {
                this.a(entityhuman, itemstack);
                this.f(entityhuman);
            } else {
                if (this.world.isClientSide || this.es() || this.isInWater()) {
                    return false;
                }

                this.eX();
                this.t(true);
                ItemStack itemstack1 = this.getEquipment(EnumItemSlot.MAINHAND);

                if (!itemstack1.isEmpty() && !entityhuman.abilities.canInstantlyBuild) {
                    this.a(itemstack1);
                }

                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(itemstack.getItem(), 1));
                this.a(entityhuman, itemstack);
            }

            entityhuman.a(enumhand, true);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.em() ? SoundEffects.ENTITY_PANDA_AGGRESSIVE_AMBIENT : (this.eF() ? SoundEffects.ENTITY_PANDA_WORRIED_AMBIENT : SoundEffects.ENTITY_PANDA_AMBIENT);
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_PANDA_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return itemstack.getItem() == Blocks.BAMBOO.getItem();
    }

    private boolean j(ItemStack itemstack) {
        return this.i(itemstack) || itemstack.getItem() == Blocks.CAKE.getItem();
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PANDA_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PANDA_HURT;
    }

    public boolean eL() {
        return !this.et() && !this.eJ() && !this.eu() && !this.eC() && !this.es();
    }

    static class i extends PathfinderGoalPanic {

        private final EntityPanda f;

        public i(EntityPanda entitypanda, double d0) {
            super(entitypanda, d0);
            this.f = entitypanda;
        }

        @Override
        public boolean a() {
            if (!this.f.isBurning()) {
                return false;
            } else {
                BlockPosition blockposition = this.a(this.a.world, this.a, 5, 4);

                if (blockposition != null) {
                    this.c = (double) blockposition.getX();
                    this.d = (double) blockposition.getY();
                    this.e = (double) blockposition.getZ();
                    return true;
                } else {
                    return this.g();
                }
            }
        }

        @Override
        public boolean b() {
            if (this.f.es()) {
                this.f.getNavigation().o();
                return false;
            } else {
                return super.b();
            }
        }
    }

    static class e extends PathfinderGoalHurtByTarget {

        private final EntityPanda a;

        public e(EntityPanda entitypanda, Class<?>... aclass) {
            super(entitypanda, aclass);
            this.a = entitypanda;
        }

        @Override
        public boolean b() {
            if (!this.a.bE && !this.a.bF) {
                return super.b();
            } else {
                this.a.setGoalTarget((EntityLiving) null);
                return false;
            }
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPanda && ((EntityPanda) entityinsentient).em()) {
                entityinsentient.setGoalTarget(entityliving);
            }

        }
    }

    static class f extends PathfinderGoal {

        private final EntityPanda a;
        private int b;

        public f(EntityPanda entitypanda) {
            this.a = entitypanda;
        }

        @Override
        public boolean a() {
            return this.b < this.a.ticksLived && this.a.eE() && this.a.eL() && this.a.random.nextInt(400) == 1;
        }

        @Override
        public boolean b() {
            return !this.a.isInWater() && (this.a.eE() || this.a.random.nextInt(600) != 1) ? this.a.random.nextInt(2000) != 1 : false;
        }

        @Override
        public void c() {
            this.a.s(true);
            this.b = 0;
        }

        @Override
        public void d() {
            this.a.s(false);
            this.b = this.a.ticksLived + 200;
        }
    }

    class k extends PathfinderGoal {

        private int b;

        public k() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (this.b <= EntityPanda.this.ticksLived && !EntityPanda.this.isBaby() && !EntityPanda.this.isInWater() && EntityPanda.this.eL() && EntityPanda.this.eq() <= 0) {
                List<EntityItem> list = EntityPanda.this.world.a(EntityItem.class, EntityPanda.this.getBoundingBox().grow(6.0D, 6.0D, 6.0D), EntityPanda.PICKUP_PREDICATE);

                return !list.isEmpty() || !EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
            } else {
                return false;
            }
        }

        @Override
        public boolean b() {
            return !EntityPanda.this.isInWater() && (EntityPanda.this.eE() || EntityPanda.this.random.nextInt(600) != 1) ? EntityPanda.this.random.nextInt(2000) != 1 : false;
        }

        @Override
        public void e() {
            if (!EntityPanda.this.es() && !EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.eX();
            }

        }

        @Override
        public void c() {
            List<EntityItem> list = EntityPanda.this.world.a(EntityItem.class, EntityPanda.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityPanda.PICKUP_PREDICATE);

            if (!list.isEmpty() && EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            } else if (!EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.eX();
            }

            this.b = 0;
        }

        @Override
        public void d() {
            ItemStack itemstack = EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                EntityPanda.this.a(itemstack);
                EntityPanda.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                int i = EntityPanda.this.eE() ? EntityPanda.this.random.nextInt(50) + 10 : EntityPanda.this.random.nextInt(150) + 10;

                this.b = EntityPanda.this.ticksLived + i * 20;
            }

            EntityPanda.this.r(false);
        }
    }

    static class c<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityPanda i;

        public c(EntityPanda entitypanda, Class<T> oclass, float f, double d0, double d1) {
            Predicate predicate = IEntitySelector.f;

            super(entitypanda, oclass, f, d0, d1, predicate::test);
            this.i = entitypanda;
        }

        @Override
        public boolean a() {
            return this.i.eF() && this.i.eL() && super.a();
        }
    }

    class d extends PathfinderGoalBreed {

        private final EntityPanda e;
        private int f;

        public d(EntityPanda entitypanda, double d0) {
            super(entitypanda, d0);
            this.e = entitypanda;
        }

        @Override
        public boolean a() {
            if (super.a() && this.e.eq() == 0) {
                if (!this.h()) {
                    if (this.f <= this.e.ticksLived) {
                        this.e.t(32);
                        this.f = this.e.ticksLived + 600;
                        if (this.e.doAITick()) {
                            EntityHuman entityhuman = this.b.a(EntityPanda.bD, (EntityLiving) this.e);

                            this.e.bN.a((EntityLiving) entityhuman);
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
            BlockPosition blockposition = new BlockPosition(this.e);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 8; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.g(blockposition).e(k, i, l);
                            if (this.b.getType(blockposition_mutableblockposition).getBlock() == Blocks.BAMBOO) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    static class l extends PathfinderGoal {

        private final EntityPanda a;

        public l(EntityPanda entitypanda) {
            this.a = entitypanda;
        }

        @Override
        public boolean a() {
            return this.a.isBaby() && this.a.eL() ? (this.a.eI() && this.a.random.nextInt(500) == 1 ? true : this.a.random.nextInt(6000) == 1) : false;
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void c() {
            this.a.u(true);
        }
    }

    static class j extends PathfinderGoal {

        private final EntityPanda a;

        public j(EntityPanda entitypanda) {
            this.a = entitypanda;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
        }

        @Override
        public boolean a() {
            if ((this.a.isBaby() || this.a.eG()) && this.a.onGround) {
                if (!this.a.eL()) {
                    return false;
                } else {
                    float f = this.a.yaw * 0.017453292F;
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

                    return this.a.world.getType((new BlockPosition(this.a)).b(i, -1, j)).isAir() ? true : (this.a.eG() && this.a.random.nextInt(60) == 1 ? true : this.a.random.nextInt(500) == 1);
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
            this.a.v(true);
        }

        @Override
        public boolean E_() {
            return false;
        }
    }

    static class g extends PathfinderGoalLookAtPlayer {

        private final EntityPanda g;

        public g(EntityPanda entitypanda, Class<? extends EntityLiving> oclass, float f) {
            super(entitypanda, oclass, f);
            this.g = entitypanda;
        }

        public void a(EntityLiving entityliving) {
            this.b = entityliving;
        }

        @Override
        public boolean b() {
            return this.b != null && super.b();
        }

        @Override
        public boolean a() {
            if (this.a.getRandom().nextFloat() >= this.d) {
                return false;
            } else {
                if (this.b == null) {
                    if (this.e == EntityHuman.class) {
                        this.b = this.a.world.a(this.f, this.a, this.a.locX(), this.a.getHeadY(), this.a.locZ());
                    } else {
                        this.b = this.a.world.b(this.e, this.f, this.a, this.a.locX(), this.a.getHeadY(), this.a.locZ(), this.a.getBoundingBox().grow((double) this.c, 3.0D, (double) this.c));
                    }
                }

                return this.g.eL() && this.b != null;
            }
        }

        @Override
        public void e() {
            if (this.b != null) {
                super.e();
            }

        }
    }

    static class b extends PathfinderGoalMeleeAttack {

        private final EntityPanda d;

        public b(EntityPanda entitypanda, double d0, boolean flag) {
            super(entitypanda, d0, flag);
            this.d = entitypanda;
        }

        @Override
        public boolean a() {
            return this.d.eL() && super.a();
        }
    }

    static class h extends ControllerMove {

        private final EntityPanda i;

        public h(EntityPanda entitypanda) {
            super(entitypanda);
            this.i = entitypanda;
        }

        @Override
        public void a() {
            if (this.i.eL()) {
                super.a();
            }
        }
    }

    public static enum Gene {

        NORMAL(0, "normal", false), LAZY(1, "lazy", false), WORRIED(2, "worried", false), PLAYFUL(3, "playful", false), BROWN(4, "brown", true), WEAK(5, "weak", true), AGGRESSIVE(6, "aggressive", false);

        private static final EntityPanda.Gene[] h = (EntityPanda.Gene[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EntityPanda.Gene::a)).toArray((i) -> {
            return new EntityPanda.Gene[i];
        });
        private final int i;
        private final String j;
        private final boolean k;

        private Gene(int i, String s, boolean flag) {
            this.i = i;
            this.j = s;
            this.k = flag;
        }

        public int a() {
            return this.i;
        }

        public String b() {
            return this.j;
        }

        public boolean isRecessive() {
            return this.k;
        }

        private static EntityPanda.Gene b(EntityPanda.Gene entitypanda_gene, EntityPanda.Gene entitypanda_gene1) {
            return entitypanda_gene.isRecessive() ? (entitypanda_gene == entitypanda_gene1 ? entitypanda_gene : EntityPanda.Gene.NORMAL) : entitypanda_gene;
        }

        public static EntityPanda.Gene a(int i) {
            if (i < 0 || i >= EntityPanda.Gene.h.length) {
                i = 0;
            }

            return EntityPanda.Gene.h[i];
        }

        public static EntityPanda.Gene a(String s) {
            EntityPanda.Gene[] aentitypanda_gene = values();
            int i = aentitypanda_gene.length;

            for (int j = 0; j < i; ++j) {
                EntityPanda.Gene entitypanda_gene = aentitypanda_gene[j];

                if (entitypanda_gene.j.equals(s)) {
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
}
