package net.minecraft.network.protocol;

public enum EnumProtocolDirection {

    SERVERBOUND, CLIENTBOUND;

    private EnumProtocolDirection() {}

    public EnumProtocolDirection a() {
        return this == EnumProtocolDirection.CLIENTBOUND ? EnumProtocolDirection.SERVERBOUND : EnumProtocolDirection.CLIENTBOUND;
    }
}
