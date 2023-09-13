package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.IRegistry;
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
        this.recipes = packetdataserializer.a(PacketPlayOutRecipeUpdate::b);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Collection) this.recipes, PacketPlayOutRecipeUpdate::a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public List<IRecipe<?>> b() {
        return this.recipes;
    }

    public static IRecipe<?> b(PacketDataSerializer packetdataserializer) {
        MinecraftKey minecraftkey = packetdataserializer.q();
        MinecraftKey minecraftkey1 = packetdataserializer.q();

        return ((RecipeSerializer) IRegistry.RECIPE_SERIALIZER.getOptional(minecraftkey).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown recipe serializer " + minecraftkey);
        })).a(minecraftkey1, packetdataserializer);
    }

    public static <T extends IRecipe<?>> void a(PacketDataSerializer packetdataserializer, T t0) {
        packetdataserializer.a(IRegistry.RECIPE_SERIALIZER.getKey(t0.getRecipeSerializer()));
        packetdataserializer.a(t0.getKey());
        t0.getRecipeSerializer().a(packetdataserializer, t0);
    }
}
