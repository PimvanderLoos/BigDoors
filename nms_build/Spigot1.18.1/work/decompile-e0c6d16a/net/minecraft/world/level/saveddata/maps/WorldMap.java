package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutMap;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.saveddata.PersistentBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldMap extends PersistentBase {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAP_SIZE = 128;
    private static final int HALF_MAP_SIZE = 64;
    public static final int MAX_SCALE = 4;
    public static final int TRACKED_DECORATION_LIMIT = 256;
    public int x;
    public int z;
    public ResourceKey<World> dimension;
    public boolean trackingPosition;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public boolean locked;
    public final List<WorldMap.WorldMapHumanTracker> carriedBy = Lists.newArrayList();
    public final Map<EntityHuman, WorldMap.WorldMapHumanTracker> carriedByPlayers = Maps.newHashMap();
    private final Map<String, MapIconBanner> bannerMarkers = Maps.newHashMap();
    public final Map<String, MapIcon> decorations = Maps.newLinkedHashMap();
    private final Map<String, WorldMapFrame> frameMarkers = Maps.newHashMap();
    private int trackedDecorationCount;

    private WorldMap(int i, int j, byte b0, boolean flag, boolean flag1, boolean flag2, ResourceKey<World> resourcekey) {
        this.scale = b0;
        this.x = i;
        this.z = j;
        this.dimension = resourcekey;
        this.trackingPosition = flag;
        this.unlimitedTracking = flag1;
        this.locked = flag2;
        this.setDirty();
    }

    public static WorldMap createFresh(double d0, double d1, byte b0, boolean flag, boolean flag1, ResourceKey<World> resourcekey) {
        int i = 128 * (1 << b0);
        int j = MathHelper.floor((d0 + 64.0D) / (double) i);
        int k = MathHelper.floor((d1 + 64.0D) / (double) i);
        int l = j * i + i / 2 - 64;
        int i1 = k * i + i / 2 - 64;

        return new WorldMap(l, i1, b0, flag, flag1, false, resourcekey);
    }

    public static WorldMap createForClient(byte b0, boolean flag, ResourceKey<World> resourcekey) {
        return new WorldMap(0, 0, b0, false, false, flag, resourcekey);
    }

    public static WorldMap load(NBTTagCompound nbttagcompound) {
        DataResult dataresult = DimensionManager.parseLegacy(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("dimension")));
        Logger logger = WorldMap.LOGGER;

        Objects.requireNonNull(logger);
        ResourceKey<World> resourcekey = (ResourceKey) dataresult.resultOrPartial(logger::error).orElseThrow(() -> {
            return new IllegalArgumentException("Invalid map dimension: " + nbttagcompound.get("dimension"));
        });
        int i = nbttagcompound.getInt("xCenter");
        int j = nbttagcompound.getInt("zCenter");
        byte b0 = (byte) MathHelper.clamp((int) nbttagcompound.getByte("scale"), (int) 0, (int) 4);
        boolean flag = !nbttagcompound.contains("trackingPosition", 1) || nbttagcompound.getBoolean("trackingPosition");
        boolean flag1 = nbttagcompound.getBoolean("unlimitedTracking");
        boolean flag2 = nbttagcompound.getBoolean("locked");
        WorldMap worldmap = new WorldMap(i, j, b0, flag, flag1, flag2, resourcekey);
        byte[] abyte = nbttagcompound.getByteArray("colors");

        if (abyte.length == 16384) {
            worldmap.colors = abyte;
        }

        NBTTagList nbttaglist = nbttagcompound.getList("banners", 10);

        for (int k = 0; k < nbttaglist.size(); ++k) {
            MapIconBanner mapiconbanner = MapIconBanner.load(nbttaglist.getCompound(k));

            worldmap.bannerMarkers.put(mapiconbanner.getId(), mapiconbanner);
            worldmap.addDecoration(mapiconbanner.getDecoration(), (GeneratorAccess) null, mapiconbanner.getId(), (double) mapiconbanner.getPos().getX(), (double) mapiconbanner.getPos().getZ(), 180.0D, mapiconbanner.getName());
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("frames", 10);

        for (int l = 0; l < nbttaglist1.size(); ++l) {
            WorldMapFrame worldmapframe = WorldMapFrame.load(nbttaglist1.getCompound(l));

            worldmap.frameMarkers.put(worldmapframe.getId(), worldmapframe);
            worldmap.addDecoration(MapIcon.Type.FRAME, (GeneratorAccess) null, "frame-" + worldmapframe.getEntityId(), (double) worldmapframe.getPos().getX(), (double) worldmapframe.getPos().getZ(), (double) worldmapframe.getRotation(), (IChatBaseComponent) null);
        }

        return worldmap;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        DataResult dataresult = MinecraftKey.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.dimension.location());
        Logger logger = WorldMap.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("dimension", nbtbase);
        });
        nbttagcompound.putInt("xCenter", this.x);
        nbttagcompound.putInt("zCenter", this.z);
        nbttagcompound.putByte("scale", this.scale);
        nbttagcompound.putByteArray("colors", this.colors);
        nbttagcompound.putBoolean("trackingPosition", this.trackingPosition);
        nbttagcompound.putBoolean("unlimitedTracking", this.unlimitedTracking);
        nbttagcompound.putBoolean("locked", this.locked);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.bannerMarkers.values().iterator();

        while (iterator.hasNext()) {
            MapIconBanner mapiconbanner = (MapIconBanner) iterator.next();

            nbttaglist.add(mapiconbanner.save());
        }

        nbttagcompound.put("banners", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.frameMarkers.values().iterator();

        while (iterator1.hasNext()) {
            WorldMapFrame worldmapframe = (WorldMapFrame) iterator1.next();

            nbttaglist1.add(worldmapframe.save());
        }

        nbttagcompound.put("frames", nbttaglist1);
        return nbttagcompound;
    }

    public WorldMap locked() {
        WorldMap worldmap = new WorldMap(this.x, this.z, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);

        worldmap.bannerMarkers.putAll(this.bannerMarkers);
        worldmap.decorations.putAll(this.decorations);
        worldmap.trackedDecorationCount = this.trackedDecorationCount;
        System.arraycopy(this.colors, 0, worldmap.colors, 0, this.colors.length);
        worldmap.setDirty();
        return worldmap;
    }

    public WorldMap scaled(int i) {
        return createFresh((double) this.x, (double) this.z, (byte) MathHelper.clamp(this.scale + i, (int) 0, (int) 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
    }

    public void tickCarriedBy(EntityHuman entityhuman, ItemStack itemstack) {
        if (!this.carriedByPlayers.containsKey(entityhuman)) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = new WorldMap.WorldMapHumanTracker(entityhuman);

            this.carriedByPlayers.put(entityhuman, worldmap_worldmaphumantracker);
            this.carriedBy.add(worldmap_worldmaphumantracker);
        }

        if (!entityhuman.getInventory().contains(itemstack)) {
            this.removeDecoration(entityhuman.getName().getString());
        }

        for (int i = 0; i < this.carriedBy.size(); ++i) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker1 = (WorldMap.WorldMapHumanTracker) this.carriedBy.get(i);
            String s = worldmap_worldmaphumantracker1.player.getName().getString();

            if (!worldmap_worldmaphumantracker1.player.isRemoved() && (worldmap_worldmaphumantracker1.player.getInventory().contains(itemstack) || itemstack.isFramed())) {
                if (!itemstack.isFramed() && worldmap_worldmaphumantracker1.player.level.dimension() == this.dimension && this.trackingPosition) {
                    this.addDecoration(MapIcon.Type.PLAYER, worldmap_worldmaphumantracker1.player.level, s, worldmap_worldmaphumantracker1.player.getX(), worldmap_worldmaphumantracker1.player.getZ(), (double) worldmap_worldmaphumantracker1.player.getYRot(), (IChatBaseComponent) null);
                }
            } else {
                this.carriedByPlayers.remove(worldmap_worldmaphumantracker1.player);
                this.carriedBy.remove(worldmap_worldmaphumantracker1);
                this.removeDecoration(s);
            }
        }

        if (itemstack.isFramed() && this.trackingPosition) {
            EntityItemFrame entityitemframe = itemstack.getFrame();
            BlockPosition blockposition = entityitemframe.getPos();
            WorldMapFrame worldmapframe = (WorldMapFrame) this.frameMarkers.get(WorldMapFrame.frameId(blockposition));

            if (worldmapframe != null && entityitemframe.getId() != worldmapframe.getEntityId() && this.frameMarkers.containsKey(worldmapframe.getId())) {
                this.removeDecoration("frame-" + worldmapframe.getEntityId());
            }

            WorldMapFrame worldmapframe1 = new WorldMapFrame(blockposition, entityitemframe.getDirection().get2DDataValue() * 90, entityitemframe.getId());

            this.addDecoration(MapIcon.Type.FRAME, entityhuman.level, "frame-" + entityitemframe.getId(), (double) blockposition.getX(), (double) blockposition.getZ(), (double) (entityitemframe.getDirection().get2DDataValue() * 90), (IChatBaseComponent) null);
            this.frameMarkers.put(worldmapframe1.getId(), worldmapframe1);
        }

        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.contains("Decorations", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Decorations", 10);

            for (int j = 0; j < nbttaglist.size(); ++j) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(j);

                if (!this.decorations.containsKey(nbttagcompound1.getString("id"))) {
                    this.addDecoration(MapIcon.Type.byIcon(nbttagcompound1.getByte("type")), entityhuman.level, nbttagcompound1.getString("id"), nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("z"), nbttagcompound1.getDouble("rot"), (IChatBaseComponent) null);
                }
            }
        }

    }

    private void removeDecoration(String s) {
        MapIcon mapicon = (MapIcon) this.decorations.remove(s);

        if (mapicon != null && mapicon.getType().shouldTrackCount()) {
            --this.trackedDecorationCount;
        }

        this.setDecorationsDirty();
    }

    public static void addTargetDecoration(ItemStack itemstack, BlockPosition blockposition, String s, MapIcon.Type mapicon_type) {
        NBTTagList nbttaglist;

        if (itemstack.hasTag() && itemstack.getTag().contains("Decorations", 9)) {
            nbttaglist = itemstack.getTag().getList("Decorations", 10);
        } else {
            nbttaglist = new NBTTagList();
            itemstack.addTagElement("Decorations", nbttaglist);
        }

        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putByte("type", mapicon_type.getIcon());
        nbttagcompound.putString("id", s);
        nbttagcompound.putDouble("x", (double) blockposition.getX());
        nbttagcompound.putDouble("z", (double) blockposition.getZ());
        nbttagcompound.putDouble("rot", 180.0D);
        nbttaglist.add(nbttagcompound);
        if (mapicon_type.hasMapColor()) {
            NBTTagCompound nbttagcompound1 = itemstack.getOrCreateTagElement("display");

            nbttagcompound1.putInt("MapColor", mapicon_type.getMapColor());
        }

    }

    private void addDecoration(MapIcon.Type mapicon_type, @Nullable GeneratorAccess generatoraccess, String s, double d0, double d1, double d2, @Nullable IChatBaseComponent ichatbasecomponent) {
        int i = 1 << this.scale;
        float f = (float) (d0 - (double) this.x) / (float) i;
        float f1 = (float) (d1 - (double) this.z) / (float) i;
        byte b0 = (byte) ((int) ((double) (f * 2.0F) + 0.5D));
        byte b1 = (byte) ((int) ((double) (f1 * 2.0F) + 0.5D));
        boolean flag = true;
        byte b2;

        if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
            d2 += d2 < 0.0D ? -8.0D : 8.0D;
            b2 = (byte) ((int) (d2 * 16.0D / 360.0D));
            if (this.dimension == World.NETHER && generatoraccess != null) {
                int j = (int) (generatoraccess.getLevelData().getDayTime() / 10L);

                b2 = (byte) (j * j * 34187121 + j * 121 >> 15 & 15);
            }
        } else {
            if (mapicon_type != MapIcon.Type.PLAYER) {
                this.removeDecoration(s);
                return;
            }

            boolean flag1 = true;

            if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
                mapicon_type = MapIcon.Type.PLAYER_OFF_MAP;
            } else {
                if (!this.unlimitedTracking) {
                    this.removeDecoration(s);
                    return;
                }

                mapicon_type = MapIcon.Type.PLAYER_OFF_LIMITS;
            }

            b2 = 0;
            if (f <= -63.0F) {
                b0 = -128;
            }

            if (f1 <= -63.0F) {
                b1 = -128;
            }

            if (f >= 63.0F) {
                b0 = 127;
            }

            if (f1 >= 63.0F) {
                b1 = 127;
            }
        }

        MapIcon mapicon = new MapIcon(mapicon_type, b0, b1, b2, ichatbasecomponent);
        MapIcon mapicon1 = (MapIcon) this.decorations.put(s, mapicon);

        if (!mapicon.equals(mapicon1)) {
            if (mapicon1 != null && mapicon1.getType().shouldTrackCount()) {
                --this.trackedDecorationCount;
            }

            if (mapicon_type.shouldTrackCount()) {
                ++this.trackedDecorationCount;
            }

            this.setDecorationsDirty();
        }

    }

    @Nullable
    public Packet<?> getUpdatePacket(int i, EntityHuman entityhuman) {
        WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) this.carriedByPlayers.get(entityhuman);

        return worldmap_worldmaphumantracker == null ? null : worldmap_worldmaphumantracker.nextUpdatePacket(i);
    }

    public void setColorsDirty(int i, int j) {
        this.setDirty();
        Iterator iterator = this.carriedBy.iterator();

        while (iterator.hasNext()) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) iterator.next();

            worldmap_worldmaphumantracker.markColorsDirty(i, j);
        }

    }

    public void setDecorationsDirty() {
        this.setDirty();
        this.carriedBy.forEach(WorldMap.WorldMapHumanTracker::markDecorationsDirty);
    }

    public WorldMap.WorldMapHumanTracker getHoldingPlayer(EntityHuman entityhuman) {
        WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) this.carriedByPlayers.get(entityhuman);

        if (worldmap_worldmaphumantracker == null) {
            worldmap_worldmaphumantracker = new WorldMap.WorldMapHumanTracker(entityhuman);
            this.carriedByPlayers.put(entityhuman, worldmap_worldmaphumantracker);
            this.carriedBy.add(worldmap_worldmaphumantracker);
        }

        return worldmap_worldmaphumantracker;
    }

    public boolean toggleBanner(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        double d0 = (double) blockposition.getX() + 0.5D;
        double d1 = (double) blockposition.getZ() + 0.5D;
        int i = 1 << this.scale;
        double d2 = (d0 - (double) this.x) / (double) i;
        double d3 = (d1 - (double) this.z) / (double) i;
        boolean flag = true;

        if (d2 >= -63.0D && d3 >= -63.0D && d2 <= 63.0D && d3 <= 63.0D) {
            MapIconBanner mapiconbanner = MapIconBanner.fromWorld(generatoraccess, blockposition);

            if (mapiconbanner == null) {
                return false;
            }

            if (this.bannerMarkers.remove(mapiconbanner.getId(), mapiconbanner)) {
                this.removeDecoration(mapiconbanner.getId());
                return true;
            }

            if (!this.isTrackedCountOverLimit(256)) {
                this.bannerMarkers.put(mapiconbanner.getId(), mapiconbanner);
                this.addDecoration(mapiconbanner.getDecoration(), generatoraccess, mapiconbanner.getId(), d0, d1, 180.0D, mapiconbanner.getName());
                return true;
            }
        }

        return false;
    }

    public void checkBanners(IBlockAccess iblockaccess, int i, int j) {
        Iterator iterator = this.bannerMarkers.values().iterator();

        while (iterator.hasNext()) {
            MapIconBanner mapiconbanner = (MapIconBanner) iterator.next();

            if (mapiconbanner.getPos().getX() == i && mapiconbanner.getPos().getZ() == j) {
                MapIconBanner mapiconbanner1 = MapIconBanner.fromWorld(iblockaccess, mapiconbanner.getPos());

                if (!mapiconbanner.equals(mapiconbanner1)) {
                    iterator.remove();
                    this.removeDecoration(mapiconbanner.getId());
                }
            }
        }

    }

    public Collection<MapIconBanner> getBanners() {
        return this.bannerMarkers.values();
    }

    public void removedFromFrame(BlockPosition blockposition, int i) {
        this.removeDecoration("frame-" + i);
        this.frameMarkers.remove(WorldMapFrame.frameId(blockposition));
    }

    public boolean updateColor(int i, int j, byte b0) {
        byte b1 = this.colors[i + j * 128];

        if (b1 != b0) {
            this.setColor(i, j, b0);
            return true;
        } else {
            return false;
        }
    }

    public void setColor(int i, int j, byte b0) {
        this.colors[i + j * 128] = b0;
        this.setColorsDirty(i, j);
    }

    public boolean isExplorationMap() {
        Iterator iterator = this.decorations.values().iterator();

        MapIcon mapicon;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            mapicon = (MapIcon) iterator.next();
        } while (mapicon.getType() != MapIcon.Type.MANSION && mapicon.getType() != MapIcon.Type.MONUMENT);

        return true;
    }

    public void addClientSideDecorations(List<MapIcon> list) {
        this.decorations.clear();
        this.trackedDecorationCount = 0;

        for (int i = 0; i < list.size(); ++i) {
            MapIcon mapicon = (MapIcon) list.get(i);

            this.decorations.put("icon-" + i, mapicon);
            if (mapicon.getType().shouldTrackCount()) {
                ++this.trackedDecorationCount;
            }
        }

    }

    public Iterable<MapIcon> getDecorations() {
        return this.decorations.values();
    }

    public boolean isTrackedCountOverLimit(int i) {
        return this.trackedDecorationCount >= i;
    }

    public class WorldMapHumanTracker {

        public final EntityHuman player;
        private boolean dirtyData = true;
        private int minDirtyX;
        private int minDirtyY;
        private int maxDirtyX = 127;
        private int maxDirtyY = 127;
        private boolean dirtyDecorations = true;
        private int tick;
        public int step;

        WorldMapHumanTracker(EntityHuman entityhuman) {
            this.player = entityhuman;
        }

        private WorldMap.b createPatch() {
            int i = this.minDirtyX;
            int j = this.minDirtyY;
            int k = this.maxDirtyX + 1 - this.minDirtyX;
            int l = this.maxDirtyY + 1 - this.minDirtyY;
            byte[] abyte = new byte[k * l];

            for (int i1 = 0; i1 < k; ++i1) {
                for (int j1 = 0; j1 < l; ++j1) {
                    abyte[i1 + j1 * k] = WorldMap.this.colors[i + i1 + (j + j1) * 128];
                }
            }

            return new WorldMap.b(i, j, k, l, abyte);
        }

        @Nullable
        Packet<?> nextUpdatePacket(int i) {
            WorldMap.b worldmap_b;

            if (this.dirtyData) {
                this.dirtyData = false;
                worldmap_b = this.createPatch();
            } else {
                worldmap_b = null;
            }

            Collection collection;

            if (this.dirtyDecorations && this.tick++ % 5 == 0) {
                this.dirtyDecorations = false;
                collection = WorldMap.this.decorations.values();
            } else {
                collection = null;
            }

            return collection == null && worldmap_b == null ? null : new PacketPlayOutMap(i, WorldMap.this.scale, WorldMap.this.locked, collection, worldmap_b);
        }

        void markColorsDirty(int i, int j) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, i);
                this.minDirtyY = Math.min(this.minDirtyY, j);
                this.maxDirtyX = Math.max(this.maxDirtyX, i);
                this.maxDirtyY = Math.max(this.maxDirtyY, j);
            } else {
                this.dirtyData = true;
                this.minDirtyX = i;
                this.minDirtyY = j;
                this.maxDirtyX = i;
                this.maxDirtyY = j;
            }

        }

        private void markDecorationsDirty() {
            this.dirtyDecorations = true;
        }
    }

    public static class b {

        public final int startX;
        public final int startY;
        public final int width;
        public final int height;
        public final byte[] mapColors;

        public b(int i, int j, int k, int l, byte[] abyte) {
            this.startX = i;
            this.startY = j;
            this.width = k;
            this.height = l;
            this.mapColors = abyte;
        }

        public void applyToMap(WorldMap worldmap) {
            for (int i = 0; i < this.width; ++i) {
                for (int j = 0; j < this.height; ++j) {
                    worldmap.setColor(this.startX + i, this.startY + j, this.mapColors[i + j * this.width]);
                }
            }

        }
    }
}
