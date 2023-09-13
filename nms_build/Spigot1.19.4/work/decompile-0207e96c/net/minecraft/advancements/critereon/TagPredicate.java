package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ChatDeserializer;

public class TagPredicate<T> {

    private final TagKey<T> tag;
    private final boolean expected;

    public TagPredicate(TagKey<T> tagkey, boolean flag) {
        this.tag = tagkey;
        this.expected = flag;
    }

    public static <T> TagPredicate<T> is(TagKey<T> tagkey) {
        return new TagPredicate<>(tagkey, true);
    }

    public static <T> TagPredicate<T> isNot(TagKey<T> tagkey) {
        return new TagPredicate<>(tagkey, false);
    }

    public boolean matches(Holder<T> holder) {
        return holder.is(this.tag) == this.expected;
    }

    public JsonElement serializeToJson() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("id", this.tag.location().toString());
        jsonobject.addProperty("expected", this.expected);
        return jsonobject;
    }

    public static <T> TagPredicate<T> fromJson(@Nullable JsonElement jsonelement, ResourceKey<? extends IRegistry<T>> resourcekey) {
        if (jsonelement == null) {
            throw new JsonParseException("Expected a tag predicate");
        } else {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "Tag Predicate");
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "id"));
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "expected");

            return new TagPredicate<>(TagKey.create(resourcekey, minecraftkey), flag);
        }
    }
}
