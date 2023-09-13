package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class EntityBee extends EntityAnimal implements EntityBird {

    private static final DataWatcherObject<Byte> bw = DataWatcher.a(EntityBee.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityBee.class, DataWatcherRegistry.b);
    private UUID hurtBy;
    private float bz;
    private float bA;
    private int bB;
    private int ticksSincePollination;
    private int cannotEnterHiveTicks;
    private int numCropsGrownSincePollination;
    private int bF = 0;
    private int bG = 0;
    @Nullable
    private BlockPosition flowerPos = null;
    @Nullable
    public BlockPosition hivePos = null;
    private EntityBee.k bJ;
    private EntityBee.e bK;
    private EntityBee.f bL;
    private int bM;

    public EntityBee(EntityTypes<? extends EntityBee> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new ControllerMoveFlying(this, 20, true);
        this.lookController = new EntityBee.j(this);
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.COCOA, -1.0F);
        this.a(PathType.FENCE, -1.0F);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityBee.bw, (byte) 0);
        this.datawatcher.register(EntityBee.bx, 0);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntityBee.b(this, 1.399999976158142D, true));
        this.goalSelector.a(1, new EntityBee.d());
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.a(TagsItem.FLOWERS), false));
        this.bJ = new EntityBee.k();
        this.goalSelector.a(4, this.bJ);
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new EntityBee.i());
        this.bK = new EntityBee.e();
        this.goalSelector.a(5, this.bK);
        this.bL = new EntityBee.f();
        this.goalSelector.a(6, this.bL);
        this.goalSelector.a(7, new EntityBee.g());
        this.goalSelector.a(8, new EntityBee.l());
        this.goalSelector.a(9, new PathfinderGoalFloat(this));
        this.targetSelector.a(1, (new EntityBee.h(this)).a(new Class[0]));
        this.targetSelector.a(2, new EntityBee.c(this));
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.hasHivePos()) {
            nbttagcompound.set("HivePos", GameProfileSerializer.a(this.getHivePos()));
        }

        if (this.hasFlowerPos()) {
            nbttagcompound.set("FlowerPos", GameProfileSerializer.a(this.getFlowerPos()));
        }

        nbttagcompound.setBoolean("HasNectar", this.hasNectar());
        nbttagcompound.setBoolean("HasStung", this.hasStung());
        nbttagcompound.setInt("TicksSincePollination", this.ticksSincePollination);
        nbttagcompound.setInt("CannotEnterHiveTicks", this.cannotEnterHiveTicks);
        nbttagcompound.setInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
        nbttagcompound.setInt("Anger", this.getAnger());
        if (this.hurtBy != null) {
            nbttagcompound.setString("HurtBy", this.hurtBy.toString());
        } else {
            nbttagcompound.setString("HurtBy", "");
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.hivePos = null;
        if (nbttagcompound.hasKey("HivePos")) {
            this.hivePos = GameProfileSerializer.c(nbttagcompound.getCompound("HivePos"));
        }

        this.flowerPos = null;
        if (nbttagcompound.hasKey("FlowerPos")) {
            this.flowerPos = GameProfileSerializer.c(nbttagcompound.getCompound("FlowerPos"));
        }

        super.a(nbttagcompound);
        this.setHasNectar(nbttagcompound.getBoolean("HasNectar"));
        this.setHasStung(nbttagcompound.getBoolean("HasStung"));
        this.setAnger(nbttagcompound.getInt("Anger"));
        this.ticksSincePollination = nbttagcompound.getInt("TicksSincePollination");
        this.cannotEnterHiveTicks = nbttagcompound.getInt("CannotEnterHiveTicks");
        this.numCropsGrownSincePollination = nbttagcompound.getInt("CropsGrownSincePollination");
        String s = nbttagcompound.getString("HurtBy");

        if (!s.isEmpty()) {
            this.hurtBy = UUID.fromString(s);
            EntityHuman entityhuman = this.world.b(this.hurtBy);

            this.setLastDamager(entityhuman);
            if (entityhuman != null) {
                this.killer = entityhuman;
                this.lastDamageByPlayerTime = this.cI();
            }
        }

    }

    @Override
    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.a((EntityLiving) this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).q(((EntityLiving) entity).df() + 1);
                byte b0 = 0;

                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 10;
                } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 18;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.POISON, b0 * 20, 0));
                }
            }

            this.setHasStung(true);
            this.setGoalTarget((EntityLiving) null);
            this.a(SoundEffects.ENTITY_BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getNumCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.a(this.world, this.locX() - 0.30000001192092896D, this.locX() + 0.30000001192092896D, this.locZ() - 0.30000001192092896D, this.locZ() + 0.30000001192092896D, this.e(0.5D), Particles.FALLING_NECTAR);
            }
        }

        this.eJ();
    }

    private void a(World world, double d0, double d1, double d2, double d3, double d4, ParticleParam particleparam) {
        world.addParticle(particleparam, MathHelper.d(world.random.nextDouble(), d0, d1), d4, MathHelper.d(world.random.nextDouble(), d2, d3), 0.0D, 0.0D, 0.0D);
    }

    private void h(BlockPosition blockposition) {
        Vec3D vec3d = new Vec3D(blockposition);
        byte b0 = 0;
        BlockPosition blockposition1 = new BlockPosition(this);
        int i = (int) vec3d.y - blockposition1.getY();

        if (i > 2) {
            b0 = 4;
        } else if (i < -2) {
            b0 = -4;
        }

        int j = 6;
        int k = 8;
        int l = blockposition1.n(blockposition);

        if (l < 15) {
            j = l / 2;
            k = l / 2;
        }

        Vec3D vec3d1 = RandomPositionGenerator.b(this, j, k, b0, vec3d, 0.3141592741012573D);

        if (vec3d1 != null) {
            this.navigation.a(0.5F);
            this.navigation.a(vec3d1.x, vec3d1.y, vec3d1.z, 1.0D);
        }
    }

    @Nullable
    public BlockPosition getFlowerPos() {
        return this.flowerPos;
    }

    public boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    public void setFlowerPos(BlockPosition blockposition) {
        this.flowerPos = blockposition;
    }

    private boolean canPollinate() {
        return this.ticksSincePollination > 3600;
    }

    private boolean eI() {
        if (this.cannotEnterHiveTicks <= 0 && !this.bJ.k() && !this.hasStung()) {
            boolean flag = this.canPollinate() || this.world.isRaining() || this.world.isNight() || this.hasNectar();

            return flag && !this.eK();
        } else {
            return false;
        }
    }

    public void setCannotEnterHiveTicks(int i) {
        this.cannotEnterHiveTicks = i;
    }

    private void eJ() {
        this.bA = this.bz;
        if (this.eQ()) {
            this.bz = Math.min(1.0F, this.bz + 0.2F);
        } else {
            this.bz = Math.max(0.0F, this.bz - 0.24F);
        }

    }

    @Override
    public void setLastDamager(@Nullable EntityLiving entityliving) {
        super.setLastDamager(entityliving);
        if (entityliving != null) {
            this.hurtBy = entityliving.getUniqueID();
        }

    }

    @Override
    protected void mobTick() {
        boolean flag = this.hasStung();

        if (this.az()) {
            ++this.bM;
        } else {
            this.bM = 0;
        }

        if (this.bM > 20) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

        if (flag) {
            ++this.bB;
            if (this.bB % 5 == 0 && this.random.nextInt(MathHelper.clamp(1200 - this.bB, 1, 1200)) == 0) {
                this.damageEntity(DamageSource.GENERIC, this.getHealth());
            }
        }

        if (this.isAngry()) {
            int i = this.getAnger();

            this.setAnger(i - 1);
            EntityLiving entityliving = this.getGoalTarget();

            if (i == 0 && entityliving != null) {
                this.a((Entity) entityliving);
            }
        }

        if (!this.hasNectar()) {
            ++this.ticksSincePollination;
        }

    }

    public void eu() {
        this.ticksSincePollination = 0;
    }

    private boolean eK() {
        if (this.hivePos == null) {
            return false;
        } else {
            TileEntity tileentity = this.world.getTileEntity(this.hivePos);

            return tileentity instanceof TileEntityBeehive && ((TileEntityBeehive) tileentity).d();
        }
    }

    public boolean isAngry() {
        return this.getAnger() > 0;
    }

    public int getAnger() {
        return (Integer) this.datawatcher.get(EntityBee.bx);
    }

    public void setAnger(int i) {
        this.datawatcher.set(EntityBee.bx, i);
    }

    private boolean i(BlockPosition blockposition) {
        TileEntity tileentity = this.world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityBeehive ? !((TileEntityBeehive) tileentity).isFull() : false;
    }

    public boolean hasHivePos() {
        return this.hivePos != null;
    }

    @Nullable
    public BlockPosition getHivePos() {
        return this.hivePos;
    }

    @Override
    protected void K() {
        super.K();
        PacketDebug.a(this);
    }

    private int getNumCropsGrownSincePollination() {
        return this.numCropsGrownSincePollination;
    }

    private void eN() {
        this.numCropsGrownSincePollination = 0;
    }

    private void eO() {
        ++this.numCropsGrownSincePollination;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide) {
            if (this.cannotEnterHiveTicks > 0) {
                --this.cannotEnterHiveTicks;
            }

            if (this.bF > 0) {
                --this.bF;
            }

            if (this.bG > 0) {
                --this.bG;
            }

            boolean flag = this.isAngry() && !this.hasStung() && this.getGoalTarget() != null && this.getGoalTarget().h((Entity) this) < 4.0D;

            this.t(flag);
            if (this.ticksLived % 20 == 0 && !this.eP()) {
                this.hivePos = null;
            }
        }

    }

    private boolean eP() {
        if (!this.hasHivePos()) {
            return false;
        } else {
            TileEntity tileentity = this.world.getTileEntity(this.hivePos);

            return tileentity != null && tileentity.getTileType() == TileEntityTypes.BEEHIVE;
        }
    }

    public boolean hasNectar() {
        return this.v(8);
    }

    public void setHasNectar(boolean flag) {
        if (flag) {
            this.eu();
        }

        this.d(8, flag);
    }

    public boolean hasStung() {
        return this.v(4);
    }

    public void setHasStung(boolean flag) {
        this.d(4, flag);
    }

    private boolean eQ() {
        return this.v(2);
    }

    private void t(boolean flag) {
        this.d(2, flag);
    }

    private boolean j(BlockPosition blockposition) {
        return !this.b(blockposition, 48);
    }

    private void d(int i, boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityBee.bw, (byte) ((Byte) this.datawatcher.get(EntityBee.bw) | i));
        } else {
            this.datawatcher.set(EntityBee.bw, (byte) ((Byte) this.datawatcher.get(EntityBee.bw) & ~i));
        }

    }

    private boolean v(int i) {
        return ((Byte) this.datawatcher.get(EntityBee.bw) & i) != 0;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.FLYING_SPEED);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.FLYING_SPEED).setValue(0.6000000238418579D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(48.0D);
    }

    @Override
    protected NavigationAbstract b(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world) {
            @Override
            public boolean a(BlockPosition blockposition) {
                return !this.b.getType(blockposition.down()).isAir();
            }

            @Override
            public void c() {
                if (!EntityBee.this.bJ.k()) {
                    super.c();
                }
            }
        };

        navigationflying.a(false);
        navigationflying.d(false);
        navigationflying.b(true);
        return navigationflying;
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return itemstack.getItem().a(TagsItem.FLOWERS);
    }

    private boolean k(BlockPosition blockposition) {
        return this.world.n(blockposition) && this.world.getType(blockposition).getBlock().a(TagsBlock.FLOWERS);
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {}

    @Override
    protected SoundEffect getSoundAmbient() {
        return null;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_BEE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public EntityBee createChild(EntityAgeable entityageable) {
        return (EntityBee) EntityTypes.BEE.a(this.world);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.5F : entitysize.height * 0.5F;
    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    protected boolean aq() {
        return true;
    }

    public void eG() {
        this.setHasNectar(false);
        this.eN();
    }

    public boolean a(Entity entity) {
        this.setAnger(400 + this.random.nextInt(400));
        if (entity instanceof EntityLiving) {
            this.setLastDamager((EntityLiving) entity);
        }

        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            if (!this.world.isClientSide && entity instanceof EntityHuman && !((EntityHuman) entity).isCreative() && this.hasLineOfSight(entity) && !this.isNoAI()) {
                this.bJ.l();
                this.a(entity);
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    @Override
    protected void c(Tag<FluidType> tag) {
        this.setMot(this.getMot().add(0.0D, 0.01D, 0.0D));
    }

    private boolean b(BlockPosition blockposition, int i) {
        return blockposition.a((BaseBlockPosition) (new BlockPosition(this)), (double) i);
    }

    class d extends EntityBee.a {

        private d() {
            super(null);
        }

        @Override
        public boolean g() {
            if (EntityBee.this.hasHivePos() && EntityBee.this.eI() && EntityBee.this.hivePos.a((IPosition) EntityBee.this.getPositionVector(), 2.0D)) {
                TileEntity tileentity = EntityBee.this.world.getTileEntity(EntityBee.this.hivePos);

                if (tileentity instanceof TileEntityBeehive) {
                    TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                    if (!tileentitybeehive.isFull()) {
                        return true;
                    }

                    EntityBee.this.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean h() {
            return false;
        }

        @Override
        public void c() {
            TileEntity tileentity = EntityBee.this.world.getTileEntity(EntityBee.this.hivePos);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.a(EntityBee.this, EntityBee.this.hasNectar());
            }

        }
    }

    class b extends PathfinderGoalMeleeAttack {

        b(EntityCreature entitycreature, double d0, boolean flag) {
            super(entitycreature, d0, flag);
        }

        @Override
        public boolean a() {
            return super.a() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
        }

        @Override
        public boolean b() {
            return super.b() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
        }
    }

    class g extends EntityBee.a {

        private g() {
            super(null);
        }

        @Override
        public boolean g() {
            return EntityBee.this.getNumCropsGrownSincePollination() >= 10 ? false : (EntityBee.this.random.nextFloat() < 0.3F ? false : EntityBee.this.hasNectar() && EntityBee.this.eP());
        }

        @Override
        public boolean h() {
            return this.g();
        }

        @Override
        public void e() {
            if (EntityBee.this.random.nextInt(30) == 0) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPosition blockposition = (new BlockPosition(EntityBee.this)).down(i);
                    IBlockData iblockdata = EntityBee.this.world.getType(blockposition);
                    Block block = iblockdata.getBlock();
                    boolean flag = false;
                    BlockStateInteger blockstateinteger = null;

                    if (block.a(TagsBlock.BEE_GROWABLES)) {
                        if (block instanceof BlockCrops) {
                            BlockCrops blockcrops = (BlockCrops) block;

                            if (!blockcrops.isRipe(iblockdata)) {
                                flag = true;
                                blockstateinteger = blockcrops.c();
                            }
                        } else {
                            int j;

                            if (block instanceof BlockStem) {
                                j = (Integer) iblockdata.get(BlockStem.AGE);
                                if (j < 7) {
                                    flag = true;
                                    blockstateinteger = BlockStem.AGE;
                                }
                            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                                j = (Integer) iblockdata.get(BlockSweetBerryBush.a);
                                if (j < 3) {
                                    flag = true;
                                    blockstateinteger = BlockSweetBerryBush.a;
                                }
                            }
                        }

                        if (flag) {
                            EntityBee.this.world.triggerEffect(2005, blockposition, 0);
                            EntityBee.this.world.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(blockstateinteger, (Integer) iblockdata.get(blockstateinteger) + 1));
                            EntityBee.this.eO();
                        }
                    }
                }

            }
        }
    }

    class i extends EntityBee.a {

        private i() {
            super(null);
        }

        @Override
        public boolean g() {
            return EntityBee.this.bF == 0 && !EntityBee.this.hasHivePos() && EntityBee.this.eI();
        }

        @Override
        public boolean h() {
            return false;
        }

        @Override
        public void c() {
            EntityBee.this.bF = 200;
            List<BlockPosition> list = this.j();

            if (!list.isEmpty()) {
                Iterator iterator = list.iterator();

                BlockPosition blockposition;

                do {
                    if (!iterator.hasNext()) {
                        EntityBee.this.bK.j();
                        EntityBee.this.hivePos = (BlockPosition) list.get(0);
                        return;
                    }

                    blockposition = (BlockPosition) iterator.next();
                } while (EntityBee.this.bK.b(blockposition));

                EntityBee.this.hivePos = blockposition;
            }
        }

        private List<BlockPosition> j() {
            BlockPosition blockposition = new BlockPosition(EntityBee.this);
            VillagePlace villageplace = ((WorldServer) EntityBee.this.world).B();
            Stream<VillagePlaceRecord> stream = villageplace.c((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.s || villageplacetype == VillagePlaceType.t;
            }, blockposition, 20, VillagePlace.Occupancy.ANY);

            return (List) stream.map(VillagePlaceRecord::f).filter((blockposition1) -> {
                return EntityBee.this.i(blockposition1);
            }).sorted(Comparator.comparingDouble((blockposition1) -> {
                return blockposition1.m(blockposition);
            })).collect(Collectors.toList());
        }
    }

    class k extends EntityBee.a {

        private final Predicate<IBlockData> c = (iblockdata) -> {
            return iblockdata.a(TagsBlock.TALL_FLOWERS) ? (iblockdata.getBlock() == Blocks.SUNFLOWER ? iblockdata.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.UPPER : true) : iblockdata.a(TagsBlock.SMALL_FLOWERS);
        };
        private int d = 0;
        private int e = 0;
        private boolean f;
        private Vec3D g;
        private int h = 0;

        k() {
            super(null);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean g() {
            if (EntityBee.this.bG > 0) {
                return false;
            } else if (EntityBee.this.hasNectar()) {
                return false;
            } else if (EntityBee.this.world.isRaining()) {
                return false;
            } else if (EntityBee.this.random.nextFloat() < 0.7F) {
                return false;
            } else {
                Optional<BlockPosition> optional = this.o();

                if (optional.isPresent()) {
                    EntityBee.this.flowerPos = (BlockPosition) optional.get();
                    EntityBee.this.navigation.a((double) EntityBee.this.flowerPos.getX() + 0.5D, (double) EntityBee.this.flowerPos.getY() + 0.5D, (double) EntityBee.this.flowerPos.getZ() + 0.5D, 1.2000000476837158D);
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean h() {
            if (!this.f) {
                return false;
            } else if (!EntityBee.this.hasFlowerPos()) {
                return false;
            } else if (EntityBee.this.world.isRaining()) {
                return false;
            } else if (this.j()) {
                return EntityBee.this.random.nextFloat() < 0.2F;
            } else if (EntityBee.this.ticksLived % 20 == 0 && !EntityBee.this.k(EntityBee.this.flowerPos)) {
                EntityBee.this.flowerPos = null;
                return false;
            } else {
                return true;
            }
        }

        private boolean j() {
            return this.d > 400;
        }

        private boolean k() {
            return this.f;
        }

        private void l() {
            this.f = false;
        }

        @Override
        public void c() {
            this.d = 0;
            this.h = 0;
            this.e = 0;
            this.f = true;
            EntityBee.this.eu();
        }

        @Override
        public void d() {
            if (this.j()) {
                EntityBee.this.setHasNectar(true);
            }

            this.f = false;
            EntityBee.this.navigation.o();
            EntityBee.this.bG = 200;
        }

        @Override
        public void e() {
            ++this.h;
            if (this.h > 600) {
                EntityBee.this.flowerPos = null;
            } else {
                Vec3D vec3d = (new Vec3D(EntityBee.this.flowerPos)).add(0.5D, 0.6000000238418579D, 0.5D);

                if (vec3d.f(EntityBee.this.getPositionVector()) > 1.0D) {
                    this.g = vec3d;
                    this.m();
                } else {
                    if (this.g == null) {
                        this.g = vec3d;
                    }

                    boolean flag = EntityBee.this.getPositionVector().f(this.g) <= 0.1D;
                    boolean flag1 = true;

                    if (!flag && this.h > 600) {
                        EntityBee.this.flowerPos = null;
                    } else {
                        if (flag) {
                            boolean flag2 = EntityBee.this.random.nextInt(100) == 0;

                            if (flag2) {
                                this.g = new Vec3D(vec3d.getX() + (double) this.n(), vec3d.getY(), vec3d.getZ() + (double) this.n());
                                EntityBee.this.navigation.o();
                            } else {
                                flag1 = false;
                            }

                            EntityBee.this.getControllerLook().a(vec3d.getX(), vec3d.getY(), vec3d.getZ());
                        }

                        if (flag1) {
                            this.m();
                        }

                        ++this.d;
                        if (EntityBee.this.random.nextFloat() < 0.05F && this.d > this.e + 60) {
                            this.e = this.d;
                            EntityBee.this.a(SoundEffects.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
                        }

                    }
                }
            }
        }

        private void m() {
            EntityBee.this.getControllerMove().a(this.g.getX(), this.g.getY(), this.g.getZ(), 0.3499999940395355D);
        }

        private float n() {
            return (EntityBee.this.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
        }

        private Optional<BlockPosition> o() {
            return this.a(this.c, 5.0D);
        }

        private Optional<BlockPosition> a(Predicate<IBlockData> predicate, double d0) {
            BlockPosition blockposition = new BlockPosition(EntityBee.this);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = 0; (double) i <= d0; i = i > 0 ? -i : 1 - i) {
                for (int j = 0; (double) j < d0; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.g(blockposition).e(k, i - 1, l);
                            if (blockposition.a((BaseBlockPosition) blockposition_mutableblockposition, d0) && predicate.test(EntityBee.this.world.getType(blockposition_mutableblockposition))) {
                                return Optional.of(blockposition_mutableblockposition);
                            }
                        }
                    }
                }
            }

            return Optional.empty();
        }
    }

    class j extends ControllerLook {

        j(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void a() {
            if (!EntityBee.this.isAngry()) {
                super.a();
            }
        }

        @Override
        protected boolean b() {
            return !EntityBee.this.bJ.k();
        }
    }

    public class f extends EntityBee.a {

        private int c;

        f() {
            super(null);
            this.c = EntityBee.this.world.random.nextInt(10);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean g() {
            return EntityBee.this.flowerPos != null && !EntityBee.this.eg() && this.j() && EntityBee.this.k(EntityBee.this.flowerPos) && !EntityBee.this.b(EntityBee.this.flowerPos, 2);
        }

        @Override
        public boolean h() {
            return this.g();
        }

        @Override
        public void c() {
            this.c = 0;
            super.c();
        }

        @Override
        public void d() {
            this.c = 0;
            EntityBee.this.navigation.o();
            EntityBee.this.navigation.g();
        }

        @Override
        public void e() {
            if (EntityBee.this.flowerPos != null) {
                ++this.c;
                if (this.c > 600) {
                    EntityBee.this.flowerPos = null;
                } else if (!EntityBee.this.navigation.n()) {
                    if (EntityBee.this.j(EntityBee.this.flowerPos)) {
                        EntityBee.this.flowerPos = null;
                    } else {
                        EntityBee.this.h(EntityBee.this.flowerPos);
                    }
                }
            }
        }

        private boolean j() {
            return EntityBee.this.ticksSincePollination > 2400;
        }
    }

    public class e extends EntityBee.a {

        private int c;
        private List<BlockPosition> d;
        @Nullable
        private PathEntity e;

        e() {
            super(null);
            this.c = EntityBee.this.world.random.nextInt(10);
            this.d = Lists.newArrayList();
            this.e = null;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean g() {
            return EntityBee.this.hivePos != null && !EntityBee.this.eg() && EntityBee.this.eI() && !this.d(EntityBee.this.hivePos) && EntityBee.this.world.getType(EntityBee.this.hivePos).a(TagsBlock.BEEHIVES);
        }

        @Override
        public boolean h() {
            return this.g();
        }

        @Override
        public void c() {
            this.c = 0;
            super.c();
        }

        @Override
        public void d() {
            this.c = 0;
            EntityBee.this.navigation.o();
            EntityBee.this.navigation.g();
        }

        @Override
        public void e() {
            if (EntityBee.this.hivePos != null) {
                ++this.c;
                if (this.c > 600) {
                    this.k();
                } else if (!EntityBee.this.navigation.n()) {
                    if (!EntityBee.this.b(EntityBee.this.hivePos, 16)) {
                        if (EntityBee.this.j(EntityBee.this.hivePos)) {
                            this.l();
                        } else {
                            EntityBee.this.h(EntityBee.this.hivePos);
                        }
                    } else {
                        boolean flag = this.a(EntityBee.this.hivePos);

                        if (!flag) {
                            this.k();
                        } else if (this.e != null && EntityBee.this.navigation.k().a(this.e)) {
                            this.l();
                        } else {
                            this.e = EntityBee.this.navigation.k();
                        }

                    }
                }
            }
        }

        private boolean a(BlockPosition blockposition) {
            EntityBee.this.navigation.a(10.0F);
            EntityBee.this.navigation.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
            return EntityBee.this.navigation.k() != null && EntityBee.this.navigation.k().h();
        }

        private boolean b(BlockPosition blockposition) {
            return this.d.contains(blockposition);
        }

        private void c(BlockPosition blockposition) {
            this.d.add(blockposition);

            while (this.d.size() > 3) {
                this.d.remove(0);
            }

        }

        private void j() {
            this.d.clear();
        }

        private void k() {
            if (EntityBee.this.hivePos != null) {
                this.c(EntityBee.this.hivePos);
            }

            this.l();
        }

        private void l() {
            EntityBee.this.hivePos = null;
            EntityBee.this.bF = 200;
        }

        private boolean d(BlockPosition blockposition) {
            if (EntityBee.this.b(blockposition, 2)) {
                return true;
            } else {
                PathEntity pathentity = EntityBee.this.navigation.k();

                return pathentity != null && pathentity.k().equals(blockposition) && pathentity.h() && pathentity.b();
            }
        }
    }

    class l extends PathfinderGoal {

        l() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return EntityBee.this.navigation.m() && EntityBee.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean b() {
            return EntityBee.this.navigation.n();
        }

        @Override
        public void c() {
            Vec3D vec3d = this.g();

            if (vec3d != null) {
                EntityBee.this.navigation.a(EntityBee.this.navigation.a(new BlockPosition(vec3d), 1), 1.0D);
            }

        }

        @Nullable
        private Vec3D g() {
            Vec3D vec3d;

            if (EntityBee.this.eP() && !EntityBee.this.b(EntityBee.this.hivePos, 40)) {
                Vec3D vec3d1 = new Vec3D(EntityBee.this.hivePos);

                vec3d = vec3d1.d(EntityBee.this.getPositionVector()).d();
            } else {
                vec3d = EntityBee.this.f(0.0F);
            }

            boolean flag = true;
            Vec3D vec3d2 = RandomPositionGenerator.a(EntityBee.this, 8, 7, vec3d, 1.5707964F, 2, 1);

            return vec3d2 != null ? vec3d2 : RandomPositionGenerator.a((EntityCreature) EntityBee.this, 8, 4, -2, vec3d, 1.5707963705062866D);
        }
    }

    abstract class a extends PathfinderGoal {

        private a() {}

        public abstract boolean g();

        public abstract boolean h();

        @Override
        public boolean a() {
            return this.g() && !EntityBee.this.isAngry();
        }

        @Override
        public boolean b() {
            return this.h() && !EntityBee.this.isAngry();
        }
    }

    static class c extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        c(EntityBee entitybee) {
            super(entitybee, EntityHuman.class, true);
        }

        @Override
        public boolean a() {
            return this.h() && super.a();
        }

        @Override
        public boolean b() {
            boolean flag = this.h();

            if (flag && this.e.getGoalTarget() != null) {
                return super.b();
            } else {
                this.g = null;
                return false;
            }
        }

        private boolean h() {
            EntityBee entitybee = (EntityBee) this.e;

            return entitybee.isAngry() && !entitybee.hasStung();
        }
    }

    class h extends PathfinderGoalHurtByTarget {

        h(EntityBee entitybee) {
            super(entitybee);
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityBee && this.e.hasLineOfSight(entityliving) && ((EntityBee) entityinsentient).a((Entity) entityliving)) {
                entityinsentient.setGoalTarget(entityliving);
            }

        }
    }
}
