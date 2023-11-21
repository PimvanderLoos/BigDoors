package nl.pim16aap2.bigDoors.util;

/**
 * Represents a door that can be logged.
 */
public interface ILoggableDoor
{
    /**
     * Get the UID of the door.
     *
     * @return The UID of the door.
     */
    long getDoorUID();

    /**
     * Get the name of the door type.
     *
     * @return The name of the door type.
     */
    String getTypeName();
}
