package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class PickedUpItemTrigger extends CriterionTriggerAbstract<PickedUpItemTrigger.a> {

    private final MinecraftKey id;

    public PickedUpItemTrigger(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    protected PickedUpItemTrigger.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "entity", lootdeserializationcontext);

        return new PickedUpItemTrigger.a(this.id, criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack, @Nullable Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entity);

        this.trigger(entityplayer, (pickedupitemtrigger_a) -> {
            return pickedupitemtrigger_a.matches(entityplayer, itemstack, loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;
        private final CriterionConditionEntity.b entity;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(minecraftkey, criterionconditionentity_b);
            this.item = criterionconditionitem;
            this.entity = criterionconditionentity_b1;
        }

        public static PickedUpItemTrigger.a thrownItemPickedUpByEntity(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            return new PickedUpItemTrigger.a(CriterionTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.getId(), criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
        }

        public static PickedUpItemTrigger.a thrownItemPickedUpByPlayer(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            return new PickedUpItemTrigger.a(CriterionTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.getId(), criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
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
