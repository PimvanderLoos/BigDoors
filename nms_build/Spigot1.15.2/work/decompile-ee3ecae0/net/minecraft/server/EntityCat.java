package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityCat extends EntityTameableAnimal {

    private static final RecipeItemStack bA = RecipeItemStack.a(Items.COD, Items.SALMON);
    private static final DataWatcherObject<Integer> bB = DataWatcher.a(EntityCat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityCat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bD = DataWatcher.a(EntityCat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bE = DataWatcher.a(EntityCat.class, DataWatcherRegistry.b);
    public static final Map<Integer, MinecraftKey> bz = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(0, new MinecraftKey("textures/entity/cat/tabby.png"));
        hashmap.put(1, new MinecraftKey("textures/entity/cat/black.png"));
        hashmap.put(2, new MinecraftKey("textures/entity/cat/red.png"));
        hashmap.put(3, new MinecraftKey("textures/entity/cat/siamese.png"));
        hashmap.put(4, new MinecraftKey("textures/entity/cat/british_shorthair.png"));
        hashmap.put(5, new MinecraftKey("textures/entity/cat/calico.png"));
        hashmap.put(6, new MinecraftKey("textures/entity/cat/persian.png"));
        hashmap.put(7, new MinecraftKey("textures/entity/cat/ragdoll.png"));
        hashmap.put(8, new MinecraftKey("textures/entity/cat/white.png"));
        hashmap.put(9, new MinecraftKey("textures/entity/cat/jellie.png"));
        hashmap.put(10, new MinecraftKey("textures/entity/cat/all_black.png"));
    });
    private EntityCat.a<EntityHuman> bF;
    private PathfinderGoalTempt bG;
    private float bH;
    private float bI;
    private float bJ;
    private float bK;
    private float bL;
    private float bM;

    public EntityCat(EntityTypes<? extends EntityCat> entitytypes, World world) {
        super(entitytypes, world);
    }

    public MinecraftKey ez() {
        return (MinecraftKey) EntityCat.bz.getOrDefault(this.getCatType(), EntityCat.bz.get(0));
    }

    @Override
    protected void initPathfinder() {
        this.goalSit = new PathfinderGoalSit(this);
        this.bG = new EntityCat.PathfinderGoalTemptChance(this, 0.6D, EntityCat.bA, true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityCat.b(this));
        this.goalSelector.a(2, this.goalSit);
        this.goalSelector.a(3, this.bG);
        this.goalSelector.a(5, new PathfinderGoalCatSitOnBed(this, 1.1D, 8));
        this.goalSelector.a(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.a(7, new PathfinderGoalJumpOnBlock(this, 0.8D));
        this.goalSelector.a(8, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(9, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(10, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(11, new PathfinderGoalRandomStrollLand(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.a(12, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityRabbit.class, false, (Predicate) null));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.bw));
    }

    public int getCatType() {
        return (Integer) this.datawatcher.get(EntityCat.bB);
    }

    public void setCatType(int i) {
        if (i < 0 || i >= 11) {
            i = this.random.nextInt(10);
        }

        this.datawatcher.set(EntityCat.bB, i);
    }

    public void u(boolean flag) {
        this.datawatcher.set(EntityCat.bC, flag);
    }

    public boolean eB() {
        return (Boolean) this.datawatcher.get(EntityCat.bC);
    }

    public void v(boolean flag) {
        this.datawatcher.set(EntityCat.bD, flag);
    }

    public boolean eC() {
        return (Boolean) this.datawatcher.get(EntityCat.bD);
    }

    public EnumColor getCollarColor() {
        return EnumColor.fromColorIndex((Integer) this.datawatcher.get(EntityCat.bE));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.datawatcher.set(EntityCat.bE, enumcolor.getColorIndex());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityCat.bB, 1);
        this.datawatcher.register(EntityCat.bC, false);
        this.datawatcher.register(EntityCat.bD, false);
        this.datawatcher.register(EntityCat.bE, EnumColor.RED.getColorIndex());
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("CatType", this.getCatType());
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getColorIndex());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setCatType(nbttagcompound.getInt("CatType"));
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromColorIndex(nbttagcompound.getInt("CollarColor")));
        }

    }

    @Override
    public void mobTick() {
        if (this.getControllerMove().b()) {
            double d0 = this.getControllerMove().c();

            if (d0 == 0.6D) {
                this.setPose(EntityPose.CROUCHING);
                this.setSprinting(false);
            } else if (d0 == 1.33D) {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
        }

    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isTamed() ? (this.isInLove() ? SoundEffects.ENTITY_CAT_PURR : (this.random.nextInt(4) == 0 ? SoundEffects.ENTITY_CAT_PURREOW : SoundEffects.ENTITY_CAT_AMBIENT)) : SoundEffects.ENTITY_CAT_STRAY_AMBIENT;
    }

    @Override
    public int A() {
        return 120;
    }

    public void eE() {
        this.a(SoundEffects.ENTITY_CAT_HISS, this.getSoundVolume(), this.dn());
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_CAT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_CAT_DEATH;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Override
    protected void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (this.i(itemstack)) {
            this.a(SoundEffects.ENTITY_CAT_EAT, 1.0F, 1.0F);
        }

        super.a(entityhuman, itemstack);
    }

    private float eF() {
        return (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
    }

    @Override
    public boolean B(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), this.eF());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.bG != null && this.bG.h() && !this.isTamed() && this.ticksLived % 100 == 0) {
            this.a(SoundEffects.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
        }

        this.eG();
    }

    private void eG() {
        if ((this.eB() || this.eC()) && this.ticksLived % 5 == 0) {
            this.a(SoundEffects.ENTITY_CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
        }

        this.eH();
        this.eI();
    }

    private void eH() {
        this.bI = this.bH;
        this.bK = this.bJ;
        if (this.eB()) {
            this.bH = Math.min(1.0F, this.bH + 0.15F);
            this.bJ = Math.min(1.0F, this.bJ + 0.08F);
        } else {
            this.bH = Math.max(0.0F, this.bH - 0.22F);
            this.bJ = Math.max(0.0F, this.bJ - 0.13F);
        }

    }

    private void eI() {
        this.bM = this.bL;
        if (this.eC()) {
            this.bL = Math.min(1.0F, this.bL + 0.1F);
        } else {
            this.bL = Math.max(0.0F, this.bL - 0.13F);
        }

    }

    @Override
    public EntityCat createChild(EntityAgeable entityageable) {
        EntityCat entitycat = (EntityCat) EntityTypes.CAT.a(this.world);

        if (entityageable instanceof EntityCat) {
            if (this.random.nextBoolean()) {
                entitycat.setCatType(this.getCatType());
            } else {
                entitycat.setCatType(((EntityCat) entityageable).getCatType());
            }

            if (this.isTamed()) {
                entitycat.setOwnerUUID(this.getOwnerUUID());
                entitycat.setTamed(true);
                if (this.random.nextBoolean()) {
                    entitycat.setCollarColor(this.getCollarColor());
                } else {
                    entitycat.setCollarColor(((EntityCat) entityageable).getCollarColor());
                }
            }
        }

        return entitycat;
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityCat)) {
            return false;
        } else {
            EntityCat entitycat = (EntityCat) entityanimal;

            return entitycat.isTamed() && super.mate(entityanimal);
        }
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        if (generatoraccess.Y() > 0.9F) {
            this.setCatType(this.random.nextInt(11));
        } else {
            this.setCatType(this.random.nextInt(10));
        }

        if (WorldGenerator.SWAMP_HUT.b(generatoraccess, new BlockPosition(this))) {
            this.setCatType(10);
            this.setPersistent();
        }

        return groupdataentity;
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else if (this.world.isClientSide) {
            return this.isTamed() && this.i((EntityLiving) entityhuman) || this.i(itemstack);
        } else {
            boolean flag;

            if (this.isTamed()) {
                if (this.i((EntityLiving) entityhuman)) {
                    if (!(item instanceof ItemDye)) {
                        if (item.isFood() && this.i(itemstack) && this.getHealth() < this.getMaxHealth()) {
                            this.a(entityhuman, itemstack);
                            this.heal((float) item.getFoodInfo().getNutrition());
                            return true;
                        }

                        flag = super.a(entityhuman, enumhand);
                        if (!flag || this.isBaby()) {
                            this.goalSit.setSitting(!this.isSitting());
                        }

                        return flag;
                    }

                    EnumColor enumcolor = ((ItemDye) item).d();

                    if (enumcolor != this.getCollarColor()) {
                        this.setCollarColor(enumcolor);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        this.setPersistent();
                        return true;
                    }
                }
            } else if (this.i(itemstack)) {
                this.a(entityhuman, itemstack);
                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.goalSit.setSitting(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }

                this.setPersistent();
                return true;
            }

            flag = super.a(entityhuman, enumhand);
            if (flag) {
                this.setPersistent();
            }

            return flag;
        }
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return EntityCat.bA.test(itemstack);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isTamed() && this.ticksLived > 2400;
    }

    @Override
    protected void er() {
        if (this.bF == null) {
            this.bF = new EntityCat.a<>(this, EntityHuman.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.a((PathfinderGoal) this.bF);
        if (!this.isTamed()) {
            this.goalSelector.a(4, this.bF);
        }

    }

    static class b extends PathfinderGoal {

        private final EntityCat a;
        private EntityHuman b;
        private BlockPosition c;
        private int d;

        public b(EntityCat entitycat) {
            this.a = entitycat;
        }

        @Override
        public boolean a() {
            if (!this.a.isTamed()) {
                return false;
            } else if (this.a.isSitting()) {
                return false;
            } else {
                EntityLiving entityliving = this.a.getOwner();

                if (entityliving instanceof EntityHuman) {
                    this.b = (EntityHuman) entityliving;
                    if (!entityliving.isSleeping()) {
                        return false;
                    }

                    if (this.a.h((Entity) this.b) > 100.0D) {
                        return false;
                    }

                    BlockPosition blockposition = new BlockPosition(this.b);
                    IBlockData iblockdata = this.a.world.getType(blockposition);

                    if (iblockdata.getBlock().a(TagsBlock.BEDS)) {
                        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBed.FACING);

                        this.c = new BlockPosition(blockposition.getX() - enumdirection.getAdjacentX(), blockposition.getY(), blockposition.getZ() - enumdirection.getAdjacentZ());
                        return !this.g();
                    }
                }

                return false;
            }
        }

        private boolean g() {
            List<EntityCat> list = this.a.world.a(EntityCat.class, (new AxisAlignedBB(this.c)).g(2.0D));
            Iterator iterator = list.iterator();

            EntityCat entitycat;

            do {
                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    entitycat = (EntityCat) iterator.next();
                } while (entitycat == this.a);
            } while (!entitycat.eB() && !entitycat.eC());

            return true;
        }

        @Override
        public boolean b() {
            return this.a.isTamed() && !this.a.isSitting() && this.b != null && this.b.isSleeping() && this.c != null && !this.g();
        }

        @Override
        public void c() {
            if (this.c != null) {
                this.a.getGoalSit().setSitting(false);
                this.a.getNavigation().a((double) this.c.getX(), (double) this.c.getY(), (double) this.c.getZ(), 1.100000023841858D);
            }

        }

        @Override
        public void d() {
            this.a.u(false);
            float f = this.a.world.f(1.0F);

            if (this.b.ef() >= 100 && (double) f > 0.77D && (double) f < 0.8D && (double) this.a.world.getRandom().nextFloat() < 0.7D) {
                this.h();
            }

            this.d = 0;
            this.a.v(false);
            this.a.getNavigation().o();
        }

        private void h() {
            Random random = this.a.getRandom();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            blockposition_mutableblockposition.a((Entity) this.a);
            this.a.a((double) (blockposition_mutableblockposition.getX() + random.nextInt(11) - 5), (double) (blockposition_mutableblockposition.getY() + random.nextInt(5) - 2), (double) (blockposition_mutableblockposition.getZ() + random.nextInt(11) - 5), false);
            blockposition_mutableblockposition.a((Entity) this.a);
            LootTable loottable = this.a.world.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.af);
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.a.world)).set(LootContextParameters.POSITION, blockposition_mutableblockposition).set(LootContextParameters.THIS_ENTITY, this.a).a(random);
            List<ItemStack> list = loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.GIFT));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                this.a.world.addEntity(new EntityItem(this.a.world, (double) ((float) blockposition_mutableblockposition.getX() - MathHelper.sin(this.a.aI * 0.017453292F)), (double) blockposition_mutableblockposition.getY(), (double) ((float) blockposition_mutableblockposition.getZ() + MathHelper.cos(this.a.aI * 0.017453292F)), itemstack));
            }

        }

        @Override
        public void e() {
            if (this.b != null && this.c != null) {
                this.a.getGoalSit().setSitting(false);
                this.a.getNavigation().a((double) this.c.getX(), (double) this.c.getY(), (double) this.c.getZ(), 1.100000023841858D);
                if (this.a.h((Entity) this.b) < 2.5D) {
                    ++this.d;
                    if (this.d > 16) {
                        this.a.u(true);
                        this.a.v(false);
                    } else {
                        this.a.a((Entity) this.b, 45.0F, 45.0F);
                        this.a.v(true);
                    }
                } else {
                    this.a.u(false);
                }
            }

        }
    }

    static class PathfinderGoalTemptChance extends PathfinderGoalTempt {

        @Nullable
        private EntityHuman chosenTarget;
        private final EntityCat d;

        public PathfinderGoalTemptChance(EntityCat entitycat, double d0, RecipeItemStack recipeitemstack, boolean flag) {
            super(entitycat, d0, recipeitemstack, flag);
            this.d = entitycat;
        }

        @Override
        public void e() {
            super.e();
            if (this.chosenTarget == null && this.a.getRandom().nextInt(600) == 0) {
                this.chosenTarget = this.target;
            } else if (this.a.getRandom().nextInt(500) == 0) {
                this.chosenTarget = null;
            }

        }

        @Override
        protected boolean g() {
            return this.chosenTarget != null && this.chosenTarget.equals(this.target) ? false : super.g();
        }

        @Override
        public boolean a() {
            return super.a() && !this.d.isTamed();
        }
    }

    static class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityCat i;

        public a(EntityCat entitycat, Class<T> oclass, float f, double d0, double d1) {
            Predicate predicate = IEntitySelector.e;

            super(entitycat, oclass, f, d0, d1, predicate::test);
            this.i = entitycat;
        }

        @Override
        public boolean a() {
            return !this.i.isTamed() && super.a();
        }

        @Override
        public boolean b() {
            return !this.i.isTamed() && super.b();
        }
    }
}
