package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutWorldParticles implements Packet<PacketListenerPlayOut> {

    private double a;
    private double b;
    private double c;
    private float d;
    private float e;
    private float f;
    private float g;
    private int h;
    private boolean i;
    private ParticleParam j;

    public PacketPlayOutWorldParticles() {}

    public <T extends ParticleParam> PacketPlayOutWorldParticles(T t0, boolean flag, double d0, double d1, double d2, float f, float f1, float f2, float f3, int i) {
        this.j = t0;
        this.i = flag;
        this.a = d0;
        this.b = d1;
        this.c = d2;
        this.d = f;
        this.e = f1;
        this.f = f2;
        this.g = f3;
        this.h = i;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        Particle<?> particle = (Particle) IRegistry.PARTICLE_TYPE.fromId(packetdataserializer.readInt());

        if (particle == null) {
            particle = Particles.BARRIER;
        }

        this.i = packetdataserializer.readBoolean();
        this.a = packetdataserializer.readDouble();
        this.b = packetdataserializer.readDouble();
        this.c = packetdataserializer.readDouble();
        this.d = packetdataserializer.readFloat();
        this.e = packetdataserializer.readFloat();
        this.f = packetdataserializer.readFloat();
        this.g = packetdataserializer.readFloat();
        this.h = packetdataserializer.readInt();
        this.j = this.a(packetdataserializer, (Particle) particle);
    }

    private <T extends ParticleParam> T a(PacketDataSerializer packetdataserializer, Particle<T> particle) {
        return particle.d().b(particle, packetdataserializer);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(IRegistry.PARTICLE_TYPE.a((Object) this.j.getParticle()));
        packetdataserializer.writeBoolean(this.i);
        packetdataserializer.writeDouble(this.a);
        packetdataserializer.writeDouble(this.b);
        packetdataserializer.writeDouble(this.c);
        packetdataserializer.writeFloat(this.d);
        packetdataserializer.writeFloat(this.e);
        packetdataserializer.writeFloat(this.f);
        packetdataserializer.writeFloat(this.g);
        packetdataserializer.writeInt(this.h);
        this.j.a(packetdataserializer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
