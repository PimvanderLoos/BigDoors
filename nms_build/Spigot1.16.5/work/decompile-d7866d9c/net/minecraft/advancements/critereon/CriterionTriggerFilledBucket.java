package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerFilledBucket extends CriterionTriggerAbstract<CriterionTriggerFilledBucket.a> {

    private static final MinecraftKey a = new MinecraftKey("filled_bucket");

    public CriterionTriggerFilledBucket() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerFilledBucket.a;
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

        private final CriterionConditionItem a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerFilledBucket.a, criterionconditionentity_b);
            this.a = criterionconditionitem;
        }

        public static CriterionTriggerFilledBucket.a a(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerFilledBucket.a(CriterionConditionEntity.b.a, criterionconditionitem);
        }

        public boolean a(ItemStack itemstack) {
            return this.a.a(itemstack);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.a.a());
            return jsonobject;
        }
    }
}
