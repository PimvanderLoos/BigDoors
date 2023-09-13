package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionDamageSource {

    public static CriterionConditionDamageSource a = new CriterionConditionDamageSource();
    private final Boolean b;
    private final Boolean c;
    private final Boolean d;
    private final Boolean e;
    private final Boolean f;
    private final Boolean g;
    private final Boolean h;
    private final CriterionConditionEntity i;
    private final CriterionConditionEntity j;

    public CriterionConditionDamageSource() {
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = null;
        this.f = null;
        this.g = null;
        this.h = null;
        this.i = CriterionConditionEntity.a;
        this.j = CriterionConditionEntity.a;
    }

    public CriterionConditionDamageSource(@Nullable Boolean obool, @Nullable Boolean obool1, @Nullable Boolean obool2, @Nullable Boolean obool3, @Nullable Boolean obool4, @Nullable Boolean obool5, @Nullable Boolean obool6, CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1) {
        this.b = obool;
        this.c = obool1;
        this.d = obool2;
        this.e = obool3;
        this.f = obool4;
        this.g = obool5;
        this.h = obool6;
        this.i = criterionconditionentity;
        this.j = criterionconditionentity1;
    }

    public boolean a(EntityPlayer entityplayer, DamageSource damagesource) {
        return this == CriterionConditionDamageSource.a ? true : (this.b != null && this.b.booleanValue() != damagesource.a() ? false : (this.c != null && this.c.booleanValue() != damagesource.isExplosion() ? false : (this.d != null && this.d.booleanValue() != damagesource.ignoresArmor() ? false : (this.e != null && this.e.booleanValue() != damagesource.ignoresInvulnerability() ? false : (this.f != null && this.f.booleanValue() != damagesource.isStarvation() ? false : (this.g != null && this.g.booleanValue() != damagesource.o() ? false : (this.h != null && this.h.booleanValue() != damagesource.isMagic() ? false : (!this.i.a(entityplayer, damagesource.i()) ? false : this.j.a(entityplayer, damagesource.getEntity())))))))));
    }

    public static CriterionConditionDamageSource a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "damage type");
            Boolean obool = a(jsonobject, "is_projectile");
            Boolean obool1 = a(jsonobject, "is_explosion");
            Boolean obool2 = a(jsonobject, "bypasses_armor");
            Boolean obool3 = a(jsonobject, "bypasses_invulnerability");
            Boolean obool4 = a(jsonobject, "bypasses_magic");
            Boolean obool5 = a(jsonobject, "is_fire");
            Boolean obool6 = a(jsonobject, "is_magic");
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("direct_entity"));
            CriterionConditionEntity criterionconditionentity1 = CriterionConditionEntity.a(jsonobject.get("source_entity"));

            return new CriterionConditionDamageSource(obool, obool1, obool2, obool3, obool4, obool5, obool6, criterionconditionentity, criterionconditionentity1);
        } else {
            return CriterionConditionDamageSource.a;
        }
    }

    @Nullable
    private static Boolean a(JsonObject jsonobject, String s) {
        return jsonobject.has(s) ? Boolean.valueOf(ChatDeserializer.j(jsonobject, s)) : null;
    }
}
