package nl.pim16aap2.bigDoors.toolUsers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.util.DoorType;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SlidingDoorCreator extends ToolUser
{
    public SlidingDoorCreator(BigDoors plugin, Player player, String name)
    {
        super(plugin, player, name, DoorType.SLIDINGDOOR);
        Util.messagePlayer(player, messages.getString("CREATOR.SLIDINGDOOR.Init"));
        if (name == null)
            Util.messagePlayer(player, messages.getString("CREATOR.GENERAL.GiveNameInstruc"));
        else
            triggerGiveTool();
    }

    @Override
    protected void triggerGiveTool()
    {
        giveToolToPlayer(messages.getString("CREATOR.SLIDINGDOOR.StickLore").split("\n"),
                         messages.getString("CREATOR.SLIDINGDOOR.StickReceived").split("\n"));
    }

    @Override
    protected boolean isReadyToCreateDoor()
    {
        return one != null && two != null && engine != null;
    }

    @Override
    protected void triggerFinishUp()
    {
        finishUp(messages.getString("CREATOR.SLIDINGDOOR.Success"));
    }

    // Make sure the power point is in the middle.
    private void setEngine()
    {
        int xMid = one.getBlockX() + (two.getBlockX() - one.getBlockX()) / 2;
        int zMid = one.getBlockZ() + (two.getBlockZ() - one.getBlockZ()) / 2;
        int yMin = one.getBlockY();
        engine = new Location(one.getWorld(), xMid, yMin, zMid);
    }

    // Make sure the second position is not the same as the first position
    private boolean isPositionValid(Location loc)
    {
        if (one == null && two == null)
            return true;
        if (one.equals(loc))
            return false;
        return true;
    }

    private void selector(Location loc, @Nullable String canBreakBlock)
    {
        if (canBreakBlock != null)
        {
            Util.messagePlayer(player, messages.getString("CREATOR.GENERAL.NoPermissionHere") + " " + canBreakBlock);
            return;
        }

        if (!isPositionValid(loc))
            return;
        if (one == null)
        {
            one = loc;
            Util.messagePlayer(player, messages.getString("CREATOR.SLIDINGDOOR.Step1"));
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
