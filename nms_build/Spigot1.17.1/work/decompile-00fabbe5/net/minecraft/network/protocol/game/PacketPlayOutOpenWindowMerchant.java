package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.trading.MerchantRecipeList;

public class PacketPlayOutOpenWindowMerchant implements Packet<PacketListenerPlayOut> {

    private final int containerId;
    private final MerchantRecipeList offers;
    private final int villagerLevel;
    private final int villagerXp;
    private final boolean showProgress;
    private final boolean canRestock;

    public PacketPlayOutOpenWindowMerchant(int i, MerchantRecipeList merchantrecipelist, int j, int k, boolean flag, boolean flag1) {
        this.containerId = i;
        this.offers = merchantrecipelist;
        this.villagerLevel = j;
        this.villagerXp = k;
        this.showProgress = flag;
        this.canRestock = flag1;
    }

    public PacketPlayOutOpenWindowMerchant(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.j();
        this.offers = MerchantRecipeList.b(packetdataserializer);
        this.villagerLevel = packetdataserializer.j();
        this.villagerXp = packetdataserializer.j();
        this.showProgress = packetdataserializer.readBoolean();
        this.canRestock = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.containerId);
        this.offers.a(packetdataserializer);
        packetdataserializer.d(this.villagerLevel);
        packetdataserializer.d(this.villagerXp);
        packetdataserializer.writeBoolean(this.showProgress);
        packetdataserializer.writeBoolean(this.canRestock);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.containerId;
    }

    public MerchantRecipeList c() {
        return this.offers;
    }

    public int d() {
        return this.villagerLevel;
    }

    public int e() {
        return this.villagerXp;
    }

    public boolean f() {
        return this.showProgress;
    }

    public boolean g() {
        return this.canRestock;
    }
}
