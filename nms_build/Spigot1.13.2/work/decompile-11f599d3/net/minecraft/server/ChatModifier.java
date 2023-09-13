package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;

public class ChatModifier {

    private ChatModifier a;
    private EnumChatFormat b;
    private Boolean c;
    private Boolean d;
    private Boolean e;
    private Boolean f;
    private Boolean g;
    private ChatClickable h;
    private ChatHoverable i;
    private String j;
    private static final ChatModifier k = new ChatModifier() {
        @Nullable
        public EnumChatFormat getColor() {
            return null;
        }

        public boolean isBold() {
            return false;
        }

        public boolean isItalic() {
            return false;
        }

        public boolean isStrikethrough() {
            return false;
        }

        public boolean isUnderlined() {
            return false;
        }

        public boolean isRandom() {
            return false;
        }

        @Nullable
        public ChatClickable h() {
            return null;
        }

        @Nullable
        public ChatHoverable i() {
            return null;
        }

        @Nullable
        public String j() {
            return null;
        }

        public ChatModifier setColor(EnumChatFormat enumchatformat) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setBold(Boolean obool) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setItalic(Boolean obool) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setStrikethrough(Boolean obool) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setUnderline(Boolean obool) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setRandom(Boolean obool) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setChatClickable(ChatClickable chatclickable) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setChatHoverable(ChatHoverable chathoverable) {
            throw new UnsupportedOperationException();
        }

        public ChatModifier setChatModifier(ChatModifier chatmodifier) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return "Style.ROOT";
        }

        public ChatModifier clone() {
            return this;
        }

        public ChatModifier n() {
            return this;
        }

