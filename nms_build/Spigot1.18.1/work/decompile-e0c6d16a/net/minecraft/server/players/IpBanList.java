package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import javax.annotation.Nullable;

public class IpBanList extends JsonList<String, IpBanEntry> {

    public IpBanList(File file) {
        super(file);
    }

    @Override
    protected JsonListEntry<String> createEntry(JsonObject jsonobject) {
        return new IpBanEntry(jsonobject);
    }

    public boolean isBanned(SocketAddress socketaddress) {
        String s = this.getIpFromAddress(socketaddress);

        return this.contains(s);
    }

    public boolean isBanned(String s) {
        return this.contains(s);
    }

    @Nullable
    public IpBanEntry get(SocketAddress socketaddress) {
        String s = this.getIpFromAddress(socketaddress);

        return (IpBanEntry) this.get((Object) s);
    }

    private String getIpFromAddress(SocketAddress socketaddress) {
        String s = socketaddress.toString();

        if (s.contains("/")) {
            s = s.substring(s.indexOf(47) + 1);
        }

        if (s.contains(":")) {
            s = s.substring(0, s.indexOf(58));
        }

        return s;
    }
}
