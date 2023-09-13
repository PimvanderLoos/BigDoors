package net.minecraft.server;

public class DataConverterHanging implements IDataConverter {

    public DataConverterHanging() {}

    public int a() {
        return 111;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        String s = nbttagcompound.getString("id");
        boolean flag = "Painting".equals(s);
        boolean flag1 = "ItemFrame".equals(s);

        if ((flag || flag1) && !nbttagcompound.hasKeyOfType("Facing", 99)) {
            EnumDirection enumdirection;

            if (nbttagcompound.hasKeyOfType("Direction", 99)) {
                enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Direction"));
                nbttagcompound.setInt("TileX", nbttagcompound.getInt("TileX") + enumdirection.getAdjacentX());
                nbttagcompound.setInt("TileY", nbttagcompound.getInt("TileY") + enumdirection.getAdjacentY());
                nbttagcompound.setInt("TileZ", nbttagcompound.getInt("TileZ") + enumdirection.getAdjacentZ());
                nbttagcompound.remove("Direction");
                if (flag1 && nbttagcompound.hasKeyOfType("ItemRotation", 99)) {
                    nbttagcompound.setByte("ItemRotation", (byte) (nbttagcompound.getByte("ItemRotation") * 2));
                }
            } else {
                enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Dir"));
                nbttagcompound.remove("Dir");
            }

            nbttagcompound.setByte("Facing", (byte) enumdirection.get2DRotationValue());
        }

        return nbttagcompound;
    }
}
