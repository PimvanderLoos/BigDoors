package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerItemDurabilityChanged extends CriterionTriggerAbstract<CriterionTriggerItemDurabilityChanged.a> {

    static final MinecraftKey ID = new MinecraftKey("item_durability_changed");

    public CriterionTriggerItemDurabilityChanged() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerItemDurabilityChanged.ID;
    }

    @Override
    public CriterionTriggerItemDurabilityChanged.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("durability"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("delta"));

        return new CriterionTriggerItemDurabilityChanged.a(criterionconditionentity_b, criterionconditionitem, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        this.trigger(entityplayer, (criteriontriggeritemdurabilitychanged_a) -> {
            return criteriontriggeritemdurabilitychanged_a.matches(itemstack, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;
        private final CriterionConditionValue.IntegerRange durability;
        private final CriterionConditionValue.IntegerRange delta;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1) {
            super(CriterionTriggerItemDurabilityChanged.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
            this.durability = criterionconditionvalue_integerrange;
            this.delta = criterionconditionvalue_integerrange1;
        }

        public static CriterionTriggerItemDurabilityChanged.a changedDurability(CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return changedDurability(CriterionConditionEntity.b.ANY, criterionconditionitem, criterionconditionvalue_integerrange);
        }

        public static CriterionTriggerItemDurabilityChanged.a changedDurability(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerItemDurabilityChanged.a(criterionconditionentity_b, criterionconditionitem, criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange.ANY);
        }

        public boolean matches(ItemStack itemstack, int i) {
            return !this.item.matches(itemstack) ? false : (!this.durability.matches(itemstack.getMaxDamage() - i) ? false : this.delta.matches(itemstack.getDamageValue() - i));
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("durability", this.durability.serializeToJson());
            jsonobject.add("delta", this.delta.serializeToJson());
            return jsonobject;
        }
    }
}
