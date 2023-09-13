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
    public BossBattleCustom a(MinecraftKey minecraftkey) {
        return (BossBattleCustom) this.events.get(minecraftkey);
    }

    public BossBattleCustom register(MinecraftKey minecraftkey, IChatBaseComponent ichatbasecomponent) {
        BossBattleCustom bossbattlecustom = new BossBattleCustom(minecraftkey, ichatbasecomponent);

        this.events.put(minecraftkey, bossbattlecustom);
        return bossbattlecustom;
    }

    public void remove(BossBattleCustom bossbattlecustom) {
        this.events.remove(bossbattlecustom.getKey());
    }

    public Collection<MinecraftKey> a() {
        return this.events.keySet();
    }

    public Collection<BossBattleCustom> getBattles() {
        return this.events.values();
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.events.values().iterator();

        while (iterator.hasNext()) {
            BossBattleCustom bossbattlecustom = (BossBattleCustom) iterator.next();

            nbttagcompound.set(bossbattlecustom.getKey().toString(), bossbattlecustom.f());
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        Iterator iterator = nbttagcompound.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            MinecraftKey minecraftkey = new MinecraftKey(s);

            this.events.put(minecraftkey, BossBattleCustom.a(nbttagcompound.getCompound(s), minecraftkey));
        }

    }

    public void a(EntityPlayer entityplayer) {
        Iterator iterator = this.events.values().iterator();

        while (iterator.hasNext()) {
            BossBattleCustom bossbattlecustom = (BossBattleCustom) iterator.next();

            bossbattlecustom.c(entityplayer);
        }

    }

    public void b(EntityPlayer entityplayer) {
        Iterator iterator = this.events.values().iterator();

        while (iterator.hasNext()) {
            BossBattleCustom bossbattlecustom = (BossBattleCustom) iterator.next();

            bossbattlecustom.d(entityplayer);
        }

    }
}
