package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerRecipeUnlocked extends CriterionTriggerAbstract<CriterionTriggerRecipeUnlocked.a> {

    private static final MinecraftKey a = new MinecraftKey("recipe_unlocked");

    public CriterionTriggerRecipeUnlocked() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerRecipeUnlocked.a;
    }

    @Override
    public CriterionTriggerRecipeUnlocked.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "recipe"));

        return new CriterionTriggerRecipeUnlocked.a(minecraftkey);
    }

    public void a(EntityPlayer entityplayer, IRecipe<?> irecipe) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerrecipeunlocked_a) -> {
            return criteriontriggerrecipeunlocked_a.a(irecipe);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final MinecraftKey a;

        public a(MinecraftKey minecraftkey) {
            super(CriterionTriggerRecipeUnlocked.a);
            this.a = minecraftkey;
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("recipe", this.a.toString());
            return jsonobject;
        }

        public boolean a(IRecipe<?> irecipe) {
            return this.a.equals(irecipe.getKey());
        }
    }
}
