package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.block.entity.TileEntityBeacon;

public class CriterionTriggerConstructBeacon extends CriterionTriggerAbstract<CriterionTriggerConstructBeacon.a> {

    private static final MinecraftKey a = new MinecraftKey("construct_beacon");

    public CriterionTriggerConstructBeacon() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerConstructBeacon.a;
    }

    @Override
    public CriterionTriggerConstructBeacon.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("level"));

        return new CriterionTriggerConstructBeacon.a(criterionconditionentity_b, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, TileEntityBeacon tileentitybeacon) {
        this.a(entityplayer, (criteriontriggerconstructbeacon_a) -> {
            return criteriontriggerconstructbeacon_a.a(tileentitybeacon);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.IntegerRange a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerConstructBeacon.a, criterionconditionentity_b);
            this.a = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerConstructBeacon.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerConstructBeacon.a(CriterionConditionEntity.b.a, criterionconditionvalue_integerrange);
        }

        public boolean a(TileEntityBeacon tileentitybeacon) {
            return this.a.d(tileentitybeacon.h());
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("level", this.a.d());
            return jsonobject;
        }
    }
}
