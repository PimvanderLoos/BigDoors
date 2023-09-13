package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerEffectsChanged extends CriterionTriggerAbstract<CriterionTriggerEffectsChanged.a> {

    private static final MinecraftKey a = new MinecraftKey("effects_changed");

    public CriterionTriggerEffectsChanged() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEffectsChanged.a;
    }

    @Override
    public CriterionTriggerEffectsChanged.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));

        return new CriterionTriggerEffectsChanged.a(criterionconditionmobeffect);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggereffectschanged_a) -> {
            return criteriontriggereffectschanged_a.a(entityplayer);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionMobEffect a;

        public a(CriterionConditionMobEffect criterionconditionmobeffect) {
            super(CriterionTriggerEffectsChanged.a);
            this.a = criterionconditionmobeffect;
        }

        public static CriterionTriggerEffectsChanged.a a(CriterionConditionMobEffect criterionconditionmobeffect) {
            return new CriterionTriggerEffectsChanged.a(criterionconditionmobeffect);
        }

        public boolean a(EntityPlayer entityplayer) {
            return this.a.a((EntityLiving) entityplayer);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("effects", this.a.b());
            return jsonobject;
        }
    }
}
