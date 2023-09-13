package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends CriterionTriggerAbstract<UsingItemTrigger.a> {

    static final MinecraftKey ID = new MinecraftKey("using_item");

    public UsingItemTrigger() {}

    @Override
    public MinecraftKey getId() {
        return UsingItemTrigger.ID;
    }

    @Override
    public UsingItemTrigger.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new UsingItemTrigger.a(criterionconditionentity_b, criterionconditionitem);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack) {
        this.trigger(entityplayer, (usingitemtrigger_a) -> {
            return usingitemtrigger_a.matches(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(UsingItemTrigger.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static UsingItemTrigger.a lookingAt(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionItem.a criterionconditionitem_a) {
            return new UsingItemTrigger.a(CriterionConditionEntity.b.wrap(criterionconditionentity_a.build()), criterionconditionitem_a.build());
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
