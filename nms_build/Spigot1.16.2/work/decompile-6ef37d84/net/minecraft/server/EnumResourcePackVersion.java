package net.minecraft.server;

public enum EnumResourcePackVersion {

    TOO_OLD("old"), TOO_NEW("new"), COMPATIBLE("compatible");

    private final IChatBaseComponent d;
    private final IChatBaseComponent e;

    private EnumResourcePackVersion(String s) {
        this.d = new ChatMessage("pack.incompatible." + s);
        this.e = new ChatMessage("pack.incompatible.confirm." + s);
    }

    public boolean a() {
        return this == EnumResourcePackVersion.COMPATIBLE;
    }

    public static EnumResourcePackVersion a(int i) {
        return i < SharedConstants.getGameVersion().getPackVersion() ? EnumResourcePackVersion.TOO_OLD : (i > SharedConstants.getGameVersion().getPackVersion() ? EnumResourcePackVersion.TOO_NEW : EnumResourcePackVersion.COMPATIBLE);
    }
}
