package nl.pim16aap2.bigDoors.toolUsers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class ToolVerifier
{
    public static final Enchantment TOOL_ENCHANTMENT = findLuck();

    private final String toolName;

    public ToolVerifier(String str)
    {
        toolName = str;
    }

    // Check if the provided itemstack is a selection tool.
    public boolean isTool(@Nullable ItemStack is)
    {
        return  is != null                                        &&
                is.getType() == Material.STICK                    &&
                is.getEnchantmentLevel(TOOL_ENCHANTMENT) == 1     &&
                is.getItemMeta().getDisplayName() != null         &&
                is.getItemMeta().getDisplayName().toString().equals(toolName);
    }

    private static Enchantment findLuck()
    {
        @Nullable Enchantment luck = Enchantment.getByName("LUCK");
        if (luck == null)
            luck = Enchantment.getByName("LUCK_OF_THE_SEA");
        return Objects.requireNonNull(luck, "Could not find the luck enchantment!");
    }
}
