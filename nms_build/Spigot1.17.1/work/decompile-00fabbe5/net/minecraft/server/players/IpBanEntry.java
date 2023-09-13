package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;

public class IpBanEntry extends ExpirableListEntry<String> {

    public IpBanEntry(String s) {
        this(s, (Date) null, (String) null, (Date) null, (String) null);
    }

    public IpBanEntry(String s, @Nullable Date date, @Nullable String s1, @Nullable Date date1, @Nullable String s2) {
        super(s, date, s1, date1, s2);
    }

    @Override
    public IChatBaseComponent e() {
        return new ChatComponentText((String) this.getKey());
    }

    public IpBanEntry(JsonObject jsonobject) {
        super(b(jsonobject), jsonobject);
    }

    private static String b(JsonObject jsonobject) {
        return jsonobject.has("ip") ? jsonobject.get("ip").getAsString() : null;
    }

    @Override
    protected void a(JsonObject jsonobject) {
        if (this.getKey() != null) {
            jsonobject.addProperty("ip", (String) this.getKey());
            super.a(jsonobject);
        }
    }
}
