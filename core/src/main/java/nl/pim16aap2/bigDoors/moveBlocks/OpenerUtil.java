package nl.pim16aap2.bigDoors.moveBlocks;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Contains some shared utility methods for the {@link Opener}s.
 */
class OpenerUtil
{
    private OpenerUtil()
    {
    }

    /**
     * Reschedules a task for a door if needed.
     * <p>
     * This is a utility method that checks if {@link #shouldRescheduleTaskForLocation(Door)} returns true. If it does, it
     * will reschedule the task using {@link #rescheduleTaskForLocation(Door, Supplier)}.
     * <p>
     * If the task does not need to be rescheduled, it will simply run the task and return the result.
     *
     * @param door The door for which to reschedule the task.
     * @param task The task to reschedule.
     * @return The result of the task.
     */
    static CompletableFuture<DoorOpenResult> rescheduleTaskForLocationIfNeeded(
        Door door,
        Supplier<CompletableFuture<DoorOpenResult>> task)
    {
        if (shouldRescheduleTaskForLocation(door))
            return rescheduleTaskForLocation(door, task);
        return task.get();
    }

    /**
     * Checks if the task for handling a door should be rescheduled.
     * <p>
     * A task needs to be rescheduled when we run on Folia (see {@link BigDoors#IS_FOLIA}) and the region thread is not
     * the same as the thread that the door's engine is running on. We use the engine location because it's guaranteed
     * to be part of the same door. In many cases, it will also be a good indication of where the door will move to.
     *
     * @param door The door for which to check if the task should be rescheduled.
     * @return True if the task should be rescheduled.
     */
    static boolean shouldRescheduleTaskForLocation(Door door)
    {
        return BigDoors.IS_FOLIA && !BigDoors.getScheduler().isRegionThread(door.getEngine());
    }

    /**
     * Reschedules a task for a door.
     * <p>
     * Should only be called if {@link #shouldRescheduleTaskForLocation(Door)} returns true.
     *
     * @param door The door for which to reschedule the task.
     * @param task The task to reschedule.
     * @return The result of the task.
     */
    static CompletableFuture<DoorOpenResult> rescheduleTaskForLocation(
        Door door,
        Supplier<CompletableFuture<DoorOpenResult>> task)
    {
        final CompletableFuture<DoorOpenResult> result = new CompletableFuture<>();

        BigDoors.getScheduler().runTask(door.getEngine(), () -> {
            task.get().whenComplete((doorOpenResult, throwable) -> {
                if (throwable != null)
                {
                    result.completeExceptionally(throwable);
                    return;
                }

                result.complete(doorOpenResult);
            });
        });

        return result.exceptionally(ex -> {
            BigDoors.get().getMyLogger().log("Failed to reschedule task for door: " + door, ex);
            return DoorOpenResult.ERROR;
        });
    }
}
