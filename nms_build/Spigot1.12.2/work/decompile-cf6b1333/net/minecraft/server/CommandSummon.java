package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandSummon extends CommandAbstract {

    public CommandSummon() {}

    public String getCommand() {
        return "summon";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.summon.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 1) {
            throw new ExceptionUsage("commands.summon.usage", new Object[0]);
        } else {
            String s = astring[0];
            BlockPosition blockposition = icommandlistener.getChunkCoordinates();
            Vec3D vec3d = icommandlistener.d();
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;

            if (astring.length >= 4) {
                d0 = b(d0, astring[1], true);
                d1 = b(d1, astring[2], false);
                d2 = b(d2, astring[3], true);
                blockposition = new BlockPosition(d0, d1, d2);
            }

            World world = icommandlistener.getWorld();

            if (!world.isLoaded(blockposition)) {
                throw new CommandException("commands.summon.outOfWorld", new Object[0]);
            } else if (EntityTypes.a.equals(new MinecraftKey(s))) {
                world.strikeLightning(new EntityLightning(world, d0, d1, d2, false));
                a(icommandlistener, (ICommand) this, "commands.summon.success", new Object[0]);
            } else {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (astring.length >= 5) {
                    String s1 = a(astring, 4);

                    try {
                        nbttagcompound = MojangsonParser.parse(s1);
                        flag = true;
                    } catch (MojangsonParseException mojangsonparseexception) {
                        throw new CommandException("commands.summon.tagError", new Object[] { mojangsonparseexception.getMessage()});
                    }
                }

                nbttagcompound.setString("id", s);
                Entity entity = ChunkRegionLoader.a(nbttagcompound, world, d0, d1, d2, true);

                if (entity == null) {
                    throw new CommandException("commands.summon.failed", new Object[0]);
                } else {
                    entity.setPositionRotation(d0, d1, d2, entity.yaw, entity.pitch);
                    if (!flag && entity instanceof EntityInsentient) {
                        ((EntityInsentient) entity).prepare(world.D(new BlockPosition(entity)), (GroupDataEntity) null);
                    }

                    a(icommandlistener, (ICommand) this, "commands.summon.success", new Object[0]);
                }
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, (Collection) EntityTypes.a()) : (astring.length > 1 && astring.length <= 4 ? a(astring, 1, blockposition) : Collections.emptyList());
    }
}
