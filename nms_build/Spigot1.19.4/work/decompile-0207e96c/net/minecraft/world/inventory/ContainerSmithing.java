package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ContainerSmithing extends ContainerAnvilAbstract {

    public static final int TEMPLATE_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITIONAL_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
    public static final int BASE_SLOT_X_PLACEMENT = 26;
    public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
    private static final int RESULT_SLOT_X_PLACEMENT = 98;
    public static final int SLOT_Y_PLACEMENT = 48;
    private final World level;
    @Nullable
    private SmithingRecipe selectedRecipe;
    private final List<SmithingRecipe> recipes;

    public ContainerSmithing(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerSmithing(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.SMITHING, i, playerinventory, containeraccess);
        this.level = playerinventory.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(Recipes.SMITHING);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 8, 48, (itemstack) -> {
            return this.recipes.stream().anyMatch((smithingrecipe) -> {
                return smithingrecipe.isTemplateIngredient(itemstack);
            });
        }).withSlot(1, 26, 48, (itemstack) -> {
            return this.recipes.stream().anyMatch((smithingrecipe) -> {
                return smithingrecipe.isBaseIngredient(itemstack) && smithingrecipe.isTemplateIngredient(((Slot) this.slots.get(0)).getItem());
            });
        }).withSlot(2, 44, 48, (itemstack) -> {
            return this.recipes.stream().anyMatch((smithingrecipe) -> {
                return smithingrecipe.isAdditionIngredient(itemstack) && smithingrecipe.isTemplateIngredient(((Slot) this.slots.get(0)).getItem());
            });
        }).withResultSlot(3, 98, 48).build();
    }

    @Override
    protected boolean isValidBlock(IBlockData iblockdata) {
        return iblockdata.is(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean mayPickup(EntityHuman entityhuman, boolean flag) {
        return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
    }

    @Override
    protected void onTake(EntityHuman entityhuman, ItemStack itemstack) {
        itemstack.onCraftedBy(entityhuman.level, entityhuman, itemstack.getCount());
        this.resultSlots.awardUsedRecipes(entityhuman);
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.shrinkStackInSlot(2);
        this.access.execute((world, blockposition) -> {
            world.levelEvent(1044, blockposition, 0);
        });
    }

    private void shrinkStackInSlot(int i) {
        ItemStack itemstack = this.inputSlots.getItem(i);

        itemstack.shrink(1);
        this.inputSlots.setItem(i, itemstack);
    }

    @Override
    public void createResult() {
        List<SmithingRecipe> list = this.level.getRecipeManager().getRecipesFor(Recipes.SMITHING, this.inputSlots, this.level);

        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            SmithingRecipe smithingrecipe = (SmithingRecipe) list.get(0);
            ItemStack itemstack = smithingrecipe.assemble(this.inputSlots, this.level.registryAccess());

            if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
                this.selectedRecipe = smithingrecipe;
                this.resultSlots.setRecipeUsed(smithingrecipe);
                this.resultSlots.setItem(0, itemstack);
            }
        }

    }

    @Override
    public int getSlotToQuickMoveTo(ItemStack itemstack) {
        return (Integer) ((Optional) this.recipes.stream().map((smithingrecipe) -> {
            return findSlotMatchingIngredient(smithingrecipe, itemstack);
        }).filter(Optional::isPresent).findFirst().orElse(Optional.of(0))).get();
    }

    private static Optional<Integer> findSlotMatchingIngredient(SmithingRecipe smithingrecipe, ItemStack itemstack) {
        return smithingrecipe.isTemplateIngredient(itemstack) ? Optional.of(0) : (smithingrecipe.isBaseIngredient(itemstack) ? Optional.of(1) : (smithingrecipe.isAdditionIngredient(itemstack) ? Optional.of(2) : Optional.empty()));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemstack, slot);
    }

    @Override
    public boolean canMoveIntoInputSlots(ItemStack itemstack) {
        return this.recipes.stream().map((smithingrecipe) -> {
            return findSlotMatchingIngredient(smithingrecipe, itemstack);
        }).anyMatch(Optional::isPresent);
    }
}
