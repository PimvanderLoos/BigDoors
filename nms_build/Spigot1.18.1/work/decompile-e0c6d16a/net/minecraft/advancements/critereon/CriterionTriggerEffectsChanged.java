package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerEffectsChanged extends CriterionTriggerAbstract<CriterionTriggerEffectsChanged.a> {

    static final MinecraftKey ID = new MinecraftKey("effects_changed");

    public CriterionTriggerEffectsChanged() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerEffectsChanged.ID;
    }

    @Override
    public CriterionTriggerEffectsChanged.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.fromJson(jsonobject.get("effects"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "source", lootdeserializationcontext);

        return new CriterionTriggerEffectsChanged.a(criterionconditionentity_b, criterionconditionmobeffect, criterionconditionentity_b1);
    }

    public void trigger(EntityPlayer entityplayer, @Nullable Entity entity) {
        LootTableInfo loottableinfo = entity != null ? CriterionConditionEntity.createContext(entityplayer, entity) : null;

        this.trigger(entityplayer, (criteriontriggereffectschanged_a) -> {
            return criteriontriggereffectschanged_a.matches(entityplayer, loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionMobEffect effects;
        private final CriterionConditionEntity.b source;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerEffectsChanged.ID, criterionconditionentity_b);
            this.effects = criterionconditionmobeffect;
            this.source = criterionconditionentity_b1;
        }

        public static CriterionTriggerEffectsChanged.a hasEffects(CriterionConditionMobEffect criterionconditionmobeffect) {
            return new CriterionTriggerEffectsChanged.a(CriterionConditionEntity.b.ANY, criterionconditionmobeffect, CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerEffectsChanged.a gotEffectsFrom(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerEffectsChanged.a(CriterionConditionEntity.b.ANY, CriterionConditionMobEffect.ANY, CriterionConditionEntity.b.wrap(criterionconditionentity));
        }

        public boolean matches(EntityPlayer entityplayer, @Nullable LootTableInfo loottableinfo) {
            return !this.effects.matches((EntityLiving) entityplayer) ? false : this.source == CriterionConditionEntity.b.ANY || loottableinfo != null && this.source.matches(loottableinfo);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("effects", this.effects.serializeToJson());
            jsonobject.add("source", this.source.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
