package net.minecraft.tags;

import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidType;

public interface ITagRegistry {

    ITagRegistry a = a(Tags.c(), Tags.c(), Tags.c(), Tags.c());

    Tags<Block> getBlockTags();

    Tags<Item> getItemTags();

    Tags<FluidType> getFluidTags();

    Tags<EntityTypes<?>> getEntityTags();

    default void bind() {
        TagStatic.a(this);
        Blocks.a();
    }

    default void a(PacketDataSerializer packetdataserializer) {
        this.getBlockTags().a(packetdataserializer, IRegistry.BLOCK);
        this.getItemTags().a(packetdataserializer, IRegistry.ITEM);
        this.getFluidTags().a(packetdataserializer, IRegistry.FLUID);
        this.getEntityTags().a(packetdataserializer, IRegistry.ENTITY_TYPE);
    }

    static ITagRegistry b(PacketDataSerializer packetdataserializer) {
        Tags<Block> tags = Tags.a(packetdataserializer, (IRegistry) IRegistry.BLOCK);
        Tags<Item> tags1 = Tags.a(packetdataserializer, (IRegistry) IRegistry.ITEM);
        Tags<FluidType> tags2 = Tags.a(packetdataserializer, (IRegistry) IRegistry.FLUID);
        Tags<EntityTypes<?>> tags3 = Tags.a(packetdataserializer, (IRegistry) IRegistry.ENTITY_TYPE);

        return a(tags, tags1, tags2, tags3);
    }

    static ITagRegistry a(final Tags<Block> tags, final Tags<Item> tags1, final Tags<FluidType> tags2, final Tags<EntityTypes<?>> tags3) {
        return new ITagRegistry() {
            @Override
            public Tags<Block> getBlockTags() {
                return tags;
            }

            @Override
            public Tags<Item> getItemTags() {
                return tags1;
            }

            @Override
            public Tags<FluidType> getFluidTags() {
                return tags2;
            }

            @Override
            public Tags<EntityTypes<?>> getEntityTags() {
                return tags3;
            }
        };
    }
}
