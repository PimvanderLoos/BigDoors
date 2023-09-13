package net.minecraft.world;

public enum EnumInteractionResult {

    SUCCESS, CONSUME, CONSUME_PARTIAL, PASS, FAIL;

    private EnumInteractionResult() {}

    public boolean a() {
        return this == EnumInteractionResult.SUCCESS || this == EnumInteractionResult.CONSUME || this == EnumInteractionResult.CONSUME_PARTIAL;
    }

    public boolean b() {
        return this == EnumInteractionResult.SUCCESS;
    }

    public boolean c() {
        return this == EnumInteractionResult.SUCCESS || this == EnumInteractionResult.CONSUME;
    }

    public static EnumInteractionResult a(boolean flag) {
        return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.CONSUME;
    }
}
