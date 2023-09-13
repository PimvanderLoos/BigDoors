package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.item.ArgumentParserItemStack;
import net.minecraft.commands.arguments.item.ArgumentPredicateItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;

public class ParticleParamItem implements ParticleParam {

    public static final ParticleParam.a<ParticleParamItem> DESERIALIZER = new ParticleParam.a<ParticleParamItem>() {
        @Override
        public ParticleParamItem fromCommand(Particle<ParticleParamItem> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            ArgumentParserItemStack.a argumentparseritemstack_a = ArgumentParserItemStack.parseForItem(BuiltInRegistries.ITEM.asLookup(), stringreader);
            ItemStack itemstack = (new ArgumentPredicateItemStack(argumentparseritemstack_a.item(), argumentparseritemstack_a.nbt())).createItemStack(1, false);

            return new ParticleParamItem(particle, itemstack);
        }

        @Override
        public ParticleParamItem fromNetwork(Particle<ParticleParamItem> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamItem(particle, packetdataserializer.readItem());
        }
    };
    private final Particle<ParticleParamItem> type;
    private final ItemStack itemStack;

    public static Codec<ParticleParamItem> codec(Particle<ParticleParamItem> particle) {
        return ItemStack.CODEC.xmap((itemstack) -> {
            return new ParticleParamItem(particle, itemstack);
        }, (particleparamitem) -> {
            return particleparamitem.itemStack;
        });
    }

    public ParticleParamItem(Particle<ParticleParamItem> particle, ItemStack itemstack) {
        this.type = particle;
        this.itemStack = itemstack;
    }

    @Override
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeItem(this.itemStack);
    }

    @Override
    public String writeToString() {
        MinecraftKey minecraftkey = BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType());

        return minecraftkey + " " + (new ArgumentPredicateItemStack(this.itemStack.getItemHolder(), this.itemStack.getTag())).serialize();
    }

    @Override
    public Particle<ParticleParamItem> getType() {
        return this.type;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}
