package net.minecraft.server;

public class DataConverterPotionId implements IDataConverter {

    private static final String[] a = new String[128];

    public DataConverterPotionId() {}

    public int a() {
        return 102;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:potion".equals(nbttagcompound.getString("id"))) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");
            short short0 = nbttagcompound.getShort("Damage");

            if (!nbttagcompound1.hasKeyOfType("Potion", 8)) {
                String s = DataConverterPotionId.a[short0 & 127];

                nbttagcompound1.setString("Potion", s == null ? "minecraft:water" : s);
                nbttagcompound.set("tag", nbttagcompound1);
                if ((short0 & 16384) == 16384) {
                    nbttagcompound.setString("id", "minecraft:splash_potion");
                }
            }

            if (short0 != 0) {
                nbttagcompound.setShort("Damage", (short) 0);
            }
        }

        return nbttagcompound;
    }

    static {
        DataConverterPotionId.a[0] = "minecraft:water";
        DataConverterPotionId.a[1] = "minecraft:regeneration";
        DataConverterPotionId.a[2] = "minecraft:swiftness";
        DataConverterPotionId.a[3] = "minecraft:fire_resistance";
        DataConverterPotionId.a[4] = "minecraft:poison";
        DataConverterPotionId.a[5] = "minecraft:healing";
        DataConverterPotionId.a[6] = "minecraft:night_vision";
        DataConverterPotionId.a[7] = null;
        DataConverterPotionId.a[8] = "minecraft:weakness";
        DataConverterPotionId.a[9] = "minecraft:strength";
        DataConverterPotionId.a[10] = "minecraft:slowness";
        DataConverterPotionId.a[11] = "minecraft:leaping";
        DataConverterPotionId.a[12] = "minecraft:harming";
        DataConverterPotionId.a[13] = "minecraft:water_breathing";
        DataConverterPotionId.a[14] = "minecraft:invisibility";
        DataConverterPotionId.a[15] = null;
        DataConverterPotionId.a[16] = "minecraft:awkward";
        DataConverterPotionId.a[17] = "minecraft:regeneration";
        DataConverterPotionId.a[18] = "minecraft:swiftness";
        DataConverterPotionId.a[19] = "minecraft:fire_resistance";
        DataConverterPotionId.a[20] = "minecraft:poison";
        DataConverterPotionId.a[21] = "minecraft:healing";
        DataConverterPotionId.a[22] = "minecraft:night_vision";
        DataConverterPotionId.a[23] = null;
        DataConverterPotionId.a[24] = "minecraft:weakness";
        DataConverterPotionId.a[25] = "minecraft:strength";
        DataConverterPotionId.a[26] = "minecraft:slowness";
        DataConverterPotionId.a[27] = "minecraft:leaping";
        DataConverterPotionId.a[28] = "minecraft:harming";
        DataConverterPotionId.a[29] = "minecraft:water_breathing";
        DataConverterPotionId.a[30] = "minecraft:invisibility";
        DataConverterPotionId.a[31] = null;
        DataConverterPotionId.a[32] = "minecraft:thick";
        DataConverterPotionId.a[33] = "minecraft:strong_regeneration";
        DataConverterPotionId.a[34] = "minecraft:strong_swiftness";
        DataConverterPotionId.a[35] = "minecraft:fire_resistance";
        DataConverterPotionId.a[36] = "minecraft:strong_poison";
        DataConverterPotionId.a[37] = "minecraft:strong_healing";
        DataConverterPotionId.a[38] = "minecraft:night_vision";
        DataConverterPotionId.a[39] = null;
        DataConverterPotionId.a[40] = "minecraft:weakness";
        DataConverterPotionId.a[41] = "minecraft:strong_strength";
        DataConverterPotionId.a[42] = "minecraft:slowness";
        DataConverterPotionId.a[43] = "minecraft:strong_leaping";
        DataConverterPotionId.a[44] = "minecraft:strong_harming";
        DataConverterPotionId.a[45] = "minecraft:water_breathing";
        DataConverterPotionId.a[46] = "minecraft:invisibility";
        DataConverterPotionId.a[47] = null;
        DataConverterPotionId.a[48] = null;
        DataConverterPotionId.a[49] = "minecraft:strong_regeneration";
        DataConverterPotionId.a[50] = "minecraft:strong_swiftness";
        DataConverterPotionId.a[51] = "minecraft:fire_resistance";
        DataConverterPotionId.a[52] = "minecraft:strong_poison";
        DataConverterPotionId.a[53] = "minecraft:strong_healing";
        DataConverterPotionId.a[54] = "minecraft:night_vision";
        DataConverterPotionId.a[55] = null;
        DataConverterPotionId.a[56] = "minecraft:weakness";
        DataConverterPotionId.a[57] = "minecraft:strong_strength";
        DataConverterPotionId.a[58] = "minecraft:slowness";
        DataConverterPotionId.a[59] = "minecraft:strong_leaping";
        DataConverterPotionId.a[60] = "minecraft:strong_harming";
        DataConverterPotionId.a[61] = "minecraft:water_breathing";
        DataConverterPotionId.a[62] = "minecraft:invisibility";
        DataConverterPotionId.a[63] = null;
        DataConverterPotionId.a[64] = "minecraft:mundane";
        DataConverterPotionId.a[65] = "minecraft:long_regeneration";
        DataConverterPotionId.a[66] = "minecraft:long_swiftness";
        DataConverterPotionId.a[67] = "minecraft:long_fire_resistance";
        DataConverterPotionId.a[68] = "minecraft:long_poison";
        DataConverterPotionId.a[69] = "minecraft:healing";
        DataConverterPotionId.a[70] = "minecraft:long_night_vision";
        DataConverterPotionId.a[71] = null;
        DataConverterPotionId.a[72] = "minecraft:long_weakness";
        DataConverterPotionId.a[73] = "minecraft:long_strength";
        DataConverterPotionId.a[74] = "minecraft:long_slowness";
        DataConverterPotionId.a[75] = "minecraft:long_leaping";
        DataConverterPotionId.a[76] = "minecraft:harming";
        DataConverterPotionId.a[77] = "minecraft:long_water_breathing";
        DataConverterPotionId.a[78] = "minecraft:long_invisibility";
        DataConverterPotionId.a[79] = null;
        DataConverterPotionId.a[80] = "minecraft:awkward";
        DataConverterPotionId.a[81] = "minecraft:long_regeneration";
        DataConverterPotionId.a[82] = "minecraft:long_swiftness";
        DataConverterPotionId.a[83] = "minecraft:long_fire_resistance";
        DataConverterPotionId.a[84] = "minecraft:long_poison";
        DataConverterPotionId.a[85] = "minecraft:healing";
        DataConverterPotionId.a[86] = "minecraft:long_night_vision";
        DataConverterPotionId.a[87] = null;
        DataConverterPotionId.a[88] = "minecraft:long_weakness";
        DataConverterPotionId.a[89] = "minecraft:long_strength";
        DataConverterPotionId.a[90] = "minecraft:long_slowness";
        DataConverterPotionId.a[91] = "minecraft:long_leaping";
        DataConverterPotionId.a[92] = "minecraft:harming";
        DataConverterPotionId.a[93] = "minecraft:long_water_breathing";
        DataConverterPotionId.a[94] = "minecraft:long_invisibility";
        DataConverterPotionId.a[95] = null;
        DataConverterPotionId.a[96] = "minecraft:thick";
        DataConverterPotionId.a[97] = "minecraft:regeneration";
        DataConverterPotionId.a[98] = "minecraft:swiftness";
        DataConverterPotionId.a[99] = "minecraft:long_fire_resistance";
        DataConverterPotionId.a[100] = "minecraft:poison";
        DataConverterPotionId.a[101] = "minecraft:strong_healing";
        DataConverterPotionId.a[102] = "minecraft:long_night_vision";
        DataConverterPotionId.a[103] = null;
        DataConverterPotionId.a[104] = "minecraft:long_weakness";
        DataConverterPotionId.a[105] = "minecraft:strength";
        DataConverterPotionId.a[106] = "minecraft:long_slowness";
        DataConverterPotionId.a[107] = "minecraft:leaping";
        DataConverterPotionId.a[108] = "minecraft:strong_harming";
        DataConverterPotionId.a[109] = "minecraft:long_water_breathing";
        DataConverterPotionId.a[110] = "minecraft:long_invisibility";
        DataConverterPotionId.a[111] = null;
        DataConverterPotionId.a[112] = null;
        DataConverterPotionId.a[113] = "minecraft:regeneration";
        DataConverterPotionId.a[114] = "minecraft:swiftness";
        DataConverterPotionId.a[115] = "minecraft:long_fire_resistance";
        DataConverterPotionId.a[116] = "minecraft:poison";
        DataConverterPotionId.a[117] = "minecraft:strong_healing";
        DataConverterPotionId.a[118] = "minecraft:long_night_vision";
        DataConverterPotionId.a[119] = null;
        DataConverterPotionId.a[120] = "minecraft:long_weakness";
        DataConverterPotionId.a[121] = "minecraft:strength";
        DataConverterPotionId.a[122] = "minecraft:long_slowness";
        DataConverterPotionId.a[123] = "minecraft:leaping";
        DataConverterPotionId.a[124] = "minecraft:strong_harming";
        DataConverterPotionId.a[125] = "minecraft:long_water_breathing";
        DataConverterPotionId.a[126] = "minecraft:long_invisibility";
        DataConverterPotionId.a[127] = null;
    }
}
