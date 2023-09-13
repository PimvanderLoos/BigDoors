package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList extends PlayerList {

    private static final Logger f = LogManager.getLogger();

    public DedicatedPlayerList(DedicatedServer dedicatedserver) {
        super(dedicatedserver);
        this.a(dedicatedserver.a("view-distance", 10));
        this.maxPlayers = dedicatedserver.a("max-players", 20);
        this.setHasWhitelist(dedicatedserver.a("white-list", false));
        if (!dedicatedserver.R()) {
            this.getProfileBans().a(true);
            this.getIPBans().a(true);
        }

        this.A();
        this.y();
        this.z();
        this.x();
        this.B();
        this.D();
        this.C();
        if (!this.getWhitelist().c().exists()) {
            this.E();
        }

    }

    public void setHasWhitelist(boolean flag) {
        super.setHasWhitelist(flag);
        this.getServer().a("white-list", (Object) Boolean.valueOf(flag));
        this.getServer().a();
    }

    public void addOp(GameProfile gameprofile) {
        super.addOp(gameprofile);
        this.C();
    }

    public void removeOp(GameProfile gameprofile) {
        super.removeOp(gameprofile);
        this.C();
    }

    public void removeWhitelist(GameProfile gameprofile) {
        super.removeWhitelist(gameprofile);
        this.E();
    }

    public void addWhitelist(GameProfile gameprofile) {
        super.addWhitelist(gameprofile);
        this.E();
    }

    public void reloadWhitelist() {
        this.D();
    }

    private void x() {
        try {
            this.getIPBans().save();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to save ip banlist: ", ioexception);
        }

    }

    private void y() {
        try {
            this.getProfileBans().save();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to save user banlist: ", ioexception);
        }

    }

    private void z() {
        try {
            this.getIPBans().load();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to load ip banlist: ", ioexception);
        }

    }

    private void A() {
        try {
            this.getProfileBans().load();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to load user banlist: ", ioexception);
        }

    }

    private void B() {
        try {
            this.getOPs().load();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to load operators list: ", exception);
        }

    }

    private void C() {
        try {
            this.getOPs().save();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to save operators list: ", exception);
        }

    }

    private void D() {
        try {
            this.getWhitelist().load();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to load white-list: ", exception);
        }

    }

    private void E() {
        try {
            this.getWhitelist().save();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to save white-list: ", exception);
        }

    }

    public boolean isWhitelisted(GameProfile gameprofile) {
        return !this.getHasWhitelist() || this.isOp(gameprofile) || this.getWhitelist().isWhitelisted(gameprofile);
    }

    public DedicatedServer getServer() {
        return (DedicatedServer) super.getServer();
    }

    public boolean f(GameProfile gameprofile) {
        return this.getOPs().b(gameprofile);
    }

    public MinecraftServer getServer() {
        return this.getServer();
    }
}
