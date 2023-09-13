package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;

public class ChatModifier {

    public static final ChatModifier EMPTY = new ChatModifier((ChatHexColor) null, (Boolean) null, (Boolean) null, (Boolean) null, (Boolean) null, (Boolean) null, (ChatClickable) null, (ChatHoverable) null, (String) null, (MinecraftKey) null);
    public static final Codec<ChatModifier> FORMATTING_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ChatHexColor.CODEC.optionalFieldOf("color").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.color);
        }), Codec.BOOL.optionalFieldOf("bold").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.bold);
        }), Codec.BOOL.optionalFieldOf("italic").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.italic);
        }), Codec.BOOL.optionalFieldOf("underlined").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.underlined);
        }), Codec.BOOL.optionalFieldOf("strikethrough").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.strikethrough);
        }), Codec.BOOL.optionalFieldOf("obfuscated").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.obfuscated);
        }), Codec.STRING.optionalFieldOf("insertion").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.insertion);
        }), MinecraftKey.CODEC.optionalFieldOf("font").forGetter((chatmodifier) -> {
            return Optional.ofNullable(chatmodifier.font);
        })).apply(instance, ChatModifier::create);
    });
    public static final MinecraftKey DEFAULT_FONT = new MinecraftKey("minecraft", "default");
    @Nullable
    final ChatHexColor color;
    @Nullable
    final Boolean bold;
    @Nullable
    final Boolean italic;
    @Nullable
    final Boolean underlined;
    @Nullable
    final Boolean strikethrough;
    @Nullable
    final Boolean obfuscated;
    @Nullable
    final ChatClickable clickEvent;
    @Nullable
    final ChatHoverable hoverEvent;
    @Nullable
    final String insertion;
    @Nullable
    final MinecraftKey font;

    private static ChatModifier create(Optional<ChatHexColor> optional, Optional<Boolean> optional1, Optional<Boolean> optional2, Optional<Boolean> optional3, Optional<Boolean> optional4, Optional<Boolean> optional5, Optional<String> optional6, Optional<MinecraftKey> optional7) {
        return new ChatModifier((ChatHexColor) optional.orElse((Object) null), (Boolean) optional1.orElse((Object) null), (Boolean) optional2.orElse((Object) null), (Boolean) optional3.orElse((Object) null), (Boolean) optional4.orElse((Object) null), (Boolean) optional5.orElse((Object) null), (ChatClickable) null, (ChatHoverable) null, (String) optional6.orElse((Object) null), (MinecraftKey) optional7.orElse((Object) null));
    }

    ChatModifier(@Nullable ChatHexColor chathexcolor, @Nullable Boolean obool, @Nullable Boolean obool1, @Nullable Boolean obool2, @Nullable Boolean obool3, @Nullable Boolean obool4, @Nullable ChatClickable chatclickable, @Nullable ChatHoverable chathoverable, @Nullable String s, @Nullable MinecraftKey minecraftkey) {
        this.color = chathexcolor;
        this.bold = obool;
        this.italic = obool1;
        this.underlined = obool2;
        this.strikethrough = obool3;
        this.obfuscated = obool4;
        this.clickEvent = chatclickable;
        this.hoverEvent = chathoverable;
        this.insertion = s;
        this.font = minecraftkey;
    }

    @Nullable
    public ChatHexColor getColor() {
        return this.color;
    }

    public boolean isBold() {
        return this.bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return this.italic == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return this.strikethrough == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return this.underlined == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return this.obfuscated == Boolean.TRUE;
    }

    public boolean isEmpty() {
        return this == ChatModifier.EMPTY;
    }

    @Nullable
    public ChatClickable getClickEvent() {
        return this.clickEvent;
    }

    @Nullable
    public ChatHoverable getHoverEvent() {
        return this.hoverEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion;
    }

    public MinecraftKey getFont() {
        return this.font != null ? this.font : ChatModifier.DEFAULT_FONT;
    }

    public ChatModifier withColor(@Nullable ChatHexColor chathexcolor) {
        return new ChatModifier(chathexcolor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withColor(@Nullable EnumChatFormat enumchatformat) {
        return this.withColor(enumchatformat != null ? ChatHexColor.fromLegacyFormat(enumchatformat) : null);
    }

    public ChatModifier withColor(int i) {
        return this.withColor(ChatHexColor.fromRgb(i));
    }

    public ChatModifier withBold(@Nullable Boolean obool) {
        return new ChatModifier(this.color, obool, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withItalic(@Nullable Boolean obool) {
        return new ChatModifier(this.color, this.bold, obool, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withUnderlined(@Nullable Boolean obool) {
        return new ChatModifier(this.color, this.bold, this.italic, obool, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withStrikethrough(@Nullable Boolean obool) {
        return new ChatModifier(this.color, this.bold, this.italic, this.underlined, obool, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withObfuscated(@Nullable Boolean obool) {
        return new ChatModifier(this.color, this.bold, this.italic, this.underlined, this.strikethrough, obool, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withClickEvent(@Nullable ChatClickable chatclickable) {
        return new ChatModifier(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, chatclickable, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier withHoverEvent(@Nullable ChatHoverable chathoverable) {
        return new ChatModifier(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, chathoverable, this.insertion, this.font);
    }

    public ChatModifier withInsertion(@Nullable String s) {
        return new ChatModifier(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, s, this.font);
    }

    public ChatModifier withFont(@Nullable MinecraftKey minecraftkey) {
        return new ChatModifier(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, minecraftkey);
    }

    public ChatModifier applyFormat(EnumChatFormat enumchatformat) {
        ChatHexColor chathexcolor = this.color;
        Boolean obool = this.bold;
        Boolean obool1 = this.italic;
        Boolean obool2 = this.strikethrough;
        Boolean obool3 = this.underlined;
        Boolean obool4 = this.obfuscated;

        switch (enumchatformat) {
            case OBFUSCATED:
                obool4 = true;
                break;
            case BOLD:
                obool = true;
                break;
            case STRIKETHROUGH:
                obool2 = true;
                break;
            case UNDERLINE:
                obool3 = true;
                break;
            case ITALIC:
                obool1 = true;
                break;
            case RESET:
                return ChatModifier.EMPTY;
            default:
                chathexcolor = ChatHexColor.fromLegacyFormat(enumchatformat);
        }

        return new ChatModifier(chathexcolor, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier applyLegacyFormat(EnumChatFormat enumchatformat) {
        ChatHexColor chathexcolor = this.color;
        Boolean obool = this.bold;
        Boolean obool1 = this.italic;
        Boolean obool2 = this.strikethrough;
        Boolean obool3 = this.underlined;
        Boolean obool4 = this.obfuscated;

        switch (enumchatformat) {
            case OBFUSCATED:
                obool4 = true;
                break;
            case BOLD:
                obool = true;
                break;
            case STRIKETHROUGH:
                obool2 = true;
                break;
            case UNDERLINE:
                obool3 = true;
                break;
            case ITALIC:
                obool1 = true;
                break;
            case RESET:
                return ChatModifier.EMPTY;
            default:
                obool4 = false;
                obool = false;
                obool2 = false;
                obool3 = false;
                obool1 = false;
                chathexcolor = ChatHexColor.fromLegacyFormat(enumchatformat);
        }

        return new ChatModifier(chathexcolor, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier applyFormats(EnumChatFormat... aenumchatformat) {
        ChatHexColor chathexcolor = this.color;
        Boolean obool = this.bold;
        Boolean obool1 = this.italic;
        Boolean obool2 = this.strikethrough;
        Boolean obool3 = this.underlined;
        Boolean obool4 = this.obfuscated;
        EnumChatFormat[] aenumchatformat1 = aenumchatformat;
        int i = aenumchatformat.length;

        for (int j = 0; j < i; ++j) {
            EnumChatFormat enumchatformat = aenumchatformat1[j];

            switch (enumchatformat) {
                case OBFUSCATED:
                    obool4 = true;
                    break;
                case BOLD:
                    obool = true;
                    break;
                case STRIKETHROUGH:
                    obool2 = true;
                    break;
                case UNDERLINE:
                    obool3 = true;
                    break;
                case ITALIC:
                    obool1 = true;
                    break;
                case RESET:
                    return ChatModifier.EMPTY;
                default:
                    chathexcolor = ChatHexColor.fromLegacyFormat(enumchatformat);
            }
        }

        return new ChatModifier(chathexcolor, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public ChatModifier applyTo(ChatModifier chatmodifier) {
        return this == ChatModifier.EMPTY ? chatmodifier : (chatmodifier == ChatModifier.EMPTY ? this : new ChatModifier(this.color != null ? this.color : chatmodifier.color, this.bold != null ? this.bold : chatmodifier.bold, this.italic != null ? this.italic : chatmodifier.italic, this.underlined != null ? this.underlined : chatmodifier.underlined, this.strikethrough != null ? this.strikethrough : chatmodifier.strikethrough, this.obfuscated != null ? this.obfuscated : chatmodifier.obfuscated, this.clickEvent != null ? this.clickEvent : chatmodifier.clickEvent, this.hoverEvent != null ? this.hoverEvent : chatmodifier.hoverEvent, this.insertion != null ? this.insertion : chatmodifier.insertion, this.font != null ? this.font : chatmodifier.font));
    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        class a {

            private boolean isNotFirst;

            a() {}

            private void prependSeparator() {
                if (this.isNotFirst) {
                    stringbuilder.append(',');
                }

                this.isNotFirst = true;
            }

            void addFlagString(String s, @Nullable Boolean obool) {
                if (obool != null) {
                    this.prependSeparator();
                    if (!obool) {
                        stringbuilder.append('!');
                    }

                    stringbuilder.append(s);
                }

            }

            void addValueString(String s, @Nullable Object object) {
                if (object != null) {
                    this.prependSeparator();
                    stringbuilder.append(s);
                    stringbuilder.append('=');
                    stringbuilder.append(object);
                }

            }
        }

        a a0 = new a();

        a0.addValueString("color", this.color);
        a0.addFlagString("bold", this.bold);
        a0.addFlagString("italic", this.italic);
        a0.addFlagString("underlined", this.underlined);
        a0.addFlagString("strikethrough", this.strikethrough);
        a0.addFlagString("obfuscated", this.obfuscated);
        a0.addValueString("clickEvent", this.clickEvent);
        a0.addValueString("hoverEvent", this.hoverEvent);
        a0.addValueString("insertion", this.insertion);
        a0.addValueString("font", this.font);
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatModifier)) {
            return false;
        } else {
            ChatModifier chatmodifier = (ChatModifier) object;

            return this.isBold() == chatmodifier.isBold() && Objects.equals(this.getColor(), chatmodifier.getColor()) && this.isItalic() == chatmodifier.isItalic() && this.isObfuscated() == chatmodifier.isObfuscated() && this.isStrikethrough() == chatmodifier.isStrikethrough() && this.isUnderlined() == chatmodifier.isUnderlined() && Objects.equals(this.getClickEvent(), chatmodifier.getClickEvent()) && Objects.equals(this.getHoverEvent(), chatmodifier.getHoverEvent()) && Objects.equals(this.getInsertion(), chatmodifier.getInsertion()) && Objects.equals(this.getFont(), chatmodifier.getFont());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
    }

    public static class ChatModifierSerializer implements JsonDeserializer<ChatModifier>, JsonSerializer<ChatModifier> {

        public ChatModifierSerializer() {}

        @Nullable
        public ChatModifier deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = jsonelement.getAsJsonObject();

                if (jsonobject == null) {
                    return null;
                } else {
                    Boolean obool = getOptionalFlag(jsonobject, "bold");
                    Boolean obool1 = getOptionalFlag(jsonobject, "italic");
                    Boolean obool2 = getOptionalFlag(jsonobject, "underlined");
                    Boolean obool3 = getOptionalFlag(jsonobject, "strikethrough");
                    Boolean obool4 = getOptionalFlag(jsonobject, "obfuscated");
                    ChatHexColor chathexcolor = getTextColor(jsonobject);
                    String s = getInsertion(jsonobject);
                    ChatClickable chatclickable = getClickEvent(jsonobject);
                    ChatHoverable chathoverable = getHoverEvent(jsonobject);
                    MinecraftKey minecraftkey = getFont(jsonobject);

                    return new ChatModifier(chathexcolor, obool, obool1, obool2, obool3, obool4, chatclickable, chathoverable, s, minecraftkey);
                }
            } else {
                return null;
            }
        }

        @Nullable
        private static MinecraftKey getFont(JsonObject jsonobject) {
            if (jsonobject.has("font")) {
                String s = ChatDeserializer.getAsString(jsonobject, "font");

                try {
                    return new MinecraftKey(s);
                } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                    throw new JsonSyntaxException("Invalid font name: " + s);
                }
            } else {
                return null;
            }
        }

        @Nullable
        private static ChatHoverable getHoverEvent(JsonObject jsonobject) {
            if (jsonobject.has("hoverEvent")) {
                JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "hoverEvent");
                ChatHoverable chathoverable = ChatHoverable.deserialize(jsonobject1);

                if (chathoverable != null && chathoverable.getAction().isAllowedFromServer()) {
                    return chathoverable;
                }
            }

            return null;
        }

        @Nullable
        private static ChatClickable getClickEvent(JsonObject jsonobject) {
            if (jsonobject.has("clickEvent")) {
                JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "clickEvent");
                String s = ChatDeserializer.getAsString(jsonobject1, "action", (String) null);
                ChatClickable.EnumClickAction chatclickable_enumclickaction = s == null ? null : ChatClickable.EnumClickAction.getByName(s);
                String s1 = ChatDeserializer.getAsString(jsonobject1, "value", (String) null);

                if (chatclickable_enumclickaction != null && s1 != null && chatclickable_enumclickaction.isAllowedFromServer()) {
                    return new ChatClickable(chatclickable_enumclickaction, s1);
                }
            }

            return null;
        }

        @Nullable
        private static String getInsertion(JsonObject jsonobject) {
            return ChatDeserializer.getAsString(jsonobject, "insertion", (String) null);
        }

        @Nullable
        private static ChatHexColor getTextColor(JsonObject jsonobject) {
            if (jsonobject.has("color")) {
                String s = ChatDeserializer.getAsString(jsonobject, "color");

                return ChatHexColor.parseColor(s);
            } else {
                return null;
            }
        }

        @Nullable
        private static Boolean getOptionalFlag(JsonObject jsonobject, String s) {
            return jsonobject.has(s) ? jsonobject.get(s).getAsBoolean() : null;
        }

        @Nullable
        public JsonElement serialize(ChatModifier chatmodifier, Type type, JsonSerializationContext jsonserializationcontext) {
            if (chatmodifier.isEmpty()) {
                return null;
            } else {
                JsonObject jsonobject = new JsonObject();

                if (chatmodifier.bold != null) {
                    jsonobject.addProperty("bold", chatmodifier.bold);
                }

                if (chatmodifier.italic != null) {
                    jsonobject.addProperty("italic", chatmodifier.italic);
                }

                if (chatmodifier.underlined != null) {
                    jsonobject.addProperty("underlined", chatmodifier.underlined);
                }

                if (chatmodifier.strikethrough != null) {
                    jsonobject.addProperty("strikethrough", chatmodifier.strikethrough);
                }

                if (chatmodifier.obfuscated != null) {
                    jsonobject.addProperty("obfuscated", chatmodifier.obfuscated);
                }

                if (chatmodifier.color != null) {
                    jsonobject.addProperty("color", chatmodifier.color.serialize());
                }

                if (chatmodifier.insertion != null) {
                    jsonobject.add("insertion", jsonserializationcontext.serialize(chatmodifier.insertion));
                }

                if (chatmodifier.clickEvent != null) {
                    JsonObject jsonobject1 = new JsonObject();

                    jsonobject1.addProperty("action", chatmodifier.clickEvent.getAction().getName());
                    jsonobject1.addProperty("value", chatmodifier.clickEvent.getValue());
                    jsonobject.add("clickEvent", jsonobject1);
                }

                if (chatmodifier.hoverEvent != null) {
                    jsonobject.add("hoverEvent", chatmodifier.hoverEvent.serialize());
                }

                if (chatmodifier.font != null) {
                    jsonobject.addProperty("font", chatmodifier.font.toString());
                }

                return jsonobject;
            }
        }
    }
}
