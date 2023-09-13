package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.ShapedRecipes;

public interface AutoRecipeAbstract<T> {

    default void placeRecipe(int i, int j, int k, IRecipe<?> irecipe, Iterator<T> iterator, int l) {
        int i1 = i;
        int j1 = j;

        if (irecipe instanceof ShapedRecipes) {
            ShapedRecipes shapedrecipes = (ShapedRecipes) irecipe;

            i1 = shapedrecipes.getWidth();
            j1 = shapedrecipes.getHeight();
        }

        int k1 = 0;
        int l1 = 0;

        while (l1 < j) {
            if (k1 == k) {
                ++k1;
            }

            boolean flag = (float) j1 < (float) j / 2.0F;
            int i2 = MathHelper.floor((float) j / 2.0F - (float) j1 / 2.0F);

            if (flag && i2 > l1) {
                k1 += i;
                ++l1;
            }

            int j2 = 0;

            while (true) {
                if (j2 < i) {
                    label76:
                    {
                        if (!iterator.hasNext()) {
                            return;
                        }

                        flag = (float) i1 < (float) i / 2.0F;
                        i2 = MathHelper.floor((float) i / 2.0F - (float) i1 / 2.0F);
                        int k2 = i1;
                        boolean flag1 = j2 < i1;

                        if (flag) {
                            k2 = i2 + i1;
                            flag1 = i2 <= j2 && j2 < i2 + i1;
                        }

                        if (flag1) {
                            this.addItemToSlot(iterator, k1, l, l1, j2);
                        } else if (k2 == j2) {
                            k1 += i - j2;
                            break label76;
                        }

                        ++k1;
                        ++j2;
                        continue;
                    }
                }

                ++l1;
                break;
            }
        }

    }

    void addItemToSlot(Iterator<T> iterator, int i, int j, int k, int l);
}
