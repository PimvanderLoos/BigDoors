package net.minecraft.server;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface LootItemFunction extends LootItemUser, BiFunction<ItemStack, LootTableInfo, ItemStack> {

    LootItemFunctionType b();

    static Consumer<ItemStack> a(BiFunction<ItemStack, LootTableInfo, ItemStack> bifunction, Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        return (itemstack) -> {
            consumer.accept(bifunction.apply(itemstack, loottableinfo));
        };
    }

    public interface a {

        LootItemFunction b();
    }
}
