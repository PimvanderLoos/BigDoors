package nl.pim16aap2.bigDoors.util;

import org.bukkit.Bukkit;

/**
 * Represents a Minecraft version.
 */
public final class MinecraftVersion
{
    /**
     * The current Minecraft version.
     */
    public static final MinecraftVersion CURRENT_VERSION = getCurrentVersion();

    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Creates a version object with the given major, minor and patch version.
     * <p>
     * For example, if major is 1, minor is 20 and patch is 5, this will create a version object representing version 1.20.5.
     *
     * @param major The major version.
     * @param minor The minor version.
     * @param patch The patch version.
     */
    public MinecraftVersion(int major, int minor, int patch)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Creates a version object with the given major version and minor version. The patch version is set to 0.
     * <p>
     * For example, if major is 1 and minor is 20, this will create a version object representing version 1.20.0.
     *
     * @param major The major version.
     * @param minor The minor version.
     */
    public MinecraftVersion(int major, int minor)
    {
        this(major, minor, 0);
    }

    /**
     * Checks if the current version is at least the given version, inclusive.
     * <p>
     * For example, if the current version is 1.20.5, this method will return true if the provided version is at most 1.20.5.
     *
     * @param major The major version to compare to.
     * @param minor The minor version to compare to.
     * @param patch The patch version to compare to.
     * @return True if the current version is at least the given version, false otherwise.
     */
    public boolean isAtLeast(int major, int minor, int patch)
    {
        if (this.major > major)
            return true;
        if (this.major < major)
            return false;

        if (this.minor > minor)
            return true;
        if (this.minor < minor)
            return false;

        return this.patch >= patch;
    }

    /**
     * Checks if the current version is at least the given version, inclusive. The patch version is considered to be 0.
     *
     * @param major The major version to compare to.
     * @param minor The minor version to compare to.
     * @return True if the current version is at least the given version, false otherwise.
     */
    public boolean isAtLeast(int major, int minor)
    {
        return isAtLeast(major, minor, 0);
    }

    /**
     * Checks if the current version is at least the given version, inclusive.
     * <p>
     * For example, if the current version is 1.20.5, this method will return true if the provided version is at most 1.20.5.
     *
     * @param other The version to compare to.
     * @return True if the current version is at least the given version, false otherwise.
     */
    public boolean isAtLeast(MinecraftVersion other)
    {
        return isAtLeast(other.major, other.minor, other.patch);
    }

    /**
     * Checks if the current version is at most the given version, exclusive.
     *
     * @param other The version to compare to, exclusive.
     * @return True if the current version is at most the given version, false otherwise.
     */
    public boolean isOlderThan(MinecraftVersion other)
    {
        return !isAtLeast(other);
    }

    /**
     * Checks if the current version is between the minimum and maximum version, inclusive.
     *
     * @param minVersion The minimum version, inclusive.
     * @param maxVersion The maximum version, inclusive.
     * @return True if this version is between the minimum and maximum version, false otherwise.
     */
    public boolean isBetween(MinecraftVersion minVersion, MinecraftVersion maxVersion)
    {
        if (!isAtLeast(minVersion))
            return false;
        return equals(maxVersion) || isOlderThan(maxVersion);
    }

    /**
     * Gets the major version.
     *
     * @return The major version.
     */
    public int getMajor()
    {
        return major;
    }

    /**
     * Gets the minor version.
     *
     * @return The minor version.
     */
    public int getMinor()
    {
        return minor;
    }

    /**
     * Gets the patch version.
     *
     * @return The patch version.
     */
    public int getPatch()
    {
        return patch;
    }

    private static MinecraftVersion getCurrentVersion()
    {
        final String versionString = Bukkit.getServer().getBukkitVersion();
        // Parse e.g. "1.20.6-R0.1-SNAPSHOT" or "1.20-R0.1-SNAPSHOT" into [1, 20, 6] or [1, 20, 0] respectively.
        final String[] versionParts = versionString.split("[.\\-]");

        return new MinecraftVersion(
            Integer.parseInt(versionParts[0]),
            Integer.parseInt(versionParts[1]),
            versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0);
    }

    @Override
    public int hashCode()
    {
        int hash = 17;
        hash = 31 * hash + major;
        hash = 31 * hash + minor;
        hash = 31 * hash + patch;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final MinecraftVersion other = (MinecraftVersion) obj;
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public String toString()
    {
        return major + "_" + minor + "_" + patch;
    }
}
