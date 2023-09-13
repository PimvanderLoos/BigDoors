package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;

public class IpBanEntry extends ExpirableListEntry<String> {

    public IpBanEntry(String s) {
        this(s, (Date) null, (String) null, (Date) null, (String) null);
    }

    public IpBanEntry(String s, @Nullable Date date, @Nullable String s1, @Nullable Date date1, @Nullable String s2) {
        super(s, date, s1, date1, s2);
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return IChatBaseComponent.literal(String.valueOf(this.getUser()));
    }

    public IpBanEntry(JsonObject jsonobject) {
        super(createIpInfo(jsonobject), jsonobject);
    }

    private static String createIpInfo(JsonObject jsonobject) {
        return jsonobject.has("ip") ? jsonobject.get("ip").getAsString() : null;
    }

    @Override
    protected void serialize(JsonObject jsonobject) {
        if (this.getUser() != null) {
            jsonobject.addProperty("ip", (String) this.getUser());
            super.serialize(jsonobject);
        }
    }
}
