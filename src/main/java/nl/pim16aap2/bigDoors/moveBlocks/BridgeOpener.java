package nl.pim16aap2.bigDoors.moveBlocks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import net.md_5.bungee.api.ChatColor;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.DoorDirection;
import nl.pim16aap2.bigDoors.util.RotateDirection;

public class BridgeOpener implements Opener
{
	private BigDoors        plugin;
	private RotateDirection upDown;

	public BridgeOpener(BigDoors plugin)
	{
		this.plugin = plugin;
	}
	
	// Check if the new position is free.
	public boolean isNewPosFree(Door door, RotateDirection upDown, DoorDirection cardinal)
	{
		int startX = 0, startY = 0, startZ = 0;
		int stopX  = 0, stopY  = 0, stopZ  = 0;
		World world = door.getWorld();

		if (upDown.equals(RotateDirection.UP))
		{
			switch (cardinal)
			{
			// North West = Min X, Min Z
			// South West = Min X, Max Z
			// North East = Max X, Min Z
			// South East = Max X, Max X
			case NORTH:
				Bukkit.broadcastMessage("U: NORTH");
				startX = door.getMinimum().getBlockX();
				stopX  = door.getMaximum().getBlockX();
				
				startY = door.getMinimum().getBlockY() + 1;
				stopY  = door.getMinimum().getBlockY() + door.getMaximum().getBlockZ() - door.getMinimum().getBlockZ();
				
				startZ = door.getMinimum().getBlockZ();
				stopZ  = door.getMinimum().getBlockZ();
				break;
				
			case SOUTH:
				Bukkit.broadcastMessage("U: SOUTH");
				startX = door.getMinimum().getBlockX();
				stopX  = door.getMaximum().getBlockX();
				
				startY = door.getMinimum().getBlockY() + 1;
				stopY  = door.getMinimum().getBlockY() + door.getMaximum().getBlockZ() - door.getMinimum().getBlockZ();
				
				startZ = door.getMaximum().getBlockZ();
				stopZ  = door.getMaximum().getBlockZ();
				break;
				
			case EAST:
				Bukkit.broadcastMessage("U: EAST");
				startX = door.getMaximum().getBlockX();
				stopX  = door.getMaximum().getBlockX();
				
				startY = door.getMinimum().getBlockY() + 1;
				stopY  = door.getMinimum().getBlockY() + door.getMaximum().getBlockX() - door.getMinimum().getBlockX();
				
				startZ = door.getMinimum().getBlockZ();
				stopZ  = door.getMaximum().getBlockZ();
				break;
				
			case WEST:
				Bukkit.broadcastMessage("U: WEST");
				startX = door.getMinimum().getBlockX();
				stopX  = door.getMinimum().getBlockX();
				
				startY = door.getMinimum().getBlockY() + 1;
				stopY  = door.getMinimum().getBlockY() + door.getMaximum().getBlockX() - door.getMinimum().getBlockX();
				
				startZ = door.getMinimum().getBlockZ();
				stopZ  = door.getMaximum().getBlockZ();
				break;
			}
		}
		else
		{
			switch (cardinal)
			{
			// North West = Min X, Min Z
			// South West = Min X, Max Z
			// North East = Max X, Min Z
			// South East = Max X, Max X
			case NORTH:
				Bukkit.broadcastMessage("D: NORTH");
				startX = door.getMinimum().getBlockX();
				stopX  = door.getMaximum().getBlockX();
				
				startY = door.getMinimum().getBlockY();
				stopY  = door.getMinimum().getBlockY();
				
				startZ = door.getMinimum().getBlockZ() - 1;
				stopZ  = door.getMinimum().getBlockZ() - door.getMaximum().getBlockY() + door.getMinimum().getBlockY();
				break;
				
			case SOUTH:
				Bukkit.broadcastMessage("D: SOUTH");
				startX = door.getMinimum().getBlockX();
				stopX  = door.getMaximum().getBlockX();
				
				startY = door.getMinimum().getBlockY();
				stopY  = door.getMinimum().getBlockY();
				
				startZ = door.getMinimum().getBlockZ() + 1;
				stopZ  = door.getMinimum().getBlockZ() + door.getMaximum().getBlockY() - door.getMinimum().getBlockY();
				break;
				
			case EAST:
				Bukkit.broadcastMessage("D: EAST");
				startX = door.getMinimum().getBlockX() + 1;
				stopX  = door.getMinimum().getBlockX() + door.getMaximum().getBlockX() - door.getMinimum().getBlockX();
				
				startY = door.getMinimum().getBlockY();
				stopY  = door.getMinimum().getBlockY();
				
				startZ = door.getMinimum().getBlockZ();
				stopZ  = door.getMaximum().getBlockZ();
				break;
				
			case WEST:
				Bukkit.broadcastMessage("D: WEST");
				startX = door.getMinimum().getBlockX() - 1;
				stopX  = door.getMinimum().getBlockX() - door.getMaximum().getBlockX() + door.getMinimum().getBlockX();
				
				startY = door.getMinimum().getBlockY();
				stopY  = door.getMinimum().getBlockY();
				
				startZ = door.getMinimum().getBlockZ();
				stopZ  = door.getMaximum().getBlockZ();
				break;
			}
		}

		int x = startX, y, z;
		while (x <= stopX)
		{
			y = startY;
			while (y <= stopY)
			{
				z = startZ;
				while (z <= stopZ)
				{			
					if (world.getBlockAt(x, y, z).getType() != Material.AIR)
					{
						Bukkit.broadcastMessage(ChatColor.RED + "Found a non-air block of the type " + world.getBlockAt(x, y, z).getType().toString() + ". Stopping checks!");
						return false;
					}
					++z;
				}
				++y;
			}
			++x;
		}
		return true;
	}

