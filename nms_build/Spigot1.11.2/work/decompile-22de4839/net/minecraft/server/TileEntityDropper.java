package net.minecraft.server;

public class TileEntityDropper extends TileEntityDispenser {

    public TileEntityDropper() {}

    public static void b(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, (DataInspector) (new DataInspectorItemList(TileEntityDropper.class, new String[] { "Items"})));
    }

    public String getName() {
        return this.hasCustomName() ? this.o : "container.dropper";
    }

    public String getContainerName() {
        return "minecraft:dropper";
    }
}