        public String k() {
            return "";
        }
    };

    public ChatModifier() {}

    @Nullable
    public EnumChatFormat getColor() {
        return this.b == null ? this.o().getColor() : this.b;
    }

    public boolean isBold() {
        return this.c == null ? this.o().isBold() : this.c;
    }

    public boolean isItalic() {
        return this.d == null ? this.o().isItalic() : this.d;
    }

    public boolean isStrikethrough() {
        return this.f == null ? this.o().isStrikethrough() : this.f;
    }

    public boolean isUnderlined() {
        return this.e == null ? this.o().isUnderlined() : this.e;
    }

    public boolean isRandom() {
        return this.g == null ? this.o().isRandom() : this.g;
    }

    public boolean g() {
        return this.c == null && this.d == null && this.f == null && this.e == null && this.g == null && this.b == null && this.h == null && this.i == null && this.j == null;
    }

    @Nullable
    public ChatClickable h() {
        return this.h == null ? this.o().h() : this.h;
    }

    @Nullable
    public ChatHoverable i() {
        return this.i == null ? this.o().i() : this.i;
    }

    @Nullable
    public String j() {
        return this.j == null ? this.o().j() : this.j;
    }

    public ChatModifier setColor(EnumChatFormat enumchatformat) {
        this.b = enumchatformat;
        return this;
    }

    public ChatModifier setBold(Boolean obool) {
        this.c = obool;
        return this;
    }

    public ChatModifier setItalic(Boolean obool) {
        this.d = obool;
        return this;
    }

    public ChatModifier setStrikethrough(Boolean obool) {
        this.f = obool;
        return this;
    }

    public ChatModifier setUnderline(Boolean obool) {
        this.e = obool;
        return this;
    }

    public ChatModifier setRandom(Boolean obool) {
        this.g = obool;
        return this;
    }

    public ChatModifier setChatClickable(ChatClickable chatclickable) {
        this.h = chatclickable;
        return this;
    }

    public ChatModifier setChatHoverable(ChatHoverable chathoverable) {
        this.i = chathoverable;
        return this;
    }

    public ChatModifier setInsertion(String s) {
        this.j = s;
        return this;
    }

    public ChatModifier setChatModifier(ChatModifier chatmodifier) {
        this.a = chatmodifier;
        return this;
    }

    public String k() {
        if (this.g()) {
            return this.a != null ? this.a.k() : "";
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.getColor() != null) {
                stringbuilder.append(this.getColor());
            }

            if (this.isBold()) {
                stringbuilder.append(EnumChatFormat.BOLD);
            }

            if (this.isItalic()) {
                stringbuilder.append(EnumChatFormat.ITALIC);
            }

            if (this.isUnderlined()) {
                stringbuilder.append(EnumChatFormat.UNDERLINE);
            }

            if (this.isRandom()) {
                stringbuilder.append(EnumChatFormat.OBFUSCATED);
            }

            if (this.isStrikethrough()) {
                stringbuilder.append(EnumChatFormat.STRIKETHROUGH);
            }

            return stringbuilder.toString();
        }
    }

    private ChatModifier o() {
        return this.a == null ? ChatModifier.k : this.a;
    }

    public String toString() {
        return "Style{hasParent=" + (this.a != null) + ", color=" + this.b + ", bold=" + this.c + ", italic=" + this.d + ", underlined=" + this.e + ", obfuscated=" + this.g + ", clickEvent=" + this.h() + ", hoverEvent=" + this.i() + ", insertion=" + this.j() + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatModifier)) {
            return false;
        } else {
            ChatModifier chatmodifier = (ChatModifier) object;
            boolean flag;

            if (this.isBold() == chatmodifier.isBold() && this.getColor() == chatmodifier.getColor() && this.isItalic() == chatmodifier.isItalic() && this.isRandom() == chatmodifier.isRandom() && this.isStrikethrough() == chatmodifier.isStrikethrough() && this.isUnderlined() == chatmodifier.isUnderlined()) {
                label65:
                {
                    if (this.h() != null) {
                        if (!this.h().equals(chatmodifier.h())) {
                            break label65;
                        }
                    } else if (chatmodifier.h() != null) {
                        break label65;
                    }

                    if (this.i() != null) {
                        if (!this.i().equals(chatmodifier.i())) {
                            break label65;
                        }
                    } else if (chatmodifier.i() != null) {
                        break label65;
                    }

                    if (this.j() != null) {
                        if (!this.j().equals(chatmodifier.j())) {
                            break label65;
                        }
                    } else if (chatmodifier.j() != null) {
                        break label65;
                    }

                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j});
    }

    public ChatModifier clone() {
        ChatModifier chatmodifier = new ChatModifier();

        chatmodifier.c = this.c;
        chatmodifier.d = this.d;
        chatmodifier.f = this.f;
        chatmodifier.e = this.e;
        chatmodifier.g = this.g;
        chatmodifier.b = this.b;
        chatmodifier.h = this.h;
        chatmodifier.i = this.i;
        chatmodifier.a = this.a;
        chatmodifier.j = this.j;
        return chatmodifier;
    }

    public ChatModifier n() {
        ChatModifier chatmodifier = new ChatModifier();

        chatmodifier.setBold(this.isBold());
        chatmodifier.setItalic(this.isItalic());
        chatmodifier.setStrikethrough(this.isStrikethrough());
        chatmodifier.setUnderline(this.isUnderlined());
        chatmodifier.setRandom(this.isRandom());
        chatmodifier.setColor(this.getColor());
        chatmodifier.setChatClickable(this.h());
        chatmodifier.setChatHoverable(this.i());
        chatmodifier.setInsertion(this.j());
        return chatmodifier;
    }

    public static class ChatModifierSerializer implements JsonDeserializer<ChatModifier>, JsonSerializer<ChatModifier> {

        public ChatModifierSerializer() {}

        @Nullable
        public ChatModifier deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonObject()) {
                ChatModifier chatmodifier = new ChatModifier();
                JsonObject jsonobject = jsonelement.getAsJsonObject();

                if (jsonobject == null) {
                    return null;
                } else {
                    if (jsonobject.has("bold")) {
                        chatmodifier.c = jsonobject.get("bold").getAsBoolean();
                    }

                    if (jsonobject.has("italic")) {
                        chatmodifier.d = jsonobject.get("italic").getAsBoolean();
                    }

                    if (jsonobject.has("underlined")) {
                        chatmodifier.e = jsonobject.get("underlined").getAsBoolean();
                    }

                    if (jsonobject.has("strikethrough")) {
                        chatmodifier.f = jsonobject.get("strikethrough").getAsBoolean();
                    }

                    if (jsonobject.has("obfuscated")) {
                        chatmodifier.g = jsonobject.get("obfuscated").getAsBoolean();
                    }

                    if (jsonobject.has("color")) {
                        chatmodifier.b = (EnumChatFormat) jsondeserializationcontext.deserialize(jsonobject.get("color"), EnumChatFormat.class);
                    }

                    if (jsonobject.has("insertion")) {
                        chatmodifier.j = jsonobject.get("insertion").getAsString();
                    }

                    JsonObject jsonobject1;
                    JsonPrimitive jsonprimitive;

                    if (jsonobject.has("clickEvent")) {
                        jsonobject1 = jsonobject.getAsJsonObject("clickEvent");
                        if (jsonobject1 != null) {
                            jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                            ChatClickable.EnumClickAction chatclickable_enumclickaction = jsonprimitive == null ? null : ChatClickable.EnumClickAction.a(jsonprimitive.getAsString());
                            JsonPrimitive jsonprimitive1 = jsonobject1.getAsJsonPrimitive("value");
                            String s = jsonprimitive1 == null ? null : jsonprimitive1.getAsString();

                            if (chatclickable_enumclickaction != null && s != null && chatclickable_enumclickaction.a()) {
                                chatmodifier.h = new ChatClickable(chatclickable_enumclickaction, s);
                            }
                        }
                    }

                    if (jsonobject.has("hoverEvent")) {
                        jsonobject1 = jsonobject.getAsJsonObject("hoverEvent");
                        if (jsonobject1 != null) {
                            jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                            ChatHoverable.EnumHoverAction chathoverable_enumhoveraction = jsonprimitive == null ? null : ChatHoverable.EnumHoverAction.a(jsonprimitive.getAsString());
                            IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) jsondeserializationcontext.deserialize(jsonobject1.get("value"), IChatBaseComponent.class);

                            if (chathoverable_enumhoveraction != null && ichatbasecomponent != null && chathoverable_enumhoveraction.a()) {
                                chatmodifier.i = new ChatHoverable(chathoverable_enumhoveraction, ichatbasecomponent);
                            }
                        }
                    }

                    return chatmodifier;
                }
            } else {
                return null;
            }
        }

        @Nullable
        public JsonElement serialize(ChatModifier chatmodifier, Type type, JsonSerializationContext jsonserializationcontext) {
            if (chatmodifier.g()) {
                return null;
            } else {
                JsonObject jsonobject = new JsonObject();

                if (chatmodifier.c != null) {
                    jsonobject.addProperty("bold", chatmodifier.c);
                }

                if (chatmodifier.d != null) {
                    jsonobject.addProperty("italic", chatmodifier.d);
                }

                if (chatmodifier.e != null) {
                    jsonobject.addProperty("underlined", chatmodifier.e);
                }

                if (chatmodifier.f != null) {
                    jsonobject.addProperty("strikethrough", chatmodifier.f);
                }

                if (chatmodifier.g != null) {
                    jsonobject.addProperty("obfuscated", chatmodifier.g);
                }

                if (chatmodifier.b != null) {
                    jsonobject.add("color", jsonserializationcontext.serialize(chatmodifier.b));
                }

                if (chatmodifier.j != null) {
                    jsonobject.add("insertion", jsonserializationcontext.serialize(chatmodifier.j));
                }

                JsonObject jsonobject1;

                if (chatmodifier.h != null) {
                    jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("action", chatmodifier.h.a().b());
                    jsonobject1.addProperty("value", chatmodifier.h.b());
                    jsonobject.add("clickEvent", jsonobject1);
                }

                if (chatmodifier.i != null) {
                    jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("action", chatmodifier.i.a().b());
                    jsonobject1.add("value", jsonserializationcontext.serialize(chatmodifier.i.b()));
                    jsonobject.add("hoverEvent", jsonobject1);
                }

                return jsonobject;
            }
        }
    }
}
