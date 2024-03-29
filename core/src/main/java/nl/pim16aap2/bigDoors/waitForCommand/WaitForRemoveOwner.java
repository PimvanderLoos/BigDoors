package nl.pim16aap2.bigDoors.waitForCommand;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.DoorAttribute;
import nl.pim16aap2.bigDoors.util.DoorOwner;
import nl.pim16aap2.bigDoors.util.Util;

public class WaitForRemoveOwner extends WaitForCommand
{
    private long doorUID;

    public WaitForRemoveOwner(BigDoors plugin, Player player, long doorUID)
    {
        super(plugin);
        this.player  = player;
        command = "removeowner";
        this.doorUID = doorUID;
        Util.messagePlayer(player, plugin.getMessages().getString("COMMAND.RemoveOwner.Init"));
        Util.messagePlayer(player, plugin.getMessages().getString("COMMAND.SetBlocksToMove.ListOfOwners"));
        String ownersStr = "";
        ArrayList<DoorOwner> doorOwners = plugin.getCommander().getDoorOwners(doorUID, player.getUniqueId());
        for (DoorOwner owner : doorOwners)
            ownersStr += owner.getPlayerName() + ", ";
        Util.messagePlayer(player, ownersStr);

        plugin.addCommandWaiter(this);
    }

    @Override
    public boolean executeCommand(String[] args)
    {
        if (!plugin.getCommander().hasPermissionForAction(player, doorUID, DoorAttribute.REMOVEOWNER))
            return true;

        // example: /BigDoors removeOwner pim16aap2
        if (args.length == 2)
        {
            UUID playerUUID = plugin.getCommander().playerUUIDFromName(args[1]);
            Door door = plugin.getCommander().getDoor(player.getUniqueId(), doorUID);

            if (playerUUID != null)
            {
                if (plugin.getCommander().removeOwner(door, playerUUID, getPlayer()))
                {
                    Util.messagePlayer(player, plugin.getMessages().getString("COMMAND.RemoveOwner.Success"));
                    isFinished = true;
                    abort();
                    return true;
                }
                Util.messagePlayer(player, plugin.getMessages().getString("COMMAND.RemoveOwner.Fail"));
                abort();
                return true;
            }
            Util.messagePlayer(player, plugin.getMessages().getString("GENERAL.PlayerNotFound") + ": \"" + args[1] + "\"");
            abort();
            return true;
        }
        abort();
        return false;
    }
}
