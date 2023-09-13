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

    private static final MinecraftKey a = new MinecraftKey("fishing_rod_hooked");

    public CriterionTriggerFishingRodHooked() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerFishingRodHooked.a;
    }

    @Override
    public CriterionTriggerFishingRodHooked.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("rod"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);
        CriterionConditionItem criterionconditionitem1 = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerFishingRodHooked.a(criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1, criterionconditionitem1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, EntityFishingHook entityfishinghook, Collection<ItemStack> collection) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, (Entity) (entityfishinghook.getHooked() != null ? entityfishinghook.getHooked() : entityfishinghook));

        this.a(entityplayer, (criteriontriggerfishingrodhooked_a) -> {
            return criteriontriggerfishingrodhooked_a.a(itemstack, loottableinfo, collection);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;
        private final CriterionConditionEntity.b b;
        private final CriterionConditionItem c;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionItem criterionconditionitem1) {
            super(CriterionTriggerFishingRodHooked.a, criterionconditionentity_b);
            this.a = criterionconditionitem;
            this.b = criterionconditionentity_b1;
            this.c = criterionconditionitem1;
        }

        public static CriterionTriggerFishingRodHooked.a a(CriterionConditionItem criterionconditionitem, CriterionConditionEntity criterionconditionentity, CriterionConditionItem criterionconditionitem1) {
            return new CriterionTriggerFishingRodHooked.a(CriterionConditionEntity.b.a, criterionconditionitem, CriterionConditionEntity.b.a(criterionconditionentity), criterionconditionitem1);
        }

        public boolean a(ItemStack itemstack, LootTableInfo loottableinfo, Collection<ItemStack> collection) {
            if (!this.a.a(itemstack)) {
                return false;
            } else if (!this.b.a(loottableinfo)) {
                return false;
            } else {
                if (this.c != CriterionConditionItem.a) {
                    boolean flag = false;
                    Entity entity = (Entity) loottableinfo.getContextParameter(LootContextParameters.THIS_ENTITY);

                    if (entity instanceof EntityItem) {
                        EntityItem entityitem = (EntityItem) entity;

                        if (this.c.a(entityitem.getItemStack())) {
                            flag = true;
                        }
                    }

                    Iterator iterator = collection.iterator();

                    while (iterator.hasNext()) {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (this.c.a(itemstack1)) {
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
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("rod", this.a.a());
            jsonobject.add("entity", this.b.a(lootserializationcontext));
            jsonobject.add("item", this.c.a());
            return jsonobject;
        }
    }
}
