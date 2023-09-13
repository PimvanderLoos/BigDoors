package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class MinecraftKey implements Comparable<MinecraftKey> {

    protected final String a;
    protected final String b;

    protected MinecraftKey(int i, String... astring) {
        this.a = StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0].toLowerCase(Locale.ROOT);
        this.b = astring[1].toLowerCase(Locale.ROOT);
        Validate.notNull(this.b);
    }

    public MinecraftKey(String s) {
        this(0, a(s));
    }

    public MinecraftKey(String s, String s1) {
        this(0, new String[] { s, s1});
    }

    protected static String[] a(String s) {
        String[] astring = new String[] { "minecraft", s};
        int i = s.indexOf(58);

        if (i >= 0) {
            astring[1] = s.substring(i + 1, s.length());
            if (i > 1) {
                astring[0] = s.substring(0, i);
            }
        }

        return astring;
    }

    public String getKey() {
        return this.b;
    }

    public String b() {
        return this.a;
    }

    public String toString() {
        return this.a + ':' + this.b;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MinecraftKey)) {
            return false;
        } else {
            MinecraftKey minecraftkey = (MinecraftKey) object;

            return this.a.equals(minecraftkey.a) && this.b.equals(minecraftkey.b);
        }
    }

    public int hashCode() {
        return 31 * this.a.hashCode() + this.b.hashCode();
    }

    public int a(MinecraftKey minecraftkey) {
        int i = this.a.compareTo(minecraftkey.a);

        if (i == 0) {
            i = this.b.compareTo(minecraftkey.b);
        }

        return i;
    }

    public int compareTo(Object object) {
        return this.a((MinecraftKey) object);
    }

    public static class a implements JsonDeserializer<MinecraftKey>, JsonSerializer<MinecraftKey> {

        public a() {}

        public MinecraftKey a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return new MinecraftKey(ChatDeserializer.a(jsonelement, "location"));
        }

        public JsonElement a(MinecraftKey minecraftkey, Type type, JsonSerializationContext jsonserializationcontext) {
            return new JsonPrimitive(minecraftkey.toString());
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((MinecraftKey) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
