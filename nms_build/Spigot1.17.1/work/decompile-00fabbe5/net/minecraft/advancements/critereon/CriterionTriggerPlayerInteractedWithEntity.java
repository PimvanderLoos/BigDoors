package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerPlayerInteractedWithEntity extends CriterionTriggerAbstract<CriterionTriggerPlayerInteractedWithEntity.a> {

    static final MinecraftKey ID = new MinecraftKey("player_interacted_with_entity");

    public CriterionTriggerPlayerInteractedWithEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerPlayerInteractedWithEntity.ID;
    }

    @Override
    protected CriterionTriggerPlayerInteractedWithEntity.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerPlayerInteractedWithEntity.a(criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerplayerinteractedwithentity_a) -> {
            return criteriontriggerplayerinteractedwithentity_a.a(itemstack, loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;
        private final CriterionConditionEntity.b entity;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerPlayerInteractedWithEntity.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
            this.entity = criterionconditionentity_b1;
        }

        public static CriterionTriggerPlayerInteractedWithEntity.a a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem.a criterionconditionitem_a, CriterionConditionEntity.b criterionconditionentity_b1) {
            return new CriterionTriggerPlayerInteractedWithEntity.a(criterionconditionentity_b, criterionconditionitem_a.b(), criterionconditionentity_b1);
        }

        public boolean a(ItemStack itemstack, LootTableInfo loottableinfo) {
            return !this.item.a(itemstack) ? false : this.entity.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.item.a());
            jsonobject.add("entity", this.entity.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
