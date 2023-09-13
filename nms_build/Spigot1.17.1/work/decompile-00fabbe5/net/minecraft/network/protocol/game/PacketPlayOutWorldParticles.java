package net.minecraft.network.protocol.game;

import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutWorldParticles implements Packet<PacketListenerPlayOut> {

    private final double x;
    private final double y;
    private final double z;
    private final float xDist;
    private final float yDist;
    private final float zDist;
    private final float maxSpeed;
    private final int count;
    private final boolean overrideLimiter;
    private final ParticleParam particle;

    public <T extends ParticleParam> PacketPlayOutWorldParticles(T t0, boolean flag, double d0, double d1, double d2, float f, float f1, float f2, float f3, int i) {
        this.particle = t0;
        this.overrideLimiter = flag;
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.xDist = f;
        this.yDist = f1;
        this.zDist = f2;
        this.maxSpeed = f3;
        this.count = i;
    }

    public PacketPlayOutWorldParticles(PacketDataSerializer packetdataserializer) {
        Particle<?> particle = (Particle) IRegistry.PARTICLE_TYPE.fromId(packetdataserializer.readInt());

        if (particle == null) {
            particle = Particles.BARRIER;
        }

        this.overrideLimiter = packetdataserializer.readBoolean();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.xDist = packetdataserializer.readFloat();
        this.yDist = packetdataserializer.readFloat();
        this.zDist = packetdataserializer.readFloat();
        this.maxSpeed = packetdataserializer.readFloat();
        this.count = packetdataserializer.readInt();
        this.particle = this.a(packetdataserializer, (Particle) particle);
    }

    private <T extends ParticleParam> T a(PacketDataSerializer packetdataserializer, Particle<T> particle) {
        return particle.d().b(particle, packetdataserializer);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(IRegistry.PARTICLE_TYPE.getId(this.particle.getParticle()));
        packetdataserializer.writeBoolean(this.overrideLimiter);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.xDist);
        packetdataserializer.writeFloat(this.yDist);
        packetdataserializer.writeFloat(this.zDist);
        packetdataserializer.writeFloat(this.maxSpeed);
        packetdataserializer.writeInt(this.count);
        this.particle.a(packetdataserializer);
    }

    public boolean b() {
        return this.overrideLimiter;
    }

    public double c() {
        return this.x;
    }

    public double d() {
        return this.y;
    }

    public double e() {
        return this.z;
    }

    public float f() {
        return this.xDist;
    }

    public float g() {
        return this.yDist;
    }

    public float h() {
        return this.zDist;
    }

    public float i() {
        return this.maxSpeed;
    }

    public int j() {
        return this.count;
    }

    public ParticleParam k() {
        return this.particle;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
