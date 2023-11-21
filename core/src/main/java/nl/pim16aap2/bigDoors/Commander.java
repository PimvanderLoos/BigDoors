package nl.pim16aap2.bigDoors;

import nl.pim16aap2.bigDoors.events.DoorDeleteEvent;
import nl.pim16aap2.bigDoors.moveBlocks.BlockMover;
import nl.pim16aap2.bigDoors.storage.sqlite.SQLiteJDBCDriverConnection;
import nl.pim16aap2.bigDoors.util.DoorAttribute;
import nl.pim16aap2.bigDoors.util.DoorDirection;
import nl.pim16aap2.bigDoors.util.DoorOwner;
import nl.pim16aap2.bigDoors.util.Messages;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Commander
{
    private static final DummyMover DUMMYMOVER = new DummyMover();

    private final BigDoors plugin;

    private final Map<Long, BlockMover> busyDoors;
    private final HashMap<UUID, String> players;
    private final SQLiteJDBCDriverConnection db;
    private final Messages messages;

    private volatile boolean animationsAllowed = false;
    private volatile boolean paused = false;

    public Commander(BigDoors plugin, SQLiteJDBCDriverConnection db)
    {
        this.plugin = plugin;
        this.db = db;
        busyDoors = newBusyDoorsMap();
        messages = plugin.getMessages();
        players = new HashMap<>();
    }

    public void prepareDatabaseForV2()
    {
        db.prepareForV2();
    }

    public void emptyBusyDoors()
    {
        busyDoors.clear();
    }

    /**
     * Cancels all active animations.
     * <p>
     * Note that this does not stop the animations from being started again. If that is the desired effect, use
     * {@link #animationsAllowed(boolean)}.
     *
     * @param onDisable Whether this method is called from the onDisable method.
     */
    public void stopMovers(boolean onDisable)
    {
        plugin.getMyLogger().logMessageToLogFile("Stopping all movers; onDisable: " + onDisable);
        plugin.getMyLogger().logMessageToLogFile("");
        Iterator<BlockMover> it = busyDoors.values().iterator();
        while (it.hasNext())
        {
            final BlockMover mover = it.next();
            plugin.getMyLogger().logMessageToLogFileForDoor(mover.getDoor(), "Commander is cancelling mover");
            mover.cancel(onDisable);
        }
    }

    // Check if a door is busy
    public boolean isDoorBusy(long doorUID)
    {
        return busyDoors.containsKey(doorUID);
    }

    /**
     * Checks if a door is currently busy. If it is not, it will be registered as
     * such using a placeholder.
     *
     * @param doorUID The UID of the door to check and potentially register as busy.
     * @return True if the door is already busy and therefore not registered as such.
     */
    public boolean isDoorBusyRegisterIfNot(long doorUID)
    {
        if (!animationsAllowed)
            return true; // 'true' because even though the door isn't busy, it's not allowed to be animated.


        // putIfAbsent returns the result of ConcurrentHashMap#put(key, value)
        // if the key did not exist in the map yet. Otherwise, it returns the
        // mapping of the key.
        // Likewise, the result of #put returns the previous mapping of the
        // key if that existed, and null otherwise.
        // Combined with the fact that null cannot be used as key or as value,
        // a return result of null means that the key was not yet in the
        // list (but is now).
        return busyDoors.putIfAbsent(doorUID, DUMMYMOVER) != null;
    }

    // Set the availability of the door.
    public void setDoorAvailable(long doorUID)
    {
        busyDoors.remove(doorUID);
    }

    public void addBlockMover(BlockMover mover)
        throws IllegalStateException
    {
        if (!animationsAllowed)
            throw new IllegalStateException(String.format(
                "[%s] Failed to add block mover: animations are not allowed at this time.",
                Util.formatDoorInfo(mover.getDoor())
            ));
        busyDoors.replace(mover.getDoorUID(), mover);
    }

    public BlockMover getBlockMover(long doorUID)
    {
        BlockMover mover = busyDoors.get(doorUID);
        return mover instanceof DummyMover ? null : mover;
    }

    public Stream<BlockMover> getBlockMovers()
    {
        return busyDoors.values().stream().filter(BM -> !(BM instanceof DummyMover));
    }

    // Check if the doors are paused.
    public boolean isPaused()
    {
        return paused;
    }

    // Toggle the paused status of all doors.
    public void togglePaused()
    {
        final boolean wasPaused = paused;
        paused = !wasPaused;
    }

    // Print an ArrayList of doors to a player.
    public void printDoors(Player player, ArrayList<Door> doors)
    {
        for (Door door : doors)
            Util.messagePlayer(player, door.toSimpleString());
    }

    /**
     * See {@link #getDoor(String, Player, boolean)} with the bypass disabled.
     */
    public Door getDoor(String doorStr, @Nullable Player player)
    {
        return getDoor(doorStr, player, false);
    }

    /**
     * Gets the door with the given name/UID. If a player object is provided,
     * it will restrict itself to only doors (co-)owned by this player.
     * <p>
     * If bypass is true, door lookups using the door's UID also includes
     * doors that are not owned by the player.
     *
     * @param doorStr The ID of the door, represented either by its name or its UID.
     * @param player  The player for which to retrieve the door. May be null.
     * @param bypass  Whether the player has bypass access to the door.
     *                This has no effect if the player is null.
     * @return The door with the provided name, if exactly one could be found.
     */
    public Door getDoor(String doorStr, @Nullable Player player, final boolean bypass)
    {
        // First try converting the doorStr to a doorUID.
        try
        {
            long doorUID = Long.parseLong(doorStr);
            return db.getDoor(player == null ? null : player.getUniqueId(), doorUID, bypass);
        }
        // If it can't convert to a long, get all doors from the player with the provided name.
        // If there is more than one, tell the player that they are going to have to make a choice.
        catch (NumberFormatException e)
        {
            if (player == null)
                return null;

            final ArrayList<Door> doors = db.getDoors(player.getUniqueId().toString(), doorStr);
            if (doors.size() == 1)
                return doors.get(0);

            if (doors.isEmpty())
                Util.messagePlayer(player, messages.getString("GENERAL.NoDoorsFound"));
            else
                Util.messagePlayer(player, messages.getString("GENERAL.MoreThan1DoorFound"));
            printDoors(player, doors);
            return null;
        }
    }

    public long addDoor(Door newDoor)
    {
        plugin.getPBCache().invalidate(Util.chunkHashFromLocation(newDoor.getPowerBlockLoc()));
        return db.insert(newDoor);
    }

    public long addDoor(Door newDoor, Player player, int permission)
    {
        if (newDoor.getPlayerUUID() != player.getUniqueId())
            newDoor.setPlayerUUID(player.getUniqueId());
        if (newDoor.getPermission() != permission)
            newDoor.setPermission(permission);
        plugin.getPBCache().invalidate(Util.chunkHashFromLocation(newDoor.getPowerBlockLoc()));
        return db.insert(newDoor);
    }

    // Add a door to the db of doors.
    public long addDoor(Door newDoor, Player player)
    {
        return addDoor(newDoor, player, 0);
    }

    private void onDoorDelete(@Nullable Door door)
    {
        if (door == null)
            return;

        Bukkit.getPluginManager().callEvent(new DoorDeleteEvent(door));
        plugin.getPBCache().invalidate(door.getPowerBlockChunkHash());
        if (plugin.getConfigLoader().refundOnDelete())
            plugin.getVaultManager().refundDoor(door);
    }

    public boolean removeDoor(Player player, long doorUID)
    {
        if (!hasPermissionForAction(player, doorUID, DoorAttribute.DELETE))
            return false;
        removeDoor(doorUID);
        return true;
    }

    public void removeDoor(long doorUID)
    {
        onDoorDelete(db.removeDoor(doorUID));
    }

    public void removeDoorsFromWorld(World world)
    {
        db.removeDoorsFromWorld(world).forEach(this::onDoorDelete);
    }

    // Returns the number of doors owner by a player and with a specific name, if
    // provided (can be null).
    public long countDoors(String playerUUID, @Nullable String doorName)
    {
        return db.countDoors(playerUUID, doorName);
    }

    // Returns an ArrayList of doors owner by a player and with a specific name, if
    // provided (can be null).
    public ArrayList<Door> getDoors(String playerUUID, @Nullable String name)
    {
        if (playerUUID == null && name == null)
            return new ArrayList<>();
        return playerUUID == null ? getDoors(name) : db.getDoors(playerUUID, name);
    }

    // Returns an Set of doors.
    // The Set is defined by door UID.
    // The door player is the creator of the door.
    public Set<Door> getDoors()
    {
        return db.getDoors();
    }

    // Returns an ArrayList of doors with a specific name.
    private ArrayList<Door> getDoors(String name)
    {
        return db.getDoors(name);
    }

    // Returns an ArrayList of doors that exist in a specific world.
    public ArrayList<Door> getDoorsInWorld(World world)
    {
        return db.getDoorsInWorld(world);
    }

    // Returns an ArrayList of doors owner by a player and with a specific name, if
    // provided (can be null).
    public ArrayList<Door> getDoorsInRange(String playerUUID, @Nullable String name, int start, int end)
    {
        return db.getDoors(playerUUID, name, start, end);
    }

    public UUID playerUUIDFromName(String playerName)
    {
        UUID uuid = players.entrySet().stream().filter(e -> e.getValue().equals(playerName)).map(Map.Entry::getKey)
            .findFirst().orElse(null);
        if (uuid != null)
            return uuid;

        uuid = db.getUUIDFromName(playerName);
        if (uuid != null)
            return uuid;

        uuid = Util.playerUUIDFromString(playerName);
        if (uuid != null)
            updatePlayer(uuid, playerName);
        return uuid;
    }

    public String playerNameFromUUID(UUID playerUUID)
    {
        // Try from HashSet first; it's the fastest.
        if (players.containsKey(playerUUID))
            return players.get(playerUUID);
        // Then try to get it from an online player.
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            return player.getName();
        // First try to get the player name from the database.
        String name = db.getPlayerName(playerUUID);
        if (name != null)
            return name;
        // As a last resort, try to get the name from an offline player. This is slow
        // af, so last resort.
        name = Util.nameFromUUID(playerUUID);
        // Then place the UUID/String combo in the db. Need moar data!
        updatePlayer(playerUUID, name);
        return name;
    }

    public void updatePlayer(UUID uuid, String playerName)
    {
        db.updatePlayerName(uuid, playerName);
        players.put(uuid, playerName);
    }

    public void updatePlayer(Player player)
    {
        updatePlayer(player.getUniqueId(), player.getName());
    }

    public void removePlayer(Player player)
    {
        players.remove(player.getUniqueId());
    }

    // Get a door with a specific doorUID.
    public Door getDoor(@Nullable UUID playerUUID, long doorUID)
    {
        return getDoor(playerUUID, doorUID, false);
    }

    public Door getDoor(@Nullable UUID playerUUID, long doorUID, final boolean bypass)
    {
        return db.getDoor(playerUUID, doorUID, bypass);
    }

    public boolean hasPermissionNodeForAction(Player player, DoorAttribute atr)
    {
        if (atr == null)
            return false;
        return player.hasPermission(DoorAttribute.getUserPermission(atr)) ||
               (DoorAttribute.getAdminPermission(atr) != null && player.hasPermission(DoorAttribute.getAdminPermission(atr)));
    }

    public boolean hasPermissionForAction(Player player, long doorUID, DoorAttribute atr)
    {
        return hasPermissionForAction(player, doorUID, atr, true);
    }

    public boolean hasPermissionForAction(Player player, long doorUID, DoorAttribute atr, boolean printMessage)
    {
        if (player.isOp() || hasPermissionNodeForAction(player, atr))
            return true;

        int playerPermission = getPermission(player.getUniqueId().toString(), doorUID);
        boolean hasPermission = playerPermission >= 0 && playerPermission <= DoorAttribute.getPermissionLevel(atr);
        if (!hasPermission && printMessage)
            Util.messagePlayer(player, plugin.getMessages().getString("GENERAL.NoPermissionForAction"));
        return hasPermission;
    }

    // Get the permission of a player on a door.
    public int getPermission(String playerUUID, long doorUID)
    {
        return db.getPermission(playerUUID, doorUID);
    }

    // Update the coordinates of a given door.
    public void updateDoorCoords(long doorUID, boolean isOpen, int blockXMin, int blockYMin, int blockZMin,
                                 int blockXMax, int blockYMax, int blockZMax)
    {
        db.updateDoorCoords(doorUID, isOpen, blockXMin, blockYMin, blockZMin, blockXMax, blockYMax, blockZMax, null);
    }

    // Update the coordinates of a given door.
    public void updateDoorCoords(long doorUID, boolean isOpen, int blockXMin, int blockYMin, int blockZMin,
                                 int blockXMax, int blockYMax, int blockZMax, DoorDirection newEngSide)
    {
        db.updateDoorCoords(doorUID, isOpen, blockXMin, blockYMin, blockZMin, blockXMax, blockYMax, blockZMax,
                            newEngSide);
    }

    public void addOwner(UUID playerUUID, Door door)
    {
        addOwner(playerUUID, door, 1);
    }

    public boolean addOwner(UUID playerUUID, Door door, int permission)
    {
        if (permission < 1 || permission > 2 || door.getPermission() != 0 || door.getPlayerUUID().equals(playerUUID))
            return false;

        db.addOwner(door.getDoorUID(), playerUUID, permission);
        return true;
    }

    public boolean removeOwner(Door door, UUID playerUUID, Player executor)
    {
        return removeOwner(door.getDoorUID(), playerUUID, executor);
    }

    public boolean removeOwner(long doorUID, UUID playerUUID, Player executor)
    {
        if (db.getPermission(playerUUID.toString(), doorUID) == 0)
            return false;
        if (!hasPermissionForAction(executor, doorUID, DoorAttribute.REMOVEOWNER))
            return false;
        return db.removeOwner(doorUID, playerUUID);
    }

    public ArrayList<DoorOwner> getDoorOwners(long doorUID, @Nullable UUID playerUUID)
    {
        return db.getOwnersOfDoor(doorUID, playerUUID);
    }

    public void updateDoorOpenDirection(long doorUID, RotateDirection openDir)
    {
        if (openDir == null)
            return;
        db.updateDoorOpenDirection(doorUID, openDir);
    }

    public void updateDoorAutoClose(long doorUID, int autoClose)
    {
        db.updateDoorAutoClose(doorUID, autoClose);
    }

    public void updateDoorNotify(long doorUID, boolean notify)
    {
        db.updateNotify(doorUID, notify);
    }

    public void updateDoorBlocksToMove(long doorID, int blocksToMove)
    {
        db.updateDoorBlocksToMove(doorID, blocksToMove);
    }

    // Change the "locked" status of a door.
    public void setLock(long doorUID, boolean newLockStatus)
    {
        db.setLock(doorUID, newLockStatus);
    }

    // Get a door from the x,y,z coordinates of its power block.
    public Door doorFromPowerBlockLoc(Location loc)
    {
        long chunkHash = Util.chunkHashFromLocation(loc);
        HashMap<Long, Long> powerBlockData = plugin.getPBCache().get(chunkHash);

        if (powerBlockData == null)
        {
            powerBlockData = db.getPowerBlockData(chunkHash);
            plugin.getPBCache().put(chunkHash, powerBlockData);
        }

        Long doorUID = powerBlockData.get(Util.locationHash(loc));
        return doorUID == null ? null : db.getDoor(null, doorUID);
    }

    public void recalculatePowerBlockHashes()
    {
        db.recalculatePowerBlockHashes();
    }

    // Change the location of a powerblock.
    public void updatePowerBlockLoc(long doorUID, Location loc)
    {
        plugin.getPBCache().invalidate(db.getDoor(null, doorUID).getPowerBlockChunkHash());
        db.updateDoorPowerBlockLoc(doorUID, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getUID());
        plugin.getPBCache().invalidate(Util.chunkHashFromLocation(loc));
    }

    public boolean isPowerBlockLocationValid(Location loc)
    {
        return db.isPowerBlockLocationEmpty(loc);
    }

    /**
     * Sets whether animations are currently allowed.
     * <p>
     * When false, no new animations will be started. Existing animations will continue to run. To stop all animations,
     * use {@link #stopMovers(boolean)}.
     *
     * @param animationsAllowed True if animations should be allowed, false otherwise.
     */
    public void animationsAllowed(boolean animationsAllowed)
    {
        plugin.getMyLogger().logMessageToLogFile("Setting animationsAllowed to: " + animationsAllowed + ".");
        this.animationsAllowed = animationsAllowed;
    }

    /**
     * Gets whether animations are currently allowed.
     *
     * @return True if animations are allowed, false otherwise.
     */
    public boolean animationsAllowed()
    {
        return animationsAllowed;
    }

    private static final class DummyMover extends BlockMover
    {
        private DummyMover()
        {
            super(BigDoors.get(), null, false);
        }

        @Override
        public synchronized void cancel0(boolean onDisable)
        {
        }

        @Override
        public void putBlocks0(boolean onDisable)
        {
        }

        @Override
        public long getDoorUID()
        {
            return -1;
        }

        @Override
        public Door getDoor()
        {
            // Return a new subclass of Door
            return new Door(-1)
            {
                @Override
                public String getTypeName()
                {
                    return null;
                }
            };
        }
    }

    private Map<Long, BlockMover> newBusyDoorsMap()
    {
        return new ConcurrentHashMap<Long, BlockMover>() {
            @Override
            public BlockMover putIfAbsent(@NotNull Long key, BlockMover value)
            {
                final BlockMover result = super.putIfAbsent(key, value);
                plugin.getMyLogger().logMessageToLogFile(String.format(
                    "[%3d - %-12s] Attempting to register door as busy if not already busy; Was already registered with: %s",
                    key, "___________", result == null ? "null" : result.getClass().getName()
                ));
                return result;
            }

            @Override
            public BlockMover replace(@NotNull Long key, @NotNull BlockMover value)
            {
                final BlockMover removed =  super.replace(key, value);
                plugin.getMyLogger().logMessageToLogFileForDoor(value.getDoor(), String.format(
                    "Replacing existing mover of type: '%s' with one of type: '%s'",
                    removed == null ? "null" : removed.getClass().getName(), value.getClass().getName()
                ));
                return removed;
            }

            @Override
            public @Nullable BlockMover put(Long key, BlockMover value)
            {
                plugin.getMyLogger().logMessageToLogFileForDoor(value.getDoor(), String.format(
                    "Registering door as busy with mover type: %s",
                    value.getClass().getName()
                ));
                return super.put(key, value);
            }

            @Override
            public BlockMover remove(Object key)
            {
                plugin.getMyLogger().logMessageToLogFile(String.format(
                    "[%3s - %-12s] Removing door from busyDoors",
                    key, "___________"
                ));
                return super.remove(key);
            }

            @Override
            public void putAll(@NotNull Map<? extends Long, ? extends BlockMover> m)
            {
                final StringBuilder sb = new StringBuilder();
                m.forEach((k, v) -> sb.append(v == null ? k : Util.formatDoorInfo(v.getDoor())).append(", "));
                if (sb.length() > 2)
                    sb.setLength(sb.length() - 2);
                plugin.getMyLogger().logMessageToLogFile("Registering movers as busy: " + sb);
                super.putAll(m);
            }

            @Override
            public void clear()
            {
                plugin.getMyLogger().logMessageToLogFile("Clearing ALL busyDoors; current size: " + size());
                super.clear();
            }
        };
    }
}
