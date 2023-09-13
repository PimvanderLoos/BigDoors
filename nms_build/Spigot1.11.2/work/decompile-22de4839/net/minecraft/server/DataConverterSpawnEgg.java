package net.minecraft.server;

public class DataConverterSpawnEgg implements IDataConverter {

    private static final String[] a = new String[256];

    public DataConverterSpawnEgg() {}

    public int a() {
        return 105;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:spawn_egg".equals(nbttagcompound.getString("id"))) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");
            NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("EntityTag");
            short short0 = nbttagcompound.getShort("Damage");

            if (!nbttagcompound2.hasKeyOfType("id", 8)) {
                String s = DataConverterSpawnEgg.a[short0 & 255];

                if (s != null) {
                    nbttagcompound2.setString("id", s);
                    nbttagcompound1.set("EntityTag", nbttagcompound2);
                    nbttagcompound.set("tag", nbttagcompound1);
                }
            }

            if (short0 != 0) {
                nbttagcompound.setShort("Damage", (short) 0);
            }
        }

        return nbttagcompound;
    }

    static {
        String[] astring = DataConverterSpawnEgg.a;

        astring[1] = "Item";
        astring[2] = "XPOrb";
        astring[7] = "ThrownEgg";
        astring[8] = "LeashKnot";
        astring[9] = "Painting";
        astring[10] = "Arrow";
        astring[11] = "Snowball";
        astring[12] = "Fireball";
        astring[13] = "SmallFireball";
        astring[14] = "ThrownEnderpearl";
        astring[15] = "EyeOfEnderSignal";
        astring[16] = "ThrownPotion";
        astring[17] = "ThrownExpBottle";
        astring[18] = "ItemFrame";
        astring[19] = "WitherSkull";
        astring[20] = "PrimedTnt";
        astring[21] = "FallingSand";
        astring[22] = "FireworksRocketEntity";
        astring[23] = "TippedArrow";
        astring[24] = "SpectralArrow";
        astring[25] = "ShulkerBullet";
        astring[26] = "DragonFireball";
        astring[30] = "ArmorStand";
        astring[41] = "Boat";
        astring[42] = "MinecartRideable";
        astring[43] = "MinecartChest";
        astring[44] = "MinecartFurnace";
        astring[45] = "MinecartTNT";
        astring[46] = "MinecartHopper";
        astring[47] = "MinecartSpawner";
        astring[40] = "MinecartCommandBlock";
        astring[48] = "Mob";
        astring[49] = "Monster";
        astring[50] = "Creeper";
        astring[51] = "Skeleton";
        astring[52] = "Spider";
        astring[53] = "Giant";
        astring[54] = "Zombie";
        astring[55] = "Slime";
        astring[56] = "Ghast";
        astring[57] = "PigZombie";
        astring[58] = "Enderman";
        astring[59] = "CaveSpider";
        astring[60] = "Silverfish";
        astring[61] = "Blaze";
        astring[62] = "LavaSlime";
        astring[63] = "EnderDragon";
        astring[64] = "WitherBoss";
        astring[65] = "Bat";
        astring[66] = "Witch";
        astring[67] = "Endermite";
        astring[68] = "Guardian";
        astring[69] = "Shulker";
        astring[90] = "Pig";
        astring[91] = "Sheep";
        astring[92] = "Cow";
        astring[93] = "Chicken";
        astring[94] = "Squid";
        astring[95] = "Wolf";
        astring[96] = "MushroomCow";
        astring[97] = "SnowMan";
        astring[98] = "Ozelot";
        astring[99] = "VillagerGolem";
        astring[100] = "EntityHorse";
        astring[101] = "Rabbit";
        astring[120] = "Villager";
        astring[200] = "EnderCrystal";
    }
}
