package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ITagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.EntityTypes;

public abstract class CriterionConditionEntityType {

    public static final CriterionConditionEntityType ANY = new CriterionConditionEntityType() {
        @Override
        public boolean a(EntityTypes<?> entitytypes) {
            return true;
        }

        @Override
        public JsonElement a() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on(", ");

    public CriterionConditionEntityType() {}

    public abstract boolean a(EntityTypes<?> entitytypes);

    public abstract JsonElement a();

    public static CriterionConditionEntityType a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            String s = ChatDeserializer.a(jsonelement, "type");
            MinecraftKey minecraftkey;

            if (s.startsWith("#")) {
                minecraftkey = new MinecraftKey(s.substring(1));
                return new CriterionConditionEntityType.a(TagsInstance.a().a(IRegistry.ENTITY_TYPE_REGISTRY, minecraftkey, (minecraftkey1) -> {
                    return new JsonSyntaxException("Unknown entity tag '" + minecraftkey1 + "'");
                }));
            } else {
                minecraftkey = new MinecraftKey(s);
                EntityTypes<?> entitytypes = (EntityTypes) IRegistry.ENTITY_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown entity type '" + minecraftkey + "', valid types are: " + CriterionConditionEntityType.COMMA_JOINER.join(IRegistry.ENTITY_TYPE.keySet()));
                });

                return new CriterionConditionEntityType.b(entitytypes);
            }
        } else {
            return CriterionConditionEntityType.ANY;
        }
    }

    public static CriterionConditionEntityType b(EntityTypes<?> entitytypes) {
        return new CriterionConditionEntityType.b(entitytypes);
    }

    public static CriterionConditionEntityType a(Tag<EntityTypes<?>> tag) {
        return new CriterionConditionEntityType.a(tag);
    }

    private static class a extends CriterionConditionEntityType {

        private final Tag<EntityTypes<?>> tag;

        public a(Tag<EntityTypes<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean a(EntityTypes<?> entitytypes) {
            return entitytypes.a(this.tag);
        }

        @Override
        public JsonElement a() {
            ITagRegistry itagregistry = TagsInstance.a();
            ResourceKey resourcekey = IRegistry.ENTITY_TYPE_REGISTRY;
            Tag tag = this.tag;

            return new JsonPrimitive("#" + itagregistry.a(resourcekey, tag, () -> {
                return new IllegalStateException("Unknown entity type tag");
            }));
        }
    }

    private static class b extends CriterionConditionEntityType {

        private final EntityTypes<?> type;

        public b(EntityTypes<?> entitytypes) {
            this.type = entitytypes;
        }

        @Override
        public boolean a(EntityTypes<?> entitytypes) {
            return this.type == entitytypes;
        }

        @Override
        public JsonElement a() {
            return new JsonPrimitive(IRegistry.ENTITY_TYPE.getKey(this.type).toString());
        }
    }
}
