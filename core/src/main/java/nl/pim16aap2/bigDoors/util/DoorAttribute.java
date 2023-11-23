package nl.pim16aap2.bigDoors.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public enum DoorAttribute
{
    LOCK                (2, "bigdoors.user.lock", "bigdoors.admin.bypass.lock"),
    TOGGLE              (2, "bigdoors.user.toggledoor", "bigdoors.admin.bypass.toggle"),
    INFO                (2, "bigdoors.user.doorinfo", "bigdoors.admin.bypass.info"),
    DELETE              (0, "bigdoors.user.delete", "bigdoors.admin.bypass.delete"),
    RELOCATEPOWERBLOCK  (1, "bigdoors.user.relocatepowerblock", "bigdoors.admin.bypass.relocatepowerblock"),
    CHANGETIMER         (1, "bigdoors.user.setautoclosetime", "bigdoors.admin.bypass.changetimer"),
    DIRECTION_STRAIGHT  (1, "bigdoors.user.direction", "bigdoors.admin.bypass.direction"),
    DIRECTION_ROTATE    (1, DIRECTION_STRAIGHT.userPermission, DIRECTION_STRAIGHT.adminPermission),
    BLOCKSTOMOVE        (1, "bigdoors.user.setblockstomove", "bigdoors.admin.bypass.blockstomove"),
    ADDOWNER            (0, "bigdoors.user.addowner", "bigdoors.admin.bypass.addowner"),
    REMOVEOWNER         (0, "bigdoors.user.removeowner", "bigdoors.admin.bypass.removeowner"),
    NOTIFICATIONS       (0, "bigdoors.user.receivenotifications", null),
    BYPASS_PROTECTIONS  (2, null, "bigdoors.admin.setbypassprotections"),
    ;

    private final @Nullable String userPermission;

    private final @Nullable String adminPermission;

    /**
     * The permission level.
     * <p>
     * 0 = Creator
     * <p>
     * 1 = Admin
     * <p>
     * 2 = User
     */
    private final int permissionLevel;

    DoorAttribute(int permissionLevel, @Nullable String userPermission, @Nullable String adminPermission)
    {
        this.permissionLevel = permissionLevel;
        this.adminPermission = adminPermission;
        this.userPermission = userPermission;
    }

    /**
     * Gets the permission level of this attribute.
     * <p>
     * Each attribute has a permission level, which determines who can use it. The permission level is as follows:
     *
     * <ul>
     *     <li>0 = Creator (1 per door)</li>
     *     <li>1 = Admin</li>
     *     <li>2 = User</li>
     * </ul>
     *
     * @return The permission level of this attribute.
     */
    public int getPermissionLevel()
    {
        return permissionLevel;
    }

    /**
     * Checks whether the given player has user-level permission to use this attribute.
     * <p>
     * If {@link #userPermission} is null, this will always return false.
     *
     * @param player The player to check.
     * @return True if the player has user-level permission, otherwise false.
     */
    public boolean hasUserPermission(Player player)
    {
        return userPermission != null && player.hasPermission(userPermission);
    }

    /**
     * Checks whether the given player has admin-level permission to use this attribute.
     * <p>
     * If {@link #adminPermission} is null, this will always return false.
     *
     * @param player The player to check.
     * @return True if the player has admin-level permission, otherwise false.
     */
    public boolean hasAdminPermission(Player player)
    {
        return adminPermission != null && player.hasPermission(adminPermission);
    }

    /**
     * Checks whether the given player has any permission to use this attribute.
     * <p>
     * See {@link #hasUserPermission(Player)} and {@link #hasAdminPermission(Player)} for more information.
     *
     * @param player The player to check.
     * @return True if the player has any permission, otherwise false.
     */
    public boolean hasAnyPermission(Player player)
    {
        return hasUserPermission(player) || hasAdminPermission(player);
    }
}
