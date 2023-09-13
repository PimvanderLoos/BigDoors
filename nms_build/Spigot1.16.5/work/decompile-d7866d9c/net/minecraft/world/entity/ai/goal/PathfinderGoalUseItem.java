package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;

public class PathfinderGoalUseItem<T extends EntityInsentient> extends PathfinderGoal {

    private final T a;
    private final ItemStack b;
    private final Predicate<? super T> c;
    private final SoundEffect d;

    public PathfinderGoalUseItem(T t0, ItemStack itemstack, @Nullable SoundEffect soundeffect, Predicate<? super T> predicate) {
        this.a = t0;
        this.b = itemstack;
        this.d = soundeffect;
        this.c = predicate;
    }

    @Override
    public boolean a() {
        return this.c.test(this.a);
    }

    @Override
    public boolean b() {
        return this.a.isHandRaised();
    }

    @Override
    public void c() {
        this.a.setSlot(EnumItemSlot.MAINHAND, this.b.cloneItemStack());
        this.a.c(EnumHand.MAIN_HAND);
    }

    @Override
    public void d() {
        this.a.setSlot(EnumItemSlot.MAINHAND, ItemStack.b);
        if (this.d != null) {
            this.a.playSound(this.d, 1.0F, this.a.getRandom().nextFloat() * 0.2F + 0.9F);
        }

    }
}
