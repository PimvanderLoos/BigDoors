package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerUsedTotem extends CriterionTriggerAbstract<CriterionTriggerUsedTotem.a> {

    private static final MinecraftKey a = new MinecraftKey("used_totem");

    public CriterionTriggerUsedTotem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerUsedTotem.a;
    }

    @Override
    public CriterionTriggerUsedTotem.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerUsedTotem.a(criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerusedtotem_a) -> {
            return criteriontriggerusedtotem_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;

        public a(CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerUsedTotem.a);
            this.a = criterionconditionitem;
        }

        public static CriterionTriggerUsedTotem.a a(IMaterial imaterial) {
            return new CriterionTriggerUsedTotem.a(CriterionConditionItem.a.a().a(imaterial).b());
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
