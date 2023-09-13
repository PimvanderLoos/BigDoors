package net.minecraft.world;

public enum EnumInteractionResult {

    SUCCESS, CONSUME, PASS, FAIL;

    private EnumInteractionResult() {}

    public boolean a() {
        return this == EnumInteractionResult.SUCCESS || this == EnumInteractionResult.CONSUME;
    }

    public boolean b() {
        return this == EnumInteractionResult.SUCCESS;
    }

    public static EnumInteractionResult a(boolean flag) {
        return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.CONSUME;
    }
}
