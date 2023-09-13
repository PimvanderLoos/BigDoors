package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.FurnaceRecipe;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSmelt extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogManager.getLogger();

    LootItemFunctionSmelt(LootItemCondition[] alootitemcondition) {
        super(alootitemcondition);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.FURNACE_SMELT;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            Optional<FurnaceRecipe> optional = loottableinfo.getLevel().getRecipeManager().getRecipeFor(Recipes.SMELTING, new InventorySubcontainer(new ItemStack[]{itemstack}), loottableinfo.getLevel());

            if (optional.isPresent()) {
                ItemStack itemstack1 = ((FurnaceRecipe) optional.get()).getResultItem();

                if (!itemstack1.isEmpty()) {
                    ItemStack itemstack2 = itemstack1.copy();

                    itemstack2.setCount(itemstack.getCount());
                    return itemstack2;
                }
            }

            LootItemFunctionSmelt.LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", itemstack);
            return itemstack;
        }
    }

    public static LootItemFunctionConditional.a<?> smelted() {
        return simpleBuilder(LootItemFunctionSmelt::new);
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSmelt> {

        public a() {}

        @Override
        public LootItemFunctionSmelt deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionSmelt(alootitemcondition);
        }
    }
}
