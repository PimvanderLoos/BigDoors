package net.minecraft.server;

public interface IDispenseBehavior {

    IDispenseBehavior NONE = (isourceblock, itemstack) -> {
        return itemstack;
    };

    ItemStack dispense(ISourceBlock isourceblock, ItemStack itemstack);
}
