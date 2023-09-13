package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportRecipeFurnace {

    private static final Logger a = LogManager.getLogger();
    private final Item b;
    private final RecipeItemStack c;
    private final float d;
    private final int e;
    private final Advancement.SerializedAdvancement f = Advancement.SerializedAdvancement.a();
    private String g;

    public DebugReportRecipeFurnace(RecipeItemStack recipeitemstack, IMaterial imaterial, float f, int i) {
        this.b = imaterial.getItem();
        this.c = recipeitemstack;
        this.d = f;
        this.e = i;
    }

    public static DebugReportRecipeFurnace a(RecipeItemStack recipeitemstack, IMaterial imaterial, float f, int i) {
        return new DebugReportRecipeFurnace(recipeitemstack, imaterial, f, i);
    }

    public DebugReportRecipeFurnace a(String s, CriterionInstance criterioninstance) {
        this.f.a(s, criterioninstance);
        return this;
    }

    public void a(Consumer<DebugReportRecipeData> consumer) {
        this.a(consumer, IRegistry.ITEM.getKey(this.b));
    }

    public void a(Consumer<DebugReportRecipeData> consumer, String s) {
        MinecraftKey minecraftkey = IRegistry.ITEM.getKey(this.b);

        if ((new MinecraftKey(s)).equals(minecraftkey)) {
            throw new IllegalStateException("Smelting Recipe " + s + " should remove its 'save' argument");
        } else {
            this.a(consumer, new MinecraftKey(s));
        }
    }

    public void a(Consumer<DebugReportRecipeData> consumer, MinecraftKey minecraftkey) {
        this.a(minecraftkey);
        this.f.a(new MinecraftKey("minecraft:recipes/root")).a("has_the_recipe", (CriterionInstance) (new CriterionTriggerRecipeUnlocked.b(minecraftkey))).a(AdvancementRewards.a.c(minecraftkey)).a(AdvancementRequirements.OR);
        consumer.accept(new DebugReportRecipeFurnace.a(minecraftkey, this.g == null ? "" : this.g, this.c, this.b, this.d, this.e, this.f, new MinecraftKey(minecraftkey.b(), "recipes/" + this.b.q().c() + "/" + minecraftkey.getKey())));
    }

    private void a(MinecraftKey minecraftkey) {
        if (this.f.c().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + minecraftkey);
        }
    }

    public static class a implements DebugReportRecipeData {

        private final MinecraftKey a;
        private final String b;
        private final RecipeItemStack c;
        private final Item d;
        private final float e;
        private final int f;
        private final Advancement.SerializedAdvancement g;
        private final MinecraftKey h;

        public a(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, Item item, float f, int i, Advancement.SerializedAdvancement advancement_serializedadvancement, MinecraftKey minecraftkey1) {
            this.a = minecraftkey;
            this.b = s;
            this.c = recipeitemstack;
            this.d = item;
            this.e = f;
            this.f = i;
            this.g = advancement_serializedadvancement;
            this.h = minecraftkey1;
        }

        public JsonObject a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("type", "smelting");
            if (!this.b.isEmpty()) {
                jsonobject.addProperty("group", this.b);
            }

            jsonobject.add("ingredient", this.c.c());
            jsonobject.addProperty("result", IRegistry.ITEM.getKey(this.d).toString());
            jsonobject.addProperty("experience", this.e);
            jsonobject.addProperty("cookingtime", this.f);
            return jsonobject;
        }

        public MinecraftKey b() {
            return this.a;
        }

        @Nullable
        public JsonObject c() {
            return this.g.b();
        }

        @Nullable
        public MinecraftKey d() {
            return this.h;
        }
    }
}
