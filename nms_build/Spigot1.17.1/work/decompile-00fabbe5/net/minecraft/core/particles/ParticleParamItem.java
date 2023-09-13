package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.item.ArgumentParserItemStack;
import net.minecraft.commands.arguments.item.ArgumentPredicateItemStack;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;

public class ParticleParamItem implements ParticleParam {

    public static final ParticleParam.a<ParticleParamItem> DESERIALIZER = new ParticleParam.a<ParticleParamItem>() {
        @Override
        public ParticleParamItem b(Particle<ParticleParamItem> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            ArgumentParserItemStack argumentparseritemstack = (new ArgumentParserItemStack(stringreader, false)).g();
            ItemStack itemstack = (new ArgumentPredicateItemStack(argumentparseritemstack.a(), argumentparseritemstack.b())).a(1, false);

            return new ParticleParamItem(particle, itemstack);
        }

        @Override
        public ParticleParamItem b(Particle<ParticleParamItem> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamItem(particle, packetdataserializer.o());
        }
    };
    private final Particle<ParticleParamItem> type;
    private final ItemStack itemStack;

    public static Codec<ParticleParamItem> a(Particle<ParticleParamItem> particle) {
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
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.itemStack);
    }

    @Override
    public String a() {
        MinecraftKey minecraftkey = IRegistry.PARTICLE_TYPE.getKey(this.getParticle());

        return minecraftkey + " " + (new ArgumentPredicateItemStack(this.itemStack.getItem(), this.itemStack.getTag())).b();
    }

    @Override
    public Particle<ParticleParamItem> getParticle() {
        return this.type;
    }

    public ItemStack c() {
        return this.itemStack;
    }
}
