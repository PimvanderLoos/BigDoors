package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    public CriterionTriggerLocation.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a((JsonElement) jsonobject);

        return new CriterionTriggerLocation.a(this.a, criterionconditionlocation);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerlocation_a) -> {
            return criteriontriggerlocation_a.a(entityplayer.getWorldServer(), entityplayer.locX(), entityplayer.locY(), entityplayer.locZ());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation a;

        public a(MinecraftKey minecraftkey, CriterionConditionLocation criterionconditionlocation) {
            super(minecraftkey);
            this.a = criterionconditionlocation;
        }

        public static CriterionTriggerLocation.a a(CriterionConditionLocation criterionconditionlocation) {
            return new CriterionTriggerLocation.a(CriterionTriggers.p.a, criterionconditionlocation);
        }

        public static CriterionTriggerLocation.a c() {
            return new CriterionTriggerLocation.a(CriterionTriggers.q.a, CriterionConditionLocation.a);
        }

        public static CriterionTriggerLocation.a d() {
            return new CriterionTriggerLocation.a(CriterionTriggers.H.a, CriterionConditionLocation.a);
        }

        public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
            return this.a.a(worldserver, d0, d1, d2);
        }

        @Override
        public JsonElement b() {
            return this.a.a();
        }
    }
}
