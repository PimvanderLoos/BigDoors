package net.minecraft.world;

public enum EnumInteractionResult {

    SUCCESS, CONSUME, CONSUME_PARTIAL, PASS, FAIL;

    private EnumInteractionResult() {}

    public boolean consumesAction() {
        return this == EnumInteractionResult.SUCCESS || this == EnumInteractionResult.CONSUME || this == EnumInteractionResult.CONSUME_PARTIAL;
    }

    public boolean shouldSwing() {
        return this == EnumInteractionResult.SUCCESS;
    }

    public boolean shouldAwardStats() {
        return this == EnumInteractionResult.SUCCESS || this == EnumInteractionResult.CONSUME;
    }

    public static EnumInteractionResult sidedSuccess(boolean flag) {
        return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.CONSUME;
    }
}
