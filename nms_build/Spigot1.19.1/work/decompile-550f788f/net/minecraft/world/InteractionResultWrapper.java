package net.minecraft.world;

public class InteractionResultWrapper<T> {

    private final EnumInteractionResult result;
    private final T object;

    public InteractionResultWrapper(EnumInteractionResult enuminteractionresult, T t0) {
        this.result = enuminteractionresult;
        this.object = t0;
    }

    public EnumInteractionResult getResult() {
        return this.result;
    }

    public T getObject() {
        return this.object;
    }

    public static <T> InteractionResultWrapper<T> success(T t0) {
        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, t0);
    }

    public static <T> InteractionResultWrapper<T> consume(T t0) {
        return new InteractionResultWrapper<>(EnumInteractionResult.CONSUME, t0);
    }

    public static <T> InteractionResultWrapper<T> pass(T t0) {
        return new InteractionResultWrapper<>(EnumInteractionResult.PASS, t0);
    }

    public static <T> InteractionResultWrapper<T> fail(T t0) {
        return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, t0);
    }

    public static <T> InteractionResultWrapper<T> sidedSuccess(T t0, boolean flag) {
        return flag ? success(t0) : consume(t0);
    }
}
