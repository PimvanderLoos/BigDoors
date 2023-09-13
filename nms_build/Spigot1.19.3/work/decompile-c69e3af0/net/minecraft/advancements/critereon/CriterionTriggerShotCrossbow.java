package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public class CriterionTriggerShotCrossbow extends CriterionTriggerAbstract<CriterionTriggerShotCrossbow.a> {

    static final MinecraftKey ID = new MinecraftKey("shot_crossbow");

    public CriterionTriggerShotCrossbow() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerShotCrossbow.ID;
    }

    @Override
    public CriterionTriggerShotCrossbow.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerShotCrossbow.a(criterionconditionentity_b, criterionconditionitem);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack) {
        this.trigger(entityplayer, (criteriontriggershotcrossbow_a) -> {
            return criteriontriggershotcrossbow_a.matches(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerShotCrossbow.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerShotCrossbow.a shotCrossbow(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerShotCrossbow.a(CriterionConditionEntity.b.ANY, criterionconditionitem);
        }

        public static CriterionTriggerShotCrossbow.a shotCrossbow(IMaterial imaterial) {
            return new CriterionTriggerShotCrossbow.a(CriterionConditionEntity.b.ANY, CriterionConditionItem.a.item().of(imaterial).build());
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
