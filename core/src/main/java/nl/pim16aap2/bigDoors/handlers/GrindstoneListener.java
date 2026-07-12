package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class GrindstoneListener implements Listener
{
    private static final String EVENT_CLASS_NAME = "org.bukkit.event.inventory.PrepareGrindstoneEvent";

    private final BigDoors bigDoors;

    private final Class<? extends Event> clzPrepareGrindstoneEvent;
    private final Method methodSetResult;
    private final InventoryType grindStoneInventoryType;

    GrindstoneListener(
        BigDoors bigDoors,
        Class<? extends Event> clzPrepareGrindstoneEvent,
        Method methodSetResult,
        InventoryType grindStoneInventoryType
    )
    {
        this.bigDoors = bigDoors;
        this.clzPrepareGrindstoneEvent = clzPrepareGrindstoneEvent;
        this.methodSetResult = methodSetResult;
        this.grindStoneInventoryType = grindStoneInventoryType;
    }

    public static void tryRegister(BigDoors bigDoors)
    {
        final Class<? extends Event> clzPrepareGrindstoneEvent;
        final Method methodSetResult;
        final InventoryType grindStoneInventoryType;
        try
        {
            clzPrepareGrindstoneEvent = Class.forName(EVENT_CLASS_NAME).asSubclass(Event.class);
            methodSetResult = clzPrepareGrindstoneEvent.getMethod("setResult", ItemStack.class);
            grindStoneInventoryType = InventoryType.valueOf("GRINDSTONE");
        }
        catch (ReflectiveOperationException e)
        {
            return;
        }

        final GrindstoneListener listener = new GrindstoneListener(
            bigDoors,
            clzPrepareGrindstoneEvent,
            methodSetResult,
            grindStoneInventoryType
        );

        Bukkit.getPluginManager().registerEvent(
            clzPrepareGrindstoneEvent,
            listener,
            EventPriority.HIGHEST,
            (ignored, event) -> listener.onPrepareGrindstone(event),
            bigDoors,
            true
        );

        Bukkit.getPluginManager().registerEvent(
            InventoryClickEvent.class,
            listener,
            EventPriority.HIGHEST,
            (ignored, event) -> listener.onInventoryClick(event),
            bigDoors,
            true
        );
    }

    void onInventoryClick(Event event)
    {
        if (!(event instanceof InventoryClickEvent))
            return;

        final InventoryClickEvent ice = (InventoryClickEvent) event;
        if (ice.getClickedInventory() == null)
            return;

        if (ice.getClickedInventory().getType() != grindStoneInventoryType)
            return;

        if (bigDoors.getTF().isTool(ice.getCurrentItem()) || bigDoors.getTF().isTool(ice.getCursor()))
            ice.setCancelled(true);
    }

    void onPrepareGrindstone(Event event)
    {
        if (!clzPrepareGrindstoneEvent.isInstance(event))
            return;

        final Inventory inventory = ((InventoryEvent) event).getInventory();
        if (!bigDoors.getTF().isTool(inventory.getItem(0)) && !bigDoors.getTF().isTool(inventory.getItem(1)))
            return;

        try
        {
            methodSetResult.invoke(event, (Object) null);
        }
        catch (ReflectiveOperationException exception)
        {
            bigDoors.getMyLogger().log(exception);
        }
    }
}
