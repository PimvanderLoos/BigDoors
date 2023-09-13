package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class MinecraftKey implements Comparable<MinecraftKey> {

    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("argument.id.invalid", new Object[0]));
    protected final String a;
    protected final String b;

    protected MinecraftKey(String[] astring) {
        this.a = StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0];
        this.b = astring[1];
        if (!this.a.chars().allMatch((i) -> {
            return i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 46;
        })) {
            throw new ResourceKeyInvalidException("Non [a-z0-9_.-] character in namespace of location: " + this.a + ':' + this.b);
        } else if (!this.b.chars().allMatch((i) -> {
            return i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 47 || i == 46;
        })) {
            throw new ResourceKeyInvalidException("Non [a-z0-9/._-] character in path of location: " + this.a + ':' + this.b);
        }
    }

    public MinecraftKey(String s) {
        this(b(s, ':'));
    }

    public MinecraftKey(String s, String s1) {
        this(new String[] { s, s1});
    }

    public static MinecraftKey a(String s, char c0) {
        return new MinecraftKey(b(s, c0));
    }

    @Nullable
    public static MinecraftKey a(String s) {
        try {
            return new MinecraftKey(s);
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            return null;
        }
    }

    protected static String[] b(String s, char c0) {
        String[] astring = new String[] { "minecraft", s};
        int i = s.indexOf(c0);

        if (i >= 0) {
            astring[1] = s.substring(i + 1, s.length());
            if (i >= 1) {
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

    public int compareTo(MinecraftKey minecraftkey) {
        int i = this.b.compareTo(minecraftkey.b);

        if (i == 0) {
            i = this.a.compareTo(minecraftkey.a);
        }

        return i;
    }

    public static MinecraftKey a(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && a(stringreader.peek())) {
            stringreader.skip();
        }

        String s = stringreader.getString().substring(i, stringreader.getCursor());

        try {
            return new MinecraftKey(s);
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            stringreader.setCursor(i);
            throw MinecraftKey.c.createWithContext(stringreader);
        }
    }

    public static boolean a(char c0) {
        return c0 >= '0' && c0 <= '9' || c0 >= 'a' && c0 <= 'z' || c0 == '_' || c0 == ':' || c0 == '/' || c0 == '.' || c0 == '-';
    }

    public static class a implements JsonDeserializer<MinecraftKey>, JsonSerializer<MinecraftKey> {

        public a() {}

        public MinecraftKey deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return new MinecraftKey(ChatDeserializer.a(jsonelement, "location"));
        }

        public JsonElement serialize(MinecraftKey minecraftkey, Type type, JsonSerializationContext jsonserializationcontext) {
            return new JsonPrimitive(minecraftkey.toString());
        }
    }
}
