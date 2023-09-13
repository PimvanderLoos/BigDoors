package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class ShapedRecipes implements RecipeCrafting {

    final int width;
    final int height;
    final NonNullList<RecipeItemStack> recipeItems;
    final ItemStack result;
    private final MinecraftKey id;
    final String group;

    public ShapedRecipes(MinecraftKey minecraftkey, String s, int i, int j, NonNullList<RecipeItemStack> nonnulllist, ItemStack itemstack) {
        this.id = minecraftkey;
        this.group = s;
        this.width = i;
        this.height = j;
        this.recipeItems = nonnulllist;
        this.result = itemstack;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED_RECIPE;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public NonNullList<RecipeItemStack> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i >= this.width && j >= this.height;
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        for (int i = 0; i <= inventorycrafting.getWidth() - this.width; ++i) {
            for (int j = 0; j <= inventorycrafting.getHeight() - this.height; ++j) {
                if (this.matches(inventorycrafting, i, j, true)) {
                    return true;
                }

                if (this.matches(inventorycrafting, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
        for (int k = 0; k < inventorycrafting.getWidth(); ++k) {
            for (int l = 0; l < inventorycrafting.getHeight(); ++l) {
                int i1 = k - i;
                int j1 = l - j;
                RecipeItemStack recipeitemstack = RecipeItemStack.EMPTY;

                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        recipeitemstack = (RecipeItemStack) this.recipeItems.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        recipeitemstack = (RecipeItemStack) this.recipeItems.get(i1 + j1 * this.width);
                    }
                }

                if (!recipeitemstack.test(inventorycrafting.getItem(k + l * inventorycrafting.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting) {
        return this.getResultItem().copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    static NonNullList<RecipeItemStack> dissolvePattern(String[] astring, Map<String, RecipeItemStack> map, int i, int j) {
        NonNullList<RecipeItemStack> nonnulllist = NonNullList.withSize(i * j, RecipeItemStack.EMPTY);
        Set<String> set = Sets.newHashSet(map.keySet());

        set.remove(" ");

        for (int k = 0; k < astring.length; ++k) {
            for (int l = 0; l < astring[k].length(); ++l) {
                String s = astring[k].substring(l, l + 1);
                RecipeItemStack recipeitemstack = (RecipeItemStack) map.get(s);

                if (recipeitemstack == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(l + i * k, recipeitemstack);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] shrink(String... astring) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < astring.length; ++i1) {
            String s = astring[i1];

            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);

            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (astring.length == l) {
            return new String[0];
        } else {
            String[] astring1 = new String[astring.length - l - k];

            for (int k1 = 0; k1 < astring1.length; ++k1) {
                astring1[k1] = astring[k1 + k].substring(i, j + 1);
            }

            return astring1;
        }
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<RecipeItemStack> nonnulllist = this.getIngredients();

        return nonnulllist.isEmpty() || nonnulllist.stream().filter((recipeitemstack) -> {
            return !recipeitemstack.isEmpty();
        }).anyMatch((recipeitemstack) -> {
            return recipeitemstack.getItems().length == 0;
        });
    }

    private static int firstNonSpace(String s) {
        int i;

        for (i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
            ;
        }

        return i;
    }

    private static int lastNonSpace(String s) {
        int i;

        for (i = s.length() - 1; i >= 0 && s.charAt(i) == ' '; --i) {
            ;
        }

        return i;
    }

    static String[] patternFromJson(JsonArray jsonarray) {
        String[] astring = new String[jsonarray.size()];

        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = ChatDeserializer.convertToString(jsonarray.get(i), "pattern[" + i + "]");

                if (s.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    static Map<String, RecipeItemStack> keyFromJson(JsonObject jsonobject) {
        Map<String, RecipeItemStack> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry) iterator.next();

            if (((String) entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put((String) entry.getKey(), RecipeItemStack.fromJson((JsonElement) entry.getValue()));
        }

        map.put(" ", RecipeItemStack.EMPTY);
        return map;
    }

    public static ItemStack itemStackFromJson(JsonObject jsonobject) {
        Item item = itemFromJson(jsonobject);

        if (jsonobject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = ChatDeserializer.getAsInt(jsonobject, "count", 1);

            if (i < 1) {
                throw new JsonSyntaxException("Invalid output count: " + i);
            } else {
                return new ItemStack(item, i);
            }
        }
    }

    public static Item itemFromJson(JsonObject jsonobject) {
        String s = ChatDeserializer.getAsString(jsonobject, "item");
        Item item = (Item) IRegistry.ITEM.getOptional(new MinecraftKey(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });

        if (item == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + s);
        } else {
            return item;
        }
    }

    public static class a implements RecipeSerializer<ShapedRecipes> {

        public a() {}

        @Override
        public ShapedRecipes fromJson(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.getAsString(jsonobject, "group", "");
            Map<String, RecipeItemStack> map = ShapedRecipes.keyFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "key"));
            String[] astring = ShapedRecipes.shrink(ShapedRecipes.patternFromJson(ChatDeserializer.getAsJsonArray(jsonobject, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<RecipeItemStack> nonnulllist = ShapedRecipes.dissolvePattern(astring, map, i, j);
            ItemStack itemstack = ShapedRecipes.itemStackFromJson(ChatDeserializer.getAsJsonObject(jsonobject, "result"));

            return new ShapedRecipes(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        @Override
        public ShapedRecipes fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.readVarInt();
            int j = packetdataserializer.readVarInt();
            String s = packetdataserializer.readUtf();
            NonNullList<RecipeItemStack> nonnulllist = NonNullList.withSize(i * j, RecipeItemStack.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, RecipeItemStack.fromNetwork(packetdataserializer));
            }

            ItemStack itemstack = packetdataserializer.readItem();

            return new ShapedRecipes(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        public void toNetwork(PacketDataSerializer packetdataserializer, ShapedRecipes shapedrecipes) {
            packetdataserializer.writeVarInt(shapedrecipes.width);
            packetdataserializer.writeVarInt(shapedrecipes.height);
            packetdataserializer.writeUtf(shapedrecipes.group);
            Iterator iterator = shapedrecipes.recipeItems.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();

                recipeitemstack.toNetwork(packetdataserializer);
            }

            packetdataserializer.writeItem(shapedrecipes.result);
        }
    }
}
