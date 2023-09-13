package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;

public class EntityEvoker extends EntityIllagerWizard {

    private EntitySheep c;

    public EntityEvoker(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
        this.b_ = 10;
    }

    protected void r() {
        super.r();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityEvoker.b(null));
        this.goalSelector.a(2, new PathfinderGoalAvoidTarget(this, EntityHuman.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.a(4, new EntityEvoker.c(null));
        this.goalSelector.a(5, new EntityEvoker.a(null));
        this.goalSelector.a(6, new EntityEvoker.d());
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
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityEvoker.class);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
    }

    protected MinecraftKey J() {
        return LootTables.au;
    }

    protected void M() {
        super.M();
    }

    public void B_() {
        super.B_();
    }

    public boolean r(Entity entity) {
        return entity == null ? false : (entity == this ? true : (super.r(entity) ? true : (entity instanceof EntityVex ? this.r(((EntityVex) entity).p()) : (entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == EnumMonsterType.ILLAGER ? this.aY() == null && entity.aY() == null : false))));
    }

    protected SoundEffect F() {
        return SoundEffects.bs;
    }

    protected SoundEffect cf() {
        return SoundEffects.bu;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.bv;
    }

    private void a(@Nullable EntitySheep entitysheep) {
        this.c = entitysheep;
    }

    @Nullable
    private EntitySheep dq() {
        return this.c;
    }

    protected SoundEffect dm() {
        return SoundEffects.bt;
    }

    public class d extends EntityIllagerWizard.c {

        final Predicate<EntitySheep> a = new Predicate() {
            public boolean a(EntitySheep entitysheep) {
                return entitysheep.getColor() == EnumColor.BLUE;
            }

            public boolean apply(Object object) {
                return this.a((EntitySheep) object);
            }
        };

        public d() {
            super();
        }

        public boolean a() {
            if (EntityEvoker.this.getGoalTarget() != null) {
                return false;
            } else if (EntityEvoker.this.dn()) {
                return false;
            } else if (EntityEvoker.this.ticksLived < this.d) {
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
            return EntityEvoker.this.dq() != null && this.c > 0;
        }

        public void d() {
            super.d();
            EntityEvoker.this.a((EntitySheep) null);
        }

        protected void j() {
            EntitySheep entitysheep = EntityEvoker.this.dq();

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
            return SoundEffects.by;
        }

        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.WOLOLO;
        }
    }

    class c extends EntityIllagerWizard.c {

        private c() {
            super();
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
            return SoundEffects.bx;
        }

        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.SUMMON_VEX;
        }

        c(Object object) {
            this();
        }
    }

    class a extends EntityIllagerWizard.c {

        private a() {
            super();
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

            if (EntityEvoker.this.h(entityliving) < 9.0D) {
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
                        AxisAlignedBB axisalignedbb = iblockdata.d(EntityEvoker.this.world, blockposition);

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
            return SoundEffects.bw;
        }

        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.FANGS;
        }

        a(Object object) {
            this();
        }
    }

    class b extends EntityIllagerWizard.b {

        private b() {
            super();
        }

        public void e() {
            if (EntityEvoker.this.getGoalTarget() != null) {
                EntityEvoker.this.getControllerLook().a(EntityEvoker.this.getGoalTarget(), (float) EntityEvoker.this.O(), (float) EntityEvoker.this.N());
            } else if (EntityEvoker.this.dq() != null) {
                EntityEvoker.this.getControllerLook().a(EntityEvoker.this.dq(), (float) EntityEvoker.this.O(), (float) EntityEvoker.this.N());
            }

        }

        b(Object object) {
            this();
        }
    }
}
