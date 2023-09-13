package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.damagesource.DamageSource;

public class CriterionConditionDamage {

    public static final CriterionConditionDamage ANY = CriterionConditionDamage.a.a().b();
    private final CriterionConditionValue.DoubleRange dealtDamage;
    private final CriterionConditionValue.DoubleRange takenDamage;
    private final CriterionConditionEntity sourceEntity;
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

    public boolean a(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
        return this == CriterionConditionDamage.ANY ? true : (!this.dealtDamage.d((double) f) ? false : (!this.takenDamage.d((double) f1) ? false : (!this.sourceEntity.a(entityplayer, damagesource.getEntity()) ? false : (this.blocked != null && this.blocked != flag ? false : this.type.a(entityplayer, damagesource)))));
    }

    public static CriterionConditionDamage a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "damage");
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange = CriterionConditionValue.DoubleRange.a(jsonobject.get("dealt"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1 = CriterionConditionValue.DoubleRange.a(jsonobject.get("taken"));
            Boolean obool = jsonobject.has("blocked") ? ChatDeserializer.j(jsonobject, "blocked") : null;
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("source_entity"));
            CriterionConditionDamageSource criterionconditiondamagesource = CriterionConditionDamageSource.a(jsonobject.get("type"));

            return new CriterionConditionDamage(criterionconditionvalue_doublerange, criterionconditionvalue_doublerange1, criterionconditionentity, obool, criterionconditiondamagesource);
        } else {
            return CriterionConditionDamage.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionDamage.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("dealt", this.dealtDamage.d());
            jsonobject.add("taken", this.takenDamage.d());
            jsonobject.add("source_entity", this.sourceEntity.a());
            jsonobject.add("type", this.type.a());
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
        private Boolean blocked;
        private CriterionConditionDamageSource type;

        public a() {
            this.dealtDamage = CriterionConditionValue.DoubleRange.ANY;
            this.takenDamage = CriterionConditionValue.DoubleRange.ANY;
            this.sourceEntity = CriterionConditionEntity.ANY;
            this.type = CriterionConditionDamageSource.ANY;
        }

        public static CriterionConditionDamage.a a() {
            return new CriterionConditionDamage.a();
        }

        public CriterionConditionDamage.a a(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.dealtDamage = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionDamage.a b(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.takenDamage = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionDamage.a a(CriterionConditionEntity criterionconditionentity) {
            this.sourceEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamage.a a(Boolean obool) {
            this.blocked = obool;
            return this;
        }

        public CriterionConditionDamage.a a(CriterionConditionDamageSource criterionconditiondamagesource) {
            this.type = criterionconditiondamagesource;
            return this;
        }

        public CriterionConditionDamage.a a(CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            this.type = criterionconditiondamagesource_a.b();
            return this;
        }

        public CriterionConditionDamage b() {
            return new CriterionConditionDamage(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
        }
    }
}
