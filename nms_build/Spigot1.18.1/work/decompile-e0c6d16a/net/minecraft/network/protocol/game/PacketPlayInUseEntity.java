package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;

public class PacketPlayInUseEntity implements Packet<PacketListenerPlayIn> {

    private final int entityId;
    private final PacketPlayInUseEntity.EnumEntityUseAction action;
    private final boolean usingSecondaryAction;
    static final PacketPlayInUseEntity.EnumEntityUseAction ATTACK_ACTION = new PacketPlayInUseEntity.EnumEntityUseAction() {
        @Override
        public PacketPlayInUseEntity.b getType() {
            return PacketPlayInUseEntity.b.ATTACK;
        }

        @Override
        public void dispatch(PacketPlayInUseEntity.c packetplayinuseentity_c) {
            packetplayinuseentity_c.onAttack();
        }

        @Override
        public void write(PacketDataSerializer packetdataserializer) {}
    };

    private PacketPlayInUseEntity(int i, boolean flag, PacketPlayInUseEntity.EnumEntityUseAction packetplayinuseentity_enumentityuseaction) {
        this.entityId = i;
        this.action = packetplayinuseentity_enumentityuseaction;
        this.usingSecondaryAction = flag;
    }

    public static PacketPlayInUseEntity createAttackPacket(Entity entity, boolean flag) {
        return new PacketPlayInUseEntity(entity.getId(), flag, PacketPlayInUseEntity.ATTACK_ACTION);
    }

    public static PacketPlayInUseEntity createInteractionPacket(Entity entity, boolean flag, EnumHand enumhand) {
        return new PacketPlayInUseEntity(entity.getId(), flag, new PacketPlayInUseEntity.d(enumhand));
    }

    public static PacketPlayInUseEntity createInteractionPacket(Entity entity, boolean flag, EnumHand enumhand, Vec3D vec3d) {
        return new PacketPlayInUseEntity(entity.getId(), flag, new PacketPlayInUseEntity.e(enumhand, vec3d));
    }

    public PacketPlayInUseEntity(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readVarInt();
        PacketPlayInUseEntity.b packetplayinuseentity_b = (PacketPlayInUseEntity.b) packetdataserializer.readEnum(PacketPlayInUseEntity.b.class);

        this.action = (PacketPlayInUseEntity.EnumEntityUseAction) packetplayinuseentity_b.reader.apply(packetdataserializer);
        this.usingSecondaryAction = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeEnum(this.action.getType());
        this.action.write(packetdataserializer);
        packetdataserializer.writeBoolean(this.usingSecondaryAction);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleInteract(this);
    }

    @Nullable
    public Entity getTarget(WorldServer worldserver) {
        return worldserver.getEntityOrPart(this.entityId);
    }

    public boolean isUsingSecondaryAction() {
        return this.usingSecondaryAction;
    }

    public void dispatch(PacketPlayInUseEntity.c packetplayinuseentity_c) {
        this.action.dispatch(packetplayinuseentity_c);
    }

    private interface EnumEntityUseAction {

        PacketPlayInUseEntity.b getType();

        void dispatch(PacketPlayInUseEntity.c packetplayinuseentity_c);

        void write(PacketDataSerializer packetdataserializer);
    }

    private static class d implements PacketPlayInUseEntity.EnumEntityUseAction {

        private final EnumHand hand;

        d(EnumHand enumhand) {
            this.hand = enumhand;
        }

        private d(PacketDataSerializer packetdataserializer) {
            this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
        }

        @Override
        public PacketPlayInUseEntity.b getType() {
            return PacketPlayInUseEntity.b.INTERACT;
        }

        @Override
        public void dispatch(PacketPlayInUseEntity.c packetplayinuseentity_c) {
            packetplayinuseentity_c.onInteraction(this.hand);
        }

        @Override
        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeEnum(this.hand);
        }
    }

    private static class e implements PacketPlayInUseEntity.EnumEntityUseAction {

        private final EnumHand hand;
        private final Vec3D location;

        e(EnumHand enumhand, Vec3D vec3d) {
            this.hand = enumhand;
            this.location = vec3d;
        }

        private e(PacketDataSerializer packetdataserializer) {
            this.location = new Vec3D((double) packetdataserializer.readFloat(), (double) packetdataserializer.readFloat(), (double) packetdataserializer.readFloat());
            this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
        }

        @Override
        public PacketPlayInUseEntity.b getType() {
            return PacketPlayInUseEntity.b.INTERACT_AT;
        }

        @Override
        public void dispatch(PacketPlayInUseEntity.c packetplayinuseentity_c) {
            packetplayinuseentity_c.onInteraction(this.hand, this.location);
        }

        @Override
        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeFloat((float) this.location.x);
            packetdataserializer.writeFloat((float) this.location.y);
            packetdataserializer.writeFloat((float) this.location.z);
            packetdataserializer.writeEnum(this.hand);
        }
    }

    private static enum b {

        INTERACT(PacketPlayInUseEntity.d::new), ATTACK((packetdataserializer) -> {
            return PacketPlayInUseEntity.ATTACK_ACTION;
        }), INTERACT_AT(PacketPlayInUseEntity.e::new);

        final Function<PacketDataSerializer, PacketPlayInUseEntity.EnumEntityUseAction> reader;

        private b(Function function) {
            this.reader = function;
        }
    }

    public interface c {

        void onInteraction(EnumHand enumhand);

        void onInteraction(EnumHand enumhand, Vec3D vec3d);

        void onAttack();
    }
}
