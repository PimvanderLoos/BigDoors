package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.damagesource.DamageSource;

public class CriterionConditionDamage {

    public static final CriterionConditionDamage ANY = CriterionConditionDamage.a.damageInstance().build();
    private final CriterionConditionValue.DoubleRange dealtDamage;
    private final CriterionConditionValue.DoubleRange takenDamage;
    private final CriterionConditionEntity sourceEntity;
    @Nullable
    private final Boolean blocked;
    private final CriterionConditionDamageSource type;

    public CriterionConditionDamage() {
        this.dealtDamage = CriterionConditionValue.DoubleRange.ANY;
        this.takenDamage = CriterionConditionValue.DoubleRange.ANY;
        this.sourceEntity = CriterionConditionEntity.ANY;
        this.blocked = null;
        this.type = CriterionConditionDamageSource.ANY;
    }

    public CriterionConditionDamage(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1, CriterionConditionEntity criterionconditionentity, @Nullable Boolean obool, CriterionConditionDamageSource criterionconditiondamagesource) {
        this.dealtDamage = criterionconditionvalue_doublerange;
        this.takenDamage = criterionconditionvalue_doublerange1;
        this.sourceEntity = criterionconditionentity;
        this.blocked = obool;
        this.type = criterionconditiondamagesource;
    }

    public boolean matches(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
        return this == CriterionConditionDamage.ANY ? true : (!this.dealtDamage.matches((double) f) ? false : (!this.takenDamage.matches((double) f1) ? false : (!this.sourceEntity.matches(entityplayer, damagesource.getEntity()) ? false : (this.blocked != null && this.blocked != flag ? false : this.type.matches(entityplayer, damagesource)))));
    }

    public static CriterionConditionDamage fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "damage");
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("dealt"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1 = CriterionConditionValue.DoubleRange.fromJson(jsonobject.get("taken"));
            Boolean obool = jsonobject.has("blocked") ? ChatDeserializer.getAsBoolean(jsonobject, "blocked") : null;
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.fromJson(jsonobject.get("source_entity"));
            CriterionConditionDamageSource criterionconditiondamagesource = CriterionConditionDamageSource.fromJson(jsonobject.get("type"));

            return new CriterionConditionDamage(criterionconditionvalue_doublerange, criterionconditionvalue_doublerange1, criterionconditionentity, obool, criterionconditiondamagesource);
        } else {
            return CriterionConditionDamage.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionDamage.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("dealt", this.dealtDamage.serializeToJson());
            jsonobject.add("taken", this.takenDamage.serializeToJson());
            jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
            jsonobject.add("type", this.type.serializeToJson());
            if (this.blocked != null) {
                jsonobject.addProperty("blocked", this.blocked);
            }

            return jsonobject;
        }
    }

    public static class a {

        private CriterionConditionValue.DoubleRange dealtDamage;
        private CriterionConditionValue.DoubleRange takenDamage;
        private CriterionConditionEntity sourceEntity;
        @Nullable
        private Boolean blocked;
        private CriterionConditionDamageSource type;

        public a() {
            this.dealtDamage = CriterionConditionValue.DoubleRange.ANY;
            this.takenDamage = CriterionConditionValue.DoubleRange.ANY;
            this.sourceEntity = CriterionConditionEntity.ANY;
            this.type = CriterionConditionDamageSource.ANY;
        }

        public static CriterionConditionDamage.a damageInstance() {
            return new CriterionConditionDamage.a();
        }

        public CriterionConditionDamage.a dealtDamage(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.dealtDamage = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionDamage.a takenDamage(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.takenDamage = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionDamage.a sourceEntity(CriterionConditionEntity criterionconditionentity) {
            this.sourceEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamage.a blocked(Boolean obool) {
            this.blocked = obool;
            return this;
        }

        public CriterionConditionDamage.a type(CriterionConditionDamageSource criterionconditiondamagesource) {
            this.type = criterionconditiondamagesource;
            return this;
        }

        public CriterionConditionDamage.a type(CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            this.type = criterionconditiondamagesource_a.build();
            return this;
        }

        public CriterionConditionDamage build() {
            return new CriterionConditionDamage(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
        }
    }
}
