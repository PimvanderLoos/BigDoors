package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.level.World;
import org.slf4j.Logger;

public class ItemKnowledgeBook extends Item {

    private static final String RECIPE_TAG = "Recipes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ItemKnowledgeBook(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (!entityhuman.getAbilities().instabuild) {
            entityhuman.setItemInHand(enumhand, ItemStack.EMPTY);
        }

        if (nbttagcompound != null && nbttagcompound.contains("Recipes", 9)) {
            if (!world.isClientSide) {
                NBTTagList nbttaglist = nbttagcompound.getList("Recipes", 8);
                List<IRecipe<?>> list = Lists.newArrayList();
                CraftingManager craftingmanager = world.getServer().getRecipeManager();

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);
                    Optional<? extends IRecipe<?>> optional = craftingmanager.byKey(new MinecraftKey(s));

                    if (!optional.isPresent()) {
                        ItemKnowledgeBook.LOGGER.error("Invalid recipe: {}", s);
                        return InteractionResultWrapper.fail(itemstack);
                    }

                    list.add((IRecipe) optional.get());
                }

                entityhuman.awardRecipes(list);
                entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            }

            return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
        } else {
            ItemKnowledgeBook.LOGGER.error("Tag not valid: {}", nbttagcompound);
            return InteractionResultWrapper.fail(itemstack);
        }
    }
}
