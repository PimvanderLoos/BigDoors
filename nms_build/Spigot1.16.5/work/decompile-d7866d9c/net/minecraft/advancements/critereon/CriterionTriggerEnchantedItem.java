package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerEnchantedItem extends CriterionTriggerAbstract<CriterionTriggerEnchantedItem.a> {

    private static final MinecraftKey a = new MinecraftKey("enchanted_item");

    public CriterionTriggerEnchantedItem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEnchantedItem.a;
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

        private final CriterionConditionItem a;
        private final CriterionConditionValue.IntegerRange b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerEnchantedItem.a, criterionconditionentity_b);
            this.a = criterionconditionitem;
            this.b = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerEnchantedItem.a c() {
            return new CriterionTriggerEnchantedItem.a(CriterionConditionEntity.b.a, CriterionConditionItem.a, CriterionConditionValue.IntegerRange.e);
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.a.a(itemstack) ? false : this.b.d(i);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.a.a());
            jsonobject.add("levels", this.b.d());
            return jsonobject;
        }
    }
}
