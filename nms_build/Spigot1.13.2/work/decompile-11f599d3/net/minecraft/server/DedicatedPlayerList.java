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
        if (!dedicatedserver.H()) {
            this.getProfileBans().a(true);
            this.getIPBans().a(true);
        }

        this.B();
        this.z();
        this.A();
        this.y();
        this.C();
        this.E();
        this.D();
        if (!this.getWhitelist().c().exists()) {
            this.F();
        }

    }

    public void setHasWhitelist(boolean flag) {
        super.setHasWhitelist(flag);
        this.getServer().a("white-list", (Object) flag);
        this.getServer().c_();
    }

    public void addOp(GameProfile gameprofile) {
        super.addOp(gameprofile);
        this.D();
    }

    public void removeOp(GameProfile gameprofile) {
        super.removeOp(gameprofile);
        this.D();
    }

    public void reloadWhitelist() {
        this.E();
    }

    private void y() {
        try {
            this.getIPBans().save();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to save ip banlist: ", ioexception);
        }

    }

    private void z() {
        try {
            this.getProfileBans().save();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to save user banlist: ", ioexception);
        }

    }

    private void A() {
        try {
            this.getIPBans().load();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to load ip banlist: ", ioexception);
        }

    }

    private void B() {
        try {
            this.getProfileBans().load();
        } catch (IOException ioexception) {
            DedicatedPlayerList.f.warn("Failed to load user banlist: ", ioexception);
        }

    }

    private void C() {
        try {
            this.getOPs().load();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to load operators list: ", exception);
        }

    }

    private void D() {
        try {
            this.getOPs().save();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to save operators list: ", exception);
        }

    }

    private void E() {
        try {
            this.getWhitelist().load();
        } catch (Exception exception) {
            DedicatedPlayerList.f.warn("Failed to load white-list: ", exception);
        }

    }

    private void F() {
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
}
