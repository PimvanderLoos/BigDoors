package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface IChatBaseComponent extends Message, Iterable<IChatBaseComponent> {

    IChatBaseComponent setChatModifier(ChatModifier chatmodifier);

    ChatModifier getChatModifier();

    IChatBaseComponent a(String s);

    IChatBaseComponent addSibling(IChatBaseComponent ichatbasecomponent);

    String getText();

    String getString();

    String c();

    List<IChatBaseComponent> a();

    IChatBaseComponent e();

    default IChatBaseComponent a(Consumer<ChatModifier> consumer) {
        consumer.accept(this.getChatModifier());
        return this;
    }

    default IChatBaseComponent a(EnumChatFormat... aenumchatformat) {
        EnumChatFormat[] aenumchatformat1 = aenumchatformat;
        int i = aenumchatformat.length;

        for (int j = 0; j < i; ++j) {
            EnumChatFormat enumchatformat = aenumchatformat1[j];

            this.a(enumchatformat);
        }

        return this;
    }

    default IChatBaseComponent a(EnumChatFormat enumchatformat) {
        ChatModifier chatmodifier = this.getChatModifier();

        if (enumchatformat.d()) {
            chatmodifier.setColor(enumchatformat);
        }

        if (enumchatformat.isFormat()) {
            switch (enumchatformat) {
            case OBFUSCATED:
                chatmodifier.setRandom(Boolean.valueOf(true));
                break;

            case BOLD:
                chatmodifier.setBold(Boolean.valueOf(true));
                break;

            case STRIKETHROUGH:
                chatmodifier.setStrikethrough(Boolean.valueOf(true));
                break;

            case UNDERLINE:
                chatmodifier.setUnderline(Boolean.valueOf(true));
                break;

            case ITALIC:
                chatmodifier.setItalic(Boolean.valueOf(true));
            }
        }

        return this;
    }

    public static class ChatSerializer implements JsonDeserializer<IChatBaseComponent>, JsonSerializer<IChatBaseComponent> {

        private static final Gson a = (Gson) SystemUtils.a(() -> {
            GsonBuilder gsonbuilder = new GsonBuilder();

            gsonbuilder.registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer());
            gsonbuilder.registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer());
            gsonbuilder.registerTypeAdapterFactory(new ChatTypeAdapterFactory());
            return gsonbuilder.create();
        });
        private static final Field b = (Field) SystemUtils.a(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");

                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException nosuchfieldexception) {
                throw new IllegalStateException("Couldn\'t get field \'pos\' for JsonReader", nosuchfieldexception);
            }
        });
        private static final Field c = (Field) SystemUtils.a(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");

                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException nosuchfieldexception) {
                throw new IllegalStateException("Couldn\'t get field \'lineStart\' for JsonReader", nosuchfieldexception);
            }
        });

        public ChatSerializer() {}

        public IChatBaseComponent a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonPrimitive()) {
                return new ChatComponentText(jsonelement.getAsString());
            } else if (!jsonelement.isJsonObject()) {
                if (jsonelement.isJsonArray()) {
                    JsonArray jsonarray = jsonelement.getAsJsonArray();
                    IChatBaseComponent ichatbasecomponent = null;
                    Iterator iterator = jsonarray.iterator();

                    while (iterator.hasNext()) {
                        JsonElement jsonelement1 = (JsonElement) iterator.next();
                        IChatBaseComponent ichatbasecomponent1 = this.a(jsonelement1, (Type) jsonelement1.getClass(), jsondeserializationcontext);

                        if (ichatbasecomponent == null) {
                            ichatbasecomponent = ichatbasecomponent1;
                        } else {
                            ichatbasecomponent.addSibling(ichatbasecomponent1);
                        }
                    }

                    return ichatbasecomponent;
                } else {
                    throw new JsonParseException("Don\'t know how to turn " + jsonelement + " into a Component");
                }
            } else {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                Object object;

                if (jsonobject.has("text")) {
                    object = new ChatComponentText(jsonobject.get("text").getAsString());
                } else if (jsonobject.has("translate")) {
                    String s = jsonobject.get("translate").getAsString();

                    if (jsonobject.has("with")) {
                        JsonArray jsonarray1 = jsonobject.getAsJsonArray("with");
                        Object[] aobject = new Object[jsonarray1.size()];

                        for (int i = 0; i < aobject.length; ++i) {
                            aobject[i] = this.a(jsonarray1.get(i), type, jsondeserializationcontext);
                            if (aobject[i] instanceof ChatComponentText) {
                                ChatComponentText chatcomponenttext = (ChatComponentText) aobject[i];

                                if (chatcomponenttext.getChatModifier().g() && chatcomponenttext.a().isEmpty()) {
                                    aobject[i] = chatcomponenttext.f();
                                }
                            }
                        }

                        object = new ChatMessage(s, aobject);
                    } else {
                        object = new ChatMessage(s, new Object[0]);
                    }
                } else if (jsonobject.has("score")) {
                    JsonObject jsonobject1 = jsonobject.getAsJsonObject("score");

                    if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }

                    object = new ChatComponentScore(ChatDeserializer.h(jsonobject1, "name"), ChatDeserializer.h(jsonobject1, "objective"));
                    if (jsonobject1.has("value")) {
                        ((ChatComponentScore) object).b(ChatDeserializer.h(jsonobject1, "value"));
                    }
                } else if (jsonobject.has("selector")) {
                    object = new ChatComponentSelector(ChatDeserializer.h(jsonobject, "selector"));
                } else {
                    if (!jsonobject.has("keybind")) {
                        throw new JsonParseException("Don\'t know how to turn " + jsonelement + " into a Component");
                    }

                    object = new ChatComponentKeybind(ChatDeserializer.h(jsonobject, "keybind"));
                }

                if (jsonobject.has("extra")) {
                    JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");

                    if (jsonarray2.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (int j = 0; j < jsonarray2.size(); ++j) {
                        ((IChatBaseComponent) object).addSibling(this.a(jsonarray2.get(j), type, jsondeserializationcontext));
                    }
                }

                ((IChatBaseComponent) object).setChatModifier((ChatModifier) jsondeserializationcontext.deserialize(jsonelement, ChatModifier.class));
                return (IChatBaseComponent) object;
            }
        }

        private void a(ChatModifier chatmodifier, JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
            JsonElement jsonelement = jsonserializationcontext.serialize(chatmodifier);

            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject1 = (JsonObject) jsonelement;
                Iterator iterator = jsonobject1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    jsonobject.add((String) entry.getKey(), (JsonElement) entry.getValue());
                }
            }

        }

        public JsonElement a(IChatBaseComponent ichatbasecomponent, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (!ichatbasecomponent.getChatModifier().g()) {
                this.a(ichatbasecomponent.getChatModifier(), jsonobject, jsonserializationcontext);
            }

            if (!ichatbasecomponent.a().isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = ichatbasecomponent.a().iterator();

                while (iterator.hasNext()) {
                    IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) iterator.next();

                    jsonarray.add(this.a(ichatbasecomponent1, (Type) ichatbasecomponent1.getClass(), jsonserializationcontext));
                }

                jsonobject.add("extra", jsonarray);
            }

            if (ichatbasecomponent instanceof ChatComponentText) {
                jsonobject.addProperty("text", ((ChatComponentText) ichatbasecomponent).f());
            } else if (ichatbasecomponent instanceof ChatMessage) {
                ChatMessage chatmessage = (ChatMessage) ichatbasecomponent;

                jsonobject.addProperty("translate", chatmessage.h());
                if (chatmessage.i() != null && chatmessage.i().length > 0) {
                    JsonArray jsonarray1 = new JsonArray();
                    Object[] aobject = chatmessage.i();
                    int i = aobject.length;

                    for (int j = 0; j < i; ++j) {
                        Object object = aobject[j];

                        if (object instanceof IChatBaseComponent) {
                            jsonarray1.add(this.a((IChatBaseComponent) object, (Type) object.getClass(), jsonserializationcontext));
                        } else {
                            jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                        }
                    }

                    jsonobject.add("with", jsonarray1);
                }
            } else if (ichatbasecomponent instanceof ChatComponentScore) {
                ChatComponentScore chatcomponentscore = (ChatComponentScore) ichatbasecomponent;
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("name", chatcomponentscore.f());
                jsonobject1.addProperty("objective", chatcomponentscore.h());
                jsonobject1.addProperty("value", chatcomponentscore.getText());
                jsonobject.add("score", jsonobject1);
            } else if (ichatbasecomponent instanceof ChatComponentSelector) {
                ChatComponentSelector chatcomponentselector = (ChatComponentSelector) ichatbasecomponent;

                jsonobject.addProperty("selector", chatcomponentselector.f());
            } else {
                if (!(ichatbasecomponent instanceof ChatComponentKeybind)) {
                    throw new IllegalArgumentException("Don\'t know how to serialize " + ichatbasecomponent + " as a Component");
                }

                ChatComponentKeybind chatcomponentkeybind = (ChatComponentKeybind) ichatbasecomponent;

                jsonobject.addProperty("keybind", chatcomponentkeybind.g());
            }

            return jsonobject;
        }

        public static String a(IChatBaseComponent ichatbasecomponent) {
            return IChatBaseComponent.ChatSerializer.a.toJson(ichatbasecomponent);
        }

        public static JsonElement b(IChatBaseComponent ichatbasecomponent) {
            return IChatBaseComponent.ChatSerializer.a.toJsonTree(ichatbasecomponent);
        }

        @Nullable
        public static IChatBaseComponent a(String s) {
            return (IChatBaseComponent) ChatDeserializer.a(IChatBaseComponent.ChatSerializer.a, s, IChatBaseComponent.class, false);
        }

        @Nullable
        public static IChatBaseComponent a(JsonElement jsonelement) {
            return (IChatBaseComponent) IChatBaseComponent.ChatSerializer.a.fromJson(jsonelement, IChatBaseComponent.class);
        }

        @Nullable
        public static IChatBaseComponent b(String s) {
            return (IChatBaseComponent) ChatDeserializer.a(IChatBaseComponent.ChatSerializer.a, s, IChatBaseComponent.class, true);
        }

        public static IChatBaseComponent a(com.mojang.brigadier.StringReader com_mojang_brigadier_stringreader) {
            try {
                JsonReader jsonreader = new JsonReader(new StringReader(com_mojang_brigadier_stringreader.getRemaining()));

                jsonreader.setLenient(false);
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) IChatBaseComponent.ChatSerializer.a.getAdapter(IChatBaseComponent.class).read(jsonreader);

                com_mojang_brigadier_stringreader.setCursor(com_mojang_brigadier_stringreader.getCursor() + a(jsonreader));
                return ichatbasecomponent;
            } catch (IOException ioexception) {
                throw new JsonParseException(ioexception);
            }
        }

        private static int a(JsonReader jsonreader) {
            try {
                return IChatBaseComponent.ChatSerializer.b.getInt(jsonreader) - IChatBaseComponent.ChatSerializer.c.getInt(jsonreader) + 1;
            } catch (IllegalAccessException illegalaccessexception) {
                throw new IllegalStateException("Couldn\'t read position of JsonReader", illegalaccessexception);
            }
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((IChatBaseComponent) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
