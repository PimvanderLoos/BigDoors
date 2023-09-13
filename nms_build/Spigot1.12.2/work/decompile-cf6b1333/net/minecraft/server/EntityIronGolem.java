package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class EntityIronGolem extends EntityGolem {

    protected static final DataWatcherObject<Byte> a = DataWatcher.a(EntityIronGolem.class, DataWatcherRegistry.a);
    private int c;
    @Nullable
    Village b;
    private int bx;
    private int by;

    public EntityIronGolem(World world) {
        super(world);
        this.setSize(1.4F, 2.7F);
    }

    protected void r() {
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, true));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 10, false, true, new Predicate() {
            public boolean a(@Nullable EntityInsentient entityinsentient) {
                return entityinsentient != null && IMonster.e.apply(entityinsentient) && !(entityinsentient instanceof EntityCreeper);
            }

            public boolean apply(@Nullable Object object) {
                return this.a((EntityInsentient) object);
            }
        }));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityIronGolem.a, Byte.valueOf((byte) 0));
    }

    protected void M() {
        if (--this.c <= 0) {
            this.c = 70 + this.random.nextInt(50);
            this.b = this.world.ak().getClosestVillage(new BlockPosition(this), 32);
            if (this.b == null) {
                this.di();
            } else {
                BlockPosition blockposition = this.b.a();

                this.a(blockposition, (int) ((float) this.b.b() * 0.6F));
            }
        }

        super.M();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeInstance(GenericAttributes.c).setValue(1.0D);
    }

    protected int d(int i) {
        return i;
    }

    protected void C(Entity entity) {
        if (entity instanceof IMonster && !(entity instanceof EntityCreeper) && this.getRandom().nextInt(20) == 0) {
            this.setGoalTarget((EntityLiving) entity);
        }

        super.C(entity);
    }

    public void n() {
        super.n();
        if (this.bx > 0) {
            --this.bx;
        }

        if (this.by > 0) {
            --this.by;
        }

        if (this.motX * this.motX + this.motZ * this.motZ > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ);
            IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));

            if (iblockdata.getMaterial() != Material.AIR) {
                this.world.addParticle(EnumParticle.BLOCK_CRACK, this.locX + ((double) this.random.nextFloat() - 0.5D) * (double) this.width, this.getBoundingBox().b + 0.1D, this.locZ + ((double) this.random.nextFloat() - 0.5D) * (double) this.width, 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D, new int[] { Block.getCombinedId(iblockdata)});
            }
        }

    }

    public boolean d(Class<? extends EntityLiving> oclass) {
        return this.isPlayerCreated() && EntityHuman.class.isAssignableFrom(oclass) ? false : (oclass == EntityCreeper.class ? false : super.d(oclass));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityIronGolem.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("PlayerCreated", this.isPlayerCreated());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setPlayerCreated(nbttagcompound.getBoolean("PlayerCreated"));
    }

    public boolean B(Entity entity) {
        this.bx = 10;
        this.world.broadcastEntityEffect(this, (byte) 4);
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) (7 + this.random.nextInt(15)));

        if (flag) {
            entity.motY += 0.4000000059604645D;
            this.a((EntityLiving) this, entity);
        }

        this.a(SoundEffects.dj, 1.0F, 1.0F);
        return flag;
    }

    public Village p() {
        return this.b;
    }

    public void a(boolean flag) {
        if (flag) {
            this.by = 400;
            this.world.broadcastEntityEffect(this, (byte) 11);
        } else {
            this.by = 0;
            this.world.broadcastEntityEffect(this, (byte) 34);
        }

    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.dl;
    }

    protected SoundEffect cf() {
        return SoundEffects.dk;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.dm, 1.0F, 1.0F);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.A;
    }

    public int dm() {
        return this.by;
    }

    public boolean isPlayerCreated() {
        return (((Byte) this.datawatcher.get(EntityIronGolem.a)).byteValue() & 1) != 0;
    }

    public void setPlayerCreated(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityIronGolem.a)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityIronGolem.a, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.datawatcher.set(EntityIronGolem.a, Byte.valueOf((byte) (b0 & -2)));
        }

    }

    public void die(DamageSource damagesource) {
        if (!this.isPlayerCreated() && this.killer != null && this.b != null) {
            this.b.a(this.killer.getName(), -5);
        }

        super.die(damagesource);
    }
}
