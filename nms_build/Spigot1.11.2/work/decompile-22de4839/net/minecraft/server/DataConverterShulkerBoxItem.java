package net.minecraft.server;

public class DataConverterShulkerBoxItem implements IDataConverter {

    public static final String[] a = new String[] { "minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

    public DataConverterShulkerBoxItem() {}

    public int a() {
        return 813;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:shulker_box".equals(nbttagcompound.getString("id")) && nbttagcompound.hasKeyOfType("tag", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");

            if (nbttagcompound1.hasKeyOfType("BlockEntityTag", 10)) {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("BlockEntityTag");

                if (nbttagcompound2.getList("Items", 10).isEmpty()) {
                    nbttagcompound2.remove("Items");
                }

                int i = nbttagcompound2.getInt("Color");

                nbttagcompound2.remove("Color");
                if (nbttagcompound2.isEmpty()) {
                    nbttagcompound1.remove("BlockEntityTag");
                }

                if (nbttagcompound1.isEmpty()) {
                    nbttagcompound.remove("tag");
                }

                nbttagcompound.setString("id", DataConverterShulkerBoxItem.a[i % 16]);
            }
        }

        return nbttagcompound;
    }
}
