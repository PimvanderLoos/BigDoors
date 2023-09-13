package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public class CriterionTriggerUsedTotem extends CriterionTriggerAbstract<CriterionTriggerUsedTotem.a> {

    static final MinecraftKey ID = new MinecraftKey("used_totem");

    public CriterionTriggerUsedTotem() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerUsedTotem.ID;
    }

    @Override
    public CriterionTriggerUsedTotem.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerUsedTotem.a(criterionconditionentity_b, criterionconditionitem);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack) {
        this.trigger(entityplayer, (criteriontriggerusedtotem_a) -> {
            return criteriontriggerusedtotem_a.matches(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerUsedTotem.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerUsedTotem.a usedTotem(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerUsedTotem.a(CriterionConditionEntity.b.ANY, criterionconditionitem);
        }

        public static CriterionTriggerUsedTotem.a usedTotem(IMaterial imaterial) {
            return new CriterionTriggerUsedTotem.a(CriterionConditionEntity.b.ANY, CriterionConditionItem.a.item().of(imaterial).build());
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
