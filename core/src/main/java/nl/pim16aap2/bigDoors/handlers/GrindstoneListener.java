package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.reflection.ReflectionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class GrindstoneListener implements Listener
{
    private static final String EVENT_CLASS_NAME = "org.bukkit.event.inventory.PrepareGrindstoneEvent";

    private final BigDoors bigDoors;

    GrindstoneListener(BigDoors bigDoors)
    {
        this.bigDoors = bigDoors;
    }

    private boolean tryRegisterOnGrindstoneInventoryClick(GrindstoneListener listener)
    {
        final @Nullable InventoryType grindStoneInventoryType = (InventoryType) ReflectionBuilder.findEnumValues()
            .inClass(InventoryType.class)
            .withName("GRINDSTONE")
            .setNullable()
            .get();

        // Grindstone inventory type was introduced in 1.14; clzPrepareGrindstoneEvent in 1.16.
        if (grindStoneInventoryType == null)
            return false;

        Bukkit.getPluginManager().registerEvent(
            InventoryClickEvent.class,
            listener,
            EventPriority.HIGHEST,
            (ignored, event) -> listener.onGrindstoneInventoryClick(event, grindStoneInventoryType),
            bigDoors,
            true
        );

        return true;
    }

    @SuppressWarnings("unchecked")
    private void tryRegisterOnPrepareGrindstone(GrindstoneListener listener)
    {
        final @Nullable Class<? extends Event> clzPrepareGrindstoneEvent = (Class<? extends Event>) ReflectionBuilder
            .findClass()
            .withNames(EVENT_CLASS_NAME)
            .setNullable()
            .get();
        if (clzPrepareGrindstoneEvent == null)
            return;

        final @Nullable Method methodSetResult = ReflectionBuilder.findMethod()
            .inClass(clzPrepareGrindstoneEvent)
            .withName("setResult")
            .withParameters(ItemStack.class)
            .setNullable()
            .get();
        if (methodSetResult == null)
            return;


        Bukkit.getPluginManager().registerEvent(
            clzPrepareGrindstoneEvent,
            listener,
            EventPriority.HIGHEST,
            (ignored, event) -> listener.onPrepareGrindstone(event, clzPrepareGrindstoneEvent, methodSetResult),
            bigDoors,
            true
        );
    }

    public static void tryRegister(BigDoors bigDoors)
    {
        final GrindstoneListener listener = new GrindstoneListener(bigDoors);

        if (!listener.tryRegisterOnGrindstoneInventoryClick(listener))
            return;

        listener.tryRegisterOnPrepareGrindstone(listener);
    }

    void onGrindstoneInventoryClick(Event event, InventoryType grindStoneInventoryType)
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

    void onPrepareGrindstone(Event event, Class<? extends Event> clzPrepareGrindstoneEvent, Method methodSetResult)
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
