package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityAgeable extends EntityCreature {

    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityAgeable.class, DataWatcherRegistry.i);
    protected int b;
    protected int c;
    protected int d;

    protected EntityAgeable(EntityTypes<? extends EntityAgeable> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
        }

        EntityAgeable.a entityageable_a = (EntityAgeable.a) groupdataentity;

        if (entityageable_a.c() && entityageable_a.a() > 0 && this.random.nextFloat() <= entityageable_a.d()) {
            this.setAgeRaw(-24000);
        }

        entityageable_a.b();
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Nullable
    public abstract EntityAgeable createChild(EntityAgeable entityageable);

    protected void a(EntityHuman entityhuman, EntityAgeable entityageable) {}

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (item instanceof ItemMonsterEgg && ((ItemMonsterEgg) item).a(itemstack.getTag(), this.getEntityType())) {
            if (!this.world.isClientSide) {
                EntityAgeable entityageable = this.createChild(this);

                if (entityageable != null) {
                    entityageable.setAgeRaw(-24000);
                    entityageable.setPositionRotation(this.locX(), this.locY(), this.locZ(), 0.0F, 0.0F);
                    this.world.addEntity(entityageable);
                    if (itemstack.hasName()) {
                        entityageable.setCustomName(itemstack.getName());
                    }

                    this.a(entityhuman, entityageable);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityAgeable.bw, false);
    }

    public int getAge() {
        return this.world.isClientSide ? ((Boolean) this.datawatcher.get(EntityAgeable.bw) ? -1 : 1) : this.b;
    }

    public void setAge(int i, boolean flag) {
        int j = this.getAge();
        int k = j;

        j += i * 20;
        if (j > 0) {
            j = 0;
        }

        int l = j - k;

        this.setAgeRaw(j);
        if (flag) {
            this.c += l;
            if (this.d == 0) {
                this.d = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAgeRaw(this.c);
        }

    }

    public void setAge(int i) {
        this.setAge(i, false);
    }

    public void setAgeRaw(int i) {
        int j = this.b;

        this.b = i;
        if (j < 0 && i >= 0 || j >= 0 && i < 0) {
            this.datawatcher.set(EntityAgeable.bw, i < 0);
            this.l();
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Age", this.getAge());
        nbttagcompound.setInt("ForcedAge", this.c);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAgeRaw(nbttagcompound.getInt("Age"));
        this.c = nbttagcompound.getInt("ForcedAge");
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAgeable.bw.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide) {
            if (this.d > 0) {
                if (this.d % 4 == 0) {
                    this.world.addParticle(Particles.HAPPY_VILLAGER, this.d(1.0D), this.cv() + 0.5D, this.g(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.d;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAgeRaw(i);
            } else if (i > 0) {
                --i;
                this.setAgeRaw(i);
            }
        }

    }

    protected void l() {}

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public static class a implements GroupDataEntity {

        private int a;
        private boolean b = true;
        private float c = 0.05F;

        public a() {}

        public int a() {
            return this.a;
        }

        public void b() {
            ++this.a;
        }

        public boolean c() {
            return this.b;
        }

        public void a(boolean flag) {
            this.b = flag;
        }

        public float d() {
            return this.c;
        }

        public void a(float f) {
            this.c = f;
        }
    }
}
