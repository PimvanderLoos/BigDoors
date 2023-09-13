package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityIllagerWizard extends EntityIllagerAbstract {

    private static final DataWatcherObject<Byte> c = DataWatcher.a(EntityIllagerWizard.class, DataWatcherRegistry.a);
    protected int b;
    private EntityIllagerWizard.Spell bx;

    public EntityIllagerWizard(World world) {
        super(world);
        this.bx = EntityIllagerWizard.Spell.NONE;
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityIllagerWizard.c, Byte.valueOf((byte) 0));
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.b = nbttagcompound.getInt("SpellTicks");
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("SpellTicks", this.b);
    }

    public boolean dn() {
        return this.world.isClientSide ? ((Byte) this.datawatcher.get(EntityIllagerWizard.c)).byteValue() > 0 : this.b > 0;
    }

    public void setSpell(EntityIllagerWizard.Spell entityillagerwizard_spell) {
        this.bx = entityillagerwizard_spell;
        this.datawatcher.set(EntityIllagerWizard.c, Byte.valueOf((byte) entityillagerwizard_spell.g));
    }

    public EntityIllagerWizard.Spell getSpell() {
        return !this.world.isClientSide ? this.bx : EntityIllagerWizard.Spell.a(((Byte) this.datawatcher.get(EntityIllagerWizard.c)).byteValue());
    }

    protected void M() {
        super.M();
        if (this.b > 0) {
            --this.b;
        }

    }

    public void B_() {
        super.B_();
        if (this.world.isClientSide && this.dn()) {
            EntityIllagerWizard.Spell entityillagerwizard_spell = this.getSpell();
            double d0 = entityillagerwizard_spell.h[0];
            double d1 = entityillagerwizard_spell.h[1];
            double d2 = entityillagerwizard_spell.h[2];
            float f = this.aN * 0.017453292F + MathHelper.cos((float) this.ticksLived * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);

            this.world.addParticle(EnumParticle.SPELL_MOB, this.locX + (double) f1 * 0.6D, this.locY + 1.8D, this.locZ + (double) f2 * 0.6D, d0, d1, d2, new int[0]);
            this.world.addParticle(EnumParticle.SPELL_MOB, this.locX - (double) f1 * 0.6D, this.locY + 1.8D, this.locZ - (double) f2 * 0.6D, d0, d1, d2, new int[0]);
        }

    }

    protected int dp() {
        return this.b;
    }

    protected abstract SoundEffect dm();

    public static enum Spell {

        NONE(0, 0.0D, 0.0D, 0.0D), SUMMON_VEX(1, 0.7D, 0.7D, 0.8D), FANGS(2, 0.4D, 0.3D, 0.35D), WOLOLO(3, 0.7D, 0.5D, 0.2D), DISAPPEAR(4, 0.3D, 0.3D, 0.8D), BLINDNESS(5, 0.1D, 0.1D, 0.2D);

        private final int g;
        private final double[] h;

        private Spell(int i, double d0, double param5, double d1) {
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

        protected int c;
        protected int d;

        protected c() {}

        public boolean a() {
            return EntityIllagerWizard.this.getGoalTarget() == null ? false : (EntityIllagerWizard.this.dn() ? false : EntityIllagerWizard.this.ticksLived >= this.d);
        }

        public boolean b() {
            return EntityIllagerWizard.this.getGoalTarget() != null && this.c > 0;
        }

        public void c() {
            this.c = this.m();
            EntityIllagerWizard.this.b = this.f();
            this.d = EntityIllagerWizard.this.ticksLived + this.i();
            SoundEffect soundeffect = this.k();

            if (soundeffect != null) {
                EntityIllagerWizard.this.a(soundeffect, 1.0F, 1.0F);
            }

            EntityIllagerWizard.this.setSpell(this.l());
        }

        public void e() {
            --this.c;
            if (this.c == 0) {
                this.j();
                EntityIllagerWizard.this.a(EntityIllagerWizard.this.dm(), 1.0F, 1.0F);
            }

        }

        protected abstract void j();

        protected int m() {
            return 20;
        }

        protected abstract int f();

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
            return EntityIllagerWizard.this.dp() > 0;
        }

        public void c() {
            super.c();
            EntityIllagerWizard.this.navigation.p();
        }

        public void d() {
            super.d();
            EntityIllagerWizard.this.setSpell(EntityIllagerWizard.Spell.NONE);
        }

        public void e() {
            if (EntityIllagerWizard.this.getGoalTarget() != null) {
                EntityIllagerWizard.this.getControllerLook().a(EntityIllagerWizard.this.getGoalTarget(), (float) EntityIllagerWizard.this.O(), (float) EntityIllagerWizard.this.N());
            }

        }
    }
}
