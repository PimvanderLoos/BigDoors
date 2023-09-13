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

    private static final DataWatcherObject<Byte> DATA_SPELL_CASTING_ID = DataWatcher.defineId(EntityIllagerWizard.class, DataWatcherRegistry.BYTE);
    protected int spellCastingTickCount;
    private EntityIllagerWizard.Spell currentSpell;

    protected EntityIllagerWizard(EntityTypes<? extends EntityIllagerWizard> entitytypes, World world) {
        super(entitytypes, world);
        this.currentSpell = EntityIllagerWizard.Spell.NONE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityIllagerWizard.DATA_SPELL_CASTING_ID, (byte) 0);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.spellCastingTickCount = nbttagcompound.getInt("SpellTicks");
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("SpellTicks", this.spellCastingTickCount);
    }

    @Override
    public EntityIllagerAbstract.a getArmPose() {
        return this.isCastingSpell() ? EntityIllagerAbstract.a.SPELLCASTING : (this.isCelebrating() ? EntityIllagerAbstract.a.CELEBRATING : EntityIllagerAbstract.a.CROSSED);
    }

    public boolean isCastingSpell() {
        return this.level.isClientSide ? (Byte) this.entityData.get(EntityIllagerWizard.DATA_SPELL_CASTING_ID) > 0 : this.spellCastingTickCount > 0;
    }

    public void setIsCastingSpell(EntityIllagerWizard.Spell entityillagerwizard_spell) {
        this.currentSpell = entityillagerwizard_spell;
        this.entityData.set(EntityIllagerWizard.DATA_SPELL_CASTING_ID, (byte) entityillagerwizard_spell.id);
    }

    public EntityIllagerWizard.Spell getCurrentSpell() {
        return !this.level.isClientSide ? this.currentSpell : EntityIllagerWizard.Spell.byId((Byte) this.entityData.get(EntityIllagerWizard.DATA_SPELL_CASTING_ID));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide && this.isCastingSpell()) {
            EntityIllagerWizard.Spell entityillagerwizard_spell = this.getCurrentSpell();
            double d0 = entityillagerwizard_spell.spellColor[0];
            double d1 = entityillagerwizard_spell.spellColor[1];
            double d2 = entityillagerwizard_spell.spellColor[2];
            float f = this.yBodyRot * 0.017453292F + MathHelper.cos((float) this.tickCount * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);

            this.level.addParticle(Particles.ENTITY_EFFECT, this.getX() + (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double) f2 * 0.6D, d0, d1, d2);
            this.level.addParticle(Particles.ENTITY_EFFECT, this.getX() - (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double) f2 * 0.6D, d0, d1, d2);
        }

    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEffect getCastingSoundEvent();

    public static enum Spell {

        NONE(0, 0.0D, 0.0D, 0.0D), SUMMON_VEX(1, 0.7D, 0.7D, 0.8D), FANGS(2, 0.4D, 0.3D, 0.35D), WOLOLO(3, 0.7D, 0.5D, 0.2D), DISAPPEAR(4, 0.3D, 0.3D, 0.8D), BLINDNESS(5, 0.1D, 0.1D, 0.2D);

        final int id;
        final double[] spellColor;

        private Spell(int i, double d0, double d1, double d2) {
            this.id = i;
            this.spellColor = new double[]{d0, d1, d2};
        }

        public static EntityIllagerWizard.Spell byId(int i) {
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
        public boolean canUse() {
            EntityLiving entityliving = EntityIllagerWizard.this.getTarget();

            return entityliving != null && entityliving.isAlive() ? (EntityIllagerWizard.this.isCastingSpell() ? false : EntityIllagerWizard.this.tickCount >= this.nextAttackTickCount) : false;
        }

        @Override
        public boolean canContinueToUse() {
            EntityLiving entityliving = EntityIllagerWizard.this.getTarget();

            return entityliving != null && entityliving.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            EntityIllagerWizard.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = EntityIllagerWizard.this.tickCount + this.getCastingInterval();
            SoundEffect soundeffect = this.getSpellPrepareSound();

            if (soundeffect != null) {
                EntityIllagerWizard.this.playSound(soundeffect, 1.0F, 1.0F);
            }

            EntityIllagerWizard.this.setIsCastingSpell(this.getSpell());
        }

        @Override
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                EntityIllagerWizard.this.playSound(EntityIllagerWizard.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEffect getSpellPrepareSound();

        protected abstract EntityIllagerWizard.Spell getSpell();
    }

    protected class b extends PathfinderGoal {

        public b() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityIllagerWizard.this.getSpellCastingTime() > 0;
        }

        @Override
        public void start() {
            super.start();
            EntityIllagerWizard.this.navigation.stop();
        }

        @Override
        public void stop() {
            super.stop();
            EntityIllagerWizard.this.setIsCastingSpell(EntityIllagerWizard.Spell.NONE);
        }

        @Override
        public void tick() {
            if (EntityIllagerWizard.this.getTarget() != null) {
                EntityIllagerWizard.this.getLookControl().setLookAt(EntityIllagerWizard.this.getTarget(), (float) EntityIllagerWizard.this.getMaxHeadYRot(), (float) EntityIllagerWizard.this.getMaxHeadXRot());
            }

        }
    }
}
