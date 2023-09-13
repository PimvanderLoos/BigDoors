package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerItemDurabilityChanged extends CriterionTriggerAbstract<CriterionTriggerItemDurabilityChanged.a> {

    static final MinecraftKey ID = new MinecraftKey("item_durability_changed");

    public CriterionTriggerItemDurabilityChanged() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerItemDurabilityChanged.ID;
    }

    @Override
    public CriterionTriggerItemDurabilityChanged.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("durability"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject.get("delta"));

        return new CriterionTriggerItemDurabilityChanged.a(criterionconditionentity_b, criterionconditionitem, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        this.a(entityplayer, (criteriontriggeritemdurabilitychanged_a) -> {
            return criteriontriggeritemdurabilitychanged_a.a(itemstack, i);
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

        public static CriterionTriggerItemDurabilityChanged.a a(CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return a(CriterionConditionEntity.b.ANY, criterionconditionitem, criterionconditionvalue_integerrange);
        }

        public static CriterionTriggerItemDurabilityChanged.a a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerItemDurabilityChanged.a(criterionconditionentity_b, criterionconditionitem, criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange.ANY);
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.item.a(itemstack) ? false : (!this.durability.d(itemstack.i() - i) ? false : this.delta.d(itemstack.getDamage() - i));
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.item.a());
            jsonobject.add("durability", this.durability.d());
            jsonobject.add("delta", this.delta.d());
            return jsonobject;
        }
    }
}
