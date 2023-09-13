package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportRecipeShaped {

    private static final Logger a = LogManager.getLogger();
    private final Item b;
    private final int c;
    private final List<String> d = Lists.newArrayList();
    private final Map<Character, RecipeItemStack> e = Maps.newLinkedHashMap();
    private final Advancement.SerializedAdvancement f = Advancement.SerializedAdvancement.a();
    private String g;

    public DebugReportRecipeShaped(IMaterial imaterial, int i) {
        this.b = imaterial.getItem();
        this.c = i;
    }

    public static DebugReportRecipeShaped a(IMaterial imaterial) {
        return a(imaterial, 1);
    }

    public static DebugReportRecipeShaped a(IMaterial imaterial, int i) {
        return new DebugReportRecipeShaped(imaterial, i);
    }

    public DebugReportRecipeShaped a(Character character, Tag<Item> tag) {
        return this.a(character, RecipeItemStack.a(tag));
    }

    public DebugReportRecipeShaped a(Character character, IMaterial imaterial) {
        return this.a(character, RecipeItemStack.a(imaterial));
    }

    public DebugReportRecipeShaped a(Character character, RecipeItemStack recipeitemstack) {
        if (this.e.containsKey(character)) {
            throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
        } else if (character == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.e.put(character, recipeitemstack);
            return this;
        }
    }

    public DebugReportRecipeShaped a(String s) {
        if (!this.d.isEmpty() && s.length() != ((String) this.d.get(0)).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.d.add(s);
            return this;
        }
    }

    public DebugReportRecipeShaped a(String s, CriterionInstance criterioninstance) {
        this.f.a(s, criterioninstance);
        return this;
    }

    public DebugReportRecipeShaped b(String s) {
        this.g = s;
        return this;
    }

    public void a(Consumer<DebugReportRecipeData> consumer) {
        this.a(consumer, IRegistry.ITEM.getKey(this.b));
    }

    public void a(Consumer<DebugReportRecipeData> consumer, String s) {
        MinecraftKey minecraftkey = IRegistry.ITEM.getKey(this.b);

        if ((new MinecraftKey(s)).equals(minecraftkey)) {
            throw new IllegalStateException("Shaped Recipe " + s + " should remove its 'save' argument");
        } else {
            this.a(consumer, new MinecraftKey(s));
        }
    }

    public void a(Consumer<DebugReportRecipeData> consumer, MinecraftKey minecraftkey) {
        this.a(minecraftkey);
        this.f.a(new MinecraftKey("minecraft:recipes/root")).a("has_the_recipe", (CriterionInstance) (new CriterionTriggerRecipeUnlocked.b(minecraftkey))).a(AdvancementRewards.a.c(minecraftkey)).a(AdvancementRequirements.OR);
        consumer.accept(new DebugReportRecipeShaped.a(minecraftkey, this.b, this.c, this.g == null ? "" : this.g, this.d, this.e, this.f, new MinecraftKey(minecraftkey.b(), "recipes/" + this.b.q().c() + "/" + minecraftkey.getKey())));
    }

    private void a(MinecraftKey minecraftkey) {
        if (this.d.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + minecraftkey + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.e.keySet());

            set.remove(' ');
            Iterator iterator = this.d.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                for (int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);

                    if (!this.e.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + minecraftkey + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + minecraftkey);
            } else if (this.d.size() == 1 && ((String) this.d.get(0)).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + minecraftkey + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.f.c().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + minecraftkey);
            }
        }
    }

    class a implements DebugReportRecipeData {

        private final MinecraftKey b;
        private final Item c;
        private final int d;
        private final String e;
        private final List<String> f;
        private final Map<Character, RecipeItemStack> g;
        private final Advancement.SerializedAdvancement h;
        private final MinecraftKey i;

        public a(MinecraftKey minecraftkey, Item item, int i, String s, List list, Map map, Advancement.SerializedAdvancement advancement_serializedadvancement, MinecraftKey minecraftkey1) {
            this.b = minecraftkey;
            this.c = item;
            this.d = i;
            this.e = s;
            this.f = list;
            this.g = map;
            this.h = advancement_serializedadvancement;
            this.i = minecraftkey1;
        }

        public JsonObject a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("type", "crafting_shaped");
            if (!this.e.isEmpty()) {
                jsonobject.addProperty("group", this.e);
            }

            JsonArray jsonarray = new JsonArray();
            Iterator iterator = this.f.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                jsonarray.add(s);
            }

            jsonobject.add("pattern", jsonarray);
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator1 = this.g.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry<Character, RecipeItemStack> entry = (Entry) iterator1.next();

                jsonobject1.add(String.valueOf(entry.getKey()), ((RecipeItemStack) entry.getValue()).c());
            }

            jsonobject.add("key", jsonobject1);
            JsonObject jsonobject2 = new JsonObject();

            jsonobject2.addProperty("item", IRegistry.ITEM.getKey(this.c).toString());
            if (this.d > 1) {
                jsonobject2.addProperty("count", this.d);
            }

            jsonobject.add("result", jsonobject2);
            return jsonobject;
        }

        public MinecraftKey b() {
            return this.b;
        }

        @Nullable
        public JsonObject c() {
            return this.h.b();
        }

        @Nullable
        public MinecraftKey d() {
            return this.i;
        }
    }
}
