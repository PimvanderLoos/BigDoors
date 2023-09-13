package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerPlayerGeneratesContainerLoot extends CriterionTriggerAbstract<CriterionTriggerPlayerGeneratesContainerLoot.a> {

    private static final MinecraftKey a = new MinecraftKey("player_generates_container_loot");

    public CriterionTriggerPlayerGeneratesContainerLoot() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerPlayerGeneratesContainerLoot.a;
    }

    @Override
    protected CriterionTriggerPlayerGeneratesContainerLoot.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "loot_table"));

        return new CriterionTriggerPlayerGeneratesContainerLoot.a(criterionconditionentity_b, minecraftkey);
    }

    public void a(EntityPlayer entityplayer, MinecraftKey minecraftkey) {
        this.a(entityplayer, (criteriontriggerplayergeneratescontainerloot_a) -> {
            return criteriontriggerplayergeneratescontainerloot_a.b(minecraftkey);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final MinecraftKey a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, MinecraftKey minecraftkey) {
            super(CriterionTriggerPlayerGeneratesContainerLoot.a, criterionconditionentity_b);
            this.a = minecraftkey;
        }

        public static CriterionTriggerPlayerGeneratesContainerLoot.a a(MinecraftKey minecraftkey) {
            return new CriterionTriggerPlayerGeneratesContainerLoot.a(CriterionConditionEntity.b.a, minecraftkey);
        }

        public boolean b(MinecraftKey minecraftkey) {
            return this.a.equals(minecraftkey);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.addProperty("loot_table", this.a.toString());
            return jsonobject;
        }
    }
}
