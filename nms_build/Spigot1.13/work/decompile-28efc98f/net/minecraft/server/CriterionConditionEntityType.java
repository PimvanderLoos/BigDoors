package net.minecraft.server;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionConditionEntityType {

    public static final CriterionConditionEntityType a = new CriterionConditionEntityType();
    private static final Joiner b = Joiner.on(", ");
    @Nullable
    private final EntityTypes<?> c;

    public CriterionConditionEntityType(EntityTypes<?> entitytypes) {
        this.c = entitytypes;
    }

    private CriterionConditionEntityType() {
        this.c = null;
    }

    public boolean a(EntityTypes<?> entitytypes) {
        return this.c == null || this.c == entitytypes;
    }

    public static CriterionConditionEntityType a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            String s = ChatDeserializer.a(jsonelement, "type");
            MinecraftKey minecraftkey = new MinecraftKey(s);
            EntityTypes entitytypes = (EntityTypes) EntityTypes.REGISTRY.get(minecraftkey);

            if (entitytypes == null) {
                throw new JsonSyntaxException("Unknown entity type \'" + minecraftkey + "\', valid types are: " + CriterionConditionEntityType.b.join(EntityTypes.REGISTRY.keySet()));
            } else {
                return new CriterionConditionEntityType(entitytypes);
            }
        } else {
            return CriterionConditionEntityType.a;
        }
    }

    public JsonElement a() {
        return (JsonElement) (this.c == null ? JsonNull.INSTANCE : new JsonPrimitive(((MinecraftKey) EntityTypes.REGISTRY.b(this.c)).toString()));
    }
}
