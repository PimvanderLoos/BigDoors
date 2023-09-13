package net.minecraft.server;

import net.minecraft.server.dedicated.DedicatedServerProperties;

public interface IMinecraftServer {

    DedicatedServerProperties getDedicatedServerProperties();

    String h_();

    int p();

    String i_();

    String getVersion();

    int getPlayerCount();

    int getMaxPlayers();

    String[] getPlayers();

    String getWorld();

    String getPlugins();

    String executeRemoteCommand(String s);
}
