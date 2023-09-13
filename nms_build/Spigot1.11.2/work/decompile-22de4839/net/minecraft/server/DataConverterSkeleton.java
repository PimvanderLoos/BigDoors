package net.minecraft.server;

public class DataConverterSkeleton implements IDataConverter {

    public DataConverterSkeleton() {}

    public int a() {
        return 701;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        String s = nbttagcompound.getString("id");

        if ("Skeleton".equals(s)) {
            int i = nbttagcompound.getInt("SkeletonType");

            if (i == 1) {
                nbttagcompound.setString("id", "WitherSkeleton");
            } else if (i == 2) {
                nbttagcompound.setString("id", "Stray");
            }

            nbttagcompound.remove("SkeletonType");
        }

        return nbttagcompound;
    }
}
