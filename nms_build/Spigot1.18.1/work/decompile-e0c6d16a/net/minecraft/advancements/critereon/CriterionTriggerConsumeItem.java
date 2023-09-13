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
    public MinecraftKey getId() {
        return CriterionTriggerConsumeItem.ID;
    }

    @Override
    public CriterionTriggerConsumeItem.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerConsumeItem.a(criterionconditionentity_b, CriterionConditionItem.fromJson(jsonobject.get("item")));
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack) {
        this.trigger(entityplayer, (criteriontriggerconsumeitem_a) -> {
            return criteriontriggerconsumeitem_a.matches(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerConsumeItem.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerConsumeItem.a usedItem() {
            return new CriterionTriggerConsumeItem.a(CriterionConditionEntity.b.ANY, CriterionConditionItem.ANY);
        }

        public static CriterionTriggerConsumeItem.a usedItem(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerConsumeItem.a(CriterionConditionEntity.b.ANY, criterionconditionitem);
        }

        public static CriterionTriggerConsumeItem.a usedItem(IMaterial imaterial) {
            return new CriterionTriggerConsumeItem.a(CriterionConditionEntity.b.ANY, new CriterionConditionItem((Tag) null, ImmutableSet.of(imaterial.asItem()), CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionEnchantments.NONE, CriterionConditionEnchantments.NONE, (PotionRegistry) null, CriterionConditionNBT.ANY));
        }

        public boolean matches(ItemStack itemstack) {
            return this.item.matches(itemstack);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }
    }
}
