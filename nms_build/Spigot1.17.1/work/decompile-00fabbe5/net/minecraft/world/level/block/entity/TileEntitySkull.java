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

    public static void a(UserCache usercache) {
        TileEntitySkull.profileCache = usercache;
    }

    public static void a(MinecraftSessionService minecraftsessionservice) {
        TileEntitySkull.sessionService = minecraftsessionservice;
    }

    public static void a(Executor executor) {
        TileEntitySkull.mainThreadExecutor = executor;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.owner != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            GameProfileSerializer.serialize(nbttagcompound1, this.owner);
            nbttagcompound.set("SkullOwner", nbttagcompound1);
        }

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("SkullOwner", 10)) {
            this.setGameProfile(GameProfileSerializer.deserialize(nbttagcompound.getCompound("SkullOwner")));
        } else if (nbttagcompound.hasKeyOfType("ExtraType", 8)) {
            String s = nbttagcompound.getString("ExtraType");

            if (!UtilColor.b(s)) {
                this.setGameProfile(new GameProfile((UUID) null, s));
            }
        }

    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntitySkull tileentityskull) {
        if (world.isBlockIndirectlyPowered(blockposition)) {
            tileentityskull.isMovingMouth = true;
            ++tileentityskull.mouthTickCount;
        } else {
            tileentityskull.isMovingMouth = false;
        }

    }

    public float a(float f) {
        return this.isMovingMouth ? (float) this.mouthTickCount + f : (float) this.mouthTickCount;
    }

    @Nullable
    public GameProfile d() {
        return this.owner;
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 4, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        return this.save(new NBTTagCompound());
    }

    public void setGameProfile(@Nullable GameProfile gameprofile) {
        synchronized (this) {
            this.owner = gameprofile;
        }

        this.f();
    }

    private void f() {
        a(this.owner, (gameprofile) -> {
            this.owner = gameprofile;
            this.update();
        });
    }

    public static void a(@Nullable GameProfile gameprofile, Consumer<GameProfile> consumer) {
        if (gameprofile != null && !UtilColor.b(gameprofile.getName()) && (!gameprofile.isComplete() || !gameprofile.getProperties().containsKey("textures")) && TileEntitySkull.profileCache != null && TileEntitySkull.sessionService != null) {
            TileEntitySkull.profileCache.a(gameprofile.getName(), (optional) -> {
                SystemUtils.f().execute(() -> {
                    SystemUtils.a(optional, (gameprofile1) -> {
                        Property property = (Property) Iterables.getFirst(gameprofile1.getProperties().get("textures"), (Object) null);

                        if (property == null) {
                            gameprofile1 = TileEntitySkull.sessionService.fillProfileProperties(gameprofile1, true);
                        }

                        TileEntitySkull.mainThreadExecutor.execute(() -> {
                            TileEntitySkull.profileCache.a(gameprofile1);
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
