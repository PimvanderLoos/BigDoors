package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {

    private final List<ItemCombinerMenuSlotDefinition.b> slots;
    private final ItemCombinerMenuSlotDefinition.b resultSlot;

    ItemCombinerMenuSlotDefinition(List<ItemCombinerMenuSlotDefinition.b> list, ItemCombinerMenuSlotDefinition.b itemcombinermenuslotdefinition_b) {
        if (!list.isEmpty() && !itemcombinermenuslotdefinition_b.equals(ItemCombinerMenuSlotDefinition.b.EMPTY)) {
            this.slots = list;
            this.resultSlot = itemcombinermenuslotdefinition_b;
        } else {
            throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
        }
    }

    public static ItemCombinerMenuSlotDefinition.a create() {
        return new ItemCombinerMenuSlotDefinition.a();
    }

    public boolean hasSlot(int i) {
        return this.slots.size() >= i;
    }

    public ItemCombinerMenuSlotDefinition.b getSlot(int i) {
        return (ItemCombinerMenuSlotDefinition.b) this.slots.get(i);
    }

    public ItemCombinerMenuSlotDefinition.b getResultSlot() {
        return this.resultSlot;
    }

    public List<ItemCombinerMenuSlotDefinition.b> getSlots() {
        return this.slots;
    }

    public int getNumOfInputSlots() {
        return this.slots.size();
    }

    public int getResultSlotIndex() {
        return this.getNumOfInputSlots();
    }

    public List<Integer> getInputSlotIndexes() {
        return (List) this.slots.stream().map(ItemCombinerMenuSlotDefinition.b::slotIndex).collect(Collectors.toList());
    }

    public static record b(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {

        static final ItemCombinerMenuSlotDefinition.b EMPTY = new ItemCombinerMenuSlotDefinition.b(0, 0, 0, (itemstack) -> {
            return true;
        });
    }

    public static class a {

        private final List<ItemCombinerMenuSlotDefinition.b> slots = new ArrayList();
        private ItemCombinerMenuSlotDefinition.b resultSlot;

        public a() {
            this.resultSlot = ItemCombinerMenuSlotDefinition.b.EMPTY;
        }

        public ItemCombinerMenuSlotDefinition.a withSlot(int i, int j, int k, Predicate<ItemStack> predicate) {
            this.slots.add(new ItemCombinerMenuSlotDefinition.b(i, j, k, predicate));
            return this;
        }

        public ItemCombinerMenuSlotDefinition.a withResultSlot(int i, int j, int k) {
            this.resultSlot = new ItemCombinerMenuSlotDefinition.b(i, j, k, (itemstack) -> {
                return false;
            });
            return this;
        }

        public ItemCombinerMenuSlotDefinition build() {
            return new ItemCombinerMenuSlotDefinition(this.slots, this.resultSlot);
        }
    }
}
