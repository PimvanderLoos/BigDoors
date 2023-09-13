package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook extends Item {

    private static final Logger a = LogManager.getLogger();

    public ItemKnowledgeBook() {
        this.d(1);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (!entityhuman.abilities.canInstantlyBuild) {
            entityhuman.a(enumhand, ItemStack.a);
        }

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Recipes", 9)) {
            if (!world.isClientSide) {
                NBTTagList nbttaglist = nbttagcompound.getList("Recipes", 8);
                ArrayList arraylist = Lists.newArrayList();

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);
                    IRecipe irecipe = CraftingManager.a(new MinecraftKey(s));

                    if (irecipe == null) {
                        ItemKnowledgeBook.a.error("Invalid recipe: " + s);
                        return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
                    }

                    arraylist.add(irecipe);
                }

                entityhuman.a((List) arraylist);
                entityhuman.b(StatisticList.b((Item) this));
            }

            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            ItemKnowledgeBook.a.error("Tag not valid: " + nbttagcompound);
            return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
        }
    }
}
