package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerConsumeItem extends CriterionTriggerAbstract<CriterionTriggerConsumeItem.a> {

    private static final MinecraftKey a = new MinecraftKey("consume_item");

    public CriterionTriggerConsumeItem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerConsumeItem.a;
    }

    @Override
    public CriterionTriggerConsumeItem.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return new CriterionTriggerConsumeItem.a(CriterionConditionItem.a(jsonobject.get("item")));
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerconsumeitem_a) -> {
            return criteriontriggerconsumeitem_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;

        public a(CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerConsumeItem.a);
            this.a = criterionconditionitem;
        }

        public static CriterionTriggerConsumeItem.a c() {
            return new CriterionTriggerConsumeItem.a(CriterionConditionItem.a);
        }

        public static CriterionTriggerConsumeItem.a a(IMaterial imaterial) {
            return new CriterionTriggerConsumeItem.a(new CriterionConditionItem((Tag) null, imaterial.getItem(), CriterionConditionValue.IntegerRange.e, CriterionConditionValue.IntegerRange.e, CriterionConditionEnchantments.b, CriterionConditionEnchantments.b, (PotionRegistry) null, CriterionConditionNBT.a));
        }

        public boolean a(ItemStack itemstack) {
            return this.a.a(itemstack);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("item", this.a.a());
            return jsonobject;
        }
    }
}
