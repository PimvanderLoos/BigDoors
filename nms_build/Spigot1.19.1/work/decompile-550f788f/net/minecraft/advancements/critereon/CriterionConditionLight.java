package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;

public class CriterionConditionLight {

    public static final CriterionConditionLight ANY = new CriterionConditionLight(CriterionConditionValue.IntegerRange.ANY);
    private final CriterionConditionValue.IntegerRange composite;

    CriterionConditionLight(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        this.composite = criterionconditionvalue_integerrange;
    }

    public boolean matches(WorldServer worldserver, BlockPosition blockposition) {
        return this == CriterionConditionLight.ANY ? true : (!worldserver.isLoaded(blockposition) ? false : this.composite.matches(worldserver.getMaxLocalRawBrightness(blockposition)));
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionLight.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("light", this.composite.serializeToJson());
            return jsonobject;
        }
    }

    public static CriterionConditionLight fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "light");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("light"));

            return new CriterionConditionLight(criterionconditionvalue_integerrange);
        } else {
            return CriterionConditionLight.ANY;
        }
    }

    public static class a {

        private CriterionConditionValue.IntegerRange composite;

        public a() {
            this.composite = CriterionConditionValue.IntegerRange.ANY;
        }

        public static CriterionConditionLight.a light() {
            return new CriterionConditionLight.a();
        }

        public CriterionConditionLight.a setComposite(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.composite = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionLight build() {
            return new CriterionConditionLight(this.composite);
        }
    }
}
