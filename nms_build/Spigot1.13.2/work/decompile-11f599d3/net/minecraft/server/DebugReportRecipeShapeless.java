package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportRecipeShapeless {

    private static final Logger a = LogManager.getLogger();
    private final Item b;
    private final int c;
    private final List<RecipeItemStack> d = Lists.newArrayList();
    private final Advancement.SerializedAdvancement e = Advancement.SerializedAdvancement.a();
    private String f;

    public DebugReportRecipeShapeless(IMaterial imaterial, int i) {
        this.b = imaterial.getItem();
        this.c = i;
    }

    public static DebugReportRecipeShapeless a(IMaterial imaterial) {
        return new DebugReportRecipeShapeless(imaterial, 1);
    }

    public static DebugReportRecipeShapeless a(IMaterial imaterial, int i) {
        return new DebugReportRecipeShapeless(imaterial, i);
    }

    public DebugReportRecipeShapeless a(Tag<Item> tag) {
        return this.a(RecipeItemStack.a(tag));
    }

    public DebugReportRecipeShapeless b(IMaterial imaterial) {
        return this.b(imaterial, 1);
    }

    public DebugReportRecipeShapeless b(IMaterial imaterial, int i) {
        for (int j = 0; j < i; ++j) {
            this.a(RecipeItemStack.a(imaterial));
        }

        return this;
    }

    public DebugReportRecipeShapeless a(RecipeItemStack recipeitemstack) {
        return this.a(recipeitemstack, 1);
    }

    public DebugReportRecipeShapeless a(RecipeItemStack recipeitemstack, int i) {
        for (int j = 0; j < i; ++j) {
            this.d.add(recipeitemstack);
        }

        return this;
    }

    public DebugReportRecipeShapeless a(String s, CriterionInstance criterioninstance) {
        this.e.a(s, criterioninstance);
        return this;
    }

    public DebugReportRecipeShapeless a(String s) {
        this.f = s;
        return this;
    }

    public void a(Consumer<DebugReportRecipeData> consumer) {
        this.a(consumer, IRegistry.ITEM.getKey(this.b));
    }

    public void a(Consumer<DebugReportRecipeData> consumer, String s) {
        MinecraftKey minecraftkey = IRegistry.ITEM.getKey(this.b);

        if ((new MinecraftKey(s)).equals(minecraftkey)) {
            throw new IllegalStateException("Shapeless Recipe " + s + " should remove its 'save' argument");
        } else {
            this.a(consumer, new MinecraftKey(s));
        }
    }

    public void a(Consumer<DebugReportRecipeData> consumer, MinecraftKey minecraftkey) {
        this.a(minecraftkey);
        this.e.a(new MinecraftKey("minecraft:recipes/root")).a("has_the_recipe", (CriterionInstance) (new CriterionTriggerRecipeUnlocked.b(minecraftkey))).a(AdvancementRewards.a.c(minecraftkey)).a(AdvancementRequirements.OR);
        consumer.accept(new DebugReportRecipeShapeless.a(minecraftkey, this.b, this.c, this.f == null ? "" : this.f, this.d, this.e, new MinecraftKey(minecraftkey.b(), "recipes/" + this.b.q().c() + "/" + minecraftkey.getKey())));
    }

    private void a(MinecraftKey minecraftkey) {
        if (this.e.c().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + minecraftkey);
        }
    }

    public static class a implements DebugReportRecipeData {

        private final MinecraftKey a;
        private final Item b;
        private final int c;
        private final String d;
        private final List<RecipeItemStack> e;
        private final Advancement.SerializedAdvancement f;
        private final MinecraftKey g;

        public a(MinecraftKey minecraftkey, Item item, int i, String s, List<RecipeItemStack> list, Advancement.SerializedAdvancement advancement_serializedadvancement, MinecraftKey minecraftkey1) {
            this.a = minecraftkey;
            this.b = item;
            this.c = i;
            this.d = s;
            this.e = list;
            this.f = advancement_serializedadvancement;
            this.g = minecraftkey1;
        }

        public JsonObject a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("type", "crafting_shapeless");
            if (!this.d.isEmpty()) {
                jsonobject.addProperty("group", this.d);
            }

            JsonArray jsonarray = new JsonArray();
            Iterator iterator = this.e.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();

                jsonarray.add(recipeitemstack.c());
            }

            jsonobject.add("ingredients", jsonarray);
            JsonObject jsonobject1 = new JsonObject();

            jsonobject1.addProperty("item", IRegistry.ITEM.getKey(this.b).toString());
            if (this.c > 1) {
                jsonobject1.addProperty("count", this.c);
            }

            jsonobject.add("result", jsonobject1);
            return jsonobject;
        }

        public MinecraftKey b() {
            return this.a;
        }

        @Nullable
        public JsonObject c() {
            return this.f.b();
        }

        @Nullable
        public MinecraftKey d() {
            return this.g;
        }
    }
}
