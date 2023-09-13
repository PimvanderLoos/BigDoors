package net.minecraft.network.chat;

public class ThrowingComponent extends Exception {

    private final IChatBaseComponent component;

    public ThrowingComponent(IChatBaseComponent ichatbasecomponent) {
        super(ichatbasecomponent.getString());
        this.component = ichatbasecomponent;
    }

    public ThrowingComponent(IChatBaseComponent ichatbasecomponent, Throwable throwable) {
        super(ichatbasecomponent.getString(), throwable);
        this.component = ichatbasecomponent;
    }

    public IChatBaseComponent getComponent() {
        return this.component;
    }
}
