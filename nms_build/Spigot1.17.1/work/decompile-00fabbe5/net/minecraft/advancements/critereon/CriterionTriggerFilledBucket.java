package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerFilledBucket extends CriterionTriggerAbstract<CriterionTriggerFilledBucket.a> {

    static final MinecraftKey ID = new MinecraftKey("filled_bucket");

    public CriterionTriggerFilledBucket() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerFilledBucket.ID;
    }

    @Override
    public CriterionTriggerFilledBucket.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerFilledBucket.a(criterionconditionentity_b, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer, (criteriontriggerfilledbucket_a) -> {
            return criteriontriggerfilledbucket_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerFilledBucket.ID, criterionconditionentity_b);
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerFilledBucket.a a(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerFilledBucket.a(CriterionConditionEntity.b.ANY, criterionconditionitem);
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
