package net.minecraft.server;

public abstract class DataInspectorTagged implements DataInspector {

    private final MinecraftKey a;

    public DataInspectorTagged(Class<?> oclass) {
        if (Entity.class.isAssignableFrom(oclass)) {
            this.a = EntityTypes.getName(oclass);
        } else if (TileEntity.class.isAssignableFrom(oclass)) {
            this.a = TileEntity.a(oclass);
        } else {
            this.a = null;
        }

    }

    public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
        if ((new MinecraftKey(nbttagcompound.getString("id"))).equals(this.a)) {
            nbttagcompound = this.b(dataconverter, nbttagcompound, i);
        }

        return nbttagcompound;
    }

    abstract NBTTagCompound b(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i);
}
