package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class ChatHoverable {

    static final Logger LOGGER = LogUtils.getLogger();
    private final ChatHoverable.EnumHoverAction<?> action;
    private final Object value;

    public <T> ChatHoverable(ChatHoverable.EnumHoverAction<T> chathoverable_enumhoveraction, T t0) {
        this.action = chathoverable_enumhoveraction;
        this.value = t0;
    }

    public ChatHoverable.EnumHoverAction<?> getAction() {
        return this.action;
    }

    @Nullable
    public <T> T getValue(ChatHoverable.EnumHoverAction<T> chathoverable_enumhoveraction) {
        return this.action == chathoverable_enumhoveraction ? chathoverable_enumhoveraction.cast(this.value) : null;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            ChatHoverable chathoverable = (ChatHoverable) object;

            return this.action == chathoverable.action && Objects.equals(this.value, chathoverable.value);
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int i = this.action.hashCode();

        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    @Nullable
    public static ChatHoverable deserialize(JsonObject jsonobject) {
        String s = ChatDeserializer.getAsString(jsonobject, "action", (String) null);

        if (s == null) {
            return null;
        } else {
            ChatHoverable.EnumHoverAction<?> chathoverable_enumhoveraction = ChatHoverable.EnumHoverAction.getByName(s);

            if (chathoverable_enumhoveraction == null) {
                return null;
            } else {
                JsonElement jsonelement = jsonobject.get("contents");

                if (jsonelement != null) {
                    return chathoverable_enumhoveraction.deserialize(jsonelement);
                } else {
                    IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(jsonobject.get("value"));

                    return ichatmutablecomponent != null ? chathoverable_enumhoveraction.deserializeFromLegacy(ichatmutablecomponent) : null;
                }
            }
        }
    }

    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("action", this.action.getName());
        jsonobject.add("contents", this.action.serializeArg(this.value));
        return jsonobject;
    }

    public static class EnumHoverAction<T> {

        public static final ChatHoverable.EnumHoverAction<IChatBaseComponent> SHOW_TEXT = new ChatHoverable.EnumHoverAction<>("show_text", true, IChatBaseComponent.ChatSerializer::fromJson, IChatBaseComponent.ChatSerializer::toJsonTree, Function.identity());
        public static final ChatHoverable.EnumHoverAction<ChatHoverable.c> SHOW_ITEM = new ChatHoverable.EnumHoverAction<>("show_item", true, ChatHoverable.c::create, ChatHoverable.c::serialize, ChatHoverable.c::create);
        public static final ChatHoverable.EnumHoverAction<ChatHoverable.b> SHOW_ENTITY = new ChatHoverable.EnumHoverAction<>("show_entity", true, ChatHoverable.b::create, ChatHoverable.b::serialize, ChatHoverable.b::create);
        private static final Map<String, ChatHoverable.EnumHoverAction<?>> LOOKUP = (Map) Stream.of(ChatHoverable.EnumHoverAction.SHOW_TEXT, ChatHoverable.EnumHoverAction.SHOW_ITEM, ChatHoverable.EnumHoverAction.SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(ChatHoverable.EnumHoverAction::getName, (chathoverable_enumhoveraction) -> {
            return chathoverable_enumhoveraction;
        }));
        private final String name;
        private final boolean allowFromServer;
        private final Function<JsonElement, T> argDeserializer;
        private final Function<T, JsonElement> argSerializer;
        private final Function<IChatBaseComponent, T> legacyArgDeserializer;

        public EnumHoverAction(String s, boolean flag, Function<JsonElement, T> function, Function<T, JsonElement> function1, Function<IChatBaseComponent, T> function2) {
            this.name = s;
            this.allowFromServer = flag;
            this.argDeserializer = function;
            this.argSerializer = function1;
            this.legacyArgDeserializer = function2;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static ChatHoverable.EnumHoverAction<?> getByName(String s) {
            return (ChatHoverable.EnumHoverAction) ChatHoverable.EnumHoverAction.LOOKUP.get(s);
        }

        T cast(Object object) {
            return object;
        }

        @Nullable
        public ChatHoverable deserialize(JsonElement jsonelement) {
            T t0 = this.argDeserializer.apply(jsonelement);

            return t0 == null ? null : new ChatHoverable(this, t0);
        }

        @Nullable
        public ChatHoverable deserializeFromLegacy(IChatBaseComponent ichatbasecomponent) {
            T t0 = this.legacyArgDeserializer.apply(ichatbasecomponent);

            return t0 == null ? null : new ChatHoverable(this, t0);
        }

        public JsonElement serializeArg(Object object) {
            return (JsonElement) this.argSerializer.apply(this.cast(object));
        }

        public String toString() {
            return "<action " + this.name + ">";
        }
    }

    public static class c {

        private final Item item;
        private final int count;
        @Nullable
        private final NBTTagCompound tag;
        @Nullable
        private ItemStack itemStack;

        c(Item item, int i, @Nullable NBTTagCompound nbttagcompound) {
            this.item = item;
            this.count = i;
            this.tag = nbttagcompound;
        }

        public c(ItemStack itemstack) {
            this(itemstack.getItem(), itemstack.getCount(), itemstack.getTag() != null ? itemstack.getTag().copy() : null);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ChatHoverable.c chathoverable_c = (ChatHoverable.c) object;

                return this.count == chathoverable_c.count && this.item.equals(chathoverable_c.item) && Objects.equals(this.tag, chathoverable_c.tag);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.item.hashCode();

            i = 31 * i + this.count;
            i = 31 * i + (this.tag != null ? this.tag.hashCode() : 0);
            return i;
        }

        public ItemStack getItemStack() {
            if (this.itemStack == null) {
                this.itemStack = new ItemStack(this.item, this.count);
                if (this.tag != null) {
                    this.itemStack.setTag(this.tag);
                }
            }

            return this.itemStack;
        }

        private static ChatHoverable.c create(JsonElement jsonelement) {
            if (jsonelement.isJsonPrimitive()) {
                return new ChatHoverable.c((Item) BuiltInRegistries.ITEM.get(new MinecraftKey(jsonelement.getAsString())), 1, (NBTTagCompound) null);
            } else {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "item");
                Item item = (Item) BuiltInRegistries.ITEM.get(new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "id")));
                int i = ChatDeserializer.getAsInt(jsonobject, "count", 1);

                if (jsonobject.has("tag")) {
                    String s = ChatDeserializer.getAsString(jsonobject, "tag");

                    try {
                        NBTTagCompound nbttagcompound = MojangsonParser.parseTag(s);

                        return new ChatHoverable.c(item, i, nbttagcompound);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ChatHoverable.LOGGER.warn("Failed to parse tag: {}", s, commandsyntaxexception);
                    }
                }

                return new ChatHoverable.c(item, i, (NBTTagCompound) null);
            }
        }

        @Nullable
        private static ChatHoverable.c create(IChatBaseComponent ichatbasecomponent) {
            try {
                NBTTagCompound nbttagcompound = MojangsonParser.parseTag(ichatbasecomponent.getString());

                return new ChatHoverable.c(ItemStack.of(nbttagcompound));
            } catch (CommandSyntaxException commandsyntaxexception) {
                ChatHoverable.LOGGER.warn("Failed to parse item tag: {}", ichatbasecomponent, commandsyntaxexception);
                return null;
            }
        }

        private JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("id", BuiltInRegistries.ITEM.getKey(this.item).toString());
            if (this.count != 1) {
                jsonobject.addProperty("count", this.count);
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", this.tag.toString());
            }

            return jsonobject;
        }
    }

    public static class b {

        public final EntityTypes<?> type;
        public final UUID id;
        @Nullable
        public final IChatBaseComponent name;
        @Nullable
        private List<IChatBaseComponent> linesCache;

        public b(EntityTypes<?> entitytypes, UUID uuid, @Nullable IChatBaseComponent ichatbasecomponent) {
            this.type = entitytypes;
            this.id = uuid;
            this.name = ichatbasecomponent;
        }

        @Nullable
        public static ChatHoverable.b create(JsonElement jsonelement) {
            if (!jsonelement.isJsonObject()) {
                return null;
            } else {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                EntityTypes<?> entitytypes = (EntityTypes) BuiltInRegistries.ENTITY_TYPE.get(new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "type")));
                UUID uuid = UUID.fromString(ChatDeserializer.getAsString(jsonobject, "id"));
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(jsonobject.get("name"));

                return new ChatHoverable.b(entitytypes, uuid, ichatmutablecomponent);
            }
        }

        @Nullable
        public static ChatHoverable.b create(IChatBaseComponent ichatbasecomponent) {
            try {
                NBTTagCompound nbttagcompound = MojangsonParser.parseTag(ichatbasecomponent.getString());
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("name"));
                EntityTypes<?> entitytypes = (EntityTypes) BuiltInRegistries.ENTITY_TYPE.get(new MinecraftKey(nbttagcompound.getString("type")));
                UUID uuid = UUID.fromString(nbttagcompound.getString("id"));

                return new ChatHoverable.b(entitytypes, uuid, ichatmutablecomponent);
            } catch (Exception exception) {
                return null;
            }
        }

        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("type", BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
            jsonobject.addProperty("id", this.id.toString());
            if (this.name != null) {
                jsonobject.add("name", IChatBaseComponent.ChatSerializer.toJsonTree(this.name));
            }

            return jsonobject;
        }

        public List<IChatBaseComponent> getTooltipLines() {
            if (this.linesCache == null) {
                this.linesCache = Lists.newArrayList();
                if (this.name != null) {
                    this.linesCache.add(this.name);
                }

                this.linesCache.add(IChatBaseComponent.translatable("gui.entity_tooltip.type", this.type.getDescription()));
                this.linesCache.add(IChatBaseComponent.literal(this.id.toString()));
            }

            return this.linesCache;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ChatHoverable.b chathoverable_b = (ChatHoverable.b) object;

                return this.type.equals(chathoverable_b.type) && this.id.equals(chathoverable_b.id) && Objects.equals(this.name, chathoverable_b.name);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.type.hashCode();

            i = 31 * i + this.id.hashCode();
            i = 31 * i + (this.name != null ? this.name.hashCode() : 0);
            return i;
        }
    }
}
