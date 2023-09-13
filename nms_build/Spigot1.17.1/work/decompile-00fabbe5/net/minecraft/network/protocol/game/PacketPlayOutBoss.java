package net.minecraft.network.protocol.game;

import java.util.UUID;
import java.util.function.Function;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.BossBattle;

public class PacketPlayOutBoss implements Packet<PacketListenerPlayOut> {

    private static final int FLAG_DARKEN = 1;
    private static final int FLAG_MUSIC = 2;
    private static final int FLAG_FOG = 4;
    private final UUID id;
    private final PacketPlayOutBoss.Action operation;
    static final PacketPlayOutBoss.Action REMOVE_OPERATION = new PacketPlayOutBoss.Action() {
        @Override
        public PacketPlayOutBoss.d a() {
            return PacketPlayOutBoss.d.REMOVE;
        }

        @Override
        public void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b) {
            packetplayoutboss_b.a(uuid);
        }

        @Override
        public void a(PacketDataSerializer packetdataserializer) {}
    };

    private PacketPlayOutBoss(UUID uuid, PacketPlayOutBoss.Action packetplayoutboss_action) {
        this.id = uuid;
        this.operation = packetplayoutboss_action;
    }

    public PacketPlayOutBoss(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.l();
        PacketPlayOutBoss.d packetplayoutboss_d = (PacketPlayOutBoss.d) packetdataserializer.a(PacketPlayOutBoss.d.class);

        this.operation = (PacketPlayOutBoss.Action) packetplayoutboss_d.reader.apply(packetdataserializer);
    }

    public static PacketPlayOutBoss createAddPacket(BossBattle bossbattle) {
        return new PacketPlayOutBoss(bossbattle.i(), new PacketPlayOutBoss.a(bossbattle));
    }

    public static PacketPlayOutBoss createRemovePacket(UUID uuid) {
        return new PacketPlayOutBoss(uuid, PacketPlayOutBoss.REMOVE_OPERATION);
    }

    public static PacketPlayOutBoss createUpdateProgressPacket(BossBattle bossbattle) {
        return new PacketPlayOutBoss(bossbattle.i(), new PacketPlayOutBoss.f(bossbattle.getProgress()));
    }

    public static PacketPlayOutBoss createUpdateNamePacket(BossBattle bossbattle) {
        return new PacketPlayOutBoss(bossbattle.i(), new PacketPlayOutBoss.e(bossbattle.j()));
    }

    public static PacketPlayOutBoss createUpdateStylePacket(BossBattle bossbattle) {
        return new PacketPlayOutBoss(bossbattle.i(), new PacketPlayOutBoss.h(bossbattle.l(), bossbattle.m()));
    }

    public static PacketPlayOutBoss createUpdatePropertiesPacket(BossBattle bossbattle) {
        return new PacketPlayOutBoss(bossbattle.i(), new PacketPlayOutBoss.g(bossbattle.isDarkenSky(), bossbattle.isPlayMusic(), bossbattle.isCreateFog()));
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.id);
        packetdataserializer.a((Enum) this.operation.a());
        this.operation.a(packetdataserializer);
    }

    static int a(boolean flag, boolean flag1, boolean flag2) {
        int i = 0;

        if (flag) {
            i |= 1;
        }

        if (flag1) {
            i |= 2;
        }

        if (flag2) {
            i |= 4;
        }

        return i;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketPlayOutBoss.b packetplayoutboss_b) {
        this.operation.a(this.id, packetplayoutboss_b);
    }

    private interface Action {

        PacketPlayOutBoss.d a();

        void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b);

        void a(PacketDataSerializer packetdataserializer);
    }

    private static enum d {

        ADD(PacketPlayOutBoss.a::new), REMOVE((packetdataserializer) -> {
            return PacketPlayOutBoss.REMOVE_OPERATION;
        }), UPDATE_PROGRESS(PacketPlayOutBoss.f::new), UPDATE_NAME(PacketPlayOutBoss.e::new), UPDATE_STYLE(PacketPlayOutBoss.h::new), UPDATE_PROPERTIES(PacketPlayOutBoss.g::new);

        final Function<PacketDataSerializer, PacketPlayOutBoss.Action> reader;

        private d(Function function) {
            this.reader = function;
        }
    }

    private static class a implements PacketPlayOutBoss.Action {

        private final IChatBaseComponent name;
        private final float progress;
        private final BossBattle.BarColor color;
        private final BossBattle.BarStyle overlay;
        private final boolean darkenScreen;
        private final boolean playMusic;
        private final boolean createWorldFog;

        a(BossBattle bossbattle) {
            this.name = bossbattle.j();
            this.progress = bossbattle.getProgress();
            this.color = bossbattle.l();
            this.overlay = bossbattle.m();
            this.darkenScreen = bossbattle.isDarkenSky();
            this.playMusic = bossbattle.isPlayMusic();
            this.createWorldFog = bossbattle.isCreateFog();
        }

        private a(PacketDataSerializer packetdataserializer) {
            this.name = packetdataserializer.i();
            this.progress = packetdataserializer.readFloat();
            this.color = (BossBattle.BarColor) packetdataserializer.a(BossBattle.BarColor.class);
            this.overlay = (BossBattle.BarStyle) packetdataserializer.a(BossBattle.BarStyle.class);
            short short0 = packetdataserializer.readUnsignedByte();

            this.darkenScreen = (short0 & 1) > 0;
            this.playMusic = (short0 & 2) > 0;
            this.createWorldFog = (short0 & 4) > 0;
        }

        @Override
        public PacketPlayOutBoss.d a() {
            return PacketPlayOutBoss.d.ADD;
        }

        @Override
        public void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b) {
            packetplayoutboss_b.a(uuid, this.name, this.progress, this.color, this.overlay, this.darkenScreen, this.playMusic, this.createWorldFog);
        }

        @Override
        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.a(this.name);
            packetdataserializer.writeFloat(this.progress);
            packetdataserializer.a((Enum) this.color);
            packetdataserializer.a((Enum) this.overlay);
            packetdataserializer.writeByte(PacketPlayOutBoss.a(this.darkenScreen, this.playMusic, this.createWorldFog));
        }
    }

    private static class f implements PacketPlayOutBoss.Action {

        private final float progress;

        f(float f) {
            this.progress = f;
        }

        private f(PacketDataSerializer packetdataserializer) {
            this.progress = packetdataserializer.readFloat();
        }

        @Override
        public PacketPlayOutBoss.d a() {
            return PacketPlayOutBoss.d.UPDATE_PROGRESS;
        }

        @Override
        public void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b) {
            packetplayoutboss_b.a(uuid, this.progress);
        }

        @Override
        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeFloat(this.progress);
        }
    }

    private static class e implements PacketPlayOutBoss.Action {

        private final IChatBaseComponent name;

        e(IChatBaseComponent ichatbasecomponent) {
            this.name = ichatbasecomponent;
        }

        private e(PacketDataSerializer packetdataserializer) {
            this.name = packetdataserializer.i();
        }

        @Override
        public PacketPlayOutBoss.d a() {
            return PacketPlayOutBoss.d.UPDATE_NAME;
        }

        @Override
        public void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b) {
            packetplayoutboss_b.a(uuid, this.name);
        }

        @Override
        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.a(this.name);
        }
    }

    private static class h implements PacketPlayOutBoss.Action {

        private final BossBattle.BarColor color;
        private final BossBattle.BarStyle overlay;

        h(BossBattle.BarColor bossbattle_barcolor, BossBattle.BarStyle bossbattle_barstyle) {
            this.color = bossbattle_barcolor;
            this.overlay = bossbattle_barstyle;
        }

        private h(PacketDataSerializer packetdataserializer) {
            this.color = (BossBattle.BarColor) packetdataserializer.a(BossBattle.BarColor.class);
            this.overlay = (BossBattle.BarStyle) packetdataserializer.a(BossBattle.BarStyle.class);
        }

        @Override
        public PacketPlayOutBoss.d a() {
            return PacketPlayOutBoss.d.UPDATE_STYLE;
        }

        @Override
        public void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b) {
            packetplayoutboss_b.a(uuid, this.color, this.overlay);
        }

        @Override
        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.a((Enum) this.color);
            packetdataserializer.a((Enum) this.overlay);
        }
    }

    private static class g implements PacketPlayOutBoss.Action {

        private final boolean darkenScreen;
        private final boolean playMusic;
        private final boolean createWorldFog;

        g(boolean flag, boolean flag1, boolean flag2) {
            this.darkenScreen = flag;
            this.playMusic = flag1;
            this.createWorldFog = flag2;
        }

        private g(PacketDataSerializer packetdataserializer) {
            short short0 = packetdataserializer.readUnsignedByte();

            this.darkenScreen = (short0 & 1) > 0;
            this.playMusic = (short0 & 2) > 0;
            this.createWorldFog = (short0 & 4) > 0;
        }

        @Override
        public PacketPlayOutBoss.d a() {
            return PacketPlayOutBoss.d.UPDATE_PROPERTIES;
        }

        @Override
        public void a(UUID uuid, PacketPlayOutBoss.b packetplayoutboss_b) {
            packetplayoutboss_b.a(uuid, this.darkenScreen, this.playMusic, this.createWorldFog);
        }

        @Override
        public void a(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeByte(PacketPlayOutBoss.a(this.darkenScreen, this.playMusic, this.createWorldFog));
        }
    }

    public interface b {

        default void a(UUID uuid, IChatBaseComponent ichatbasecomponent, float f, BossBattle.BarColor bossbattle_barcolor, BossBattle.BarStyle bossbattle_barstyle, boolean flag, boolean flag1, boolean flag2) {}

        default void a(UUID uuid) {}

        default void a(UUID uuid, float f) {}

        default void a(UUID uuid, IChatBaseComponent ichatbasecomponent) {}

        default void a(UUID uuid, BossBattle.BarColor bossbattle_barcolor, BossBattle.BarStyle bossbattle_barstyle) {}

        default void a(UUID uuid, boolean flag, boolean flag1, boolean flag2) {}
    }
}
