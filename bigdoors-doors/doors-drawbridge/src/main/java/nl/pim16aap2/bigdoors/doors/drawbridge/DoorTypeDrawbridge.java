package nl.pim16aap2.bigdoors.doors.drawbridge;

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

public final class DoorTypeDrawbridge extends DoorType
{
    private static final int TYPE_VERSION = 1;

    private static final DoorTypeDrawbridge INSTANCE = new DoorTypeDrawbridge();

    private static final AudioSet AUDIO_SET = new AudioSet(
        new AudioDescription("bd.drawbridge-rattling", 0.8f, 0.7f, 750),
        new AudioDescription("bd.closing-vault-door", 0.2f, 0.15f, 750));

    private DoorTypeDrawbridge()
    {
        super(Constants.PLUGIN_NAME, "DrawBridge", TYPE_VERSION,
              Arrays.asList(RotateDirection.NORTH, RotateDirection.EAST,
                            RotateDirection.SOUTH, RotateDirection.WEST), "door.type.drawbridge");
    }

    @Override
    public Creator getCreator(Creator.Context context, IPPlayer player, @Nullable String name)
    {
        return new CreatorDrawbridge(context, player, name);
    }

    /**
     * Obtains the instance of this type.
     *
     * @return The instance of this type.
     */
    public static DoorTypeDrawbridge get()
    {
        return INSTANCE;
    }

    @Override
    public Class<? extends AbstractDoor> getDoorClass()
    {
        return Drawbridge.class;
    }

    @Override
    public AudioSet getAudioSet()
    {
        return AUDIO_SET;
    }
}
