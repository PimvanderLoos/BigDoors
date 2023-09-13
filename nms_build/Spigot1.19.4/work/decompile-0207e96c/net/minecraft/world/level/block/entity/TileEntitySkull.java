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
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.Services;
import net.minecraft.server.players.UserCache;
import net.minecraft.util.UtilColor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntitySkull extends TileEntity {

    public static final String TAG_SKULL_OWNER = "SkullOwner";
    public static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
    @Nullable
    private static UserCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    public GameProfile owner;
    @Nullable
    public MinecraftKey noteBlockSound;
    private int animationTickCount;
    private boolean isAnimating;

    public TileEntitySkull(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SKULL, blockposition, iblockdata);
    }

    public static void setup(Services services, Executor executor) {
        TileEntitySkull.profileCache = services.profileCache();
        TileEntitySkull.sessionService = services.sessionService();
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

        if (this.noteBlockSound != null) {
            nbttagcompound.putString("note_block_sound", this.noteBlockSound.toString());
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

        if (nbttagcompound.contains("note_block_sound", 8)) {
            this.noteBlockSound = MinecraftKey.tryParse(nbttagcompound.getString("note_block_sound"));
        }

    }

    public static void animation(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntitySkull tileentityskull) {
        if (world.hasNeighborSignal(blockposition)) {
            tileentityskull.isAnimating = true;
            ++tileentityskull.animationTickCount;
        } else {
            tileentityskull.isAnimating = false;
        }

    }

    public float getAnimation(float f) {
        return this.isAnimating ? (float) this.animationTickCount + f : (float) this.animationTickCount;
    }

    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    @Nullable
    public MinecraftKey getNoteBlockSound() {
        return this.noteBlockSound;
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
                            MinecraftSessionService minecraftsessionservice = TileEntitySkull.sessionService;

                            if (minecraftsessionservice == null) {
                                return;
                            }

                            gameprofile1 = minecraftsessionservice.fillProfileProperties(gameprofile1, true);
                        }

                        Executor executor = TileEntitySkull.mainThreadExecutor;

                        if (executor != null) {
                            executor.execute(() -> {
                                UserCache usercache = TileEntitySkull.profileCache;

                                if (usercache != null) {
                                    usercache.add(gameprofile1);
                                    consumer.accept(gameprofile1);
                                }

                            });
                        }

                    }, () -> {
                        Executor executor = TileEntitySkull.mainThreadExecutor;

                        if (executor != null) {
                            executor.execute(() -> {
                                consumer.accept(gameprofile);
                            });
                        }

                    });
                });
            });
        } else {
            consumer.accept(gameprofile);
        }
    }
}
