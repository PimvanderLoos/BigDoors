package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;

public abstract class ExpirableListEntry<T> extends JsonListEntry<T> {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    public static final String EXPIRES_NEVER = "forever";
    protected final Date created;
    protected final String source;
    @Nullable
    protected final Date expires;
    protected final String reason;

    public ExpirableListEntry(T t0, @Nullable Date date, @Nullable String s, @Nullable Date date1, @Nullable String s1) {
        super(t0);
        this.created = date == null ? new Date() : date;
        this.source = s == null ? "(Unknown)" : s;
        this.expires = date1;
        this.reason = s1 == null ? "Banned by an operator." : s1;
    }

    protected ExpirableListEntry(T t0, JsonObject jsonobject) {
        super(t0);

        Date date;

        try {
            date = jsonobject.has("created") ? ExpirableListEntry.DATE_FORMAT.parse(jsonobject.get("created").getAsString()) : new Date();
        } catch (ParseException parseexception) {
            date = new Date();
        }

        this.created = date;
        this.source = jsonobject.has("source") ? jsonobject.get("source").getAsString() : "(Unknown)";

        Date date1;

        try {
            date1 = jsonobject.has("expires") ? ExpirableListEntry.DATE_FORMAT.parse(jsonobject.get("expires").getAsString()) : null;
        } catch (ParseException parseexception1) {
            date1 = null;
        }

        this.expires = date1;
        this.reason = jsonobject.has("reason") ? jsonobject.get("reason").getAsString() : "Banned by an operator.";
    }

    public Date getCreated() {
        return this.created;
    }

    public String getSource() {
        return this.source;
    }

    @Nullable
    public Date getExpires() {
        return this.expires;
    }

    public String getReason() {
        return this.reason;
    }

    public abstract IChatBaseComponent getDisplayName();

    @Override
    boolean hasExpired() {
        return this.expires == null ? false : this.expires.before(new Date());
    }

    @Override
    protected void serialize(JsonObject jsonobject) {
        jsonobject.addProperty("created", ExpirableListEntry.DATE_FORMAT.format(this.created));
        jsonobject.addProperty("source", this.source);
        jsonobject.addProperty("expires", this.expires == null ? "forever" : ExpirableListEntry.DATE_FORMAT.format(this.expires));
        jsonobject.addProperty("reason", this.reason);
    }
}
