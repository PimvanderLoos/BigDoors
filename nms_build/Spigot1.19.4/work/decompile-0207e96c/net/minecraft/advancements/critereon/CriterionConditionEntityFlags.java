package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class CriterionConditionEntityFlags {

    public static final CriterionConditionEntityFlags ANY = (new CriterionConditionEntityFlags.a()).build();
    @Nullable
    private final Boolean isOnFire;
    @Nullable
    private final Boolean isCrouching;
    @Nullable
    private final Boolean isSprinting;
    @Nullable
    private final Boolean isSwimming;
    @Nullable
    private final Boolean isBaby;

    public CriterionConditionEntityFlags(@Nullable Boolean obool, @Nullable Boolean obool1, @Nullable Boolean obool2, @Nullable Boolean obool3, @Nullable Boolean obool4) {
        this.isOnFire = obool;
        this.isCrouching = obool1;
        this.isSprinting = obool2;
        this.isSwimming = obool3;
        this.isBaby = obool4;
    }

    public boolean matches(Entity entity) {
        return this.isOnFire != null && entity.isOnFire() != this.isOnFire ? false : (this.isCrouching != null && entity.isCrouching() != this.isCrouching ? false : (this.isSprinting != null && entity.isSprinting() != this.isSprinting ? false : (this.isSwimming != null && entity.isSwimming() != this.isSwimming ? false : this.isBaby == null || !(entity instanceof EntityLiving) || ((EntityLiving) entity).isBaby() == this.isBaby)));
    }

    @Nullable
    private static Boolean getOptionalBoolean(JsonObject jsonobject, String s) {
        return jsonobject.has(s) ? ChatDeserializer.getAsBoolean(jsonobject, s) : null;
    }

    public static CriterionConditionEntityFlags fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "entity flags");
            Boolean obool = getOptionalBoolean(jsonobject, "is_on_fire");
            Boolean obool1 = getOptionalBoolean(jsonobject, "is_sneaking");
            Boolean obool2 = getOptionalBoolean(jsonobject, "is_sprinting");
            Boolean obool3 = getOptionalBoolean(jsonobject, "is_swimming");
            Boolean obool4 = getOptionalBoolean(jsonobject, "is_baby");

            return new CriterionConditionEntityFlags(obool, obool1, obool2, obool3, obool4);
        } else {
            return CriterionConditionEntityFlags.ANY;
        }
    }

    private void addOptionalBoolean(JsonObject jsonobject, String s, @Nullable Boolean obool) {
        if (obool != null) {
            jsonobject.addProperty(s, obool);
        }

    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionEntityFlags.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            this.addOptionalBoolean(jsonobject, "is_on_fire", this.isOnFire);
            this.addOptionalBoolean(jsonobject, "is_sneaking", this.isCrouching);
            this.addOptionalBoolean(jsonobject, "is_sprinting", this.isSprinting);
            this.addOptionalBoolean(jsonobject, "is_swimming", this.isSwimming);
            this.addOptionalBoolean(jsonobject, "is_baby", this.isBaby);
            return jsonobject;
        }
    }

    public static class a {

        @Nullable
        private Boolean isOnFire;
        @Nullable
        private Boolean isCrouching;
        @Nullable
        private Boolean isSprinting;
        @Nullable
        private Boolean isSwimming;
        @Nullable
        private Boolean isBaby;

        public a() {}

        public static CriterionConditionEntityFlags.a flags() {
            return new CriterionConditionEntityFlags.a();
        }

        public CriterionConditionEntityFlags.a setOnFire(@Nullable Boolean obool) {
            this.isOnFire = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a setCrouching(@Nullable Boolean obool) {
            this.isCrouching = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a setSprinting(@Nullable Boolean obool) {
            this.isSprinting = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a setSwimming(@Nullable Boolean obool) {
            this.isSwimming = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a setIsBaby(@Nullable Boolean obool) {
            this.isBaby = obool;
            return this;
        }

        public CriterionConditionEntityFlags build() {
            return new CriterionConditionEntityFlags(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}
