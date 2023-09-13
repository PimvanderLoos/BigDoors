package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PacketPlayOutRecipeUpdate implements Packet<PacketListenerPlayOut> {

    private final List<IRecipe<?>> recipes;

    public PacketPlayOutRecipeUpdate(Collection<IRecipe<?>> collection) {
        this.recipes = Lists.newArrayList(collection);
    }

    public PacketPlayOutRecipeUpdate(PacketDataSerializer packetdataserializer) {
        this.recipes = packetdataserializer.readList(PacketPlayOutRecipeUpdate::fromNetwork);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeCollection(this.recipes, PacketPlayOutRecipeUpdate::toNetwork);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateRecipes(this);
    }

    public List<IRecipe<?>> getRecipes() {
        return this.recipes;
    }

    public static IRecipe<?> fromNetwork(PacketDataSerializer packetdataserializer) {
        MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();
        MinecraftKey minecraftkey1 = packetdataserializer.readResourceLocation();

        return ((RecipeSerializer) BuiltInRegistries.RECIPE_SERIALIZER.getOptional(minecraftkey).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown recipe serializer " + minecraftkey);
        })).fromNetwork(minecraftkey1, packetdataserializer);
    }

    public static <T extends IRecipe<?>> void toNetwork(PacketDataSerializer packetdataserializer, T t0) {
        packetdataserializer.writeResourceLocation(BuiltInRegistries.RECIPE_SERIALIZER.getKey(t0.getSerializer()));
        packetdataserializer.writeResourceLocation(t0.getId());
        t0.getSerializer().toNetwork(packetdataserializer, t0);
    }
}
