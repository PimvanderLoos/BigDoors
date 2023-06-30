package nl.pim16aap2.bigDoors.toolUsers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.util.DoorType;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PortcullisCreator extends ToolUser
{
    public PortcullisCreator(BigDoors plugin, Player player, String name)
    {
        super(plugin, player, name, DoorType.PORTCULLIS);
        Util.messagePlayer(player, messages.getString("CREATOR.PORTCULLIS.Init"));
        if (name == null)
            Util.messagePlayer(player, ChatColor.GREEN, messages.getString("CREATOR.GENERAL.GiveNameInstruc"));
        else
            triggerGiveTool();
    }

    @Override
    protected void triggerGiveTool()
    {
        giveToolToPlayer(messages.getString("CREATOR.PORTCULLIS.StickLore").split("\n"),
                         messages.getString("CREATOR.PORTCULLIS.StickReceived").split("\n"));
    }

    @Override
    protected boolean isReadyToCreateDoor()
    {
        return one != null && two != null && engine != null;
    }

    @Override
    protected void triggerFinishUp()
    {
        finishUp(messages.getString("CREATOR.PORTCULLIS.Success"));
    }

    // Make sure the power point is in the middle.
    private void setEngine()
    {
        int xMid = one.getBlockX() + (two.getBlockX() - one.getBlockX()) / 2;
        int zMid = one.getBlockZ() + (two.getBlockZ() - one.getBlockZ()) / 2;
        int yMin = one.getBlockY();
        engine = new Location(one.getWorld(), xMid, yMin, zMid);
    }

    private void selector(Location loc, @Nullable String canBreakBlock)
    {
        if (canBreakBlock != null)
        {
            Util.messagePlayer(player, messages.getString("CREATOR.GENERAL.NoPermissionHere") + " " + canBreakBlock);
            return;
        }

        if (one == null)
        {
            one = loc;
            Util.messagePlayer(player, messages.getString("CREATOR.PORTCULLIS.Step1"));
        }
        else
            two = loc;

        if (one != null && two != null)
        {
            minMaxFix();
            setEngine();
            setIsDone(true);
        }
    }

    // Take care of the selection points.
    @Override
    public void selector(Location loc)
    {
        if (name == null)
        {
            Util.messagePlayer(player, messages.getString("CREATOR.GENERAL.GiveNameInstruc"));
            return;
        }
        plugin.canBreakBlock(player.getUniqueId(), player.getName(), loc)
              .thenApply(canBreakBlock -> BigDoors.getScheduler().runTask(() -> selector(loc, canBreakBlock)));
    }
}
