package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityIllagerWizard extends EntityIllagerAbstract {

    private static final DataWatcherObject<Byte> c = DataWatcher.a(EntityIllagerWizard.class, DataWatcherRegistry.a);
    protected int b;
    private EntityIllagerWizard.Spell bC;

    protected EntityIllagerWizard(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.bC = EntityIllagerWizard.Spell.NONE;
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityIllagerWizard.c, (byte) 0);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.b = nbttagcompound.getInt("SpellTicks");
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("SpellTicks", this.b);
    }

    public boolean dA() {
        return this.world.isClientSide ? (Byte) this.datawatcher.get(EntityIllagerWizard.c) > 0 : this.b > 0;
    }

    public void setSpell(EntityIllagerWizard.Spell entityillagerwizard_spell) {
        this.bC = entityillagerwizard_spell;
        this.datawatcher.set(EntityIllagerWizard.c, (byte) entityillagerwizard_spell.g);
    }

    public EntityIllagerWizard.Spell getSpell() {
        return !this.world.isClientSide ? this.bC : EntityIllagerWizard.Spell.a((Byte) this.datawatcher.get(EntityIllagerWizard.c));
    }

    protected void mobTick() {
        super.mobTick();
        if (this.b > 0) {
            --this.b;
        }

    }

    public void tick() {
        super.tick();
        if (this.world.isClientSide && this.dA()) {
            EntityIllagerWizard.Spell entityillagerwizard_spell = this.getSpell();
            double d0 = entityillagerwizard_spell.h[0];
            double d1 = entityillagerwizard_spell.h[1];
            double d2 = entityillagerwizard_spell.h[2];
            float f = this.aQ * 0.017453292F + MathHelper.cos((float) this.ticksLived * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);

            this.world.addParticle(Particles.s, this.locX + (double) f1 * 0.6D, this.locY + 1.8D, this.locZ + (double) f2 * 0.6D, d0, d1, d2);
            this.world.addParticle(Particles.s, this.locX - (double) f1 * 0.6D, this.locY + 1.8D, this.locZ - (double) f2 * 0.6D, d0, d1, d2);
        }

    }

    protected int dC() {
        return this.b;
    }

    protected abstract SoundEffect dz();

    public static enum Spell {

        NONE(0, 0.0D, 0.0D, 0.0D), SUMMON_VEX(1, 0.7D, 0.7D, 0.8D), FANGS(2, 0.4D, 0.3D, 0.35D), WOLOLO(3, 0.7D, 0.5D, 0.2D), DISAPPEAR(4, 0.3D, 0.3D, 0.8D), BLINDNESS(5, 0.1D, 0.1D, 0.2D);

        private final int g;
        private final double[] h;

        private Spell(int i, double d0, double d1, double d2) {
            this.g = i;
            this.h = new double[] { d0, d1, d2};
        }

        public static EntityIllagerWizard.Spell a(int i) {
            EntityIllagerWizard.Spell[] aentityillagerwizard_spell = values();
            int j = aentityillagerwizard_spell.length;

            for (int k = 0; k < j; ++k) {
                EntityIllagerWizard.Spell entityillagerwizard_spell = aentityillagerwizard_spell[k];

                if (i == entityillagerwizard_spell.g) {
                    return entityillagerwizard_spell;
                }
            }

            return EntityIllagerWizard.Spell.NONE;
        }
    }

    public abstract class c extends PathfinderGoal {

        protected int b;
        protected int c;

        protected c() {}

        public boolean a() {
            return EntityIllagerWizard.this.getGoalTarget() == null ? false : (EntityIllagerWizard.this.dA() ? false : EntityIllagerWizard.this.ticksLived >= this.c);
        }

        public boolean b() {
            return EntityIllagerWizard.this.getGoalTarget() != null && this.b > 0;
        }

        public void c() {
            this.b = this.m();
            EntityIllagerWizard.this.b = this.g();
            this.c = EntityIllagerWizard.this.ticksLived + this.i();
            SoundEffect soundeffect = this.k();

            if (soundeffect != null) {
                EntityIllagerWizard.this.a(soundeffect, 1.0F, 1.0F);
            }

            EntityIllagerWizard.this.setSpell(this.l());
        }

        public void e() {
            --this.b;
            if (this.b == 0) {
                this.j();
                EntityIllagerWizard.this.a(EntityIllagerWizard.this.dz(), 1.0F, 1.0F);
            }

        }

        protected abstract void j();

        protected int m() {
            return 20;
        }

        protected abstract int g();

        protected abstract int i();

        @Nullable
        protected abstract SoundEffect k();

        protected abstract EntityIllagerWizard.Spell l();
    }

    public class b extends PathfinderGoal {

        public b() {
            this.a(3);
        }

        public boolean a() {
            return EntityIllagerWizard.this.dC() > 0;
        }

        public void c() {
            super.c();
            EntityIllagerWizard.this.navigation.q();
        }

        public void d() {
            super.d();
            EntityIllagerWizard.this.setSpell(EntityIllagerWizard.Spell.NONE);
        }

        public void e() {
            if (EntityIllagerWizard.this.getGoalTarget() != null) {
                EntityIllagerWizard.this.getControllerLook().a(EntityIllagerWizard.this.getGoalTarget(), (float) EntityIllagerWizard.this.L(), (float) EntityIllagerWizard.this.K());
            }

        }
    }
}
