package net.minecraft.network.protocol.game;

import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.registries.BuiltInRegistries;
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
        Particle<?> particle = (Particle) packetdataserializer.readById(BuiltInRegistries.PARTICLE_TYPE);

        this.overrideLimiter = packetdataserializer.readBoolean();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.xDist = packetdataserializer.readFloat();
        this.yDist = packetdataserializer.readFloat();
        this.zDist = packetdataserializer.readFloat();
        this.maxSpeed = packetdataserializer.readFloat();
        this.count = packetdataserializer.readInt();
        this.particle = this.readParticle(packetdataserializer, particle);
    }

    private <T extends ParticleParam> T readParticle(PacketDataSerializer packetdataserializer, Particle<T> particle) {
        return particle.getDeserializer().fromNetwork(particle, packetdataserializer);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeId(BuiltInRegistries.PARTICLE_TYPE, this.particle.getType());
        packetdataserializer.writeBoolean(this.overrideLimiter);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.xDist);
        packetdataserializer.writeFloat(this.yDist);
        packetdataserializer.writeFloat(this.zDist);
        packetdataserializer.writeFloat(this.maxSpeed);
        packetdataserializer.writeInt(this.count);
        this.particle.writeToNetwork(packetdataserializer);
    }

    public boolean isOverrideLimiter() {
        return this.overrideLimiter;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getXDist() {
        return this.xDist;
    }

    public float getYDist() {
        return this.yDist;
    }

    public float getZDist() {
        return this.zDist;
    }

    public float getMaxSpeed() {
        return this.maxSpeed;
    }

    public int getCount() {
        return this.count;
    }

    public ParticleParam getParticle() {
        return this.particle;
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleParticleEvent(this);
    }
}
