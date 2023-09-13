package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerEnchantedItem extends CriterionTriggerAbstract<CriterionTriggerEnchantedItem.a> {

    private static final MinecraftKey a = new MinecraftKey("enchanted_item");

    public CriterionTriggerEnchantedItem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEnchantedItem.a;
    }

    @Override
    public CriterionTriggerEnchantedItem.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("levels"));

        return new CriterionTriggerEnchantedItem.a(criterionconditionitem, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerenchanteditem_a) -> {
            return criteriontriggerenchanteditem_a.a(itemstack, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;
        private final CriterionConditionValue.IntegerRange b;

        public a(CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerEnchantedItem.a);
            this.a = criterionconditionitem;
            this.b = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerEnchantedItem.a c() {
            return new CriterionTriggerEnchantedItem.a(CriterionConditionItem.a, CriterionConditionValue.IntegerRange.e);
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.a.a(itemstack) ? false : this.b.d(i);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("item", this.a.a());
            jsonobject.add("levels", this.b.d());
            return jsonobject;
        }
    }
}
