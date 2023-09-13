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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.ChatTypeAdapterFactory;
import net.minecraft.util.FormattedString;

public interface IChatBaseComponent extends Message, IChatFormatted {

    ChatModifier getStyle();

    ComponentContents getContents();

    @Override
    default String getString() {
        return IChatFormatted.super.getString();
    }

    default String getString(int i) {
        StringBuilder stringbuilder = new StringBuilder();

        this.visit((s) -> {
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

    default IChatMutableComponent plainCopy() {
        return IChatMutableComponent.create(this.getContents());
    }

    default IChatMutableComponent copy() {
        return new IChatMutableComponent(this.getContents(), new ArrayList(this.getSiblings()), this.getStyle());
    }

    FormattedString getVisualOrderText();

    @Override
    default <T> Optional<T> visit(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
        ChatModifier chatmodifier1 = this.getStyle().applyTo(chatmodifier);
        Optional<T> optional = this.getContents().visit(ichatformatted_b, chatmodifier1);

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

                optional1 = ichatbasecomponent.visit(ichatformatted_b, chatmodifier1);
            } while (!optional1.isPresent());

            return optional1;
        }
    }

    @Override
    default <T> Optional<T> visit(IChatFormatted.a<T> ichatformatted_a) {
        Optional<T> optional = this.getContents().visit(ichatformatted_a);

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

                optional1 = ichatbasecomponent.visit(ichatformatted_a);
            } while (!optional1.isPresent());

            return optional1;
        }
    }

    default List<IChatBaseComponent> toFlatList() {
        return this.toFlatList(ChatModifier.EMPTY);
    }

    default List<IChatBaseComponent> toFlatList(ChatModifier chatmodifier) {
        List<IChatBaseComponent> list = Lists.newArrayList();

        this.visit((chatmodifier1, s) -> {
            if (!s.isEmpty()) {
                list.add(literal(s).withStyle(chatmodifier1));
            }

            return Optional.empty();
        }, chatmodifier);
        return list;
    }

    default boolean contains(IChatBaseComponent ichatbasecomponent) {
        if (this.equals(ichatbasecomponent)) {
            return true;
        } else {
            List<IChatBaseComponent> list = this.toFlatList();
            List<IChatBaseComponent> list1 = ichatbasecomponent.toFlatList(this.getStyle());

            return Collections.indexOfSubList(list, list1) != -1;
        }
    }

    static IChatBaseComponent nullToEmpty(@Nullable String s) {
        return (IChatBaseComponent) (s != null ? literal(s) : CommonComponents.EMPTY);
    }

    static IChatMutableComponent literal(String s) {
        return IChatMutableComponent.create(new LiteralContents(s));
    }

    static IChatMutableComponent translatable(String s) {
        return IChatMutableComponent.create(new TranslatableContents(s, (String) null, TranslatableContents.NO_ARGS));
    }

    static IChatMutableComponent translatable(String s, Object... aobject) {
        return IChatMutableComponent.create(new TranslatableContents(s, (String) null, aobject));
    }

    static IChatMutableComponent translatableWithFallback(String s, @Nullable String s1) {
        return IChatMutableComponent.create(new TranslatableContents(s, s1, TranslatableContents.NO_ARGS));
    }

    static IChatMutableComponent translatableWithFallback(String s, @Nullable String s1, Object... aobject) {
        return IChatMutableComponent.create(new TranslatableContents(s, s1, aobject));
    }

    static IChatMutableComponent empty() {
        return IChatMutableComponent.create(ComponentContents.EMPTY);
    }

    static IChatMutableComponent keybind(String s) {
        return IChatMutableComponent.create(new KeybindContents(s));
    }

    static IChatMutableComponent nbt(String s, boolean flag, Optional<IChatBaseComponent> optional, DataSource datasource) {
        return IChatMutableComponent.create(new NbtContents(s, flag, optional, datasource));
    }

    static IChatMutableComponent score(String s, String s1) {
        return IChatMutableComponent.create(new ScoreContents(s, s1));
    }

    static IChatMutableComponent selector(String s, Optional<IChatBaseComponent> optional) {
        return IChatMutableComponent.create(new SelectorContents(s, optional));
    }

    public static class ChatSerializer implements JsonDeserializer<IChatMutableComponent>, JsonSerializer<IChatBaseComponent> {

        private static final Gson GSON = (Gson) SystemUtils.make(() -> {
            GsonBuilder gsonbuilder = new GsonBuilder();

            gsonbuilder.disableHtmlEscaping();
            gsonbuilder.registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer());
            gsonbuilder.registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer());
            gsonbuilder.registerTypeAdapterFactory(new ChatTypeAdapterFactory());
            return gsonbuilder.create();
        });
        private static final Field JSON_READER_POS = (Field) SystemUtils.make(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");

                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException nosuchfieldexception) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
            }
        });
        private static final Field JSON_READER_LINESTART = (Field) SystemUtils.make(() -> {
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
                return IChatBaseComponent.literal(jsonelement.getAsString());
            } else {
                IChatMutableComponent ichatmutablecomponent;

                if (!jsonelement.isJsonObject()) {
                    if (jsonelement.isJsonArray()) {
                        JsonArray jsonarray = jsonelement.getAsJsonArray();

                        ichatmutablecomponent = null;
                        Iterator iterator = jsonarray.iterator();

                        while (iterator.hasNext()) {
                            JsonElement jsonelement1 = (JsonElement) iterator.next();
                            IChatMutableComponent ichatmutablecomponent1 = this.deserialize(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);

                            if (ichatmutablecomponent == null) {
                                ichatmutablecomponent = ichatmutablecomponent1;
                            } else {
                                ichatmutablecomponent.append((IChatBaseComponent) ichatmutablecomponent1);
                            }
                        }

                        return ichatmutablecomponent;
                    } else {
                        throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
                    }
                } else {
                    JsonObject jsonobject = jsonelement.getAsJsonObject();
                    String s;

                    if (jsonobject.has("text")) {
                        s = ChatDeserializer.getAsString(jsonobject, "text");
                        ichatmutablecomponent = s.isEmpty() ? IChatBaseComponent.empty() : IChatBaseComponent.literal(s);
                    } else if (jsonobject.has("translate")) {
                        s = ChatDeserializer.getAsString(jsonobject, "translate");
                        String s1 = ChatDeserializer.getAsString(jsonobject, "fallback", (String) null);

                        if (jsonobject.has("with")) {
                            JsonArray jsonarray1 = ChatDeserializer.getAsJsonArray(jsonobject, "with");
                            Object[] aobject = new Object[jsonarray1.size()];

                            for (int i = 0; i < aobject.length; ++i) {
                                aobject[i] = unwrapTextArgument(this.deserialize(jsonarray1.get(i), type, jsondeserializationcontext));
                            }

                            ichatmutablecomponent = IChatBaseComponent.translatableWithFallback(s, s1, aobject);
                        } else {
                            ichatmutablecomponent = IChatBaseComponent.translatableWithFallback(s, s1);
                        }
                    } else if (jsonobject.has("score")) {
                        JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "score");

                        if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                            throw new JsonParseException("A score component needs a least a name and an objective");
                        }

                        ichatmutablecomponent = IChatBaseComponent.score(ChatDeserializer.getAsString(jsonobject1, "name"), ChatDeserializer.getAsString(jsonobject1, "objective"));
                    } else if (jsonobject.has("selector")) {
                        Optional<IChatBaseComponent> optional = this.parseSeparator(type, jsondeserializationcontext, jsonobject);

                        ichatmutablecomponent = IChatBaseComponent.selector(ChatDeserializer.getAsString(jsonobject, "selector"), optional);
                    } else if (jsonobject.has("keybind")) {
                        ichatmutablecomponent = IChatBaseComponent.keybind(ChatDeserializer.getAsString(jsonobject, "keybind"));
                    } else {
                        if (!jsonobject.has("nbt")) {
                            throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
                        }

                        s = ChatDeserializer.getAsString(jsonobject, "nbt");
                        Optional<IChatBaseComponent> optional1 = this.parseSeparator(type, jsondeserializationcontext, jsonobject);
                        boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "interpret", false);
                        Object object;

                        if (jsonobject.has("block")) {
                            object = new BlockDataSource(ChatDeserializer.getAsString(jsonobject, "block"));
                        } else if (jsonobject.has("entity")) {
                            object = new EntityDataSource(ChatDeserializer.getAsString(jsonobject, "entity"));
                        } else {
                            if (!jsonobject.has("storage")) {
                                throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
                            }

                            object = new StorageDataSource(new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "storage")));
                        }

                        ichatmutablecomponent = IChatBaseComponent.nbt(s, flag, optional1, (DataSource) object);
                    }

                    if (jsonobject.has("extra")) {
                        JsonArray jsonarray2 = ChatDeserializer.getAsJsonArray(jsonobject, "extra");

                        if (jsonarray2.size() <= 0) {
                            throw new JsonParseException("Unexpected empty array of components");
                        }

                        for (int j = 0; j < jsonarray2.size(); ++j) {
                            ichatmutablecomponent.append((IChatBaseComponent) this.deserialize(jsonarray2.get(j), type, jsondeserializationcontext));
                        }
                    }

                    ichatmutablecomponent.setStyle((ChatModifier) jsondeserializationcontext.deserialize(jsonelement, ChatModifier.class));
                    return ichatmutablecomponent;
                }
            }
        }

        private static Object unwrapTextArgument(Object object) {
            if (object instanceof IChatBaseComponent) {
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) object;

                if (ichatbasecomponent.getStyle().isEmpty() && ichatbasecomponent.getSiblings().isEmpty()) {
                    ComponentContents componentcontents = ichatbasecomponent.getContents();

                    if (componentcontents instanceof LiteralContents) {
                        LiteralContents literalcontents = (LiteralContents) componentcontents;

                        return literalcontents.text();
                    }
                }
            }

            return object;
        }

        private Optional<IChatBaseComponent> parseSeparator(Type type, JsonDeserializationContext jsondeserializationcontext, JsonObject jsonobject) {
            return jsonobject.has("separator") ? Optional.of(this.deserialize(jsonobject.get("separator"), type, jsondeserializationcontext)) : Optional.empty();
        }

        private void serializeStyle(ChatModifier chatmodifier, JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
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

            if (!ichatbasecomponent.getStyle().isEmpty()) {
                this.serializeStyle(ichatbasecomponent.getStyle(), jsonobject, jsonserializationcontext);
            }

            if (!ichatbasecomponent.getSiblings().isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = ichatbasecomponent.getSiblings().iterator();

                while (iterator.hasNext()) {
                    IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) iterator.next();

                    jsonarray.add(this.serialize(ichatbasecomponent1, IChatBaseComponent.class, jsonserializationcontext));
                }

                jsonobject.add("extra", jsonarray);
            }

            ComponentContents componentcontents = ichatbasecomponent.getContents();

            if (componentcontents == ComponentContents.EMPTY) {
                jsonobject.addProperty("text", "");
            } else if (componentcontents instanceof LiteralContents) {
                LiteralContents literalcontents = (LiteralContents) componentcontents;

                jsonobject.addProperty("text", literalcontents.text());
            } else if (componentcontents instanceof TranslatableContents) {
                TranslatableContents translatablecontents = (TranslatableContents) componentcontents;

                jsonobject.addProperty("translate", translatablecontents.getKey());
                String s = translatablecontents.getFallback();

                if (s != null) {
                    jsonobject.addProperty("fallback", s);
                }

                if (translatablecontents.getArgs().length > 0) {
                    JsonArray jsonarray1 = new JsonArray();
                    Object[] aobject = translatablecontents.getArgs();
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
            } else if (componentcontents instanceof ScoreContents) {
                ScoreContents scorecontents = (ScoreContents) componentcontents;
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("name", scorecontents.getName());
                jsonobject1.addProperty("objective", scorecontents.getObjective());
                jsonobject.add("score", jsonobject1);
            } else if (componentcontents instanceof SelectorContents) {
                SelectorContents selectorcontents = (SelectorContents) componentcontents;

                jsonobject.addProperty("selector", selectorcontents.getPattern());
                this.serializeSeparator(jsonserializationcontext, jsonobject, selectorcontents.getSeparator());
            } else if (componentcontents instanceof KeybindContents) {
                KeybindContents keybindcontents = (KeybindContents) componentcontents;

                jsonobject.addProperty("keybind", keybindcontents.getName());
            } else {
                if (!(componentcontents instanceof NbtContents)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + componentcontents + " as a Component");
                }

                NbtContents nbtcontents = (NbtContents) componentcontents;

                jsonobject.addProperty("nbt", nbtcontents.getNbtPath());
                jsonobject.addProperty("interpret", nbtcontents.isInterpreting());
                this.serializeSeparator(jsonserializationcontext, jsonobject, nbtcontents.getSeparator());
                DataSource datasource = nbtcontents.getDataSource();

                if (datasource instanceof BlockDataSource) {
                    BlockDataSource blockdatasource = (BlockDataSource) datasource;

                    jsonobject.addProperty("block", blockdatasource.posPattern());
                } else if (datasource instanceof EntityDataSource) {
                    EntityDataSource entitydatasource = (EntityDataSource) datasource;

                    jsonobject.addProperty("entity", entitydatasource.selectorPattern());
                } else {
                    if (!(datasource instanceof StorageDataSource)) {
                        throw new IllegalArgumentException("Don't know how to serialize " + componentcontents + " as a Component");
                    }

                    StorageDataSource storagedatasource = (StorageDataSource) datasource;

                    jsonobject.addProperty("storage", storagedatasource.id().toString());
                }
            }

            return jsonobject;
        }

        private void serializeSeparator(JsonSerializationContext jsonserializationcontext, JsonObject jsonobject, Optional<IChatBaseComponent> optional) {
            optional.ifPresent((ichatbasecomponent) -> {
                jsonobject.add("separator", this.serialize(ichatbasecomponent, ichatbasecomponent.getClass(), jsonserializationcontext));
            });
        }

        public static String toJson(IChatBaseComponent ichatbasecomponent) {
            return IChatBaseComponent.ChatSerializer.GSON.toJson(ichatbasecomponent);
        }

        public static String toStableJson(IChatBaseComponent ichatbasecomponent) {
            return ChatDeserializer.toStableString(toJsonTree(ichatbasecomponent));
        }

        public static JsonElement toJsonTree(IChatBaseComponent ichatbasecomponent) {
            return IChatBaseComponent.ChatSerializer.GSON.toJsonTree(ichatbasecomponent);
        }

        @Nullable
        public static IChatMutableComponent fromJson(String s) {
            return (IChatMutableComponent) ChatDeserializer.fromNullableJson(IChatBaseComponent.ChatSerializer.GSON, s, IChatMutableComponent.class, false);
        }

        @Nullable
        public static IChatMutableComponent fromJson(JsonElement jsonelement) {
            return (IChatMutableComponent) IChatBaseComponent.ChatSerializer.GSON.fromJson(jsonelement, IChatMutableComponent.class);
        }

        @Nullable
        public static IChatMutableComponent fromJsonLenient(String s) {
            return (IChatMutableComponent) ChatDeserializer.fromNullableJson(IChatBaseComponent.ChatSerializer.GSON, s, IChatMutableComponent.class, true);
        }

        public static IChatMutableComponent fromJson(com.mojang.brigadier.StringReader com_mojang_brigadier_stringreader) {
            try {
                JsonReader jsonreader = new JsonReader(new StringReader(com_mojang_brigadier_stringreader.getRemaining()));

                jsonreader.setLenient(false);
                IChatMutableComponent ichatmutablecomponent = (IChatMutableComponent) IChatBaseComponent.ChatSerializer.GSON.getAdapter(IChatMutableComponent.class).read(jsonreader);

                com_mojang_brigadier_stringreader.setCursor(com_mojang_brigadier_stringreader.getCursor() + getPos(jsonreader));
                return ichatmutablecomponent;
            } catch (StackOverflowError | IOException ioexception) {
                throw new JsonParseException(ioexception);
            }
        }

        private static int getPos(JsonReader jsonreader) {
            try {
                return IChatBaseComponent.ChatSerializer.JSON_READER_POS.getInt(jsonreader) - IChatBaseComponent.ChatSerializer.JSON_READER_LINESTART.getInt(jsonreader) + 1;
            } catch (IllegalAccessException illegalaccessexception) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
            }
        }
    }
}
