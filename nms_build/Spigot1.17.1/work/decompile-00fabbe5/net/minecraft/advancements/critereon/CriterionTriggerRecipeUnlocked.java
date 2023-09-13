package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.crafting.IRecipe;

public class CriterionTriggerRecipeUnlocked extends CriterionTriggerAbstract<CriterionTriggerRecipeUnlocked.a> {

    static final MinecraftKey ID = new MinecraftKey("recipe_unlocked");

    public CriterionTriggerRecipeUnlocked() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerRecipeUnlocked.ID;
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
        return new CriterionTriggerRecipeUnlocked.a(CriterionConditionEntity.b.ANY, minecraftkey);
    }

    public static class a extends CriterionInstanceAbstract {

        private final MinecraftKey recipe;

        public a(CriterionConditionEntity.b criterionconditionentity_b, MinecraftKey minecraftkey) {
            super(CriterionTriggerRecipeUnlocked.ID, criterionconditionentity_b);
            this.recipe = minecraftkey;
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.addProperty("recipe", this.recipe.toString());
            return jsonobject;
        }

        public boolean a(IRecipe<?> irecipe) {
            return this.recipe.equals(irecipe.getKey());
        }
    }
}
