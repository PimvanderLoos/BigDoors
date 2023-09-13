package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public class CriterionTriggerUsedTotem extends CriterionTriggerAbstract<CriterionTriggerUsedTotem.a> {

    private static final MinecraftKey a = new MinecraftKey("used_totem");

    public CriterionTriggerUsedTotem() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerUsedTotem.a;
    }

    @Override
    public CriterionTriggerUsedTotem.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerUsedTotem.a(criterionconditionentity_b, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.a(entityplayer, (criteriontriggerusedtotem_a) -> {
            return criteriontriggerusedtotem_a.a(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerUsedTotem.a, criterionconditionentity_b);
            this.a = criterionconditionitem;
        }

        public static CriterionTriggerUsedTotem.a a(IMaterial imaterial) {
            return new CriterionTriggerUsedTotem.a(CriterionConditionEntity.b.a, CriterionConditionItem.a.a().a(imaterial).b());
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
