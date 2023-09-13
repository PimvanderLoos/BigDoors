package net.minecraft.world.level.gameevent;

import net.minecraft.core.IRegistry;

public class GameEvent {

    public static final GameEvent BLOCK_ATTACH = a("block_attach");
    public static final GameEvent BLOCK_CHANGE = a("block_change");
    public static final GameEvent BLOCK_CLOSE = a("block_close");
    public static final GameEvent BLOCK_DESTROY = a("block_destroy");
    public static final GameEvent BLOCK_DETACH = a("block_detach");
    public static final GameEvent BLOCK_OPEN = a("block_open");
    public static final GameEvent BLOCK_PLACE = a("block_place");
    public static final GameEvent BLOCK_PRESS = a("block_press");
    public static final GameEvent BLOCK_SWITCH = a("block_switch");
    public static final GameEvent BLOCK_UNPRESS = a("block_unpress");
    public static final GameEvent BLOCK_UNSWITCH = a("block_unswitch");
    public static final GameEvent CONTAINER_CLOSE = a("container_close");
    public static final GameEvent CONTAINER_OPEN = a("container_open");
    public static final GameEvent DISPENSE_FAIL = a("dispense_fail");
    public static final GameEvent DRINKING_FINISH = a("drinking_finish");
    public static final GameEvent EAT = a("eat");
    public static final GameEvent ELYTRA_FREE_FALL = a("elytra_free_fall");
    public static final GameEvent ENTITY_DAMAGED = a("entity_damaged");
    public static final GameEvent ENTITY_KILLED = a("entity_killed");
    public static final GameEvent ENTITY_PLACE = a("entity_place");
    public static final GameEvent EQUIP = a("equip");
    public static final GameEvent EXPLODE = a("explode");
    public static final GameEvent FISHING_ROD_CAST = a("fishing_rod_cast");
    public static final GameEvent FISHING_ROD_REEL_IN = a("fishing_rod_reel_in");
    public static final GameEvent FLAP = a("flap");
    public static final GameEvent FLUID_PICKUP = a("fluid_pickup");
    public static final GameEvent FLUID_PLACE = a("fluid_place");
    public static final GameEvent HIT_GROUND = a("hit_ground");
    public static final GameEvent MOB_INTERACT = a("mob_interact");
    public static final GameEvent LIGHTNING_STRIKE = a("lightning_strike");
    public static final GameEvent MINECART_MOVING = a("minecart_moving");
    public static final GameEvent PISTON_CONTRACT = a("piston_contract");
    public static final GameEvent PISTON_EXTEND = a("piston_extend");
    public static final GameEvent PRIME_FUSE = a("prime_fuse");
    public static final GameEvent PROJECTILE_LAND = a("projectile_land");
    public static final GameEvent PROJECTILE_SHOOT = a("projectile_shoot");
    public static final GameEvent RAVAGER_ROAR = a("ravager_roar");
    public static final GameEvent RING_BELL = a("ring_bell");
    public static final GameEvent SHEAR = a("shear");
    public static final GameEvent SHULKER_CLOSE = a("shulker_close");
    public static final GameEvent SHULKER_OPEN = a("shulker_open");
    public static final GameEvent SPLASH = a("splash");
    public static final GameEvent STEP = a("step");
    public static final GameEvent SWIM = a("swim");
    public static final GameEvent WOLF_SHAKING = a("wolf_shaking");
    public static final int DEFAULT_NOTIFICATION_RADIUS = 16;
    private final String name;
    private final int notificationRadius;

    public GameEvent(String s, int i) {
        this.name = s;
        this.notificationRadius = i;
    }

    public String a() {
        return this.name;
    }

    public int b() {
        return this.notificationRadius;
    }

    private static GameEvent a(String s) {
        return a(s, 16);
    }

    private static GameEvent a(String s, int i) {
        return (GameEvent) IRegistry.a((IRegistry) IRegistry.GAME_EVENT, s, (Object) (new GameEvent(s, i)));
    }

    public String toString() {
        return "Game Event{ " + this.name + " , " + this.notificationRadius + "}";
    }
}
