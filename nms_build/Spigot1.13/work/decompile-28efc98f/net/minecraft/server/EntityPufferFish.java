package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPufferFish extends EntityFish {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityPufferFish.class, DataWatcherRegistry.b);
    private int c;
    private int bC;
    private static final Predicate<EntityLiving> bD = (entityliving) -> {
        return entityliving == null ? false : (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).u()) ? false : entityliving.getMonsterType() != EnumMonsterType.e);
    };
    private float bE = -1.0F;
    private float bF;

    public EntityPufferFish(World world) {
        super(EntityTypes.PUFFERFISH, world);
        this.setSize(0.7F, 0.7F);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityPufferFish.b, Integer.valueOf(0));
    }

    public int getPuffState() {
        return ((Integer) this.datawatcher.get(EntityPufferFish.b)).intValue();
    }

    public void setPuffState(int i) {
        this.datawatcher.set(EntityPufferFish.b, Integer.valueOf(i));
        this.e(i);
    }

    private void e(int i) {
        float f = 1.0F;

        if (i == 1) {
            f = 0.7F;
        } else if (i == 0) {
            f = 0.5F;
        }

        this.a(f);
    }

    public final void setSize(float f, float f1) {
        boolean flag = this.bE > 0.0F;

        this.bE = f;
        this.bF = f1;
        if (!flag) {
            this.a(1.0F);
        }

    }

    private void a(float f) {
        super.setSize(this.bE * f, this.bF * f);
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        this.e(this.getPuffState());
        super.a(datawatcherobject);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("PuffState", this.getPuffState());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setPuffState(nbttagcompound.getInt("PuffState"));
    }

    @Nullable
    protected MinecraftKey G() {
        return LootTables.aF;
    }

    protected ItemStack dB() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    protected void n() {
        super.n();
        this.goalSelector.a(1, new EntityPufferFish.a(this));
    }

    public void tick() {
        if (this.isAlive() && !this.world.isClientSide) {
            if (this.c > 0) {
                if (this.getPuffState() == 0) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_UP, this.cD(), this.cE());
                    this.setPuffState(1);
                } else if (this.c > 40 && this.getPuffState() == 1) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_UP, this.cD(), this.cE());
                    this.setPuffState(2);
                }

                ++this.c;
            } else if (this.getPuffState() != 0) {
                if (this.bC > 60 && this.getPuffState() == 2) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_OUT, this.cD(), this.cE());
                    this.setPuffState(1);
                } else if (this.bC > 100 && this.getPuffState() == 1) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_OUT, this.cD(), this.cE());
                    this.setPuffState(0);
                }

                ++this.bC;
            }
        }

        super.tick();
    }

    public void k() {
        super.k();
        if (this.getPuffState() > 0) {
            List list = this.world.a(EntityInsentient.class, this.getBoundingBox().g(0.3D), EntityPufferFish.bD);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (entityinsentient.isAlive()) {
                    this.a(entityinsentient);
                }
            }
        }

    }

    private void a(EntityInsentient entityinsentient) {
        int i = this.getPuffState();

        if (entityinsentient.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            entityinsentient.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0));
            this.a(SoundEffects.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
        }

    }

    public void d(EntityHuman entityhuman) {
        int i = this.getPuffState();

        if (entityhuman instanceof EntityPlayer && i > 0 && entityhuman.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutGameStateChange(9, 0.0F));
            entityhuman.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0));
        }

    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_PUFFER_FISH_AMBIENT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_PUFFER_FISH_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_PUFFER_FISH_HURT;
    }

    protected SoundEffect dD() {
        return SoundEffects.ENTITY_PUFFER_FISH_FLOP;
    }

    static class a extends PathfinderGoal {

        private final EntityPufferFish a;

        public a(EntityPufferFish entitypufferfish) {
            this.a = entitypufferfish;
        }

        public boolean a() {
            List list = this.a.world.a(EntityLiving.class, this.a.getBoundingBox().g(2.0D), EntityPufferFish.bD);

            return !list.isEmpty();
        }

        public void c() {
            this.a.c = 1;
            this.a.bC = 0;
        }

        public void d() {
            this.a.c = 0;
        }

        public boolean b() {
            List list = this.a.world.a(EntityLiving.class, this.a.getBoundingBox().g(2.0D), EntityPufferFish.bD);

            return !list.isEmpty();
        }
    }
}
