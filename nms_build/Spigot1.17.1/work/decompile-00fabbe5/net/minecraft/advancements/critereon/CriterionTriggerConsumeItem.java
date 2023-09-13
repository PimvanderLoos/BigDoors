package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.level.IMaterial;

public class CriterionTriggerConsumeItem extends CriterionTriggerAbstract<CriterionTriggerConsumeItem.a> {

    static final MinecraftKey ID = new MinecraftKey("consume_item");

    public CriterionTriggerConsumeItem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerConsumeItem.ID;
    }

    @Override
    public CriterionTriggerConsumeItem.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerConsumeItem.a(criterionconditionentity_b, CriterionConditionItem.a(jsonobject.get("item")));
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer, (criteriontriggerconsumeitem_a) -> {
            return criteriontriggerconsumeitem_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerConsumeItem.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerConsumeItem.a c() {
            return new CriterionTriggerConsumeItem.a(CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY);
        }

        public static CriterionTriggerConsumeItem.a a(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerConsumeItem.a(CriterionConditionEntity.b.ANY, criterionconditionitem);
        }

        public static CriterionTriggerConsumeItem.a a(IMaterial imaterial) {
            return new CriterionTriggerConsumeItem.a(CriterionConditionEntity.b.ANY, new CriterionConditionItem((Tag) null, ImmutableSet.of(imaterial.getItem()), CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionEnchantments.NONE, CriterionConditionEnchantments.NONE, (PotionRegistry) null, CriterionConditionNBT.ANY));
        }

        public boolean a(ItemStack itemstack) {
            return this.item.a(itemstack);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.item.a());
            return jsonobject;
        }
    }
}
