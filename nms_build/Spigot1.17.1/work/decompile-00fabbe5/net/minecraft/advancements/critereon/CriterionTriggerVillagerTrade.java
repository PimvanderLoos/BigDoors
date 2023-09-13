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
    public MinecraftKey a() {
        return CriterionTriggerVillagerTrade.ID;
    }

    @Override
    public CriterionTriggerVillagerTrade.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "villager", lootdeserializationcontext);
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerVillagerTrade.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, EntityVillagerAbstract entityvillagerabstract, ItemStack itemstack) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entityvillagerabstract);

        this.a(entityplayer, (criteriontriggervillagertrade_a) -> {
            return criteriontriggervillagertrade_a.a(loottableinfo, itemstack);
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

        public static CriterionTriggerVillagerTrade.a c() {
            return new CriterionTriggerVillagerTrade.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY);
        }

        public boolean a(LootTableInfo loottableinfo, ItemStack itemstack) {
            return !this.villager.a(loottableinfo) ? false : this.item.a(itemstack);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.item.a());
            jsonobject.add("villager", this.villager.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
