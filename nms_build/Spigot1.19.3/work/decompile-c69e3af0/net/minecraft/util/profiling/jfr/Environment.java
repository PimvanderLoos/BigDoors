package net.minecraft.util.profiling.jfr;

import net.minecraft.server.MinecraftServer;

public enum Environment {

    CLIENT("client"), SERVER("server");

    private final String description;

    private Environment(String s) {
        this.description = s;
    }

    public static Environment from(MinecraftServer minecraftserver) {
        return minecraftserver.isDedicatedServer() ? Environment.SERVER : Environment.CLIENT;
    }

    public String getDescription() {
        return this.description;
    }
}
