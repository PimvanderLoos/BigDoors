package net.minecraft.world.entity;

public interface VariantHolder<T> {

    void setVariant(T t0);

    T getVariant();
}
