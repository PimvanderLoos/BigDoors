package nl.pim16aap2.bigDoors.compatibility;

import nl.pim16aap2.bigDoors.codegeneration.FakePlayerClassGenerator;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class GeneratedFakePlayerInstantiator implements IFakePlayerInstantiator
{
    private final Constructor<?> ctor;

    GeneratedFakePlayerInstantiator()
        throws Exception
    {
        this.ctor = new FakePlayerClassGenerator().getGeneratedConstructor();
    }

    @Override
    public @Nullable Player getFakePlayer(OfflinePlayer oPlayer, String playerName, Location location)
    {
        try
        {
            return (Player) ctor.newInstance(oPlayer, location);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
