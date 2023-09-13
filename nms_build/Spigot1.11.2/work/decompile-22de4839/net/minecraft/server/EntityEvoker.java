package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;

public class EntityEvoker extends EntityMonster {

    protected static final DataWatcherObject<Byte> a = DataWatcher.a(EntityEvoker.class, DataWatcherRegistry.a);
    private int b;
    private int c;
    private EntitySheep bw;

    public EntityEvoker(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
        this.b_ = 10;
    }

    protected void r() {
        super.r();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityEvoker.b());
        this.goalSelector.a(2, new PathfinderGoalAvoidTarget(this, EntityHuman.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.a(4, new EntityEvoker.c(null));
        this.goalSelector.a(5, new EntityEvoker.a(null));
        this.goalSelector.a(6, new EntityEvoker.e());
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityEvoker.class}));
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true)).b(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, false)).b(300));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, false));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(12.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(24.0D);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityEvoker.a, Byte.valueOf((byte) 0));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityEvoker.class);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.b = nbttagcompound.getInt("SpellTicks");
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("SpellTicks", this.b);
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ILLAGER;
    }

    protected MinecraftKey J() {
        return LootTables.au;
    }

    public boolean o() {
        return this.world.isClientSide ? ((Byte) this.datawatcher.get(EntityEvoker.a)).byteValue() > 0 : this.b > 0;
    }

    public void a(int i) {
        this.datawatcher.set(EntityEvoker.a, Byte.valueOf((byte) i));
    }

    private int di() {
        return this.b;
    }

    protected void M() {
        super.M();
        if (this.b > 0) {
            --this.b;
        }

    }

    public void A_() {
        super.A_();
        if (this.world.isClientSide && this.o()) {
            byte b0 = ((Byte) this.datawatcher.get(EntityEvoker.a)).byteValue();
            double d0 = 0.7D;
            double d1 = 0.5D;
            double d2 = 0.2D;

            if (b0 == 2) {
                d0 = 0.4D;
                d1 = 0.3D;
                d2 = 0.35D;
            } else if (b0 == 1) {
                d0 = 0.7D;
                d1 = 0.7D;
                d2 = 0.8D;
            }

            float f = this.aN * 0.017453292F + MathHelper.cos((float) this.ticksLived * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);

            this.world.addParticle(EnumParticle.SPELL_MOB, this.locX + (double) f1 * 0.6D, this.locY + 1.8D, this.locZ + (double) f2 * 0.6D, d0, d1, d2, new int[0]);
            this.world.addParticle(EnumParticle.SPELL_MOB, this.locX - (double) f1 * 0.6D, this.locY + 1.8D, this.locZ - (double) f2 * 0.6D, d0, d1, d2, new int[0]);
        }

    }

    public boolean r(Entity entity) {
        return entity == null ? false : (entity == this ? true : (super.r(entity) ? true : (entity instanceof EntityVex ? this.r(((EntityVex) entity).o()) : (entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == EnumMonsterType.ILLAGER ? this.aQ() == null && entity.aQ() == null : false))));
    }

    protected SoundEffect G() {
        return SoundEffects.bm;
    }

    protected SoundEffect bX() {
        return SoundEffects.bo;
    }

    protected SoundEffect bW() {
        return SoundEffects.bp;
    }

    private void a(@Nullable EntitySheep entitysheep) {
        this.bw = entitysheep;
    }

    @Nullable
    private EntitySheep dj() {
        return this.bw;
    }

    public class e extends EntityEvoker.d {

        final Predicate<EntitySheep> a = new Predicate() {
            public boolean a(EntitySheep entitysheep) {
                return entitysheep.getColor() == EnumColor.BLUE;
            }

            public boolean apply(Object object) {
                return this.a((EntitySheep) object);
            }
        };

        public e() {
            super(null);
        }

        public boolean a() {
            if (EntityEvoker.this.getGoalTarget() != null) {
                return false;
            } else if (EntityEvoker.this.o()) {
                return false;
            } else if (EntityEvoker.this.ticksLived < this.c) {
                return false;
            } else if (!EntityEvoker.this.world.getGameRules().getBoolean("mobGriefing")) {
                return false;
            } else {
                List list = EntityEvoker.this.world.a(EntitySheep.class, EntityEvoker.this.getBoundingBox().grow(16.0D, 4.0D, 16.0D), this.a);

                if (list.isEmpty()) {
                    return false;
                } else {
                    EntityEvoker.this.a((EntitySheep) list.get(EntityEvoker.this.random.nextInt(list.size())));
                    return true;
                }
            }
        }

        public boolean b() {
            return EntityEvoker.this.dj() != null && this.b > 0;
        }

        public void d() {
            super.d();
            EntityEvoker.this.a((EntitySheep) null);
        }

        protected void j() {
            EntitySheep entitysheep = EntityEvoker.this.dj();

            if (entitysheep != null && entitysheep.isAlive()) {
                entitysheep.setColor(EnumColor.RED);
            }

        }

        protected int m() {
            return 40;
        }

        protected int f() {
            return 60;
        }

        protected int i() {
            return 140;
        }

        protected SoundEffect k() {
            return SoundEffects.bs;
        }

        protected int l() {
            return 3;
        }
    }

    class c extends EntityEvoker.d {

        private c() {
            super(null);
        }

        public boolean a() {
            if (!super.a()) {
                return false;
            } else {
                int i = EntityEvoker.this.world.a(EntityVex.class, EntityEvoker.this.getBoundingBox().g(16.0D)).size();

                return EntityEvoker.this.random.nextInt(8) + 1 > i;
            }
        }

        protected int f() {
            return 100;
        }

        protected int i() {
            return 340;
        }

        protected void j() {
            for (int i = 0; i < 3; ++i) {
                BlockPosition blockposition = (new BlockPosition(EntityEvoker.this)).a(-2 + EntityEvoker.this.random.nextInt(5), 1, -2 + EntityEvoker.this.random.nextInt(5));
                EntityVex entityvex = new EntityVex(EntityEvoker.this.world);

                entityvex.setPositionRotation(blockposition, 0.0F, 0.0F);
                entityvex.prepare(EntityEvoker.this.world.D(blockposition), (GroupDataEntity) null);
                entityvex.a((EntityInsentient) EntityEvoker.this);
                entityvex.g(blockposition);
                entityvex.a(20 * (30 + EntityEvoker.this.random.nextInt(90)));
                EntityEvoker.this.world.addEntity(entityvex);
            }

        }

        protected SoundEffect k() {
            return SoundEffects.br;
        }

        protected int l() {
            return 1;
        }

        c(Object object) {
            this();
        }
    }

    class a extends EntityEvoker.d {

        private a() {
            super(null);
        }

        protected int f() {
            return 40;
        }

        protected int i() {
            return 100;
        }

        protected void j() {
            EntityLiving entityliving = EntityEvoker.this.getGoalTarget();
            double d0 = Math.min(entityliving.locY, EntityEvoker.this.locY);
            double d1 = Math.max(entityliving.locY, EntityEvoker.this.locY) + 1.0D;
            float f = (float) MathHelper.c(entityliving.locZ - EntityEvoker.this.locZ, entityliving.locX - EntityEvoker.this.locX);
            int i;

            if (EntityEvoker.this.h((Entity) entityliving) < 9.0D) {
                float f1;

                for (i = 0; i < 5; ++i) {
                    f1 = f + (float) i * 3.1415927F * 0.4F;
                    this.a(EntityEvoker.this.locX + (double) MathHelper.cos(f1) * 1.5D, EntityEvoker.this.locZ + (double) MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for (i = 0; i < 8; ++i) {
                    f1 = f + (float) i * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
                    this.a(EntityEvoker.this.locX + (double) MathHelper.cos(f1) * 2.5D, EntityEvoker.this.locZ + (double) MathHelper.sin(f1) * 2.5D, d0, d1, f1, 3);
                }
            } else {
                for (i = 0; i < 16; ++i) {
                    double d2 = 1.25D * (double) (i + 1);
                    int j = 1 * i;

                    this.a(EntityEvoker.this.locX + (double) MathHelper.cos(f) * d2, EntityEvoker.this.locZ + (double) MathHelper.sin(f) * d2, d0, d1, f, j);
                }
            }

        }

        private void a(double d0, double d1, double d2, double d3, float f, int i) {
            BlockPosition blockposition = new BlockPosition(d0, d3, d1);
            boolean flag = false;
            double d4 = 0.0D;

            do {
                if (!EntityEvoker.this.world.d(blockposition, true) && EntityEvoker.this.world.d(blockposition.down(), true)) {
                    if (!EntityEvoker.this.world.isEmpty(blockposition)) {
                        IBlockData iblockdata = EntityEvoker.this.world.getType(blockposition);
                        AxisAlignedBB axisalignedbb = iblockdata.c(EntityEvoker.this.world, blockposition);

                        if (axisalignedbb != null) {
                            d4 = axisalignedbb.e;
                        }
                    }

                    flag = true;
                    break;
                }

                blockposition = blockposition.down();
            } while (blockposition.getY() >= MathHelper.floor(d2) - 1);

            if (flag) {
                EntityEvokerFangs entityevokerfangs = new EntityEvokerFangs(EntityEvoker.this.world, d0, (double) blockposition.getY() + d4, d1, f, i, EntityEvoker.this);

                EntityEvoker.this.world.addEntity(entityevokerfangs);
            }

        }

        protected SoundEffect k() {
            return SoundEffects.bq;
        }

        protected int l() {
            return 2;
        }

        a(Object object) {
            this();
        }
    }

    abstract class d extends PathfinderGoal {

        protected int b;
        protected int c;

        private d() {}

        public boolean a() {
            return EntityEvoker.this.getGoalTarget() == null ? false : (EntityEvoker.this.o() ? false : EntityEvoker.this.ticksLived >= this.c);
        }

        public boolean b() {
            return EntityEvoker.this.getGoalTarget() != null && this.b > 0;
        }

        public void c() {
            this.b = this.m();
            EntityEvoker.this.b = this.f();
            this.c = EntityEvoker.this.ticksLived + this.i();
            EntityEvoker.this.a(this.k(), 1.0F, 1.0F);
            EntityEvoker.this.c = this.l();
        }

        public void e() {
            --this.b;
            if (this.b == 0) {
                this.j();
                EntityEvoker.this.a(SoundEffects.bn, 1.0F, 1.0F);
            }

        }

        protected abstract void j();

        protected int m() {
            return 20;
        }

        protected abstract int f();

        protected abstract int i();

        protected abstract SoundEffect k();

        protected abstract int l();

        d(Object object) {
            this();
        }
    }

    class b extends PathfinderGoal {

        public b() {
            this.a(3);
        }

        public boolean a() {
            return EntityEvoker.this.di() > 0;
        }

        public void c() {
            super.c();
            EntityEvoker.this.a(EntityEvoker.this.c);
            EntityEvoker.this.navigation.o();
        }

        public void d() {
            super.d();
            EntityEvoker.this.a(0);
        }

        public void e() {
            if (EntityEvoker.this.getGoalTarget() != null) {
                EntityEvoker.this.getControllerLook().a(EntityEvoker.this.getGoalTarget(), (float) EntityEvoker.this.cL(), (float) EntityEvoker.this.N());
            } else if (EntityEvoker.this.dj() != null) {
                EntityEvoker.this.getControllerLook().a(EntityEvoker.this.dj(), (float) EntityEvoker.this.cL(), (float) EntityEvoker.this.N());
            }

        }
    }
}
