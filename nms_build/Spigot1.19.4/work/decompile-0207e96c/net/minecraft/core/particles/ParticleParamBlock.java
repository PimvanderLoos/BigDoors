package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ParticleParamBlock implements ParticleParam {

    public static final ParticleParam.a<ParticleParamBlock> DESERIALIZER = new ParticleParam.a<ParticleParamBlock>() {
        @Override
        public ParticleParamBlock fromCommand(Particle<ParticleParamBlock> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            return new ParticleParamBlock(particle, ArgumentBlock.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), stringreader, false).blockState());
        }

        @Override
        public ParticleParamBlock fromNetwork(Particle<ParticleParamBlock> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamBlock(particle, (IBlockData) packetdataserializer.readById(Block.BLOCK_STATE_REGISTRY));
        }
    };
    private final Particle<ParticleParamBlock> type;
    private final IBlockData state;

    public static Codec<ParticleParamBlock> codec(Particle<ParticleParamBlock> particle) {
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
    public void writeToNetwork(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeId(Block.BLOCK_STATE_REGISTRY, this.state);
    }

    @Override
    public String writeToString() {
        MinecraftKey minecraftkey = BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType());

        return minecraftkey + " " + ArgumentBlock.serialize(this.state);
    }

    @Override
    public Particle<ParticleParamBlock> getType() {
        return this.type;
    }

    public IBlockData getState() {
        return this.state;
    }
}
