package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ParticleParamBlock implements ParticleParam {

    public static final ParticleParam.a<ParticleParamBlock> DESERIALIZER = new ParticleParam.a<ParticleParamBlock>() {
        @Override
        public ParticleParamBlock b(Particle<ParticleParamBlock> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            return new ParticleParamBlock(particle, (new ArgumentBlock(stringreader, false)).a(false).getBlockData());
        }

        @Override
        public ParticleParamBlock b(Particle<ParticleParamBlock> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamBlock(particle, (IBlockData) Block.BLOCK_STATE_REGISTRY.fromId(packetdataserializer.j()));
        }
    };
    private final Particle<ParticleParamBlock> type;
    private final IBlockData state;

    public static Codec<ParticleParamBlock> a(Particle<ParticleParamBlock> particle) {
        return IBlockData.CODEC.xmap((iblockdata) -> {
            return new ParticleParamBlock(particle, iblockdata);
        }, (particleparamblock) -> {
            return particleparamblock.state;
        });
    }

    public ParticleParamBlock(Particle<ParticleParamBlock> particle, IBlockData iblockdata) {
        this.type = particle;
        this.state = iblockdata;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(Block.BLOCK_STATE_REGISTRY.getId(this.state));
    }

    @Override
    public String a() {
        MinecraftKey minecraftkey = IRegistry.PARTICLE_TYPE.getKey(this.getParticle());

        return minecraftkey + " " + ArgumentBlock.a(this.state);
    }

    @Override
    public Particle<ParticleParamBlock> getParticle() {
        return this.type;
    }

    public IBlockData c() {
        return this.state;
    }
}
