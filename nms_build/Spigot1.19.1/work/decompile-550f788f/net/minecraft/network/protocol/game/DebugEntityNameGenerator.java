package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.SystemUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;

public class DebugEntityNameGenerator {

    private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook", "Dirt", "Mud", "Sad", "Hard", "Crook", "Sneak", "Stink", "Weird", "Fire", "Soot", "Soft", "Rough", "Cling", "Scar"};
    private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Wart", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue", "Voice", "Lip", "Mouth", "Snail", "Toe", "Ear", "Hair", "Beard", "Shirt", "Fist"};

    public DebugEntityNameGenerator() {}

    public static String getEntityName(Entity entity) {
        if (entity instanceof EntityHuman) {
            return entity.getName().getString();
        } else {
            IChatBaseComponent ichatbasecomponent = entity.getCustomName();

            return ichatbasecomponent != null ? ichatbasecomponent.getString() : getEntityName(entity.getUUID());
        }
    }

    public static String getEntityName(UUID uuid) {
        RandomSource randomsource = getRandom(uuid);
        String s = getRandomString(randomsource, DebugEntityNameGenerator.NAMES_FIRST_PART);

        return s + getRandomString(randomsource, DebugEntityNameGenerator.NAMES_SECOND_PART);
    }

    private static String getRandomString(RandomSource randomsource, String[] astring) {
        return (String) SystemUtils.getRandom((Object[]) astring, randomsource);
    }

    private static RandomSource getRandom(UUID uuid) {
        return RandomSource.create((long) (uuid.hashCode() >> 2));
    }
}
