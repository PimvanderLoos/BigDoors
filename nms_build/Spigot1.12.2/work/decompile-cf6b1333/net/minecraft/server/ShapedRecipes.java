package net.minecraft.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class ShapedRecipes implements IRecipe {

    private final int width;
    private final int height;
    private final NonNullList<RecipeItemStack> items;
    private final ItemStack result;
    private final String e;

    public ShapedRecipes(String s, int i, int j, NonNullList<RecipeItemStack> nonnulllist, ItemStack itemstack) {
        this.e = s;
        this.width = i;
        this.height = j;
        this.items = nonnulllist;
        this.result = itemstack;
    }

    public ItemStack b() {
        return this.result;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack.getItem().r()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().q()));
            }
        }

        return nonnulllist;
    }

    public NonNullList<RecipeItemStack> d() {
        return this.items;
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        for (int i = 0; i <= 3 - this.width; ++i) {
            for (int j = 0; j <= 3 - this.height; ++j) {
                if (this.a(inventorycrafting, i, j, true)) {
                    return true;
                }

                if (this.a(inventorycrafting, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean a(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int i1 = k - i;
                int j1 = l - j;
                RecipeItemStack recipeitemstack = RecipeItemStack.a;

                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        recipeitemstack = (RecipeItemStack) this.items.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        recipeitemstack = (RecipeItemStack) this.items.get(i1 + j1 * this.width);
                    }
                }

                if (!recipeitemstack.a(inventorycrafting.c(k, l))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        return this.b().cloneItemStack();
    }

    public int f() {
        return this.width;
    }

    public int g() {
        return this.height;
    }

    public static ShapedRecipes a(JsonObject jsonobject) {
        String s = ChatDeserializer.a(jsonobject, "group", "");
        Map map = b(ChatDeserializer.t(jsonobject, "key"));
        String[] astring = a(a(ChatDeserializer.u(jsonobject, "pattern")));
        int i = astring[0].length();
        int j = astring.length;
        NonNullList nonnulllist = a(astring, map, i, j);
        ItemStack itemstack = a(ChatDeserializer.t(jsonobject, "result"), true);

        return new ShapedRecipes(s, i, j, nonnulllist, itemstack);
    }

    private static NonNullList<RecipeItemStack> a(String[] astring, Map<String, RecipeItemStack> map, int i, int j) {
        NonNullList nonnulllist = NonNullList.a(i * j, RecipeItemStack.a);
        HashSet hashset = Sets.newHashSet(map.keySet());

        hashset.remove(" ");

        for (int k = 0; k < astring.length; ++k) {
            for (int l = 0; l < astring[k].length(); ++l) {
                String s = astring[k].substring(l, l + 1);
                RecipeItemStack recipeitemstack = (RecipeItemStack) map.get(s);

                if (recipeitemstack == null) {
                    throw new JsonSyntaxException("Pattern references symbol \'" + s + "\' but it\'s not defined in the key");
                }

                hashset.remove(s);
                nonnulllist.set(l + i * k, recipeitemstack);
            }
        }

        if (!hashset.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren\'t used in pattern: " + hashset);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] a(String... astring) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < astring.length; ++i1) {
            String s = astring[i1];

            i = Math.min(i, a(s));
            int j1 = b(s);

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

    private static int a(String s) {
        int i;

        for (i = 0; i < s.length() && s.charAt(i) == 32; ++i) {
            ;
        }

        return i;
    }

    private static int b(String s) {
        int i;

        for (i = s.length() - 1; i >= 0 && s.charAt(i) == 32; --i) {
            ;
        }

        return i;
    }

    private static String[] a(JsonArray jsonarray) {
        String[] astring = new String[jsonarray.size()];

        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = ChatDeserializer.a(jsonarray.get(i), "pattern[" + i + "]");

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

    private static Map<String, RecipeItemStack> b(JsonObject jsonobject) {
        HashMap hashmap = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: \'" + (String) entry.getKey() + "\' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: \' \' is a reserved symbol.");
            }

            hashmap.put(entry.getKey(), a((JsonElement) entry.getValue()));
        }

        hashmap.put(" ", RecipeItemStack.a);
        return hashmap;
    }

    public static RecipeItemStack a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (jsonelement.isJsonObject()) {
                return RecipeItemStack.a(new ItemStack[] { a(jsonelement.getAsJsonObject(), false)});
            } else if (!jsonelement.isJsonArray()) {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            } else {
                JsonArray jsonarray = jsonelement.getAsJsonArray();

                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    ItemStack[] aitemstack = new ItemStack[jsonarray.size()];

                    for (int i = 0; i < jsonarray.size(); ++i) {
                        aitemstack[i] = a(ChatDeserializer.m(jsonarray.get(i), "item"), false);
                    }

                    return RecipeItemStack.a(aitemstack);
                }
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static ItemStack a(JsonObject jsonobject, boolean flag) {
        String s = ChatDeserializer.h(jsonobject, "item");
        Item item = (Item) Item.REGISTRY.get(new MinecraftKey(s));

        if (item == null) {
            throw new JsonSyntaxException("Unknown item \'" + s + "\'");
        } else if (item.k() && !jsonobject.has("data")) {
            throw new JsonParseException("Missing data for item \'" + s + "\'");
        } else {
            int i = ChatDeserializer.a(jsonobject, "data", 0);
            int j = flag ? ChatDeserializer.a(jsonobject, "count", 1) : 1;

            return new ItemStack(item, j, i);
        }
    }
}
