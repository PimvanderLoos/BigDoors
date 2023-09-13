package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerEnchantedItem extends CriterionTriggerAbstract<CriterionTriggerEnchantedItem.a> {

    static final MinecraftKey ID = new MinecraftKey("enchanted_item");

    public CriterionTriggerEnchantedItem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEnchantedItem.ID;
    }

    @Override
    public CriterionTriggerEnchantedItem.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("levels"));

        return new CriterionTriggerEnchantedItem.a(criterionconditionentity_b, criterionconditionitem, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        this.a(entityplayer, (criteriontriggerenchanteditem_a) -> {
            return criteriontriggerenchanteditem_a.a(itemstack, i);
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

        public static CriterionTriggerEnchantedItem.a c() {
            return new CriterionTriggerEnchantedItem.a(CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY, CriterionConditionValue.IntegerRange.ANY);
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.item.a(itemstack) ? false : this.levels.d(i);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.item.a());
            jsonobject.add("levels", this.levels.d());
            return jsonobject;
        }
    }
}
