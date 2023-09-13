package net.minecraft.server.packs;

public enum EnumResourcePackType {

    CLIENT_RESOURCES("assets"), SERVER_DATA("data");

    private final String directory;

    private EnumResourcePackType(String s) {
        this.directory = s;
    }

    public String getDirectory() {
        return this.directory;
    }
}
