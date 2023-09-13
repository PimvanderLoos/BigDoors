package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerFilledBucket extends CriterionTriggerAbstract<CriterionTriggerFilledBucket.a> {

    private static final MinecraftKey a = new MinecraftKey("filled_bucket");

    public CriterionTriggerFilledBucket() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerFilledBucket.a;
    }

    @Override
    public CriterionTriggerFilledBucket.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerFilledBucket.a(criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerfilledbucket_a) -> {
            return criteriontriggerfilledbucket_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;

        public a(CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerFilledBucket.a);
            this.a = criterionconditionitem;
        }

        public static CriterionTriggerFilledBucket.a a(CriterionConditionItem criterionconditionitem) {
            return new CriterionTriggerFilledBucket.a(criterionconditionitem);
        }

        public boolean a(ItemStack itemstack) {
            return this.a.a(itemstack);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("item", this.a.a());
            return jsonobject;
        }
    }
}
