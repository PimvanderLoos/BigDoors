package nl.pim16aap2.bigdoors.spigot.util.implementations;

import nl.pim16aap2.bigdoors.api.IConfigReader;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

/**
 * Spigot implementation for {@link IConfigReader}.
 *
 * @author Pim
 */
public record ConfigReaderSpigot(FileConfiguration config) implements IConfigReader
{
    @Override
    public @Nullable Object get(String path, @Nullable Object fallback)
    {
        return config.get(path, fallback);
    }
}
