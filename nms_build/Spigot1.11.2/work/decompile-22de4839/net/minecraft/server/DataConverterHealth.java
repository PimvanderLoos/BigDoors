package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class DataConverterHealth implements IDataConverter {

    private static final Set<String> a = Sets.newHashSet(new String[] { "ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie"});

    public DataConverterHealth() {}

    public int a() {
        return 109;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (DataConverterHealth.a.contains(nbttagcompound.getString("id"))) {
            float f;

            if (nbttagcompound.hasKeyOfType("HealF", 99)) {
                f = nbttagcompound.getFloat("HealF");
                nbttagcompound.remove("HealF");
            } else {
                if (!nbttagcompound.hasKeyOfType("Health", 99)) {
                    return nbttagcompound;
                }

                f = nbttagcompound.getFloat("Health");
            }

            nbttagcompound.setFloat("Health", f);
        }

        return nbttagcompound;
    }
}
