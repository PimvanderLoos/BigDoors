package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

public class CriterionTriggerImpossible implements CriterionTrigger<CriterionTriggerImpossible.a> {

    private static final MinecraftKey a = new MinecraftKey("impossible");

    public CriterionTriggerImpossible() {}

    public MinecraftKey a() {
        return CriterionTriggerImpossible.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerImpossible.a> criteriontrigger_a) {}

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerImpossible.a> criteriontrigger_a) {}

    public void a(AdvancementDataPlayer advancementdataplayer) {}

    public CriterionTriggerImpossible.a b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return new CriterionTriggerImpossible.a();
    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    public static class a extends CriterionInstanceAbstract {

        public a() {
            super(CriterionTriggerImpossible.a);
        }
    }
}
