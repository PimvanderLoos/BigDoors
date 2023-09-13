package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerVillagerTrade extends CriterionTriggerAbstract<CriterionTriggerVillagerTrade.a> {

    private static final MinecraftKey a = new MinecraftKey("villager_trade");

    public CriterionTriggerVillagerTrade() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerVillagerTrade.a;
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

        private final CriterionConditionEntity.b a;
        private final CriterionConditionItem b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerVillagerTrade.a, criterionconditionentity_b);
            this.a = criterionconditionentity_b1;
            this.b = criterionconditionitem;
        }

        public static CriterionTriggerVillagerTrade.a c() {
            return new CriterionTriggerVillagerTrade.a(CriterionConditionEntity.b.a, CriterionConditionEntity.b.a, CriterionConditionItem.a);
        }

        public boolean a(LootTableInfo loottableinfo, ItemStack itemstack) {
            return !this.a.a(loottableinfo) ? false : this.b.a(itemstack);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.b.a());
            jsonobject.add("villager", this.a.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
