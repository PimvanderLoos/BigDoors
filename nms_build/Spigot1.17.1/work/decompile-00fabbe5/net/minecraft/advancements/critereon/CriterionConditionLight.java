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

    public boolean a(WorldServer worldserver, BlockPosition blockposition) {
        return this == CriterionConditionLight.ANY ? true : (!worldserver.o(blockposition) ? false : this.composite.d(worldserver.getLightLevel(blockposition)));
    }

    public JsonElement a() {
        if (this == CriterionConditionLight.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("light", this.composite.d());
            return jsonobject;
        }
    }

    public static CriterionConditionLight a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "light");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("light"));

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

        public static CriterionConditionLight.a a() {
            return new CriterionConditionLight.a();
        }

        public CriterionConditionLight.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.composite = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionLight b() {
            return new CriterionConditionLight(this.composite);
        }
    }
}
