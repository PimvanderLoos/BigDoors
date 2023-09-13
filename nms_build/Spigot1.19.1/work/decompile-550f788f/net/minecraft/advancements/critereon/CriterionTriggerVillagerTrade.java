package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerVillagerTrade extends CriterionTriggerAbstract<CriterionTriggerVillagerTrade.a> {

    static final MinecraftKey ID = new MinecraftKey("villager_trade");

    public CriterionTriggerVillagerTrade() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerVillagerTrade.ID;
    }

    @Override
    public CriterionTriggerVillagerTrade.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "villager", lootdeserializationcontext);
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerVillagerTrade.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionitem);
    }

    public void trigger(EntityPlayer entityplayer, EntityVillagerAbstract entityvillagerabstract, ItemStack itemstack) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entityvillagerabstract);

        this.trigger(entityplayer, (criteriontriggervillagertrade_a) -> {
            return criteriontriggervillagertrade_a.matches(loottableinfo, itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b villager;
        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerVillagerTrade.ID, criterionconditionentity_b);
            this.villager = criterionconditionentity_b1;
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerVillagerTrade.a tradedWithVillager() {
            return new CriterionTriggerVillagerTrade.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY);
        }

        public static CriterionTriggerVillagerTrade.a tradedWithVillager(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerVillagerTrade.a(CriterionConditionEntity.b.wrap(criterionconditionentity_a.build()), CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY);
        }

        public boolean matches(LootTableInfo loottableinfo, ItemStack itemstack) {
            return !this.villager.matches(loottableinfo) ? false : this.item.matches(itemstack);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("villager", this.villager.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
