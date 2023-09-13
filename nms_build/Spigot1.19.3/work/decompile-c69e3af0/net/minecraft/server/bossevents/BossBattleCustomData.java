package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;

public class BossBattleCustomData {

    private final Map<MinecraftKey, BossBattleCustom> events = Maps.newHashMap();

    public BossBattleCustomData() {}

    @Nullable
    public BossBattleCustom get(MinecraftKey minecraftkey) {
        return (BossBattleCustom) this.events.get(minecraftkey);
    }

    public BossBattleCustom create(MinecraftKey minecraftkey, IChatBaseComponent ichatbasecomponent) {
        BossBattleCustom bossbattlecustom = new BossBattleCustom(minecraftkey, ichatbasecomponent);

        this.events.put(minecraftkey, bossbattlecustom);
        return bossbattlecustom;
    }

    public void remove(BossBattleCustom bossbattlecustom) {
        this.events.remove(bossbattlecustom.getTextId());
    }

    public Collection<MinecraftKey> getIds() {
        return this.events.keySet();
    }

    public Collection<BossBattleCustom> getEvents() {
        return this.events.values();
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.events.values().iterator();

        while (iterator.hasNext()) {
            BossBattleCustom bossbattlecustom = (BossBattleCustom) iterator.next();

            nbttagcompound.put(bossbattlecustom.getTextId().toString(), bossbattlecustom.save());
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        Iterator iterator = nbttagcompound.getAllKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            MinecraftKey minecraftkey = new MinecraftKey(s);

            this.events.put(minecraftkey, BossBattleCustom.load(nbttagcompound.getCompound(s), minecraftkey));
        }

    }

    public void onPlayerConnect(EntityPlayer entityplayer) {
        Iterator iterator = this.events.values().iterator();

        while (iterator.hasNext()) {
            BossBattleCustom bossbattlecustom = (BossBattleCustom) iterator.next();

            bossbattlecustom.onPlayerConnect(entityplayer);
        }

    }

    public void onPlayerDisconnect(EntityPlayer entityplayer) {
        Iterator iterator = this.events.values().iterator();

        while (iterator.hasNext()) {
            BossBattleCustom bossbattlecustom = (BossBattleCustom) iterator.next();

            bossbattlecustom.onPlayerDisconnect(entityplayer);
        }

    }
}
