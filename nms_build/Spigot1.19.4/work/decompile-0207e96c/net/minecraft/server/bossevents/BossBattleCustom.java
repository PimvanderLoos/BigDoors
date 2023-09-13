package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.BossBattleServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.BossBattle;

public class BossBattleCustom extends BossBattleServer {

    private final MinecraftKey id;
    private final Set<UUID> players = Sets.newHashSet();
    private int value;
    private int max = 100;

    public BossBattleCustom(MinecraftKey minecraftkey, IChatBaseComponent ichatbasecomponent) {
        super(ichatbasecomponent, BossBattle.BarColor.WHITE, BossBattle.BarStyle.PROGRESS);
        this.id = minecraftkey;
        this.setProgress(0.0F);
    }

    public MinecraftKey getTextId() {
        return this.id;
    }

    @Override
    public void addPlayer(EntityPlayer entityplayer) {
        super.addPlayer(entityplayer);
        this.players.add(entityplayer.getUUID());
    }

    public void addOfflinePlayer(UUID uuid) {
        this.players.add(uuid);
    }

    @Override
    public void removePlayer(EntityPlayer entityplayer) {
        super.removePlayer(entityplayer);
        this.players.remove(entityplayer.getUUID());
    }

    @Override
    public void removeAllPlayers() {
        super.removeAllPlayers();
        this.players.clear();
    }

    public int getValue() {
        return this.value;
    }

    public int getMax() {
        return this.max;
    }

    public void setValue(int i) {
        this.value = i;
        this.setProgress(MathHelper.clamp((float) i / (float) this.max, 0.0F, 1.0F));
    }

    public void setMax(int i) {
        this.max = i;
        this.setProgress(MathHelper.clamp((float) this.value / (float) i, 0.0F, 1.0F));
    }

    public final IChatBaseComponent getDisplayName() {
        return ChatComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((chatmodifier) -> {
            return chatmodifier.withColor(this.getColor().getFormatting()).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.literal(this.getTextId().toString()))).withInsertion(this.getTextId().toString());
        });
    }

    public boolean setPlayers(Collection<EntityPlayer> collection) {
        Set<UUID> set = Sets.newHashSet();
        Set<EntityPlayer> set1 = Sets.newHashSet();
        Iterator iterator = this.players.iterator();

        UUID uuid;
        boolean flag;
        Iterator iterator1;

        while (iterator.hasNext()) {
            uuid = (UUID) iterator.next();
            flag = false;
            iterator1 = collection.iterator();

            while (true) {
                if (iterator1.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator1.next();

                    if (!entityplayer.getUUID().equals(uuid)) {
                        continue;
                    }

                    flag = true;
                }

                if (!flag) {
                    set.add(uuid);
                }
                break;
            }
        }

        iterator = collection.iterator();

        EntityPlayer entityplayer1;

        while (iterator.hasNext()) {
            entityplayer1 = (EntityPlayer) iterator.next();
            flag = false;
            iterator1 = this.players.iterator();

            while (true) {
                if (iterator1.hasNext()) {
                    UUID uuid1 = (UUID) iterator1.next();

                    if (!entityplayer1.getUUID().equals(uuid1)) {
                        continue;
                    }

                    flag = true;
                }

                if (!flag) {
                    set1.add(entityplayer1);
                }
                break;
            }
        }

        iterator = set.iterator();

        while (iterator.hasNext()) {
            uuid = (UUID) iterator.next();
            Iterator iterator2 = this.getPlayers().iterator();

            while (true) {
                if (iterator2.hasNext()) {
                    EntityPlayer entityplayer2 = (EntityPlayer) iterator2.next();

                    if (!entityplayer2.getUUID().equals(uuid)) {
                        continue;
                    }

                    this.removePlayer(entityplayer2);
                }

                this.players.remove(uuid);
                break;
            }
        }

        iterator = set1.iterator();

        while (iterator.hasNext()) {
            entityplayer1 = (EntityPlayer) iterator.next();
            this.addPlayer(entityplayer1);
        }

        return !set.isEmpty() || !set1.isEmpty();
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("Name", IChatBaseComponent.ChatSerializer.toJson(this.name));
        nbttagcompound.putBoolean("Visible", this.isVisible());
        nbttagcompound.putInt("Value", this.value);
        nbttagcompound.putInt("Max", this.max);
        nbttagcompound.putString("Color", this.getColor().getName());
        nbttagcompound.putString("Overlay", this.getOverlay().getName());
        nbttagcompound.putBoolean("DarkenScreen", this.shouldDarkenScreen());
        nbttagcompound.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
        nbttagcompound.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            UUID uuid = (UUID) iterator.next();

            nbttaglist.add(GameProfileSerializer.createUUID(uuid));
        }

        nbttagcompound.put("Players", nbttaglist);
        return nbttagcompound;
    }

    public static BossBattleCustom load(NBTTagCompound nbttagcompound, MinecraftKey minecraftkey) {
        BossBattleCustom bossbattlecustom = new BossBattleCustom(minecraftkey, IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("Name")));

        bossbattlecustom.setVisible(nbttagcompound.getBoolean("Visible"));
        bossbattlecustom.setValue(nbttagcompound.getInt("Value"));
        bossbattlecustom.setMax(nbttagcompound.getInt("Max"));
        bossbattlecustom.setColor(BossBattle.BarColor.byName(nbttagcompound.getString("Color")));
        bossbattlecustom.setOverlay(BossBattle.BarStyle.byName(nbttagcompound.getString("Overlay")));
        bossbattlecustom.setDarkenScreen(nbttagcompound.getBoolean("DarkenScreen"));
        bossbattlecustom.setPlayBossMusic(nbttagcompound.getBoolean("PlayBossMusic"));
        bossbattlecustom.setCreateWorldFog(nbttagcompound.getBoolean("CreateWorldFog"));
        NBTTagList nbttaglist = nbttagcompound.getList("Players", 11);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            bossbattlecustom.addOfflinePlayer(GameProfileSerializer.loadUUID(nbttaglist.get(i)));
        }

        return bossbattlecustom;
    }

    public void onPlayerConnect(EntityPlayer entityplayer) {
        if (this.players.contains(entityplayer.getUUID())) {
            this.addPlayer(entityplayer);
        }

    }

    public void onPlayerDisconnect(EntityPlayer entityplayer) {
        super.removePlayer(entityplayer);
    }
}
