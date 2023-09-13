package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.EntityTypes;

public abstract class CriterionConditionEntityType {

    public static final CriterionConditionEntityType ANY = new CriterionConditionEntityType() {
        @Override
        public boolean matches(EntityTypes<?> entitytypes) {
            return true;
        }

        @Override
        public JsonElement serializeToJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on(", ");

    public CriterionConditionEntityType() {}

    public abstract boolean matches(EntityTypes<?> entitytypes);

    public abstract JsonElement serializeToJson();

    public static CriterionConditionEntityType fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            String s = ChatDeserializer.convertToString(jsonelement, "type");
            MinecraftKey minecraftkey;

            if (s.startsWith("#")) {
                minecraftkey = new MinecraftKey(s.substring(1));
                return new CriterionConditionEntityType.a(TagKey.create(Registries.ENTITY_TYPE, minecraftkey));
            } else {
                minecraftkey = new MinecraftKey(s);
                EntityTypes<?> entitytypes = (EntityTypes) BuiltInRegistries.ENTITY_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown entity type '" + minecraftkey + "', valid types are: " + CriterionConditionEntityType.COMMA_JOINER.join(BuiltInRegistries.ENTITY_TYPE.keySet()));
                });

                return new CriterionConditionEntityType.b(entitytypes);
            }
        } else {
            return CriterionConditionEntityType.ANY;
        }
    }

    public static CriterionConditionEntityType of(EntityTypes<?> entitytypes) {
        return new CriterionConditionEntityType.b(entitytypes);
    }

    public static CriterionConditionEntityType of(TagKey<EntityTypes<?>> tagkey) {
        return new CriterionConditionEntityType.a(tagkey);
    }

    private static class a extends CriterionConditionEntityType {

        private final TagKey<EntityTypes<?>> tag;

        public a(TagKey<EntityTypes<?>> tagkey) {
            this.tag = tagkey;
        }

        @Override
        public boolean matches(EntityTypes<?> entitytypes) {
            return entitytypes.is(this.tag);
        }

        @Override
        public JsonElement serializeToJson() {
            return new JsonPrimitive("#" + this.tag.location());
        }
    }

    private static class b extends CriterionConditionEntityType {

        private final EntityTypes<?> type;

        public b(EntityTypes<?> entitytypes) {
            this.type = entitytypes;
        }

        @Override
        public boolean matches(EntityTypes<?> entitytypes) {
            return this.type == entitytypes;
        }

        @Override
        public JsonElement serializeToJson() {
            return new JsonPrimitive(BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
        }
    }
}
