package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArgumentPredicateItemStack implements Predicate<ItemStack> {

    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("arguments.item.overstacked", object, object1);
    });
    private final Holder<Item> item;
    @Nullable
    private final NBTTagCompound tag;

    public ArgumentPredicateItemStack(Holder<Item> holder, @Nullable NBTTagCompound nbttagcompound) {
        this.item = holder;
        this.tag = nbttagcompound;
    }

    public Item getItem() {
        return (Item) this.item.value();
    }

    public boolean test(ItemStack itemstack) {
        return itemstack.is(this.item) && GameProfileSerializer.compareNbt(this.tag, itemstack.getTag(), true);
    }

    public ItemStack createItemStack(int i, boolean flag) throws CommandSyntaxException {
        ItemStack itemstack = new ItemStack(this.item, i);

        if (this.tag != null) {
            itemstack.setTag(this.tag);
        }

        if (flag && i > itemstack.getMaxStackSize()) {
            throw ArgumentPredicateItemStack.ERROR_STACK_TOO_BIG.create(this.getItemName(), itemstack.getMaxStackSize());
        } else {
            return itemstack;
        }
    }

    public String serialize() {
        StringBuilder stringbuilder = new StringBuilder(this.getItemName());

        if (this.tag != null) {
            stringbuilder.append(this.tag);
        }

        return stringbuilder.toString();
    }

    private String getItemName() {
        return this.item.unwrapKey().map(ResourceKey::location).orElseGet(() -> {
            return "unknown[" + this.item + "]";
        }).toString();
    }
}
