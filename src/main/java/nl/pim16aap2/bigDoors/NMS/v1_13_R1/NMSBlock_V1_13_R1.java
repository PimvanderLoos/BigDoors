package nl.pim16aap2.bigDoors.NMS.v1_13_R1;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.BlockRotatable;
import net.minecraft.server.v1_13_R1.EnumBlockRotation;
import net.minecraft.server.v1_13_R1.EnumDirection.EnumAxis;
import net.minecraft.server.v1_13_R1.IBlockData;
import nl.pim16aap2.bigDoors.NMS.NMSBlock_Vall;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;
import nl.pim16aap2.bigDoors.util.XMaterial;

public class NMSBlock_V1_13_R1 extends net.minecraft.server.v1_13_R1.Block implements NMSBlock_Vall
{
	private IBlockData blockData;
	private XMaterial  xmat;
	
	public NMSBlock_V1_13_R1(World world, int x, int y, int z)
	{
		super(net.minecraft.server.v1_13_R1.Block.Info.a(((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z)).getBlock()));
		this.blockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z));
		super.v(blockData);
		this.xmat = XMaterial.fromString(world.getBlockAt(new Location(world, x, y, z)).getType().toString());
	}
	
	public void rotateBlock(RotateDirection rotDir)
	{
		EnumBlockRotation rot;
		switch(rotDir)
		{
		case CLOCKWISE:
			rot = EnumBlockRotation.CLOCKWISE_90;
			break;
		case COUNTERCLOCKWISE:
			rot = EnumBlockRotation.COUNTERCLOCKWISE_90;
			break;
		default:
			rot = EnumBlockRotation.NONE;
		}
		this.blockData = blockData.a(rot);
	}

	@Override
	public void putBlock(Location loc)
	{
		((CraftWorld) loc.getWorld()).getHandle().setTypeAndData(
				new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), this.blockData, 1);
		if (Util.needsRefresh(this.xmat))
		{
			loc.getWorld().getBlockAt(loc).setType(XMaterial.AIR.parseMaterial());
			loc.getWorld().getBlockAt(loc).setType(xmat.parseMaterial());
		}
	}

	@Override
	public void rotateBlockUpDown(boolean NS)
	{		
		EnumAxis axis    = blockData.get(BlockRotatable.AXIS);
		EnumAxis newAxis = axis;
		switch(axis)
		{
		case X:
			newAxis = NS ? EnumAxis.X : EnumAxis.Y;
			break;
		case Y:
			newAxis = NS ? EnumAxis.Z : EnumAxis.X;
			break;
		case Z:
			newAxis = NS ? EnumAxis.Y : EnumAxis.Z;
			break;
		}
		this.blockData = blockData.set(BlockRotatable.AXIS, newAxis);
	}

	@Override
	public void rotateCylindrical(RotateDirection rotDir)
	{
		if (rotDir.equals(RotateDirection.CLOCKWISE))
			this.blockData = blockData.a(EnumBlockRotation.CLOCKWISE_90);
		else 
			this.blockData = blockData.a(EnumBlockRotation.COUNTERCLOCKWISE_90);
	}
	
	@Override
	public String toString()
	{
		return this.blockData.toString();
	}
}