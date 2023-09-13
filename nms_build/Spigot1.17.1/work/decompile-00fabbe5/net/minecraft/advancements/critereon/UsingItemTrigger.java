package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends CriterionTriggerAbstract<UsingItemTrigger.a> {

    static final MinecraftKey ID = new MinecraftKey("using_item");

    public UsingItemTrigger() {}

    @Override
    public MinecraftKey a() {
        return UsingItemTrigger.ID;
    }

    @Override
    public UsingItemTrigger.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new UsingItemTrigger.a(criterionconditionentity_b, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer, (usingitemtrigger_a) -> {
            return usingitemtrigger_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(UsingItemTrigger.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static UsingItemTrigger.a a(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionItem.a criterionconditionitem_a) {
            return new UsingItemTrigger.a(CriterionConditionEntity.b.a(criterionconditionentity_a.b()), criterionconditionitem_a.b());
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
