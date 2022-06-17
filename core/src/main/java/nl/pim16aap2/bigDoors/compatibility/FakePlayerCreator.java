package nl.pim16aap2.bigDoors.compatibility;

import nl.pim16aap2.bigDoors.BigDoors;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

/**
 * Class used to create a fake-online player who is actually offline.
 *
 * @author Pim
 */
public class FakePlayerCreator
{
    public static final String FAKE_PLAYER_METADATA = "isBigDoorsFakePlayer";

    private final @Nullable IFakePlayerInstantiator fakePlayerInstantiator;

    public FakePlayerCreator(final JavaPlugin plugin)
    {
        fakePlayerInstantiator = createFakePlayerInstantiator(plugin);
    }

    @Nullable Player getFakePlayer(OfflinePlayer oPlayer, String playerName, Location location)
    {
        return fakePlayerInstantiator == null ? null :
               fakePlayerInstantiator.getFakePlayer(oPlayer, playerName, location.clone());
    }

    private @Nullable IFakePlayerInstantiator createFakePlayerInstantiator(JavaPlugin plugin)
    {
        try
        {
            // <1.17 does not have access to ByteBuddy as those versions did not have the library loader.
            if (BigDoors.getMCVersion().isAtLeast(BigDoors.MCVersion.v1_17))
                return new GeneratedFakePlayerInstantiator();
            return new FakePlayerInstantiator(plugin);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