	// Check if the bridge should go up or down.
	public RotateDirection getUpDown(Door door)
	{
		int height = Math.abs(door.getMinimum().getBlockY() - door.getMaximum().getBlockY());
		if (height > 0)
			return RotateDirection.DOWN;
		return RotateDirection.UP;
	}
	
	// Figure out which way the bridge should go.
	public DoorDirection getOpenDirection(Door door)
	{
		RotateDirection upDown = getUpDown(door);
		DoorDirection cDir     = getCurrentDirection(door);
		boolean NS  = cDir    == DoorDirection.NORTH || cDir == DoorDirection.SOUTH;
		
		return 	!NS && isNewPosFree(door, upDown, DoorDirection.NORTH) ? DoorDirection.NORTH :
				 NS && isNewPosFree(door, upDown, DoorDirection.EAST ) ? DoorDirection.EAST  : 
				!NS && isNewPosFree(door, upDown, DoorDirection.SOUTH) ? DoorDirection.SOUTH : 
				 NS && isNewPosFree(door, upDown, DoorDirection.WEST ) ? DoorDirection.WEST  : null;
	}

	// Get the "current direction". In this context this means on which side of the drawbridge the engine is.
	@Override
	public DoorDirection getCurrentDirection(Door door)
	{	
		return door.getEngSide();
	}

	@Override
	public boolean chunksLoaded(Door door)
	{
		// Return true if the chunk at the max and at the min of the chunks were loaded correctly.
		if (door.getWorld() == null)
			plugin.getMyLogger().logMessage("World is null for door \""    + door.getName().toString() + "\"",          true, false);
		if (door.getWorld().getChunkAt(door.getMaximum()) == null)
			plugin.getMyLogger().logMessage("Chunk at maximum for door \"" + door.getName().toString() + "\" is null!", true, false);
		if (door.getWorld().getChunkAt(door.getMinimum()) == null)
			plugin.getMyLogger().logMessage("Chunk at minimum for door \"" + door.getName().toString() + "\" is null!", true, false);
		
		return door.getWorld().getChunkAt(door.getMaximum()).load() && door.getWorld().getChunkAt(door.getMinimum()).isLoaded();
	}

	@Override
	public boolean openDoor(Door door, double speed)
	{
		return openDoor(door, speed, false);
	}

	@Override
	public boolean openDoor(Door door, double speed, boolean silent)
	{
		if (plugin.getCommander().isDoorBusy(door.getDoorUID()))
		{
			if (!silent)
				plugin.getMyLogger().myLogger(Level.INFO, "Bridge " + door.getName() + " is not available right now!");
			return true;
		}
		
		if (!chunksLoaded(door))
		{
			plugin.getMyLogger().logMessage(ChatColor.RED + "Chunk for bridge " + door.getName() + " is not loaded!", true, false);
			return true;
		}
		
		DoorDirection currentDirection = getCurrentDirection(door);
		if (currentDirection == null)
		{
			plugin.getMyLogger().logMessage("Current direction is null for bridge " + door.getName() + " (" + door.getDoorUID() + ")!", true, false);
			return false;
		}
		this.upDown = getUpDown(door);
		if (upDown == null)
		{
			plugin.getMyLogger().logMessage("UpDown direction is null for bridge " + door.getName() + " (" + door.getDoorUID() + ")!", true, false);
			return false;
		}
		DoorDirection openDirection = getOpenDirection(door);
		if (openDirection == null)
		{
			plugin.getMyLogger().logMessage("OpenDirection direction is null for bridge " + door.getName() + " (" + door.getDoorUID() + ")!", true, false);
			return false;
		}
		
		if (!silent)
			plugin.getMyLogger().myLogger(Level.INFO, "CurrentDirection = " + currentDirection + ", performing " + openDirection + "(" + upDown + ")" + " rotation.");

		// Change door availability so it cannot be opened again (just temporarily, don't worry!).
		plugin.getCommander().setDoorBusy(door.getDoorUID());

		new BridgeMover(plugin, door.getWorld(), speed, door, this.upDown, openDirection);
		
//		// Tell the door object it has been opened and what its new coordinates are.
		toggleOpen  (door);
		updateCoords(door, openDirection, this.upDown);
		return true;
	}

	@Override
	public void updateCoords(Door door, DoorDirection currentDirection, RotateDirection rotDirection)
	{
		// TODO Auto-generated method stub	
	}

	// TODO: Can probably be deprecated.
	@Override @Deprecated
	public void toggleOpen(Door door)
	{
		door.setStatus(!door.getStatus());
	}
}