package nl.pim16aap2.bigDoors.compatibility;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

interface IFakePlayerInstantiator
{
    @Nullable Player getFakePlayer(OfflinePlayer oPlayer, String playerName, Location location);
}
