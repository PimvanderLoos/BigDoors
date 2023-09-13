package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EntityFishingHook;

public class CriterionConditionInOpenWater {

    public static final CriterionConditionInOpenWater ANY = new CriterionConditionInOpenWater(false);
    private static final String IN_OPEN_WATER_KEY = "in_open_water";
    private final boolean inOpenWater;

    private CriterionConditionInOpenWater(boolean flag) {
        this.inOpenWater = flag;
    }

    public static CriterionConditionInOpenWater a(boolean flag) {
        return new CriterionConditionInOpenWater(flag);
    }

    public static CriterionConditionInOpenWater a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "fishing_hook");
            JsonElement jsonelement1 = jsonobject.get("in_open_water");

            return jsonelement1 != null ? new CriterionConditionInOpenWater(ChatDeserializer.c(jsonelement1, "in_open_water")) : CriterionConditionInOpenWater.ANY;
        } else {
            return CriterionConditionInOpenWater.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionInOpenWater.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("in_open_water", new JsonPrimitive(this.inOpenWater));
            return jsonobject;
        }
    }

    public boolean a(Entity entity) {
        if (this == CriterionConditionInOpenWater.ANY) {
            return true;
        } else if (!(entity instanceof EntityFishingHook)) {
            return false;
        } else {
            EntityFishingHook entityfishinghook = (EntityFishingHook) entity;

            return this.inOpenWater == entityfishinghook.isInOpenWater();
        }
    }
}
