package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatHoverable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ChatHoverable.EnumHoverAction<?> b;
    private final Object c;

    public <T> ChatHoverable(ChatHoverable.EnumHoverAction<T> chathoverable_enumhoveraction, T t0) {
        this.b = chathoverable_enumhoveraction;
        this.c = t0;
    }

    public ChatHoverable.EnumHoverAction<?> a() {
        return this.b;
    }

    @Nullable
    public <T> T a(ChatHoverable.EnumHoverAction<T> chathoverable_enumhoveraction) {
        return this.b == chathoverable_enumhoveraction ? chathoverable_enumhoveraction.b(this.c) : null;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            ChatHoverable chathoverable = (ChatHoverable) object;

            return this.b == chathoverable.b && Objects.equals(this.c, chathoverable.c);
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.b + ", value='" + this.c + '\'' + '}';
    }

    public int hashCode() {
        int i = this.b.hashCode();

        i = 31 * i + (this.c != null ? this.c.hashCode() : 0);
        return i;
    }

    @Nullable
    public static ChatHoverable a(JsonObject jsonobject) {
        String s = ChatDeserializer.a(jsonobject, "action", (String) null);

        if (s == null) {
            return null;
        } else {
            ChatHoverable.EnumHoverAction<?> chathoverable_enumhoveraction = ChatHoverable.EnumHoverAction.a(s);

            if (chathoverable_enumhoveraction == null) {
                return null;
            } else {
                JsonElement jsonelement = jsonobject.get("contents");

                if (jsonelement != null) {
                    return chathoverable_enumhoveraction.a(jsonelement);
                } else {
                    IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(jsonobject.get("value"));

                    return ichatmutablecomponent != null ? chathoverable_enumhoveraction.a((IChatBaseComponent) ichatmutablecomponent) : null;
                }
            }
        }
    }

    public JsonObject b() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("action", this.b.b());
        jsonobject.add("contents", this.b.a(this.c));
        return jsonobject;
    }

    public static class EnumHoverAction<T> {

        public static final ChatHoverable.EnumHoverAction<IChatBaseComponent> SHOW_TEXT = new ChatHoverable.EnumHoverAction<>("show_text", true, IChatBaseComponent.ChatSerializer::a, IChatBaseComponent.ChatSerializer::b, Function.identity());
        public static final ChatHoverable.EnumHoverAction<ChatHoverable.c> SHOW_ITEM = new ChatHoverable.EnumHoverAction<>("show_item", true, (jsonelement) -> {
            return ChatHoverable.c.b(jsonelement);
        }, (object) -> {
            return ((ChatHoverable.c) object).b();
        }, (ichatbasecomponent) -> {
            return ChatHoverable.c.b(ichatbasecomponent);
        });
        public static final ChatHoverable.EnumHoverAction<ChatHoverable.b> SHOW_ENTITY = new ChatHoverable.EnumHoverAction<>("show_entity", true, ChatHoverable.b::a, ChatHoverable.b::a, ChatHoverable.b::a);
        private static final Map<String, ChatHoverable.EnumHoverAction> d = (Map) Stream.of(ChatHoverable.EnumHoverAction.SHOW_TEXT, ChatHoverable.EnumHoverAction.SHOW_ITEM, ChatHoverable.EnumHoverAction.SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(ChatHoverable.EnumHoverAction::b, (chathoverable_enumhoveraction) -> {
            return chathoverable_enumhoveraction;
        }));
        private final String e;
        private final boolean f;
        private final Function<JsonElement, T> g;
        private final Function<T, JsonElement> h;
        private final Function<IChatBaseComponent, T> i;

        public EnumHoverAction(String s, boolean flag, Function<JsonElement, T> function, Function<T, JsonElement> function1, Function<IChatBaseComponent, T> function2) {
            this.e = s;
            this.f = flag;
            this.g = function;
            this.h = function1;
            this.i = function2;
        }

        public boolean a() {
            return this.f;
        }

        public String b() {
            return this.e;
        }

        @Nullable
        public static ChatHoverable.EnumHoverAction a(String s) {
            return (ChatHoverable.EnumHoverAction) ChatHoverable.EnumHoverAction.d.get(s);
        }

        private T b(Object object) {
            return object;
        }

        @Nullable
        public ChatHoverable a(JsonElement jsonelement) {
            T t0 = this.g.apply(jsonelement);

            return t0 == null ? null : new ChatHoverable(this, t0);
        }

        @Nullable
        public ChatHoverable a(IChatBaseComponent ichatbasecomponent) {
            T t0 = this.i.apply(ichatbasecomponent);

            return t0 == null ? null : new ChatHoverable(this, t0);
        }

        public JsonElement a(Object object) {
            return (JsonElement) this.h.apply(this.b(object));
        }

        public String toString() {
            return "<action " + this.e + ">";
        }
    }

    public static class c {

        private final Item a;
        private final int b;
        @Nullable
        private final NBTTagCompound c;

        c(Item item, int i, @Nullable NBTTagCompound nbttagcompound) {
            this.a = item;
            this.b = i;
            this.c = nbttagcompound;
        }

        public c(ItemStack itemstack) {
            this(itemstack.getItem(), itemstack.getCount(), itemstack.getTag() != null ? itemstack.getTag().clone() : null);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ChatHoverable.c chathoverable_c = (ChatHoverable.c) object;

                return this.b == chathoverable_c.b && this.a.equals(chathoverable_c.a) && Objects.equals(this.c, chathoverable_c.c);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.a.hashCode();

            i = 31 * i + this.b;
            i = 31 * i + (this.c != null ? this.c.hashCode() : 0);
            return i;
        }

        private static ChatHoverable.c b(JsonElement jsonelement) {
            if (jsonelement.isJsonPrimitive()) {
                return new ChatHoverable.c((Item) IRegistry.ITEM.get(new MinecraftKey(jsonelement.getAsString())), 1, (NBTTagCompound) null);
            } else {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "item");
                Item item = (Item) IRegistry.ITEM.get(new MinecraftKey(ChatDeserializer.h(jsonobject, "id")));
                int i = ChatDeserializer.a(jsonobject, "count", (int) 1);

                if (jsonobject.has("tag")) {
                    String s = ChatDeserializer.h(jsonobject, "tag");

                    try {
                        NBTTagCompound nbttagcompound = MojangsonParser.parse(s);

                        return new ChatHoverable.c(item, i, nbttagcompound);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ChatHoverable.LOGGER.warn("Failed to parse tag: {}", s, commandsyntaxexception);
                    }
                }

                return new ChatHoverable.c(item, i, (NBTTagCompound) null);
            }
        }

        @Nullable
        private static ChatHoverable.c b(IChatBaseComponent ichatbasecomponent) {
            try {
                NBTTagCompound nbttagcompound = MojangsonParser.parse(ichatbasecomponent.getString());

                return new ChatHoverable.c(ItemStack.a(nbttagcompound));
            } catch (CommandSyntaxException commandsyntaxexception) {
                ChatHoverable.LOGGER.warn("Failed to parse item tag: {}", ichatbasecomponent, commandsyntaxexception);
                return null;
            }
        }

        private JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("id", IRegistry.ITEM.getKey(this.a).toString());
            if (this.b != 1) {
                jsonobject.addProperty("count", this.b);
            }

            if (this.c != null) {
                jsonobject.addProperty("tag", this.c.toString());
            }

            return jsonobject;
        }
    }

    public static class b {

        public final EntityTypes<?> a;
        public final UUID b;
        @Nullable
        public final IChatBaseComponent c;

        public b(EntityTypes<?> entitytypes, UUID uuid, @Nullable IChatBaseComponent ichatbasecomponent) {
            this.a = entitytypes;
            this.b = uuid;
            this.c = ichatbasecomponent;
        }

        @Nullable
        public static ChatHoverable.b a(JsonElement jsonelement) {
            if (!jsonelement.isJsonObject()) {
                return null;
            } else {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                EntityTypes<?> entitytypes = (EntityTypes) IRegistry.ENTITY_TYPE.get(new MinecraftKey(ChatDeserializer.h(jsonobject, "type")));
                UUID uuid = UUID.fromString(ChatDeserializer.h(jsonobject, "id"));
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(jsonobject.get("name"));

                return new ChatHoverable.b(entitytypes, uuid, ichatmutablecomponent);
            }
        }

        @Nullable
        public static ChatHoverable.b a(IChatBaseComponent ichatbasecomponent) {
            try {
                NBTTagCompound nbttagcompound = MojangsonParser.parse(ichatbasecomponent.getString());
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("name"));
                EntityTypes<?> entitytypes = (EntityTypes) IRegistry.ENTITY_TYPE.get(new MinecraftKey(nbttagcompound.getString("type")));
                UUID uuid = UUID.fromString(nbttagcompound.getString("id"));

                return new ChatHoverable.b(entitytypes, uuid, ichatmutablecomponent);
            } catch (CommandSyntaxException | JsonSyntaxException jsonsyntaxexception) {
                return null;
            }
        }

        public JsonElement a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("type", IRegistry.ENTITY_TYPE.getKey(this.a).toString());
            jsonobject.addProperty("id", this.b.toString());
            if (this.c != null) {
                jsonobject.add("name", IChatBaseComponent.ChatSerializer.b(this.c));
            }

            return jsonobject;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ChatHoverable.b chathoverable_b = (ChatHoverable.b) object;

                return this.a.equals(chathoverable_b.a) && this.b.equals(chathoverable_b.b) && Objects.equals(this.c, chathoverable_b.c);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.a.hashCode();

            i = 31 * i + this.b.hashCode();
            i = 31 * i + (this.c != null ? this.c.hashCode() : 0);
            return i;
        }
    }
}
