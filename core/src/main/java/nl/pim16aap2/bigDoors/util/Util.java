package nl.pim16aap2.bigDoors.util;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.hash.Hashing;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.semver4j.Semver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util
{
    private static final Set<Material> WHITELIST = EnumSet.noneOf(Material.class);
    private static final Set<Material> BLACKLIST = EnumSet.noneOf(Material.class);
    private static final Set<Material> DESTROYLIST = EnumSet.noneOf(Material.class);
    private static final Map<DoorDirection, RotateDirection> doorDirectionMapper = new EnumMap<>(DoorDirection.class);
    private static final Map<RotateDirection, DoorDirection> rotateDirectionMapper = new EnumMap<>(RotateDirection.class);
    static
    {
        doorDirectionMapper.put(DoorDirection.NORTH, RotateDirection.NORTH);
        doorDirectionMapper.put(DoorDirection.EAST, RotateDirection.EAST);
        doorDirectionMapper.put(DoorDirection.SOUTH, RotateDirection.SOUTH);
        doorDirectionMapper.put(DoorDirection.WEST, RotateDirection.WEST);

        rotateDirectionMapper.put(RotateDirection.NORTH, DoorDirection.NORTH);
        rotateDirectionMapper.put(RotateDirection.EAST, DoorDirection.EAST);
        rotateDirectionMapper.put(RotateDirection.SOUTH, DoorDirection.SOUTH);
        rotateDirectionMapper.put(RotateDirection.WEST, DoorDirection.WEST);
    }

    private Util()
    {
        // STAY OUT!
        throw new IllegalAccessError();
    }

    public static void processConfig(ConfigLoader configLoader)
    {
        WHITELIST.clear();
        BLACKLIST.clear();
        DESTROYLIST.clear();

        WHITELIST.addAll(configLoader.getWhitelist());
        BLACKLIST.addAll(configLoader.getBlacklist());
        DESTROYLIST.addAll(configLoader.getDestroyList());

        final boolean mcMMO = checkMcMMO();

        for (Material mat : Material.values())
        {
            if (WHITELIST.contains(mat))
                continue;
            if ((mcMMO && isOre(mat)) ||
                (!Util.isAllowedBlockBackDoor(mat)))
                BLACKLIST.add(mat);
        }

        DESTROYLIST.add(Material.AIR);
        final @Nullable Material caveAir = XMaterial.CAVE_AIR.parseMaterial();
        if (caveAir != null)
            DESTROYLIST.add(caveAir);
    }

    private static boolean checkMcMMO()
    {
        final boolean mcMMO = Bukkit.getPluginManager().isPluginEnabled("mcMMO");
        if (mcMMO)
            BigDoors.get().getMyLogger().warn("mcMMO detected! All ores are blacklisted to avoid item duplication! " +
                                                  "This can be overridden in the config.");
        return mcMMO;
    }

    public static boolean isPosInCuboid(Location pos, Location min, Location max)
    {
        return pos.getBlockX() >= min.getBlockX() && pos.getBlockX() <= max.getBlockX() &&
            pos.getBlockY() >= min.getBlockY() && pos.getBlockY() <= max.getBlockY() &&
            pos.getBlockZ() >= min.getBlockZ() && pos.getBlockZ() <= max.getBlockZ();
    }

    public static @Nullable <T> T firstNonNull(Supplier<T>... suppliers)
    {
        for (Supplier<T> supplier : suppliers)
        {
            T ret = supplier.get();
            if (ret != null)
                return ret;
        }
        return null;
    }

    public static Optional<DoorDirection> getDoorDirection(RotateDirection rot)
    {
        return Optional.ofNullable(rot == null ? null : rotateDirectionMapper.get(rot));
    }

    public static RotateDirection getRotateDirection(DoorDirection dir)
    {
        if (dir == null)
            return RotateDirection.NONE;
        final RotateDirection mapped = doorDirectionMapper.get(dir);
        if (mapped == null)
            throw new IllegalStateException("Failed to find rotate direction for direction: " + dir);
        return mapped;
    }

    // Send a message to a player in a specific color.
    public static void messagePlayer(Player player, ChatColor color, String s)
    {
        player.sendMessage(color + s);
    }

    public static String throwableToString(Throwable t)
    {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void broadcastMessage(String message)
    {
        if (ConfigLoader.DEBUG)
            Bukkit.broadcastMessage(message);
    }

    public static String locIntToString(Location loc)
    {
        return String.format("(%d;%d;%d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static String locDoubleToString(Location loc)
    {
        return String.format("(%.2f;%.2f;%.2f)", loc.getX(), loc.getY(), loc.getZ());
    }

    public static long chunkHashFromLocation(Location loc)
    {
        return chunkHashFromLocation(loc.getBlockX(), loc.getBlockZ(), loc.getWorld().getUID());
    }

    public static long chunkHashFromLocation(int x, int z, UUID worldUUID)
    {
        int chunk_X = x >> 4;
        int chunk_Z = z >> 4;
        long hash = 3;
        hash = 19 * hash + worldUUID.hashCode();
        hash = 19 * hash + (int) (Double.doubleToLongBits(chunk_X) ^ (Double.doubleToLongBits(chunk_X) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(chunk_Z) ^ (Double.doubleToLongBits(chunk_Z) >>> 32));
        return hash;
    }

    public static long locationHash(Location loc)
    {
        return loc.hashCode();
    }

    public static long locationHash(int x, int y, int z, UUID worldUUID)
    {
        return locationHash(new Location(Bukkit.getWorld(worldUUID), x, y, z));
    }

    static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom srnd = new SecureRandom();
    static Random rnd = new Random();

    public static String randomString(int length)
    {
        StringBuilder sb = new StringBuilder(length);
        for (int idx = 0; idx != length; ++idx)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public static String secureRandomString(int length)
    {
        StringBuilder sb = new StringBuilder(length);
        for (int idx = 0; idx != length; ++idx)
            sb.append(chars.charAt(srnd.nextInt(chars.length())));
        return sb.toString();
    }

    public static String readSHA256FromURL(final URL url)
    {
        try (Scanner scanner = new Scanner(url.openStream()))
        {
            String hash = scanner.nextLine();
            return hash.length() == 64 ? hash : "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private static final Pattern VERSION_CLEANUP = Pattern.compile("\\d+(\\.\\d+)+");

    /**
     * Gets the 'cleaned' version number of the current version. E.g. "Alpha
     * 0.1.8.22 (b620)" would return "0.1.8.22".
     *
     * @return The 'cleaned' version number of this version.
     */
    public static String getCleanedVersionString()
    {
        return getCleanedVersionString(BigDoors.get().getDescription().getVersion());
    }

    /**
     * Gets the 'cleaned' version number of the specified version. E.g. "Alpha
     * 0.1.8.22 (b620)" would return "0.1.8.22".
     *
     * @return The 'cleaned' version number of this version.
     */
    public static String getCleanedVersionString(String version)
    {
        Matcher matcher = VERSION_CLEANUP.matcher(version);
        if (!matcher.find())
            return "";
        return matcher.group(0);
    }

    public static String getSHA256(final File file) throws IOException
    {
        return com.google.common.io.Files.hash(file, Hashing.sha256()).toString();
    }

    public static String nameFromUUID(UUID playerUUID)
    {
        if (playerUUID == null)
            return null;
        String output = null;
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            output = player.getName();
        else
            output = Bukkit.getOfflinePlayer(playerUUID).getName();
        return output;
    }

    public static String playerUUIDStrFromString(String input)
    {
        UUID playerUUID = playerUUIDFromString(input);
        return playerUUID == null ? null : playerUUID.toString();
    }

    public static UUID playerUUIDFromString(String input)
    {
        Player player = null;
        player = Bukkit.getPlayer(input);
        if (player == null)
            try
            {
                player = Bukkit.getPlayer(UUID.fromString(input));
            }
            // Not doing anything with catch because I really couldn't care less if it
            // didn't work.
            catch (Exception e)
            {
            }
        if (player != null)
            return player.getName().equals(input) ? player.getUniqueId() : null;

        OfflinePlayer offPlayer = null;
        try
        {
            offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(input));
        }
        // Not doing anything with catch because I really couldn't care less if it
        // didn't work.
        catch (Exception e)
        {
        }
        if (offPlayer != null)
            return offPlayer.getName().equals(input) ? offPlayer.getUniqueId() : null;
        return null;
    }

    public static String getBasicDoorInfo(Door door)
    {
        return String.format("%5d (%d): %s", door.getDoorUID(), door.getPermission(), door.getName());
    }

    public static String getFullDoorInfo(Door door)
    {

        return door == null ? "Door not found!" :
               door.getDoorUID() + ": " + door.getName()
                   + ", Min("
                       + door.getMinimum().getBlockX() + ";"
                       + door.getMinimum().getBlockY() + ";"
                       + door.getMinimum().getBlockZ() + ")"
                   + ", Max("
                       + door.getMaximum().getBlockX() + ";"
                       + door.getMaximum().getBlockY() + ";"
                       + door.getMaximum().getBlockZ() + ")"
                   + ", Engine("
                       + door.getEngine().getBlockX() + ";"
                       + door.getEngine().getBlockY() + ";"
                       + door.getEngine().getBlockZ() + ")"
                   + ", "
                   + (door.isLocked() ? "" : "NOT ") + "locked"
                   + "; Type=" + door.getType()
                   + (door.getEngSide() == null ? "" :
                      ("; EngineSide = " + door.getEngSide().toString()
                          + "; doorLen = " + door.getLength()))
                   + ", PowerBlockPos = ("
                       + door.getPowerBlockLoc().getBlockX() + ";"
                       + door.getPowerBlockLoc().getBlockY() + ";"
                       + door.getPowerBlockLoc().getBlockZ() + ") = ("
                       + door.getPowerBlockChunkHash() + ")"
                   + ". It is " + (door.isOpen() ? "OPEN." : "CLOSED.")
                   + " OpenDir = " + door.getOpenDir().toString()
                   + ", Looking " + door.getLookingDir().toString()
                   + ". It " + (door.getAutoClose() == -1 ? "does not auto close." :
                                ("auto closes after " + door.getAutoClose() + " seconds."))
                   + " It will " + (door.bypassProtections() ? "" : "NOT ") + "bypass protections."
            ;
    }

    // Play sound at a location.
    public static void playSound(Location loc, String sound, float volume, float pitch)
    {
        final int range = BigDoors.get().getConfigLoader().getSoundRange();
        if (range < 1)
            return;

        BigDoors.getScheduler().runTask(loc, () ->
        {
            for (Entity ent : loc.getWorld().getNearbyEntities(loc, range, range, range))
                if (ent instanceof Player)
                    ((Player) ent).playSound(loc, sound, volume, pitch);
        });
    }

    public static int getMaxDoorsForPlayer(Player player)
    {
        if (player.isOp())
            return -1;
        return getHighestPermissionSuffix(player, "bigdoors.own.");
    }

    public static int getMaxDoorSizeForPlayer(Player player)
    {
        if (player.isOp())
            return -1;
        return getHighestPermissionSuffix(player, "bigdoors.maxsize.");
    }

    /**
     * Gets the lowest value from a number of integers where value >= 0.
     *
     * @param values The integers for which to find the lowest positive value.
     * @return The lowest positive value of the provided integers or -1 if all values were negative.
     */
    public static int minPositive(final int... values)
    {
        boolean foundPositive = false;
        int lowest = Integer.MAX_VALUE;
        for (int value : values)
        {
            if (value < 0)
                continue;
            foundPositive = true;
            if (value < lowest)
                lowest = value;
        }
        return foundPositive ? lowest : -1;
    }

    private static int getHighestPermissionSuffix(Player player, String permissionNode)
    {
        int ret = -1;
        for (PermissionAttachmentInfo perms : player.getEffectivePermissions())
            if (perms.getPermission().startsWith(permissionNode))
                try
                {
                    ret = Math.max(ret, Integer.parseInt(perms.getPermission().split(permissionNode)[1]));
                }
                catch (Exception e)
                {
                }
        return ret;
    }

    public static int tickRateFromSpeed(double speed)
    {
        int tickRate;
        if (speed > 9)
            tickRate = 1;
        else if (speed > 7)
            tickRate = 2;
        else if (speed > 6)
            tickRate = 3;
        else
            tickRate = 4;
        return tickRate;
    }

    // Return {time, tickRate, distanceMultiplier} for a given door size.
    public static double[] calculateTimeAndTickRate(int doorSize, double time, double speedMultiplier, double baseSpeed)
    {
        double[] ret = new double[3];
        double distance = Math.PI * doorSize / 2;
        if (time == 0.0)
            time = baseSpeed + doorSize / 3.5;
        double speed = distance / time;
        if (speedMultiplier != 1.0 && speedMultiplier != 0.0)
        {
            speed *= speedMultiplier;
            time = distance / speed;
        }

        // Too fast or too slow!
        double maxSpeed = 11;
        if (speed > maxSpeed || speed <= 0)
            time = distance / maxSpeed;

        double distanceMultiplier = speed > 4 ? 1.01 : speed > 3.918 ? 1.08 : speed > 3.916 ? 1.10 :
            speed > 2.812 ? 1.12 : speed > 2.537 ? 1.19 : speed > 2.2 ? 1.22 : speed > 2.0 ? 1.23 :
            speed > 1.770 ? 1.25 : speed > 1.570 ? 1.28 : 1.30;
        ret[0] = time;
        ret[1] = tickRateFromSpeed(speed);
        ret[2] = distanceMultiplier;
        return ret;
    }

    public static double doubleFromString(String input, double defaultVal)
    {
        try
        {
            return input == null ? defaultVal : Double.parseDouble(input);
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }

    public static long longFromString(String input, long defaultVal)
    {
        try
        {
            return input == null ? defaultVal : Long.parseLong(input);
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }

    // Send a message to a player.
    public static void messagePlayer(Player player, String s)
    {
        messagePlayer(player, ChatColor.WHITE, s);
    }

    // Send an array of messages to a player.
    public static void messagePlayer(Player player, String[] s)
    {
        String message = "";
        for (String str : s)
            message += str + "\n";
        messagePlayer(player, message);
    }

    // Send an array of messages to a player.
    public static void messagePlayer(Player player, ChatColor color, String[] s)
    {
        String message = "";
        for (String str : s)
            message += str + "\n";
        messagePlayer(player, color, message);
    }

    // Swap min and max values for type mode (0/1/2 -> X/Y/Z) for a specified door.
    public static void swap(Door door, int mode)
    {
        Location newMin = door.getMinimum();
        Location newMax = door.getMaximum();
        double temp;
        switch (mode)
        {
        case 0:
            temp = door.getMaximum().getX();
            newMax.setX(newMin.getX());
            newMin.setX(temp);
            break;
        case 1:
            temp = door.getMaximum().getY();
            newMax.setY(newMin.getY());
            newMin.setY(temp);
            break;
        case 2:
            temp = door.getMaximum().getZ();
            newMax.setZ(newMin.getZ());
            newMin.setZ(temp);
            break;
        }
    }

    private static @Nullable XMaterial matchXMaterial(Material mat)
    {
        try
        {
            return XMaterial.matchXMaterial(mat);
        }
        catch (Exception e)
        {
            BigDoors.get().getMyLogger().warn("Could not determine material of mat: " + mat.name() +
                                                  ". Reason: " + e.getMessage());
            return null;
        }
    }

    public static boolean isAirOrWater(Material mat)
    {
        final @Nullable XMaterial xMat = matchXMaterial(mat);
        return xMat == XMaterial.AIR || xMat == XMaterial.CAVE_AIR || xMat == XMaterial.WATER || xMat == XMaterial.LAVA;
    }

    /**
     * Checks if a given material can be overwritten by a door toggle.
     * <p>
     * For example, air and water can be overwritten, but stuff like snow might be as well.
     *
     * @param mat The material to check.
     * @return True if the material can be overwritten by a door toggle.
     */
    public static boolean canOverwriteMaterial(Material mat)
    {
        return DESTROYLIST.contains(mat);
    }

    // Certain materials can rotate, but they don't rotate in exactly the same way.
    public static int canRotate(Material mat)
    {
        if (mat.toString().endsWith("_WALL"))
            return 5;

        if (mat.toString().endsWith("BUTTON") || mat.toString().endsWith("RAIL") ||
            mat.toString().endsWith("DOOR") || mat.toString().endsWith("_HEAD") ||
            mat.toString().endsWith("_SIGN") || mat.toString().endsWith("_BANNER") ||
            mat.toString().endsWith("FENCE_GATE"))
            return 8;

        // Panes only have to rotate on 1.13+. On versions before, rotating it only changes its color...
        if ((BigDoors.isOnFlattenedVersion()) && (mat.toString().endsWith("GLASS_PANE")))
            return 3;

        final @Nullable XMaterial xmat = matchXMaterial(mat);
        if (xmat == null)
            return 0;

        // All the pre-1.13 stairs: (new stairs are handled differently)
        if (!BigDoors.isOnFlattenedVersion() &&
            (xmat.equals(XMaterial.OAK_STAIRS) || xmat.equals(XMaterial.COBBLESTONE_STAIRS) ||
             xmat.equals(XMaterial.BRICK_STAIRS) || xmat.equals(XMaterial.STONE_BRICK_STAIRS) ||
             xmat.equals(XMaterial.NETHER_BRICK_STAIRS) || xmat.equals(XMaterial.SANDSTONE_STAIRS) ||
             xmat.equals(XMaterial.SPRUCE_STAIRS) || xmat.equals(XMaterial.BIRCH_STAIRS) ||
             xmat.equals(XMaterial.JUNGLE_STAIRS) || xmat.equals(XMaterial.QUARTZ_STAIRS) ||
             xmat.equals(XMaterial.ACACIA_STAIRS) || xmat.equals(XMaterial.DARK_OAK_STAIRS) ||
             xmat.equals(XMaterial.RED_SANDSTONE_STAIRS) || xmat.equals(XMaterial.PURPUR_STAIRS)))
            return 2;

        if (mat.toString().endsWith("STAIRS"))
            return 9;

        if (xmat.equals(XMaterial.ACACIA_LOG) || xmat.equals(XMaterial.BIRCH_LOG) ||
            xmat.equals(XMaterial.DARK_OAK_LOG) || xmat.equals(XMaterial.JUNGLE_LOG) ||
            xmat.equals(XMaterial.OAK_LOG) || xmat.equals(XMaterial.SPRUCE_LOG))
            return 1;
        if (xmat.equals(XMaterial.ANVIL))
            return 4;
        if (xmat.equals(XMaterial.STRIPPED_ACACIA_LOG) || xmat.equals(XMaterial.STRIPPED_BIRCH_LOG) ||
            xmat.equals(XMaterial.STRIPPED_SPRUCE_LOG) || xmat.equals(XMaterial.STRIPPED_DARK_OAK_LOG) ||
            xmat.equals(XMaterial.STRIPPED_JUNGLE_LOG) || xmat.equals(XMaterial.STRIPPED_OAK_LOG) ||
            xmat.equals(XMaterial.CHAIN))
            return 6;
        if (xmat.equals(XMaterial.END_ROD))
            return 7;
        if (xmat.equals(XMaterial.LIGHTNING_ROD) || xmat.equals(XMaterial.REDSTONE_WIRE) ||
            xmat.equals(XMaterial.REPEATER) || xmat.equals(XMaterial.TRIPWIRE_HOOK) ||
            xmat.equals(XMaterial.WALL_TORCH) || xmat.equals(XMaterial.SOUL_WALL_TORCH) ||
            xmat.equals(XMaterial.REDSTONE_WALL_TORCH) || xmat.equals(XMaterial.VINE) ||
            xmat.equals(XMaterial.COMPARATOR) || xmat.equals(XMaterial.LADDER) ||
            xmat.equals(XMaterial.LEVER) || xmat.equals(XMaterial.BIG_DRIPLEAF) ||
            xmat.equals(XMaterial.BIG_DRIPLEAF_STEM) || xmat.equals(XMaterial.SMALL_DRIPLEAF) ||
            xmat.equals(XMaterial.SCULK_VEIN))
            return 8;
        return 0;
    }

    /**
     * Checks if a block can be animated or not.
     *
     * @param mat The material of the block to analyze.
     * @return True if the material can be animated.
     */
    public static boolean isAllowedBlock(Material mat)
    {
        if (isAirOrWater(mat))
            return false;
        return WHITELIST.contains(mat) || !BLACKLIST.contains(mat);
    }

    public static boolean isOre(Material mat)
    {
        final @Nullable XMaterial xMat = matchXMaterial(mat);
        if (xMat == null)
            return false;
        return xMat == XMaterial.ANCIENT_DEBRIS || xMat.name().endsWith("_ORE");
    }

    // Certain blocks don't work in doors, so don't allow their usage.
    public static boolean isAllowedBlockBackDoor(Material mat)
    {
        if (BigDoors.get().getConfigLoader().getBlacklist().contains(mat))
            return false;

        String name = mat.toString();

        if (name.endsWith("SLAB") || name.endsWith("STAIRS") || name.endsWith("WALL"))
            return true;

        if (name.contains("POLISHED") || name.contains("SMOOTH") ||
            name.contains("BRICKS") || name.contains("DEEPSLATE"))
            return true;

        if (name.endsWith("TRAPDOOR"))
            return BigDoors.SERVER_VERSION.isGreaterThanOrEqualTo(Semver.of(1, 18, 0));

        if (name.endsWith("BANNER") || name.endsWith("SHULKER_BOX") || name.endsWith("DOOR") ||
            name.endsWith("BED") || name.endsWith("SIGN") || name.endsWith("HEAD") || name.endsWith("SKULL"))
            return false;

        if (name.endsWith("CARPET") || name.endsWith("BUTTON") || name.endsWith("PRESSURE_PLATE") ||
            name.endsWith("SAPLING") || name.endsWith("TORCH") || name.endsWith("RAIL") || name.endsWith("TULIP"))
            return BigDoors.SERVER_VERSION.isGreaterThanOrEqualTo(Semver.of(1, 18, 0));

        final @Nullable XMaterial xmat = matchXMaterial(mat);
        if (xmat == null)
            return false;

        switch (xmat)
        {
        case CAKE:
        case LEVER:
        case REDSTONE_WIRE:
        case TRIPWIRE:
        case TRIPWIRE_HOOK:
        case BROWN_MUSHROOM:
        case RED_MUSHROOM:
        case DEAD_BUSH:
        case FERN:
        case LARGE_FERN:
        case ROSE_BUSH:
        case ATTACHED_MELON_STEM:
        case ATTACHED_PUMPKIN_STEM:
        case WHITE_TULIP:
        case LILY_PAD:
        case SUGAR_CANE:
        case PUMPKIN_STEM:
        case NETHER_WART:
        case NETHER_WART_BLOCK:
        case VINE:
        case CHORUS_FLOWER:
        case CHORUS_FRUIT:
        case CHORUS_PLANT:
        case SUNFLOWER:
        case REPEATER:
        case COMPARATOR:
        case SEA_PICKLE:
        case POPPY:
        case BLUE_ORCHID:
        case ALLIUM:
        case AZURE_BLUET:
        case OXEYE_DAISY:
        case LILAC:
        case PEONY:
        case SHORT_GRASS:
        case TALL_GRASS:
        case SEAGRASS:
        case TALL_SEAGRASS:
        case LADDER:
        case DANDELION:
        case CORNFLOWER:
        case LILY_OF_THE_VALLEY:
        case WITHER_ROSE:
        case SWEET_BERRY_BUSH:
        case LANTERN:
        case BELL:
        case CAVE_VINES:
        case CAVE_VINES_PLANT:
        case GLOW_LICHEN:
        case MOSS_CARPET:
        case AMETHYST_CLUSTER:
        case BIG_DRIPLEAF:
        case BIG_DRIPLEAF_STEM:
            return BigDoors.SERVER_VERSION.isGreaterThanOrEqualTo(Semver.of(1, 18, 0));



        case AIR:
        case WATER:
        case LAVA:

        case GLOW_ITEM_FRAME:
        case ITEM_FRAME:

        case ARMOR_STAND:
        case BREWING_STAND:
        case CAULDRON:
        case CHEST:
        case DROPPER:
        case DRAGON_EGG:
        case ENDER_CHEST:
        case HOPPER:
        case JUKEBOX:
        case PAINTING:

        case SPAWNER:
        case FURNACE:
        case FURNACE_MINECART:

        case REDSTONE:
        case TRAPPED_CHEST:

        case COMMAND_BLOCK:
        case COMMAND_BLOCK_MINECART:

        case STRUCTURE_BLOCK:
        case STRUCTURE_VOID:

            /* 1.14 start */
        case BARREL:

        case BLAST_FURNACE:
        case CARTOGRAPHY_TABLE:
        case COMPOSTER:
        case FLETCHING_TABLE:

        case GRINDSTONE:
        case JIGSAW:
        case LECTERN:
        case LOOM:
        case SMITHING_TABLE:
        case STONECUTTER:
            /* 1.14 end */

            /* 1.15 start */
        case BEEHIVE:
        case BEE_NEST:
            /* 1.15 end */

        case FROGSPAWN:

            return false;
        default:
            return true;
        }
    }

    public static Optional<UUID> parseUUID(final String str)
    {
        try
        {
            return Optional.of(UUID.fromString(str));
        }
        catch (IllegalArgumentException e)
        {
            return Optional.empty();
        }
    }

    public static OptionalInt parseInt(final String str)
    {
        if (str == null)
            return OptionalInt.empty();
        try
        {
            return OptionalInt.of(Integer.parseInt(str));
        }
        catch (NumberFormatException e)
        {
            return OptionalInt.empty();
        }
    }

    public static boolean between(int value, int start, int end)
    {
        return value <= end && value >= start;
    }

    /**
     * Logs a throwable and returns a fallback value.
     * <p>
     * Mostly useful for {@link CompletableFuture#exceptionally(Function)}.
     *
     * @param throwable
     *     The throwable to send to the logger.
     * @param fallback
     *     The fallback value to return.
     * @param <T>
     *     The type of the fallback value.
     * @return The fallback value.
     */
    @Contract("_, !null -> !null")
    public static <T> T exceptionally(Throwable throwable, @Nullable T fallback)
    {
        BigDoors.get().getMyLogger().log(throwable);
        return fallback;
    }

    /**
     * See {@link #exceptionally(Throwable, Object)} with a null fallback value.
     *
     * @return Always null
     */
    public static @Nullable <T> T exceptionally(Throwable throwable)
    {
        return exceptionally(throwable, null);
    }

    /**
     * Maps a group of CompletableFutures to a single CompletableFuture with a list of results.
     * <p>
     * The result will wait for each of the futures to complete and once all of them have completed gather the results
     * and return the list.
     * <p>
     * Each entry in the list maps to the result of a single future.
     *
     * @param futures
     *     The completable futures whose results to collect into a list.
     * @param <T>
     *     The type of data.
     * @return The list of results obtained from the CompletableFutures in the same order as provided. The list will
     * have a size that matches the number of input futures.
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> getAllCompletableFutureResults(CompletableFuture<T>... futures)
    {
        final CompletableFuture<Void> result = CompletableFuture.allOf(futures);
        return result.thenApply(
            ignored ->
            {
                final List<T> ret = new ArrayList<>(futures.length);
                for (final CompletableFuture<T> future : futures)
                    ret.add(future.join());
                return ret;
            }).exceptionally(throwable -> exceptionally(throwable, Collections.emptyList()));
    }

    /**
     * Converts a {@link Future} into a {@link CompletableFuture} by waiting for it asynchronously.
     *
     * @param fut
     *     The future to convert.
     * @param timeout
     *     The amount of time to wait for the future. See {@link Future#get(long, TimeUnit)}. If this value <= 0, no
     *     timeout is used at all.
     * @param unit
     *     The unit of the timeout value. Only used when timeout > 0.
     * @param fallback
     *     The fallback value to return in case an error was encountered while getting the future. This includes
     *     timeouts and interrupts.
     * @return The CompletableFuture waiting for the Future to complete.
     * @param <T> The type of the data in the (completable) future.
     */
    public static <T> CompletableFuture<T> futureToCompletableFuture(
        Future<T> fut, long timeout, TimeUnit unit, @Nullable T fallback)
    {
        return CompletableFuture.supplyAsync(
            () ->
            {
                try
                {
                    if (timeout > 0)
                        return fut.get(timeout, unit);
                    return fut.get();
                }
                catch (ExecutionException e)
                {
                    BigDoors.get().getMyLogger().log("Ran into error while getting future result!", e);
                    throw new RuntimeException(e);
                }
                catch (InterruptedException e)
                {
                    BigDoors.get().getMyLogger().log("Thread was interrupted waiting for future result!", e);
                    Thread.currentThread().interrupt();
                }
                catch (TimeoutException e)
                {
                    BigDoors.get().getMyLogger().log(
                        "Timed out after " + timeout + " " + unit.name() + " for future!", e);
                    throw new RuntimeException(e);
                }
                return fallback;
            }).exceptionally(throwable -> exceptionally(throwable, fallback));
    }

    /**
     * Runs a task on the main thread.
     * </p>
     * See {@link BukkitScheduler#callSyncMethod(Plugin, Callable)} and
     * {@link #futureToCompletableFuture(Future, long, TimeUnit, Object)}.
     *
     * @param callable
     *     The function to run on the main thread.
     * @param timeout
     *     The amount of time to wait for the future. See {@link Future#get(long, TimeUnit)}. If this value <= 0, no
     *     timeout is used at all.
     * @param unit
     *     The unit of the timeout value. Only used when timeout > 0.
     * @param fallback
     *     The fallback value to return in case an error was encountered while getting the result. This includes
     *     timeouts and interrupts.
     * @return The CompletableFuture waiting for the callable to complete.
     * @param <T> The type of the data provided by the callable.
     */
    public static <T> CompletableFuture<T> runSync(
        Callable<T> callable, long timeout, TimeUnit unit, @Nullable T fallback)
    {
        return futureToCompletableFuture(
            BigDoors.getScheduler().callSyncMethod(callable), timeout, unit, fallback);
    }
}
