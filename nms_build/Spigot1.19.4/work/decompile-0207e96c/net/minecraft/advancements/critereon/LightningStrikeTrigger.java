package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class LightningStrikeTrigger extends CriterionTriggerAbstract<LightningStrikeTrigger.a> {

    static final MinecraftKey ID = new MinecraftKey("lightning_strike");

    public LightningStrikeTrigger() {}

    @Override
    public MinecraftKey getId() {
        return LightningStrikeTrigger.ID;
    }

    @Override
    public LightningStrikeTrigger.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "lightning", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b2 = CriterionConditionEntity.b.fromJson(jsonobject, "bystander", lootdeserializationcontext);

        return new LightningStrikeTrigger.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionentity_b2);
    }

    public void trigger(EntityPlayer entityplayer, EntityLightning entitylightning, List<Entity> list) {
        List<LootTableInfo> list1 = (List) list.stream().map((entity) -> {
            return CriterionConditionEntity.createContext(entityplayer, entity);
        }).collect(Collectors.toList());
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entitylightning);

        this.trigger(entityplayer, (lightningstriketrigger_a) -> {
            return lightningstriketrigger_a.matches(loottableinfo, list1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b lightning;
        private final CriterionConditionEntity.b bystander;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionEntity.b criterionconditionentity_b2) {
            super(LightningStrikeTrigger.ID, criterionconditionentity_b);
            this.lightning = criterionconditionentity_b1;
            this.bystander = criterionconditionentity_b2;
        }

        public static LightningStrikeTrigger.a lighthingStrike(CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1) {
            return new LightningStrikeTrigger.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.wrap(criterionconditionentity), CriterionConditionEntity.b.wrap(criterionconditionentity1));
        }

        public boolean matches(LootTableInfo loottableinfo, List<LootTableInfo> list) {
            if (!this.lightning.matches(loottableinfo)) {
                return false;
            } else {
                if (this.bystander != CriterionConditionEntity.b.ANY) {
                    Stream stream = list.stream();
                    CriterionConditionEntity.b criterionconditionentity_b = this.bystander;

                    Objects.requireNonNull(this.bystander);
                    if (stream.noneMatch(criterionconditionentity_b::matches)) {
                        return false;
                    }
                }

                return true;
            }
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("lightning", this.lightning.toJson(lootserializationcontext));
            jsonobject.add("bystander", this.bystander.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
