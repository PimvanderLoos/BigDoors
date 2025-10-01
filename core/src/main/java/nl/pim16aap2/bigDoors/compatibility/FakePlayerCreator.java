package nl.pim16aap2.bigDoors.compatibility;

import nl.pim16aap2.bigDoors.BigDoors;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Class used to create a fake-online player who is actually offline.
 *
 * @author Pim
 */
public class FakePlayerCreator
{
    public static final String FAKE_PLAYER_METADATA = "isBigDoorsFakePlayer";

    private final @Nullable BiFunction<OfflinePlayer, Location, Player> fakePlayerInstantiator;

    public FakePlayerCreator(final BigDoors plugin)
    {
        fakePlayerInstantiator = createFakePlayerInstantiator(plugin);
    }

    @Nullable Player getFakePlayer(OfflinePlayer oPlayer, Location location)
    {
        return fakePlayerInstantiator == null ? null : fakePlayerInstantiator.apply(oPlayer, location);
    }

    private @Nullable BiFunction<OfflinePlayer, Location, Player> createFakePlayerInstantiator(BigDoors plugin)
    {
        try
        {
            return new FakePlayerClassGenerator(plugin).getInstantiator();

        }
        catch (Exception e)
        {
            new RuntimeException("Failed to create fake player constructor!", e).printStackTrace();
            return null;
        }
    }
}
