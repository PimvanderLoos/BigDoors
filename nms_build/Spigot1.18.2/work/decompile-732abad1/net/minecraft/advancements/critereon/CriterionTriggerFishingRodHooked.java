package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class CriterionTriggerFishingRodHooked extends CriterionTriggerAbstract<CriterionTriggerFishingRodHooked.a> {

    static final MinecraftKey ID = new MinecraftKey("fishing_rod_hooked");

    public CriterionTriggerFishingRodHooked() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerFishingRodHooked.ID;
    }

    @Override
    public CriterionTriggerFishingRodHooked.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("rod"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "entity", lootdeserializationcontext);
        CriterionConditionItem criterionconditionitem1 = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerFishingRodHooked.a(criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1, criterionconditionitem1);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack, EntityFishingHook entityfishinghook, Collection<ItemStack> collection) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, (Entity) (entityfishinghook.getHookedIn() != null ? entityfishinghook.getHookedIn() : entityfishinghook));

        this.trigger(entityplayer, (criteriontriggerfishingrodhooked_a) -> {
            return criteriontriggerfishingrodhooked_a.matches(itemstack, loottableinfo, collection);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem rod;
        private final CriterionConditionEntity.b entity;
        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionItem criterionconditionitem1) {
            super(CriterionTriggerFishingRodHooked.ID, criterionconditionentity_b);
            this.rod = criterionconditionitem;
            this.entity = criterionconditionentity_b1;
            this.item = criterionconditionitem1;
        }

        public static CriterionTriggerFishingRodHooked.a fishedItem(CriterionConditionItem criterionconditionitem, CriterionConditionEntity criterionconditionentity, CriterionConditionItem criterionconditionitem1) {
            return new CriterionTriggerFishingRodHooked.a(CriterionConditionEntity.b.ANY, criterionconditionitem, CriterionConditionEntity.b.wrap(criterionconditionentity), criterionconditionitem1);
        }

        public boolean matches(ItemStack itemstack, LootTableInfo loottableinfo, Collection<ItemStack> collection) {
            if (!this.rod.matches(itemstack)) {
                return false;
            } else if (!this.entity.matches(loottableinfo)) {
                return false;
            } else {
                if (this.item != CriterionConditionItem.ANY) {
                    boolean flag = false;
                    Entity entity = (Entity) loottableinfo.getParamOrNull(LootContextParameters.THIS_ENTITY);

                    if (entity instanceof EntityItem) {
                        EntityItem entityitem = (EntityItem) entity;

                        if (this.item.matches(entityitem.getItem())) {
                            flag = true;
                        }
                    }

                    Iterator iterator = collection.iterator();

                    while (iterator.hasNext()) {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (this.item.matches(itemstack1)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }

                return true;
            }
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("rod", this.rod.serializeToJson());
            jsonobject.add("entity", this.entity.toJson(lootserializationcontext));
            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }
    }
}
