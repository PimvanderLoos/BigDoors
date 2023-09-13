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
    public MinecraftKey a() {
        return LightningStrikeTrigger.ID;
    }

    @Override
    public LightningStrikeTrigger.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "lightning", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b2 = CriterionConditionEntity.b.a(jsonobject, "bystander", lootdeserializationcontext);

        return new LightningStrikeTrigger.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionentity_b2);
    }

    public void a(EntityPlayer entityplayer, EntityLightning entitylightning, List<Entity> list) {
        List<LootTableInfo> list1 = (List) list.stream().map((entity) -> {
            return CriterionConditionEntity.b(entityplayer, entity);
        }).collect(Collectors.toList());
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entitylightning);

        this.a(entityplayer, (lightningstriketrigger_a) -> {
            return lightningstriketrigger_a.a(loottableinfo, list1);
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

        public static LightningStrikeTrigger.a a(CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1) {
            return new LightningStrikeTrigger.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), CriterionConditionEntity.b.a(criterionconditionentity1));
        }

        public boolean a(LootTableInfo loottableinfo, List<LootTableInfo> list) {
            if (!this.lightning.a(loottableinfo)) {
                return false;
            } else {
                if (this.bystander != CriterionConditionEntity.b.ANY) {
                    Stream stream = list.stream();
                    CriterionConditionEntity.b criterionconditionentity_b = this.bystander;

                    Objects.requireNonNull(this.bystander);
                    if (stream.noneMatch(criterionconditionentity_b::a)) {
                        return false;
                    }
                }

                return true;
            }
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("lightning", this.lightning.a(lootserializationcontext));
            jsonobject.add("bystander", this.bystander.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
