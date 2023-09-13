package net.minecraft.world.entity.ai.util;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.Navigation;

public class PathfinderGoalUtil {

    public static boolean a(EntityInsentient entityinsentient) {
        return entityinsentient.getNavigation() instanceof Navigation;
    }
}
