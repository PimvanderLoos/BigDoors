package net.minecraft.server;

import javax.annotation.Nullable;

public class TileEntitySign extends TileEntity {

    public final IChatBaseComponent[] lines = new IChatBaseComponent[] { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    public int f = -1;
    public boolean isEditable = true;
    private EntityHuman h;
    private final CommandObjectiveExecutor i = new CommandObjectiveExecutor();

    public TileEntitySign() {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }

        this.i.b(nbttagcompound);
        return nbttagcompound;
    }

    protected void b(World world) {
        this.a(world);
    }

    public void load(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.load(nbttagcompound);
        ICommandListener icommandlistener = new ICommandListener() {
            public String getName() {
                return "Sign";
            }

            public boolean a(int i, String s) {
                return true;
            }

            public BlockPosition getChunkCoordinates() {
                return TileEntitySign.this.position;
            }

            public Vec3D d() {
                return new Vec3D((double) TileEntitySign.this.position.getX() + 0.5D, (double) TileEntitySign.this.position.getY() + 0.5D, (double) TileEntitySign.this.position.getZ() + 0.5D);
            }

            public World getWorld() {
                return TileEntitySign.this.world;
            }

            public MinecraftServer C_() {
                return TileEntitySign.this.world.getMinecraftServer();
            }
        };

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);

            try {
                this.lines[i] = ChatComponentUtils.filterForDisplay(icommandlistener, ichatbasecomponent, (Entity) null);
            } catch (CommandException commandexception) {
                this.lines[i] = ichatbasecomponent;
            }
        }

        this.i.a(nbttagcompound);
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 9, this.d());
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    public boolean isFilteredNBT() {
        return true;
    }

    public boolean a() {
        return this.isEditable;
    }

    public void a(EntityHuman entityhuman) {
        this.h = entityhuman;
    }

    public EntityHuman e() {
        return this.h;
    }

    public boolean b(final EntityHuman entityhuman) {
        ICommandListener icommandlistener = new ICommandListener() {
            public String getName() {
                return entityhuman.getName();
            }

            public IChatBaseComponent getScoreboardDisplayName() {
                return entityhuman.getScoreboardDisplayName();
            }

            public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

            public boolean a(int i, String s) {
                return i <= 2;
            }

            public BlockPosition getChunkCoordinates() {
                return TileEntitySign.this.position;
            }

            public Vec3D d() {
                return new Vec3D((double) TileEntitySign.this.position.getX() + 0.5D, (double) TileEntitySign.this.position.getY() + 0.5D, (double) TileEntitySign.this.position.getZ() + 0.5D);
            }

            public World getWorld() {
                return entityhuman.getWorld();
            }

            public Entity f() {
                return entityhuman;
            }

            public boolean getSendCommandFeedback() {
                return false;
            }

            public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
                if (TileEntitySign.this.world != null && !TileEntitySign.this.world.isClientSide) {
                    TileEntitySign.this.i.a(TileEntitySign.this.world.getMinecraftServer(), this, commandobjectiveexecutor_enumcommandresult, i);
                }

            }

            public MinecraftServer C_() {
                return entityhuman.C_();
            }
        };
        IChatBaseComponent[] aichatbasecomponent = this.lines;
        int i = aichatbasecomponent.length;

        for (int j = 0; j < i; ++j) {
            IChatBaseComponent ichatbasecomponent = aichatbasecomponent[j];
            ChatModifier chatmodifier = ichatbasecomponent == null ? null : ichatbasecomponent.getChatModifier();

            if (chatmodifier != null && chatmodifier.h() != null) {
                ChatClickable chatclickable = chatmodifier.h();

                if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                    entityhuman.C_().getCommandHandler().a(icommandlistener, chatclickable.b());
                }
            }
        }

        return true;
    }

    public CommandObjectiveExecutor f() {
        return this.i;
    }
}
