package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.crafting.IRecipe;

public class CriterionTriggerRecipeUnlocked extends CriterionTriggerAbstract<CriterionTriggerRecipeUnlocked.a> {

    private static final MinecraftKey a = new MinecraftKey("recipe_unlocked");

    public CriterionTriggerRecipeUnlocked() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerRecipeUnlocked.a;
    }

    @Override
    public CriterionTriggerRecipeUnlocked.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "recipe"));

        return new CriterionTriggerRecipeUnlocked.a(criterionconditionentity_b, minecraftkey);
    }

    public void a(EntityPlayer entityplayer, IRecipe<?> irecipe) {
        this.a(entityplayer, (criteriontriggerrecipeunlocked_a) -> {
            return criteriontriggerrecipeunlocked_a.a(irecipe);
        });
    }

    public static CriterionTriggerRecipeUnlocked.a a(MinecraftKey minecraftkey) {
        return new CriterionTriggerRecipeUnlocked.a(CriterionConditionEntity.b.a, minecraftkey);
    }

    public static class a extends CriterionInstanceAbstract {

        private final MinecraftKey a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, MinecraftKey minecraftkey) {
            super(CriterionTriggerRecipeUnlocked.a, criterionconditionentity_b);
            this.a = minecraftkey;
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.addProperty("recipe", this.a.toString());
            return jsonobject;
        }

        public boolean a(IRecipe<?> irecipe) {
            return this.a.equals(irecipe.getKey());
        }
    }
}
