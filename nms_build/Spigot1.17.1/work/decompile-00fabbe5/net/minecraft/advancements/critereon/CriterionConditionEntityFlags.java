package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class CriterionConditionEntityFlags {

    public static final CriterionConditionEntityFlags ANY = (new CriterionConditionEntityFlags.a()).b();
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

    public boolean a(Entity entity) {
        return this.isOnFire != null && entity.isBurning() != this.isOnFire ? false : (this.isCrouching != null && entity.isCrouching() != this.isCrouching ? false : (this.isSprinting != null && entity.isSprinting() != this.isSprinting ? false : (this.isSwimming != null && entity.isSwimming() != this.isSwimming ? false : this.isBaby == null || !(entity instanceof EntityLiving) || ((EntityLiving) entity).isBaby() == this.isBaby)));
    }

    @Nullable
    private static Boolean a(JsonObject jsonobject, String s) {
        return jsonobject.has(s) ? ChatDeserializer.j(jsonobject, s) : null;
    }

    public static CriterionConditionEntityFlags a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "entity flags");
            Boolean obool = a(jsonobject, "is_on_fire");
            Boolean obool1 = a(jsonobject, "is_sneaking");
            Boolean obool2 = a(jsonobject, "is_sprinting");
            Boolean obool3 = a(jsonobject, "is_swimming");
            Boolean obool4 = a(jsonobject, "is_baby");

            return new CriterionConditionEntityFlags(obool, obool1, obool2, obool3, obool4);
        } else {
            return CriterionConditionEntityFlags.ANY;
        }
    }

    private void a(JsonObject jsonobject, String s, @Nullable Boolean obool) {
        if (obool != null) {
            jsonobject.addProperty(s, obool);
        }

    }

    public JsonElement a() {
        if (this == CriterionConditionEntityFlags.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            this.a(jsonobject, "is_on_fire", this.isOnFire);
            this.a(jsonobject, "is_sneaking", this.isCrouching);
            this.a(jsonobject, "is_sprinting", this.isSprinting);
            this.a(jsonobject, "is_swimming", this.isSwimming);
            this.a(jsonobject, "is_baby", this.isBaby);
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

        public static CriterionConditionEntityFlags.a a() {
            return new CriterionConditionEntityFlags.a();
        }

        public CriterionConditionEntityFlags.a a(@Nullable Boolean obool) {
            this.isOnFire = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a b(@Nullable Boolean obool) {
            this.isCrouching = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a c(@Nullable Boolean obool) {
            this.isSprinting = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a d(@Nullable Boolean obool) {
            this.isSwimming = obool;
            return this;
        }

        public CriterionConditionEntityFlags.a e(@Nullable Boolean obool) {
            this.isBaby = obool;
            return this;
        }

        public CriterionConditionEntityFlags b() {
            return new CriterionConditionEntityFlags(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}
