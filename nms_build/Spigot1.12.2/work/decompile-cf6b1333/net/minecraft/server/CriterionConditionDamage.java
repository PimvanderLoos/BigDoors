package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionDamage {

    public static CriterionConditionDamage a = new CriterionConditionDamage();
    private final CriterionConditionValue b;
    private final CriterionConditionValue c;
    private final CriterionConditionEntity d;
    private final Boolean e;
    private final CriterionConditionDamageSource f;

    public CriterionConditionDamage() {
        this.b = CriterionConditionValue.a;
        this.c = CriterionConditionValue.a;
        this.d = CriterionConditionEntity.a;
        this.e = null;
        this.f = CriterionConditionDamageSource.a;
    }

    public CriterionConditionDamage(CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1, CriterionConditionEntity criterionconditionentity, @Nullable Boolean obool, CriterionConditionDamageSource criterionconditiondamagesource) {
        this.b = criterionconditionvalue;
        this.c = criterionconditionvalue1;
        this.d = criterionconditionentity;
        this.e = obool;
        this.f = criterionconditiondamagesource;
    }

    public boolean a(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
        return this == CriterionConditionDamage.a ? true : (!this.b.a(f) ? false : (!this.c.a(f1) ? false : (!this.d.a(entityplayer, damagesource.getEntity()) ? false : (this.e != null && this.e.booleanValue() != flag ? false : this.f.a(entityplayer, damagesource)))));
    }

    public static CriterionConditionDamage a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "damage");
            CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("dealt"));
            CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject.get("taken"));
            Boolean obool = jsonobject.has("blocked") ? Boolean.valueOf(ChatDeserializer.j(jsonobject, "blocked")) : null;
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("source_entity"));
            CriterionConditionDamageSource criterionconditiondamagesource = CriterionConditionDamageSource.a(jsonobject.get("type"));

            return new CriterionConditionDamage(criterionconditionvalue, criterionconditionvalue1, criterionconditionentity, obool, criterionconditiondamagesource);
        } else {
            return CriterionConditionDamage.a;
        }
    }
}
