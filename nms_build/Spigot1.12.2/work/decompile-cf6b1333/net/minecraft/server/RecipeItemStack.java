package net.minecraft.server;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;

public class RecipeItemStack implements Predicate<ItemStack> {

    public static final RecipeItemStack a = new RecipeItemStack(new ItemStack[0], null) {
        public boolean a(@Nullable ItemStack itemstack) {
            return itemstack.isEmpty();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((ItemStack) object);
        }
    };
    public final ItemStack[] choices;
    private IntList c;

    private RecipeItemStack(ItemStack... aitemstack) {
        this.choices = aitemstack;
    }

    public boolean a(@Nullable ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        } else {
            ItemStack[] aitemstack = this.choices;
            int i = aitemstack.length;

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack1 = aitemstack[j];

                if (itemstack1.getItem() == itemstack.getItem()) {
                    int k = itemstack1.getData();

                    if (k == 32767 || k == itemstack.getData()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public IntList b() {
        if (this.c == null) {
            this.c = new IntArrayList(this.choices.length);
            ItemStack[] aitemstack = this.choices;
            int i = aitemstack.length;

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = aitemstack[j];

                this.c.add(AutoRecipeStackManager.b(itemstack));
            }

            this.c.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.c;
    }

    public static RecipeItemStack a(Item item) {
        return a(new ItemStack[] { new ItemStack(item, 1, 32767)});
    }

    public static RecipeItemStack a(Item... aitem) {
        ItemStack[] aitemstack = new ItemStack[aitem.length];

        for (int i = 0; i < aitem.length; ++i) {
            aitemstack[i] = new ItemStack(aitem[i]);
        }

        return a(aitemstack);
    }

    public static RecipeItemStack a(ItemStack... aitemstack) {
        if (aitemstack.length > 0) {
            ItemStack[] aitemstack1 = aitemstack;
            int i = aitemstack.length;

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = aitemstack1[j];

                if (!itemstack.isEmpty()) {
                    return new RecipeItemStack(aitemstack);
                }
            }
        }

        return RecipeItemStack.a;
    }

    public boolean apply(@Nullable Object object) {
        return this.a((ItemStack) object);
    }

    RecipeItemStack(ItemStack[] aitemstack, Object object) {
        this(aitemstack);
    }
}
