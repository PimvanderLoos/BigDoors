package net.minecraft.network.chat;

import com.google.common.collect.Lists;
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
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.ChatTypeAdapterFactory;
import net.minecraft.util.FormattedString;

public interface IChatBaseComponent extends Message, IChatFormatted {

    ChatModifier getChatModifier();

    String getText();

    @Override
    default String getString() {
        return IChatFormatted.super.getString();
    }

    default String a(int i) {
        StringBuilder stringbuilder = new StringBuilder();

        this.a((s) -> {
            int j = i - stringbuilder.length();

            if (j <= 0) {
                return IChatBaseComponent.STOP_ITERATION;
            } else {
                stringbuilder.append(s.length() <= j ? s : s.substring(0, j));
                return Optional.empty();
            }
        });
        return stringbuilder.toString();
    }

    List<IChatBaseComponent> getSiblings();

    IChatMutableComponent g();

    IChatMutableComponent mutableCopy();

    FormattedString f();

    @Override
    default <T> Optional<T> a(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        ChatModifier chatmodifier1 = this.getChatModifier().setChatModifier(chatmodifier);
        Optional<T> optional = this.b(ichatformatted_b, chatmodifier1);

        if (optional.isPresent()) {
            return optional;
        } else {
            Iterator iterator = this.getSiblings().iterator();

            Optional optional1;

            do {
                if (!iterator.hasNext()) {
                    return Optional.empty();
                }

                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

                optional1 = ichatbasecomponent.a(ichatformatted_b, chatmodifier1);
            } while (!optional1.isPresent());

            return optional1;
        }
    }

    @Override
    default <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
        Optional<T> optional = this.b(ichatformatted_a);

