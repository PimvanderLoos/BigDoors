package net.minecraft.server;

public class PathfinderGoalUtil {

    public static boolean a(EntityInsentient entityinsentient) {
        return entityinsentient.getNavigation() instanceof Navigation;
    }
}
