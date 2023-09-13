package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityLiving;

public class CriterionTriggerEffectsChanged extends CriterionTriggerAbstract<CriterionTriggerEffectsChanged.a> {

    private static final MinecraftKey a = new MinecraftKey("effects_changed");

    public CriterionTriggerEffectsChanged() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEffectsChanged.a;
    }

    @Override
    public CriterionTriggerEffectsChanged.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));

        return new CriterionTriggerEffectsChanged.a(criterionconditionentity_b, criterionconditionmobeffect);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer, (criteriontriggereffectschanged_a) -> {
            return criteriontriggereffectschanged_a.a(entityplayer);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionMobEffect a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionMobEffect criterionconditionmobeffect) {
            super(CriterionTriggerEffectsChanged.a, criterionconditionentity_b);
            this.a = criterionconditionmobeffect;
        }

        public static CriterionTriggerEffectsChanged.a a(CriterionConditionMobEffect criterionconditionmobeffect) {
            return new CriterionTriggerEffectsChanged.a(CriterionConditionEntity.b.a, criterionconditionmobeffect);
        }

        public boolean a(EntityPlayer entityplayer) {
            return this.a.a((EntityLiving) entityplayer);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("effects", this.a.b());
            return jsonobject;
        }
    }
}
