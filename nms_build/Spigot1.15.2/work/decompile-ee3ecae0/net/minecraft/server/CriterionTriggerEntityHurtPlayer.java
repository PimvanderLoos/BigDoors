package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerEntityHurtPlayer extends CriterionTriggerAbstract<CriterionTriggerEntityHurtPlayer.a> {

    private static final MinecraftKey a = new MinecraftKey("entity_hurt_player");

    public CriterionTriggerEntityHurtPlayer() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEntityHurtPlayer.a;
    }

    @Override
    public CriterionTriggerEntityHurtPlayer.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionDamage criterionconditiondamage = CriterionConditionDamage.a(jsonobject.get("damage"));

        return new CriterionTriggerEntityHurtPlayer.a(criterionconditiondamage);
    }

    public void a(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerentityhurtplayer_a) -> {
            return criteriontriggerentityhurtplayer_a.a(entityplayer, damagesource, f, f1, flag);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDamage a;

        public a(CriterionConditionDamage criterionconditiondamage) {
            super(CriterionTriggerEntityHurtPlayer.a);
            this.a = criterionconditiondamage;
        }

        public static CriterionTriggerEntityHurtPlayer.a a(CriterionConditionDamage.a criterionconditiondamage_a) {
            return new CriterionTriggerEntityHurtPlayer.a(criterionconditiondamage_a.b());
        }

        public boolean a(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
            return this.a.a(entityplayer, damagesource, f, f1, flag);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("damage", this.a.a());
            return jsonobject;
        }
    }
}
