package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ParticleParamBlock implements ParticleParam {

    public static final ParticleParam.a<ParticleParamBlock> a = new ParticleParam.a<ParticleParamBlock>() {
        @Override
        public ParticleParamBlock b(Particle<ParticleParamBlock> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            return new ParticleParamBlock(particle, (new ArgumentBlock(stringreader, false)).a(false).getBlockData());
        }

        @Override
        public ParticleParamBlock b(Particle<ParticleParamBlock> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamBlock(particle, (IBlockData) Block.REGISTRY_ID.fromId(packetdataserializer.i()));
        }
    };
    private final Particle<ParticleParamBlock> b;
    private final IBlockData c;

    public static Codec<ParticleParamBlock> a(Particle<ParticleParamBlock> particle) {
        return IBlockData.b.xmap((iblockdata) -> {
            return new ParticleParamBlock(particle, iblockdata);
        }, (particleparamblock) -> {
            return particleparamblock.c;
        });
    }

    public ParticleParamBlock(Particle<ParticleParamBlock> particle, IBlockData iblockdata) {
        this.b = particle;
        this.c = iblockdata;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(Block.REGISTRY_ID.getId(this.c));
    }

    @Override
    public String a() {
        return IRegistry.PARTICLE_TYPE.getKey(this.getParticle()) + " " + ArgumentBlock.a(this.c);
    }

    @Override
    public Particle<ParticleParamBlock> getParticle() {
        return this.b;
    }
}
