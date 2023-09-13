package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerThrownItemPickedUpByEntity extends CriterionTriggerAbstract<CriterionTriggerThrownItemPickedUpByEntity.a> {

    static final MinecraftKey ID = new MinecraftKey("thrown_item_picked_up_by_entity");

    public CriterionTriggerThrownItemPickedUpByEntity() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerThrownItemPickedUpByEntity.ID;
    }

    @Override
    protected CriterionTriggerThrownItemPickedUpByEntity.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerThrownItemPickedUpByEntity.a(criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack, Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entity);

        this.trigger(entityplayer, (criteriontriggerthrownitempickedupbyentity_a) -> {
            return criteriontriggerthrownitempickedupbyentity_a.matches(entityplayer, itemstack, loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;
        private final CriterionConditionEntity.b entity;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerThrownItemPickedUpByEntity.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
            this.entity = criterionconditionentity_b1;
        }

        public static CriterionTriggerThrownItemPickedUpByEntity.a itemPickedUpByEntity(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem.a criterionconditionitem_a, CriterionConditionEntity.b criterionconditionentity_b1) {
            return new CriterionTriggerThrownItemPickedUpByEntity.a(criterionconditionentity_b, criterionconditionitem_a.build(), criterionconditionentity_b1);
        }

        public boolean matches(EntityPlayer entityplayer, ItemStack itemstack, LootTableInfo loottableinfo) {
            return !this.item.matches(itemstack) ? false : this.entity.matches(loottableinfo);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("entity", this.entity.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
