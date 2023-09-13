package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerThrownItemPickedUpByEntity extends CriterionTriggerAbstract<CriterionTriggerThrownItemPickedUpByEntity.a> {

    private static final MinecraftKey a = new MinecraftKey("thrown_item_picked_up_by_entity");

    public CriterionTriggerThrownItemPickedUpByEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerThrownItemPickedUpByEntity.a;
    }

    @Override
    protected CriterionTriggerThrownItemPickedUpByEntity.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerThrownItemPickedUpByEntity.a(criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerthrownitempickedupbyentity_a) -> {
            return criteriontriggerthrownitempickedupbyentity_a.a(entityplayer, itemstack, loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;
        private final CriterionConditionEntity.b b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerThrownItemPickedUpByEntity.a, criterionconditionentity_b);
            this.a = criterionconditionitem;
            this.b = criterionconditionentity_b1;
        }

        public static CriterionTriggerThrownItemPickedUpByEntity.a a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem.a criterionconditionitem_a, CriterionConditionEntity.b criterionconditionentity_b1) {
            return new CriterionTriggerThrownItemPickedUpByEntity.a(criterionconditionentity_b, criterionconditionitem_a.b(), criterionconditionentity_b1);
        }

        public boolean a(EntityPlayer entityplayer, ItemStack itemstack, LootTableInfo loottableinfo) {
            return !this.a.a(itemstack) ? false : this.b.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.a.a());
            jsonobject.add("entity", this.b.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
