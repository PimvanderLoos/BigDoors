package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerEnchantedItem extends CriterionTriggerAbstract<CriterionTriggerEnchantedItem.a> {

    static final MinecraftKey ID = new MinecraftKey("enchanted_item");

    public CriterionTriggerEnchantedItem() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerEnchantedItem.ID;
    }

    @Override
    public CriterionTriggerEnchantedItem.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("levels"));

        return new CriterionTriggerEnchantedItem.a(criterionconditionentity_b, criterionconditionitem, criterionconditionvalue_integerrange);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        this.trigger(entityplayer, (criteriontriggerenchanteditem_a) -> {
            return criteriontriggerenchanteditem_a.matches(itemstack, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;
        private final CriterionConditionValue.IntegerRange levels;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerEnchantedItem.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
            this.levels = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerEnchantedItem.a enchantedItem() {
            return new CriterionTriggerEnchantedItem.a(CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY, CriterionConditionValue.IntegerRange.ANY);
        }

        public boolean matches(ItemStack itemstack, int i) {
            return !this.item.matches(itemstack) ? false : this.levels.matches(i);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("levels", this.levels.serializeToJson());
            return jsonobject;
        }
    }
}
