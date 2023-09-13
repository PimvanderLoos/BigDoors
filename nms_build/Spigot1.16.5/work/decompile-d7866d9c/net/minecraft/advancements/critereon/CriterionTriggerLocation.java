package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;

public class CriterionTriggerLocation extends CriterionTriggerAbstract<CriterionTriggerLocation.a> {

    private final MinecraftKey a;

    public CriterionTriggerLocation(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    @Override
    public MinecraftKey a() {
        return this.a;
    }

    @Override
    public CriterionTriggerLocation.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "location", jsonobject);
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a((JsonElement) jsonobject1);

        return new CriterionTriggerLocation.a(this.a, criterionconditionentity_b, criterionconditionlocation);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer, (criteriontriggerlocation_a) -> {
            return criteriontriggerlocation_a.a(entityplayer.getWorldServer(), entityplayer.locX(), entityplayer.locY(), entityplayer.locZ());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation a;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation) {
            super(minecraftkey, criterionconditionentity_b);
            this.a = criterionconditionlocation;
        }

        public static CriterionTriggerLocation.a a(CriterionConditionLocation criterionconditionlocation) {
            return new CriterionTriggerLocation.a(CriterionTriggers.p.a, CriterionConditionEntity.b.a, criterionconditionlocation);
        }

        public static CriterionTriggerLocation.a c() {
            return new CriterionTriggerLocation.a(CriterionTriggers.q.a, CriterionConditionEntity.b.a, CriterionConditionLocation.a);
        }

        public static CriterionTriggerLocation.a d() {
            return new CriterionTriggerLocation.a(CriterionTriggers.H.a, CriterionConditionEntity.b.a, CriterionConditionLocation.a);
        }

        public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
            return this.a.a(worldserver, d0, d1, d2);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("location", this.a.a());
            return jsonobject;
        }
    }
}
