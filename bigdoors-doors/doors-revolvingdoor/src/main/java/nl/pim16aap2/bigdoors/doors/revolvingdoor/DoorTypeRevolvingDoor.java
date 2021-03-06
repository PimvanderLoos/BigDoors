package nl.pim16aap2.bigdoors.doors.revolvingdoor;

import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.audio.AudioDescription;
import nl.pim16aap2.bigdoors.audio.AudioSet;
import nl.pim16aap2.bigdoors.doors.AbstractDoor;
import nl.pim16aap2.bigdoors.doortypes.DoorType;
import nl.pim16aap2.bigdoors.tooluser.creator.Creator;
import nl.pim16aap2.bigdoors.util.Constants;
import nl.pim16aap2.bigdoors.util.RotateDirection;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class DoorTypeRevolvingDoor extends DoorType
{
    private static final int TYPE_VERSION = 1;

    private static final DoorTypeRevolvingDoor INSTANCE = new DoorTypeRevolvingDoor();

    private static final AudioSet AUDIO_SET = new AudioSet(
        new AudioDescription("bd.dragging2", 0.8f, 0.7f, 750),
        new AudioDescription("bd.thud", 0.2f, 0.15f, 250));

    private DoorTypeRevolvingDoor()
    {
        super(Constants.PLUGIN_NAME, "RevolvingDoor", TYPE_VERSION,
              Arrays.asList(RotateDirection.CLOCKWISE, RotateDirection.COUNTERCLOCKWISE), "door.type.revolving_door");
    }

    /**
     * Obtains the instance of this type.
     *
     * @return The instance of this type.
     */
    public static DoorTypeRevolvingDoor get()
    {
        return INSTANCE;
    }

    @Override
    public Class<? extends AbstractDoor> getDoorClass()
    {
        return RevolvingDoor.class;
    }

    @Override
    public Creator getCreator(Creator.Context context, IPPlayer player, @Nullable String name)
    {
        return new CreatorRevolvingDoor(context, player, name);
    }

    @Override
    public AudioSet getAudioSet()
    {
        return AUDIO_SET;
    }
}
