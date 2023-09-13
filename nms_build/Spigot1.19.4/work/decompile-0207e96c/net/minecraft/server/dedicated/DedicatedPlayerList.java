package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.WorldNBTStorage;
import org.slf4j.Logger;

public class DedicatedPlayerList extends PlayerList {

    private static final Logger LOGGER = LogUtils.getLogger();

    public DedicatedPlayerList(DedicatedServer dedicatedserver, LayeredRegistryAccess<RegistryLayer> layeredregistryaccess, WorldNBTStorage worldnbtstorage) {
        super(dedicatedserver, layeredregistryaccess, worldnbtstorage, dedicatedserver.getProperties().maxPlayers);
        DedicatedServerProperties dedicatedserverproperties = dedicatedserver.getProperties();

        this.setViewDistance(dedicatedserverproperties.viewDistance);
        this.setSimulationDistance(dedicatedserverproperties.simulationDistance);
        super.setUsingWhiteList((Boolean) dedicatedserverproperties.whiteList.get());
        this.loadUserBanList();
        this.saveUserBanList();
        this.loadIpBanList();
        this.saveIpBanList();
        this.loadOps();
        this.loadWhiteList();
        this.saveOps();
        if (!this.getWhiteList().getFile().exists()) {
            this.saveWhiteList();
        }

    }

    @Override
    public void setUsingWhiteList(boolean flag) {
        super.setUsingWhiteList(flag);
        this.getServer().storeUsingWhiteList(flag);
    }

    @Override
    public void op(GameProfile gameprofile) {
        super.op(gameprofile);
        this.saveOps();
    }

    @Override
    public void deop(GameProfile gameprofile) {
        super.deop(gameprofile);
        this.saveOps();
    }

    @Override
    public void reloadWhiteList() {
        this.loadWhiteList();
    }

    private void saveIpBanList() {
        try {
            this.getIpBans().save();
        } catch (IOException ioexception) {
            DedicatedPlayerList.LOGGER.warn("Failed to save ip banlist: ", ioexception);
        }

    }

    private void saveUserBanList() {
        try {
            this.getBans().save();
        } catch (IOException ioexception) {
            DedicatedPlayerList.LOGGER.warn("Failed to save user banlist: ", ioexception);
        }

    }

    private void loadIpBanList() {
        try {
            this.getIpBans().load();
        } catch (IOException ioexception) {
            DedicatedPlayerList.LOGGER.warn("Failed to load ip banlist: ", ioexception);
        }

    }

    private void loadUserBanList() {
        try {
            this.getBans().load();
        } catch (IOException ioexception) {
            DedicatedPlayerList.LOGGER.warn("Failed to load user banlist: ", ioexception);
        }

    }

    private void loadOps() {
        try {
            this.getOps().load();
        } catch (Exception exception) {
            DedicatedPlayerList.LOGGER.warn("Failed to load operators list: ", exception);
        }

    }

    private void saveOps() {
        try {
            this.getOps().save();
        } catch (Exception exception) {
            DedicatedPlayerList.LOGGER.warn("Failed to save operators list: ", exception);
        }

    }

    private void loadWhiteList() {
        try {
            this.getWhiteList().load();
        } catch (Exception exception) {
            DedicatedPlayerList.LOGGER.warn("Failed to load white-list: ", exception);
        }

    }

    private void saveWhiteList() {
        try {
            this.getWhiteList().save();
        } catch (Exception exception) {
            DedicatedPlayerList.LOGGER.warn("Failed to save white-list: ", exception);
        }

    }

    @Override
    public boolean isWhiteListed(GameProfile gameprofile) {
        return !this.isUsingWhitelist() || this.isOp(gameprofile) || this.getWhiteList().isWhiteListed(gameprofile);
    }

    @Override
    public DedicatedServer getServer() {
        return (DedicatedServer) super.getServer();
    }

    @Override
    public boolean canBypassPlayerLimit(GameProfile gameprofile) {
        return this.getOps().canBypassPlayerLimit(gameprofile);
    }
}
