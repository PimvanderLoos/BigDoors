package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArgumentPredicateItemStack implements Predicate<ItemStack> {

    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("arguments.item.overstacked", new Object[]{object, object1});
    });
    private final Item item;
    @Nullable
    private final NBTTagCompound tag;

    public ArgumentPredicateItemStack(Item item, @Nullable NBTTagCompound nbttagcompound) {
        this.item = item;
        this.tag = nbttagcompound;
    }

    public Item a() {
        return this.item;
    }

    public boolean test(ItemStack itemstack) {
        return itemstack.a(this.item) && GameProfileSerializer.a(this.tag, itemstack.getTag(), true);
    }

    public ItemStack a(int i, boolean flag) throws CommandSyntaxException {
        ItemStack itemstack = new ItemStack(this.item, i);

        if (this.tag != null) {
            itemstack.setTag(this.tag);
        }

        if (flag && i > itemstack.getMaxStackSize()) {
            throw ArgumentPredicateItemStack.ERROR_STACK_TOO_BIG.create(IRegistry.ITEM.getKey(this.item), itemstack.getMaxStackSize());
        } else {
            return itemstack;
        }
    }

    public String b() {
        StringBuilder stringbuilder = new StringBuilder(IRegistry.ITEM.getId(this.item));

        if (this.tag != null) {
            stringbuilder.append(this.tag);
        }

        return stringbuilder.toString();
    }
}
