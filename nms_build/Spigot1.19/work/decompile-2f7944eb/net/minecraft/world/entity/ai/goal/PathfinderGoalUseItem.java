package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;

public class PathfinderGoalUseItem<T extends EntityInsentient> extends PathfinderGoal {

    private final T mob;
    private final ItemStack item;
    private final Predicate<? super T> canUseSelector;
    @Nullable
    private final SoundEffect finishUsingSound;

    public PathfinderGoalUseItem(T t0, ItemStack itemstack, @Nullable SoundEffect soundeffect, Predicate<? super T> predicate) {
        this.mob = t0;
        this.item = itemstack;
        this.finishUsingSound = soundeffect;
        this.canUseSelector = predicate;
    }

    @Override
    public boolean canUse() {
        return this.canUseSelector.test(this.mob);
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isUsingItem();
    }

    @Override
    public void start() {
        this.mob.setItemSlot(EnumItemSlot.MAINHAND, this.item.copy());
        this.mob.startUsingItem(EnumHand.MAIN_HAND);
    }

    @Override
    public void stop() {
        this.mob.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        if (this.finishUsingSound != null) {
            this.mob.playSound(this.finishUsingSound, 1.0F, this.mob.getRandom().nextFloat() * 0.2F + 0.9F);
        }

    }
}
