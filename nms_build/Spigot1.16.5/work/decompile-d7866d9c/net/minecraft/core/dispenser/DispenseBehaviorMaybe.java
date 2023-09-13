package net.minecraft.core.dispenser;

import net.minecraft.core.ISourceBlock;

public abstract class DispenseBehaviorMaybe extends DispenseBehaviorItem {

    private boolean dispensed = true;

    public DispenseBehaviorMaybe() {}

    public boolean a() {
        return this.dispensed;
    }

    public void a(boolean flag) {
        this.dispensed = flag;
    }

    @Override
    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(this.a() ? 1000 : 1001, isourceblock.getBlockPosition(), 0);
    }
}
