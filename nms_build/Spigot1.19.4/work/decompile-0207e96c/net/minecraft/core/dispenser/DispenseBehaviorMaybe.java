package net.minecraft.core.dispenser;

import net.minecraft.core.ISourceBlock;

public abstract class DispenseBehaviorMaybe extends DispenseBehaviorItem {

    private boolean success = true;

    public DispenseBehaviorMaybe() {}

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean flag) {
        this.success = flag;
    }

    @Override
    protected void playSound(ISourceBlock isourceblock) {
        isourceblock.getLevel().levelEvent(this.isSuccess() ? 1000 : 1001, isourceblock.getPos(), 0);
    }
}
