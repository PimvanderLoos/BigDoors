package net.minecraft.server;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;

public class TileEntitySkull extends TileEntity implements ITickable {

    private int a;
    public int rotation;
    private GameProfile g;
    private int h;
    private boolean i;
    private static UserCache j;
    private static MinecraftSessionService k;

    public TileEntitySkull() {}

    public static void a(UserCache usercache) {
        TileEntitySkull.j = usercache;
    }

    public static void a(MinecraftSessionService minecraftsessionservice) {
        TileEntitySkull.k = minecraftsessionservice;
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setByte("SkullType", (byte) (this.a & 255));
        nbttagcompound.setByte("Rot", (byte) (this.rotation & 255));
        if (this.g != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            GameProfileSerializer.serialize(nbttagcompound1, this.g);
            nbttagcompound.set("Owner", nbttagcompound1);
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a = nbttagcompound.getByte("SkullType");
        this.rotation = nbttagcompound.getByte("Rot");
        if (this.a == 3) {
            if (nbttagcompound.hasKeyOfType("Owner", 10)) {
                this.g = GameProfileSerializer.deserialize(nbttagcompound.getCompound("Owner"));
            } else if (nbttagcompound.hasKeyOfType("ExtraType", 8)) {
                String s = nbttagcompound.getString("ExtraType");

                if (!UtilColor.b(s)) {
                    this.g = new GameProfile((UUID) null, s);
                    this.i();
                }
            }
        }

    }

    public void e() {
        if (this.a == 5) {
            if (this.world.isBlockIndirectlyPowered(this.position)) {
                this.i = true;
                ++this.h;
            } else {
                this.i = false;
            }
        }

    }

    @Nullable
    public GameProfile getGameProfile() {
        return this.g;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 4, this.d());
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    public void setSkullType(int i) {
        this.a = i;
        this.g = null;
    }

    public void setGameProfile(@Nullable GameProfile gameprofile) {
        this.a = 3;
        this.g = gameprofile;
        this.i();
    }

    private void i() {
        this.g = b(this.g);
        this.update();
    }

    public static GameProfile b(GameProfile gameprofile) {
        if (gameprofile != null && !UtilColor.b(gameprofile.getName())) {
            if (gameprofile.isComplete() && gameprofile.getProperties().containsKey("textures")) {
                return gameprofile;
            } else if (TileEntitySkull.j != null && TileEntitySkull.k != null) {
                GameProfile gameprofile1 = TileEntitySkull.j.getProfile(gameprofile.getName());

                if (gameprofile1 == null) {
                    return gameprofile;
                } else {
                    Property property = (Property) Iterables.getFirst(gameprofile1.getProperties().get("textures"), (Object) null);

                    if (property == null) {
                        gameprofile1 = TileEntitySkull.k.fillProfileProperties(gameprofile1, true);
                    }

                    return gameprofile1;
                }
            } else {
                return gameprofile;
            }
        } else {
            return gameprofile;
        }
    }

    public int getSkullType() {
        return this.a;
    }

    public void setRotation(int i) {
        this.rotation = i;
    }

    public void a(EnumBlockMirror enumblockmirror) {
        if (this.world != null && this.world.getType(this.getPosition()).get(BlockSkull.FACING) == EnumDirection.UP) {
            this.rotation = enumblockmirror.a(this.rotation, 16);
        }

    }

    public void a(EnumBlockRotation enumblockrotation) {
        if (this.world != null && this.world.getType(this.getPosition()).get(BlockSkull.FACING) == EnumDirection.UP) {
            this.rotation = enumblockrotation.a(this.rotation, 16);
        }

    }
}
