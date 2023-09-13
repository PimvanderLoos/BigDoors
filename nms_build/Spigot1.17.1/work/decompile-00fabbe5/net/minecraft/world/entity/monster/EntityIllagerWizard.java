package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.level.World;

public abstract class EntityIllagerWizard extends EntityIllagerAbstract {

    private static final DataWatcherObject<Byte> DATA_SPELL_CASTING_ID = DataWatcher.a(EntityIllagerWizard.class, DataWatcherRegistry.BYTE);
    protected int spellCastingTickCount;
    private EntityIllagerWizard.Spell currentSpell;

    protected EntityIllagerWizard(EntityTypes<? extends EntityIllagerWizard> entitytypes, World world) {
        super(entitytypes, world);
        this.currentSpell = EntityIllagerWizard.Spell.NONE;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityIllagerWizard.DATA_SPELL_CASTING_ID, (byte) 0);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.spellCastingTickCount = nbttagcompound.getInt("SpellTicks");
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("SpellTicks", this.spellCastingTickCount);
    }

    @Override
    public EntityIllagerAbstract.a n() {
        return this.fG() ? EntityIllagerAbstract.a.SPELLCASTING : (this.fN() ? EntityIllagerAbstract.a.CELEBRATING : EntityIllagerAbstract.a.CROSSED);
    }

    public boolean fG() {
        return this.level.isClientSide ? (Byte) this.entityData.get(EntityIllagerWizard.DATA_SPELL_CASTING_ID) > 0 : this.spellCastingTickCount > 0;
    }

    public void setSpell(EntityIllagerWizard.Spell entityillagerwizard_spell) {
        this.currentSpell = entityillagerwizard_spell;
        this.entityData.set(EntityIllagerWizard.DATA_SPELL_CASTING_ID, (byte) entityillagerwizard_spell.id);
    }

    public EntityIllagerWizard.Spell getSpell() {
        return !this.level.isClientSide ? this.currentSpell : EntityIllagerWizard.Spell.a((Byte) this.entityData.get(EntityIllagerWizard.DATA_SPELL_CASTING_ID));
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && this.fG()) {
            EntityIllagerWizard.Spell entityillagerwizard_spell = this.getSpell();
            double d0 = entityillagerwizard_spell.spellColor[0];
            double d1 = entityillagerwizard_spell.spellColor[1];
            double d2 = entityillagerwizard_spell.spellColor[2];
            float f = this.yBodyRot * 0.017453292F + MathHelper.cos((float) this.tickCount * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);

            this.level.addParticle(Particles.ENTITY_EFFECT, this.locX() + (double) f1 * 0.6D, this.locY() + 1.8D, this.locZ() + (double) f2 * 0.6D, d0, d1, d2);
            this.level.addParticle(Particles.ENTITY_EFFECT, this.locX() - (double) f1 * 0.6D, this.locY() + 1.8D, this.locZ() - (double) f2 * 0.6D, d0, d1, d2);
        }

    }

    protected int fI() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEffect getSoundCastSpell();

    public static enum Spell {

        NONE(0, 0.0D, 0.0D, 0.0D), SUMMON_VEX(1, 0.7D, 0.7D, 0.8D), FANGS(2, 0.4D, 0.3D, 0.35D), WOLOLO(3, 0.7D, 0.5D, 0.2D), DISAPPEAR(4, 0.3D, 0.3D, 0.8D), BLINDNESS(5, 0.1D, 0.1D, 0.2D);

        final int id;
        final double[] spellColor;

        private Spell(int i, double d0, double d1, double d2) {
            this.id = i;
            this.spellColor = new double[]{d0, d1, d2};
        }

        public static EntityIllagerWizard.Spell a(int i) {
            EntityIllagerWizard.Spell[] aentityillagerwizard_spell = values();
            int j = aentityillagerwizard_spell.length;

            for (int k = 0; k < j; ++k) {
                EntityIllagerWizard.Spell entityillagerwizard_spell = aentityillagerwizard_spell[k];

                if (i == entityillagerwizard_spell.id) {
                    return entityillagerwizard_spell;
                }
            }

            return EntityIllagerWizard.Spell.NONE;
        }
    }

    protected abstract class PathfinderGoalCastSpell extends PathfinderGoal {

        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        protected PathfinderGoalCastSpell() {}

        @Override
        public boolean a() {
            EntityLiving entityliving = EntityIllagerWizard.this.getGoalTarget();

            return entityliving != null && entityliving.isAlive() ? (EntityIllagerWizard.this.fG() ? false : EntityIllagerWizard.this.tickCount >= this.nextAttackTickCount) : false;
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = EntityIllagerWizard.this.getGoalTarget();

            return entityliving != null && entityliving.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void c() {
            this.attackWarmupDelay = this.m();
            EntityIllagerWizard.this.spellCastingTickCount = this.g();
            this.nextAttackTickCount = EntityIllagerWizard.this.tickCount + this.h();
            SoundEffect soundeffect = this.k();

            if (soundeffect != null) {
                EntityIllagerWizard.this.playSound(soundeffect, 1.0F, 1.0F);
            }

            EntityIllagerWizard.this.setSpell(this.getCastSpell());
        }

        @Override
        public void e() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.j();
                EntityIllagerWizard.this.playSound(EntityIllagerWizard.this.getSoundCastSpell(), 1.0F, 1.0F);
            }

        }

        protected abstract void j();

        protected int m() {
            return 20;
        }

        protected abstract int g();

        protected abstract int h();

        @Nullable
        protected abstract SoundEffect k();

        protected abstract EntityIllagerWizard.Spell getCastSpell();
    }

    protected class b extends PathfinderGoal {

        public b() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return EntityIllagerWizard.this.fI() > 0;
        }

        @Override
        public void c() {
            super.c();
            EntityIllagerWizard.this.navigation.o();
        }

        @Override
        public void d() {
            super.d();
            EntityIllagerWizard.this.setSpell(EntityIllagerWizard.Spell.NONE);
        }

        @Override
        public void e() {
            if (EntityIllagerWizard.this.getGoalTarget() != null) {
                EntityIllagerWizard.this.getControllerLook().a(EntityIllagerWizard.this.getGoalTarget(), (float) EntityIllagerWizard.this.fa(), (float) EntityIllagerWizard.this.eZ());
            }

        }
    }
}
