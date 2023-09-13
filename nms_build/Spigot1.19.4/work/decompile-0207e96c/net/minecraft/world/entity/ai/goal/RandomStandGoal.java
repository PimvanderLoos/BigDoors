package net.minecraft.world.entity.ai.goal;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;

public class RandomStandGoal extends PathfinderGoal {

    private final EntityHorseAbstract horse;
    private int nextStand;

    public RandomStandGoal(EntityHorseAbstract entityhorseabstract) {
        this.horse = entityhorseabstract;
        this.resetStandInterval(entityhorseabstract);
    }

    @Override
    public void start() {
        this.horse.standIfPossible();
        this.playStandSound();
    }

    private void playStandSound() {
        SoundEffect soundeffect = this.horse.getAmbientStandSound();

        if (soundeffect != null) {
            this.horse.playSound(soundeffect);
        }

    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public boolean canUse() {
        ++this.nextStand;
        if (this.nextStand > 0 && this.horse.getRandom().nextInt(1000) < this.nextStand) {
            this.resetStandInterval(this.horse);
            return !this.horse.isImmobile() && this.horse.getRandom().nextInt(10) == 0;
        } else {
            return false;
        }
    }

    private void resetStandInterval(EntityHorseAbstract entityhorseabstract) {
        this.nextStand = -entityhorseabstract.getAmbientStandInterval();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
