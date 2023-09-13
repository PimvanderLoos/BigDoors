package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.item.ArgumentParserItemStack;
import net.minecraft.commands.arguments.item.ArgumentPredicateItemStack;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.item.ItemStack;

public class ParticleParamItem implements ParticleParam {

    public static final ParticleParam.a<ParticleParamItem> a = new ParticleParam.a<ParticleParamItem>() {
        @Override
        public ParticleParamItem b(Particle<ParticleParamItem> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            ArgumentParserItemStack argumentparseritemstack = (new ArgumentParserItemStack(stringreader, false)).h();
            ItemStack itemstack = (new ArgumentPredicateItemStack(argumentparseritemstack.b(), argumentparseritemstack.c())).a(1, false);

            return new ParticleParamItem(particle, itemstack);
        }

        @Override
        public ParticleParamItem b(Particle<ParticleParamItem> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamItem(particle, packetdataserializer.n());
        }
    };
    private final Particle<ParticleParamItem> b;
    private final ItemStack c;

    public static Codec<ParticleParamItem> a(Particle<ParticleParamItem> particle) {
        return ItemStack.a.xmap((itemstack) -> {
            return new ParticleParamItem(particle, itemstack);
        }, (particleparamitem) -> {
            return particleparamitem.c;
        });
    }

    public ParticleParamItem(Particle<ParticleParamItem> particle, ItemStack itemstack) {
        this.b = particle;
        this.c = itemstack;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.c);
    }

    @Override
    public String a() {
        return IRegistry.PARTICLE_TYPE.getKey(this.getParticle()) + " " + (new ArgumentPredicateItemStack(this.c.getItem(), this.c.getTag())).c();
    }

    @Override
    public Particle<ParticleParamItem> getParticle() {
        return this.b;
    }
}
