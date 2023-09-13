package net.minecraft.world.entity.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeItemStack;

public class AutoRecipeStackManager {

    private static final int EMPTY = 0;
    public final Int2IntMap contents = new Int2IntOpenHashMap();

    public AutoRecipeStackManager() {}

    public void a(ItemStack itemstack) {
        if (!itemstack.g() && !itemstack.hasEnchantments() && !itemstack.hasName()) {
            this.b(itemstack);
        }

    }

    public void b(ItemStack itemstack) {
        this.a(itemstack, 64);
    }

    public void a(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty()) {
            int j = c(itemstack);
            int k = Math.min(i, itemstack.getCount());

            this.b(j, k);
        }

    }

    public static int c(ItemStack itemstack) {
        return IRegistry.ITEM.getId(itemstack.getItem());
    }

    boolean b(int i) {
        return this.contents.get(i) > 0;
    }

    int a(int i, int j) {
        int k = this.contents.get(i);

        if (k >= j) {
            this.contents.put(i, k - j);
            return i;
        } else {
            return 0;
        }
    }

    void b(int i, int j) {
        this.contents.put(i, this.contents.get(i) + j);
    }

    public boolean a(IRecipe<?> irecipe, @Nullable IntList intlist) {
        return this.a(irecipe, intlist, 1);
    }

    public boolean a(IRecipe<?> irecipe, @Nullable IntList intlist, int i) {
        return (new AutoRecipeStackManager.a(irecipe)).a(i, intlist);
    }

    public int b(IRecipe<?> irecipe, @Nullable IntList intlist) {
        return this.a(irecipe, Integer.MAX_VALUE, intlist);
    }

    public int a(IRecipe<?> irecipe, int i, @Nullable IntList intlist) {
        return (new AutoRecipeStackManager.a(irecipe)).b(i, intlist);
    }

    public static ItemStack a(int i) {
        return i == 0 ? ItemStack.EMPTY : new ItemStack(Item.getById(i));
    }

    public void a() {
        this.contents.clear();
    }

    private class a {

        private final IRecipe<?> recipe;
        private final List<RecipeItemStack> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] items;
        private final int itemCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public a(IRecipe irecipe) {
            this.recipe = irecipe;
            this.ingredients.addAll(irecipe.a());
            this.ingredients.removeIf(RecipeItemStack::d);
            this.ingredientCount = this.ingredients.size();
            this.items = this.a();
            this.itemCount = this.items.length;
            this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);

            for (int i = 0; i < this.ingredients.size(); ++i) {
                IntList intlist = ((RecipeItemStack) this.ingredients.get(i)).b();

                for (int j = 0; j < this.itemCount; ++j) {
                    if (intlist.contains(this.items[j])) {
                        this.data.set(this.d(true, j, i));
                    }
                }
            }

        }

        public boolean a(int i, @Nullable IntList intlist) {
            if (i <= 0) {
                return true;
            } else {
                int j;

                for (j = 0; this.a(i); ++j) {
                    AutoRecipeStackManager.this.a(this.items[this.path.getInt(0)], i);
                    int k = this.path.size() - 1;

                    this.c(this.path.getInt(k));

                    for (int l = 0; l < k; ++l) {
                        this.c((l & 1) == 0, this.path.get(l), this.path.get(l + 1));
                    }

                    this.path.clear();
                    this.data.clear(0, this.ingredientCount + this.itemCount);
                }

                boolean flag = j == this.ingredientCount;
                boolean flag1 = flag && intlist != null;

                if (flag1) {
                    intlist.clear();
                }

                this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
                int i1 = 0;
                List<RecipeItemStack> list = this.recipe.a();

                for (int j1 = 0; j1 < list.size(); ++j1) {
                    if (flag1 && ((RecipeItemStack) list.get(j1)).d()) {
                        intlist.add(0);
                    } else {
                        for (int k1 = 0; k1 < this.itemCount; ++k1) {
                            if (this.b(false, i1, k1)) {
                                this.c(true, k1, i1);
                                AutoRecipeStackManager.this.b(this.items[k1], i);
                                if (flag1) {
                                    intlist.add(this.items[k1]);
                                }
                            }
                        }

                        ++i1;
                    }
                }

                return flag;
            }
        }

        private int[] a() {
            IntAVLTreeSet intavltreeset = new IntAVLTreeSet();
            Iterator iterator = this.ingredients.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();

                intavltreeset.addAll(recipeitemstack.b());
            }

            IntIterator intiterator = intavltreeset.iterator();

            while (intiterator.hasNext()) {
                if (!AutoRecipeStackManager.this.b(intiterator.nextInt())) {
                    intiterator.remove();
                }
            }

            return intavltreeset.toIntArray();
        }

        private boolean a(int i) {
            int j = this.itemCount;

            for (int k = 0; k < j; ++k) {
                if (AutoRecipeStackManager.this.contents.get(this.items[k]) >= i) {
                    this.a(false, k);

                    while (!this.path.isEmpty()) {
                        int l = this.path.size();
                        boolean flag = (l & 1) == 1;
                        int i1 = this.path.getInt(l - 1);

                        if (!flag && !this.b(i1)) {
                            break;
                        }

                        int j1 = flag ? this.ingredientCount : j;

                        int k1;

                        for (k1 = 0; k1 < j1; ++k1) {
                            if (!this.b(flag, k1) && this.a(flag, i1, k1) && this.b(flag, i1, k1)) {
                                this.a(flag, k1);
                                break;
                            }
                        }

                        k1 = this.path.size();
                        if (k1 == l) {
                            this.path.removeInt(k1 - 1);
                        }
                    }

                    if (!this.path.isEmpty()) {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean b(int i) {
            return this.data.get(this.d(i));
        }

        private void c(int i) {
            this.data.set(this.d(i));
        }

        private int d(int i) {
            return this.ingredientCount + this.itemCount + i;
        }

        private boolean a(boolean flag, int i, int j) {
            return this.data.get(this.d(flag, i, j));
        }

        private boolean b(boolean flag, int i, int j) {
            return flag != this.data.get(1 + this.d(flag, i, j));
        }

        private void c(boolean flag, int i, int j) {
            this.data.flip(1 + this.d(flag, i, j));
        }

        private int d(boolean flag, int i, int j) {
            int k = flag ? i * this.ingredientCount + j : j * this.ingredientCount + i;

            return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * k;
        }

        private void a(boolean flag, int i) {
            this.data.set(this.c(flag, i));
            this.path.add(i);
        }

        private boolean b(boolean flag, int i) {
            return this.data.get(this.c(flag, i));
        }

        private int c(boolean flag, int i) {
            return (flag ? 0 : this.ingredientCount) + i;
        }

        public int b(int i, @Nullable IntList intlist) {
            int j = 0;
            int k = Math.min(i, this.b()) + 1;

            while (true) {
                while (true) {
                    int l = (j + k) / 2;

                    if (this.a(l, (IntList) null)) {
                        if (k - j <= 1) {
                            if (l > 0) {
                                this.a(l, intlist);
                            }

                            return l;
                        }

                        j = l;
                    } else {
                        k = l;
                    }
                }
            }
        }

        private int b() {
            int i = Integer.MAX_VALUE;
            Iterator iterator = this.ingredients.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();
                int j = 0;

                int k;

                for (IntListIterator intlistiterator = recipeitemstack.b().iterator(); intlistiterator.hasNext(); j = Math.max(j, AutoRecipeStackManager.this.contents.get(k))) {
                    k = (Integer) intlistiterator.next();
                }

                if (i > 0) {
                    i = Math.min(i, j);
                }
            }

            return i;
        }
    }
}