        if (optional.isPresent()) {
            return optional;
        } else {
            Iterator iterator = this.getSiblings().iterator();

            Optional optional1;

            do {
                if (!iterator.hasNext()) {
                    return Optional.empty();
                }

                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

                optional1 = ichatbasecomponent.a(ichatformatted_a);
            } while (!optional1.isPresent());

            return optional1;
        }
    }

    default <T> Optional<T> b(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        return ichatformatted_b.accept(chatmodifier, this.getText());
    }

    default <T> Optional<T> b(IChatFormatted.a<T> ichatformatted_a) {
        return ichatformatted_a.accept(this.getText());
    }

    default List<IChatBaseComponent> b(ChatModifier chatmodifier) {
        List<IChatBaseComponent> list = Lists.newArrayList();

        this.a((chatmodifier1, s) -> {
            if (!s.isEmpty()) {
                list.add((new ChatComponentText(s)).c(chatmodifier1));
            }

            return Optional.empty();
        }, chatmodifier);
        return list;
    }

    static IChatBaseComponent a(@Nullable String s) {
        return (IChatBaseComponent) (s != null ? new ChatComponentText(s) : ChatComponentText.EMPTY);
    }

    public static class ChatSerializer implements JsonDeserializer<IChatMutableComponent>, JsonSerializer<IChatBaseComponent> {

        private static final Gson GSON = (Gson) SystemUtils.a(() -> {
            GsonBuilder gsonbuilder = new GsonBuilder();

            gsonbuilder.disableHtmlEscaping();
            gsonbuilder.registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer());
            gsonbuilder.registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer());
            gsonbuilder.registerTypeAdapterFactory(new ChatTypeAdapterFactory());
            return gsonbuilder.create();
        });
        private static final Field JSON_READER_POS = (Field) SystemUtils.a(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");

                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException nosuchfieldexception) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
            }
        });
        private static final Field JSON_READER_LINESTART = (Field) SystemUtils.a(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");

                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException nosuchfieldexception) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", nosuchfieldexception);
            }
        });

        public ChatSerializer() {}

        public IChatMutableComponent deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonPrimitive()) {
                return new ChatComponentText(jsonelement.getAsString());
            } else if (!jsonelement.isJsonObject()) {
                if (jsonelement.isJsonArray()) {
                    JsonArray jsonarray = jsonelement.getAsJsonArray();
                    IChatMutableComponent ichatmutablecomponent = null;
                    Iterator iterator = jsonarray.iterator();

                    while (iterator.hasNext()) {
                        JsonElement jsonelement1 = (JsonElement) iterator.next();
                        IChatMutableComponent ichatmutablecomponent1 = this.deserialize(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);

                        if (ichatmutablecomponent == null) {
                            ichatmutablecomponent = ichatmutablecomponent1;
                        } else {
                            ichatmutablecomponent.addSibling(ichatmutablecomponent1);
                        }
                    }

                    return ichatmutablecomponent;
                } else {
                    throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
                }
            } else {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                Object object;

                if (jsonobject.has("text")) {
                    object = new ChatComponentText(ChatDeserializer.h(jsonobject, "text"));
                } else {
                    String s;

                    if (jsonobject.has("translate")) {
                        s = ChatDeserializer.h(jsonobject, "translate");
                        if (jsonobject.has("with")) {
                            JsonArray jsonarray1 = ChatDeserializer.u(jsonobject, "with");
                            Object[] aobject = new Object[jsonarray1.size()];

                            for (int i = 0; i < aobject.length; ++i) {
                                aobject[i] = this.deserialize(jsonarray1.get(i), type, jsondeserializationcontext);
                                if (aobject[i] instanceof ChatComponentText) {
                                    ChatComponentText chatcomponenttext = (ChatComponentText) aobject[i];

                                    if (chatcomponenttext.getChatModifier().g() && chatcomponenttext.getSiblings().isEmpty()) {
                                        aobject[i] = chatcomponenttext.h();
                                    }
                                }
                            }

                            object = new ChatMessage(s, aobject);
                        } else {
                            object = new ChatMessage(s);
                        }
                    } else if (jsonobject.has("score")) {
                        JsonObject jsonobject1 = ChatDeserializer.t(jsonobject, "score");

                        if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                            throw new JsonParseException("A score component needs a least a name and an objective");
                        }

                        object = new ChatComponentScore(ChatDeserializer.h(jsonobject1, "name"), ChatDeserializer.h(jsonobject1, "objective"));
                    } else if (jsonobject.has("selector")) {
                        Optional<IChatBaseComponent> optional = this.a(type, jsondeserializationcontext, jsonobject);

                        object = new ChatComponentSelector(ChatDeserializer.h(jsonobject, "selector"), optional);
                    } else if (jsonobject.has("keybind")) {
                        object = new ChatComponentKeybind(ChatDeserializer.h(jsonobject, "keybind"));
                    } else {
                        if (!jsonobject.has("nbt")) {
                            throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
                        }

                        s = ChatDeserializer.h(jsonobject, "nbt");
                        Optional<IChatBaseComponent> optional1 = this.a(type, jsondeserializationcontext, jsonobject);
                        boolean flag = ChatDeserializer.a(jsonobject, "interpret", false);

                        if (jsonobject.has("block")) {
                            object = new ChatComponentNBT.a(s, flag, ChatDeserializer.h(jsonobject, "block"), optional1);
                        } else if (jsonobject.has("entity")) {
                            object = new ChatComponentNBT.b(s, flag, ChatDeserializer.h(jsonobject, "entity"), optional1);
                        } else {
                            if (!jsonobject.has("storage")) {
                                throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
                            }

                            object = new ChatComponentNBT.c(s, flag, new MinecraftKey(ChatDeserializer.h(jsonobject, "storage")), optional1);
                        }
                    }
                }

                if (jsonobject.has("extra")) {
                    JsonArray jsonarray2 = ChatDeserializer.u(jsonobject, "extra");

                    if (jsonarray2.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (int j = 0; j < jsonarray2.size(); ++j) {
                        ((IChatMutableComponent) object).addSibling(this.deserialize(jsonarray2.get(j), type, jsondeserializationcontext));
                    }
                }

                ((IChatMutableComponent) object).setChatModifier((ChatModifier) jsondeserializationcontext.deserialize(jsonelement, ChatModifier.class));
                return (IChatMutableComponent) object;
            }
        }

        private Optional<IChatBaseComponent> a(Type type, JsonDeserializationContext jsondeserializationcontext, JsonObject jsonobject) {
            return jsonobject.has("separator") ? Optional.of(this.deserialize(jsonobject.get("separator"), type, jsondeserializationcontext)) : Optional.empty();
        }

        private void a(ChatModifier chatmodifier, JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
            JsonElement jsonelement = jsonserializationcontext.serialize(chatmodifier);

            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject1 = (JsonObject) jsonelement;
                Iterator iterator = jsonobject1.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, JsonElement> entry = (Entry) iterator.next();

                    jsonobject.add((String) entry.getKey(), (JsonElement) entry.getValue());
                }
            }

        }

        public JsonElement serialize(IChatBaseComponent ichatbasecomponent, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (!ichatbasecomponent.getChatModifier().g()) {
                this.a(ichatbasecomponent.getChatModifier(), jsonobject, jsonserializationcontext);
            }

            if (!ichatbasecomponent.getSiblings().isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = ichatbasecomponent.getSiblings().iterator();

                while (iterator.hasNext()) {
                    IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) iterator.next();

                    jsonarray.add(this.serialize(ichatbasecomponent1, ichatbasecomponent1.getClass(), jsonserializationcontext));
                }

                jsonobject.add("extra", jsonarray);
            }

            if (ichatbasecomponent instanceof ChatComponentText) {
                jsonobject.addProperty("text", ((ChatComponentText) ichatbasecomponent).h());
            } else if (ichatbasecomponent instanceof ChatMessage) {
                ChatMessage chatmessage = (ChatMessage) ichatbasecomponent;

                jsonobject.addProperty("translate", chatmessage.getKey());
                if (chatmessage.getArgs() != null && chatmessage.getArgs().length > 0) {
                    JsonArray jsonarray1 = new JsonArray();
                    Object[] aobject = chatmessage.getArgs();
                    int i = aobject.length;

                    for (int j = 0; j < i; ++j) {
                        Object object = aobject[j];

                        if (object instanceof IChatBaseComponent) {
                            jsonarray1.add(this.serialize((IChatBaseComponent) object, object.getClass(), jsonserializationcontext));
                        } else {
                            jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                        }
                    }

                    jsonobject.add("with", jsonarray1);
                }
            } else if (ichatbasecomponent instanceof ChatComponentScore) {
                ChatComponentScore chatcomponentscore = (ChatComponentScore) ichatbasecomponent;
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("name", chatcomponentscore.h());
                jsonobject1.addProperty("objective", chatcomponentscore.j());
                jsonobject.add("score", jsonobject1);
            } else if (ichatbasecomponent instanceof ChatComponentSelector) {
                ChatComponentSelector chatcomponentselector = (ChatComponentSelector) ichatbasecomponent;

                jsonobject.addProperty("selector", chatcomponentselector.h());
                this.a(jsonserializationcontext, jsonobject, chatcomponentselector.j());
            } else if (ichatbasecomponent instanceof ChatComponentKeybind) {
                ChatComponentKeybind chatcomponentkeybind = (ChatComponentKeybind) ichatbasecomponent;

                jsonobject.addProperty("keybind", chatcomponentkeybind.i());
            } else {
                if (!(ichatbasecomponent instanceof ChatComponentNBT)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + ichatbasecomponent + " as a Component");
                }

                ChatComponentNBT chatcomponentnbt = (ChatComponentNBT) ichatbasecomponent;

                jsonobject.addProperty("nbt", chatcomponentnbt.h());
                jsonobject.addProperty("interpret", chatcomponentnbt.i());
                this.a(jsonserializationcontext, jsonobject, chatcomponentnbt.separator);
                if (ichatbasecomponent instanceof ChatComponentNBT.a) {
                    ChatComponentNBT.a chatcomponentnbt_a = (ChatComponentNBT.a) ichatbasecomponent;

                    jsonobject.addProperty("block", chatcomponentnbt_a.j());
                } else if (ichatbasecomponent instanceof ChatComponentNBT.b) {
                    ChatComponentNBT.b chatcomponentnbt_b = (ChatComponentNBT.b) ichatbasecomponent;

                    jsonobject.addProperty("entity", chatcomponentnbt_b.j());
                } else {
                    if (!(ichatbasecomponent instanceof ChatComponentNBT.c)) {
                        throw new IllegalArgumentException("Don't know how to serialize " + ichatbasecomponent + " as a Component");
                    }

                    ChatComponentNBT.c chatcomponentnbt_c = (ChatComponentNBT.c) ichatbasecomponent;

                    jsonobject.addProperty("storage", chatcomponentnbt_c.j().toString());
                }
            }

            return jsonobject;
        }

        private void a(JsonSerializationContext jsonserializationcontext, JsonObject jsonobject, Optional<IChatBaseComponent> optional) {
            optional.ifPresent((ichatbasecomponent) -> {
                jsonobject.add("separator", this.serialize(ichatbasecomponent, ichatbasecomponent.getClass(), jsonserializationcontext));
            });
        }

        public static String a(IChatBaseComponent ichatbasecomponent) {
            return IChatBaseComponent.ChatSerializer.GSON.toJson(ichatbasecomponent);
        }

        public static JsonElement b(IChatBaseComponent ichatbasecomponent) {
            return IChatBaseComponent.ChatSerializer.GSON.toJsonTree(ichatbasecomponent);
        }

        @Nullable
        public static IChatMutableComponent a(String s) {
            return (IChatMutableComponent) ChatDeserializer.a(IChatBaseComponent.ChatSerializer.GSON, s, IChatMutableComponent.class, false);
        }

        @Nullable
        public static IChatMutableComponent a(JsonElement jsonelement) {
            return (IChatMutableComponent) IChatBaseComponent.ChatSerializer.GSON.fromJson(jsonelement, IChatMutableComponent.class);
        }

        @Nullable
        public static IChatMutableComponent b(String s) {
            return (IChatMutableComponent) ChatDeserializer.a(IChatBaseComponent.ChatSerializer.GSON, s, IChatMutableComponent.class, true);
        }

        public static IChatMutableComponent a(com.mojang.brigadier.StringReader com_mojang_brigadier_stringreader) {
            try {
                JsonReader jsonreader = new JsonReader(new StringReader(com_mojang_brigadier_stringreader.getRemaining()));

                jsonreader.setLenient(false);
                IChatMutableComponent ichatmutablecomponent = (IChatMutableComponent) IChatBaseComponent.ChatSerializer.GSON.getAdapter(IChatMutableComponent.class).read(jsonreader);

                com_mojang_brigadier_stringreader.setCursor(com_mojang_brigadier_stringreader.getCursor() + a(jsonreader));
                return ichatmutablecomponent;
            } catch (StackOverflowError | IOException ioexception) {
                throw new JsonParseException(ioexception);
            }
        }

        private static int a(JsonReader jsonreader) {
            try {
                return IChatBaseComponent.ChatSerializer.JSON_READER_POS.getInt(jsonreader) - IChatBaseComponent.ChatSerializer.JSON_READER_LINESTART.getInt(jsonreader) + 1;
            } catch (IllegalAccessException illegalaccessexception) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
            }
        }
    }
}
