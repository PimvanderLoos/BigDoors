package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerImpossible implements CriterionTrigger<CriterionTriggerImpossible.a> {

    private static final MinecraftKey a = new MinecraftKey("impossible");

    public CriterionTriggerImpossible() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerImpossible.a;
    }

    @Override
    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerImpossible.a> criteriontrigger_a) {}

    @Override
    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerImpossible.a> criteriontrigger_a) {}

    @Override
    public void a(AdvancementDataPlayer advancementdataplayer) {}

    @Override
    public CriterionTriggerImpossible.a a(JsonObject jsonobject, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerImpossible.a();
    }

    public static class a implements CriterionInstance {

        public a() {}

        @Override
        public MinecraftKey a() {
            return CriterionTriggerImpossible.a;
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            return new JsonObject();
        }
    }
}
