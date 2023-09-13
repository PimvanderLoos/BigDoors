package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ParticleParamBlock implements ParticleParam {

    public static final ParticleParam.a<ParticleParamBlock> a = new ParticleParam.a() {
        public ParticleParamBlock a(Particle<ParticleParamBlock> particle, StringReader stringreader) throws CommandSyntaxException {
            stringreader.expect(' ');
            return new ParticleParamBlock(particle, (new ArgumentBlock(stringreader, false)).a(false).b());
        }

        public ParticleParamBlock a(Particle<ParticleParamBlock> particle, PacketDataSerializer packetdataserializer) {
            return new ParticleParamBlock(particle, (IBlockData) Block.REGISTRY_ID.fromId(packetdataserializer.g()));
        }

        public ParticleParam b(Particle particle, PacketDataSerializer packetdataserializer) {
            return this.a(particle, packetdataserializer);
        }

        public ParticleParam b(Particle particle, StringReader stringreader) throws CommandSyntaxException {
            return this.a(particle, stringreader);
        }
    };
    private final Particle<ParticleParamBlock> b;
    private final IBlockData c;

    public ParticleParamBlock(Particle<ParticleParamBlock> particle, IBlockData iblockdata) {
        this.b = particle;
        this.c = iblockdata;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(Block.REGISTRY_ID.getId(this.c));
    }

    public String a() {
        return this.b().d() + " " + ArgumentBlock.a(this.c, (NBTTagCompound) null);
    }

    public Particle<ParticleParamBlock> b() {
        return this.b;
    }
}
