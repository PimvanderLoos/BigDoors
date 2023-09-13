package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.players.UserCache;
import net.minecraft.util.UtilColor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntitySkull extends TileEntity {

    public static final String TAG_SKULL_OWNER = "SkullOwner";
    @Nullable
    private static UserCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    public GameProfile owner;
    private int mouthTickCount;
    private boolean isMovingMouth;

    public TileEntitySkull(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SKULL, blockposition, iblockdata);
    }

    public static void setup(UserCache usercache, MinecraftSessionService minecraftsessionservice, Executor executor) {
        TileEntitySkull.profileCache = usercache;
        TileEntitySkull.sessionService = minecraftsessionservice;
        TileEntitySkull.mainThreadExecutor = executor;
    }

    public static void clear() {
        TileEntitySkull.profileCache = null;
        TileEntitySkull.sessionService = null;
        TileEntitySkull.mainThreadExecutor = null;
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (this.owner != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            GameProfileSerializer.writeGameProfile(nbttagcompound1, this.owner);
            nbttagcompound.put("SkullOwner", nbttagcompound1);
        }

    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("SkullOwner", 10)) {
            this.setOwner(GameProfileSerializer.readGameProfile(nbttagcompound.getCompound("SkullOwner")));
        } else if (nbttagcompound.contains("ExtraType", 8)) {
            String s = nbttagcompound.getString("ExtraType");

            if (!UtilColor.isNullOrEmpty(s)) {
                this.setOwner(new GameProfile((UUID) null, s));
            }
        }

    }

    public static void dragonHeadAnimation(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntitySkull tileentityskull) {
        if (world.hasNeighborSignal(blockposition)) {
            tileentityskull.isMovingMouth = true;
            ++tileentityskull.mouthTickCount;
        } else {
            tileentityskull.isMovingMouth = false;
        }

    }

    public float getMouthAnimation(float f) {
        return this.isMovingMouth ? (float) this.mouthTickCount + f : (float) this.mouthTickCount;
    }

    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void setOwner(@Nullable GameProfile gameprofile) {
        synchronized (this) {
            this.owner = gameprofile;
        }

        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        updateGameprofile(this.owner, (gameprofile) -> {
            this.owner = gameprofile;
            this.setChanged();
        });
    }

    public static void updateGameprofile(@Nullable GameProfile gameprofile, Consumer<GameProfile> consumer) {
        if (gameprofile != null && !UtilColor.isNullOrEmpty(gameprofile.getName()) && (!gameprofile.isComplete() || !gameprofile.getProperties().containsKey("textures")) && TileEntitySkull.profileCache != null && TileEntitySkull.sessionService != null) {
            TileEntitySkull.profileCache.getAsync(gameprofile.getName(), (optional) -> {
                SystemUtils.backgroundExecutor().execute(() -> {
                    SystemUtils.ifElse(optional, (gameprofile1) -> {
                        Property property = (Property) Iterables.getFirst(gameprofile1.getProperties().get("textures"), (Object) null);

                        if (property == null) {
                            gameprofile1 = TileEntitySkull.sessionService.fillProfileProperties(gameprofile1, true);
                        }

                        TileEntitySkull.mainThreadExecutor.execute(() -> {
                            TileEntitySkull.profileCache.add(gameprofile1);
                            consumer.accept(gameprofile1);
                        });
                    }, () -> {
                        TileEntitySkull.mainThreadExecutor.execute(() -> {
                            consumer.accept(gameprofile);
                        });
                    });
                });
            });
        } else {
            consumer.accept(gameprofile);
        }
    }
}
