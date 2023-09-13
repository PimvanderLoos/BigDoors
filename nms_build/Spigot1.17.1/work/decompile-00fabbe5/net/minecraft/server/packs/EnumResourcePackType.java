package net.minecraft.server.packs;

import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.PackType;

public enum EnumResourcePackType {

    CLIENT_RESOURCES("assets", PackType.RESOURCE), SERVER_DATA("data", PackType.DATA);

    private final String directory;
    private final PackType bridgeType;

    private EnumResourcePackType(String s, PackType packtype) {
        this.directory = s;
        this.bridgeType = packtype;
    }

    public String a() {
        return this.directory;
    }

    public int a(GameVersion gameversion) {
        return gameversion.getPackVersion(this.bridgeType);
    }
}
