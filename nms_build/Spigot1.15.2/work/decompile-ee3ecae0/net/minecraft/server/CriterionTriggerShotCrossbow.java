package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerShotCrossbow extends CriterionTriggerAbstract<CriterionTriggerShotCrossbow.a> {

    private static final MinecraftKey a = new MinecraftKey("shot_crossbow");

    public CriterionTriggerShotCrossbow() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerShotCrossbow.a;
    }

    @Override
    public CriterionTriggerShotCrossbow.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerShotCrossbow.a(criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggershotcrossbow_a) -> {
            return criteriontriggershotcrossbow_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;

        public a(CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerShotCrossbow.a);
            this.a = criterionconditionitem;
        }

        public static CriterionTriggerShotCrossbow.a a(IMaterial imaterial) {
            return new CriterionTriggerShotCrossbow.a(CriterionConditionItem.a.a().a(imaterial).b());
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
