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
        this.containerId = packetdataserializer.readVarInt();
        this.offers = MerchantRecipeList.createFromStream(packetdataserializer);
        this.villagerLevel = packetdataserializer.readVarInt();
        this.villagerXp = packetdataserializer.readVarInt();
        this.showProgress = packetdataserializer.readBoolean();
        this.canRestock = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.containerId);
        this.offers.writeToStream(packetdataserializer);
        packetdataserializer.writeVarInt(this.villagerLevel);
        packetdataserializer.writeVarInt(this.villagerXp);
        packetdataserializer.writeBoolean(this.showProgress);
        packetdataserializer.writeBoolean(this.canRestock);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleMerchantOffers(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public MerchantRecipeList getOffers() {
        return this.offers;
    }

    public int getVillagerLevel() {
        return this.villagerLevel;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public boolean showProgress() {
        return this.showProgress;
    }

    public boolean canRestock() {
        return this.canRestock;
    }
}
