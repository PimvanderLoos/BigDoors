package nl.pim16aap2.bigdoors.commands;

import lombok.SneakyThrows;
import nl.pim16aap2.bigdoors.UnitTestUtil;
import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.doors.AbstractDoor;
import nl.pim16aap2.bigdoors.localization.ILocalizer;
import nl.pim16aap2.bigdoors.managers.DatabaseManager;
import nl.pim16aap2.bigdoors.util.doorretriever.DoorRetriever;
import nl.pim16aap2.bigdoors.util.doorretriever.DoorRetrieverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static nl.pim16aap2.bigdoors.commands.CommandTestingUtil.*;

class AddOwnerTest
{
    private ILocalizer localizer;

    @Mock
    private DatabaseManager databaseManager;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private AddOwner.IFactory factory;

    private DoorRetriever doorRetriever;

    @Mock
    private AbstractDoor door;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private IPPlayer commandSender;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private IPPlayer target;

    private AddOwner addOwner0;
    private AddOwner addOwner1;
    private AddOwner addOwner2;

    @BeforeEach
    void init()
    {
        MockitoAnnotations.openMocks(this);

        localizer = UnitTestUtil.initLocalizer();

        Mockito.when(databaseManager.addOwner(Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.any()))
               .thenReturn(CompletableFuture.completedFuture(DatabaseManager.ActionResult.SUCCESS));

        Mockito.when(factory.newAddOwner(Mockito.any(ICommandSender.class),
                                         Mockito.any(DoorRetriever.class),
                                         Mockito.any(IPPlayer.class),
                                         Mockito.anyInt()))
               .thenAnswer((Answer<AddOwner>) invoc ->
                   new AddOwner(invoc.getArgument(0, ICommandSender.class), localizer,
                                invoc.getArgument(1, DoorRetriever.class),
                                invoc.getArgument(2, IPPlayer.class), invoc.getArgument(3, Integer.class),
                                databaseManager));

        initCommandSenderPermissions(commandSender, true, true);
        doorRetriever = DoorRetrieverFactory.ofDoor(door);

        addOwner0 = factory.newAddOwner(commandSender, doorRetriever, target, 0);
        addOwner1 = factory.newAddOwner(commandSender, doorRetriever, target, 1);
        addOwner2 = factory.newAddOwner(commandSender, doorRetriever, target, 2);
    }

    @Test
    void testInputValidity()
    {
        Assertions.assertFalse(addOwner0.validInput());
        Assertions.assertTrue(addOwner1.validInput());
        Assertions.assertTrue(addOwner2.validInput());
        Assertions.assertFalse(factory.newAddOwner(commandSender, doorRetriever, target, 3).validInput());
    }

    @Test
    void testIsAllowed()
    {
        Assertions.assertTrue(addOwner1.isAllowed(door, true));
        Assertions.assertFalse(addOwner1.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner0));
        Assertions.assertFalse(addOwner0.isAllowed(door, false));
        Assertions.assertTrue(addOwner1.isAllowed(door, false));
        Assertions.assertTrue(addOwner2.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner1));
        Assertions.assertFalse(addOwner0.isAllowed(door, false));
        Assertions.assertFalse(addOwner1.isAllowed(door, false));
        Assertions.assertTrue(addOwner2.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner2));
        Assertions.assertFalse(addOwner0.isAllowed(door, false));
        Assertions.assertFalse(addOwner1.isAllowed(door, false));
        Assertions.assertFalse(addOwner2.isAllowed(door, false));
    }

    @Test
    void nonPlayer()
    {
        final ICommandSender server = Mockito.mock(ICommandSender.class, Answers.CALLS_REAL_METHODS);
        final AddOwner addOwner = factory.newAddOwner(server, doorRetriever, target, 0);

        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner0));
        Assertions.assertFalse(addOwner.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner1));
        Assertions.assertTrue(addOwner.isAllowed(door, false));
    }

    @Test
    void testIsAllowedExistingTarget()
    {
        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner0));
        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner1));
        Assertions.assertFalse(addOwner1.isAllowed(door, false));
        Assertions.assertTrue(addOwner2.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner1));
        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner1));
        Assertions.assertFalse(addOwner1.isAllowed(door, false));
        Assertions.assertFalse(addOwner2.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner0));
        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner2));
        Assertions.assertTrue(addOwner1.isAllowed(door, false));
        Assertions.assertFalse(addOwner2.isAllowed(door, false));

        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner1));
        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner0));
        Assertions.assertFalse(addOwner1.isAllowed(door, false));
        Assertions.assertFalse(addOwner2.isAllowed(door, false));

        // It should never be possible to re-assign level 0 ownership, even with bypass enabled.
        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner0));
        Assertions.assertFalse(addOwner1.isAllowed(door, true));
        Assertions.assertFalse(addOwner2.isAllowed(door, true));
    }

    // TODO: Re-implement this.
//    @Test
//    @SneakyThrows
//    void testDelayedInput()
//    {
//        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner0));
//        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner1));
//
//        final int first = AddOwner.runDelayed(commandSender, doorRetriever);
//        final int second = AddOwner.provideDelayedInput(commandSender, target);
//
//        Assertions.assertTrue(first.get(1, TimeUnit.SECONDS));
//        Assertions.assertEquals(first, second);
//
//        Mockito.verify(databaseManager, Mockito.times(1)).addOwner(door, target, AddOwner.DEFAULT_PERMISSION_LEVEL,
//                                                                   commandSender.getPlayer().orElse(null));
//    }

    @Test
    @SneakyThrows
    void testDatabaseInteraction()
    {
        Mockito.when(door.getDoorOwner(commandSender)).thenReturn(Optional.of(doorOwner0));
        Mockito.when(door.getDoorOwner(target)).thenReturn(Optional.of(doorOwner1));
        Mockito.when(door.isDoorOwner(Mockito.any(UUID.class))).thenReturn(true);
        Mockito.when(door.isDoorOwner(Mockito.any(IPPlayer.class))).thenReturn(true);

        final CompletableFuture<Boolean> result =
            factory.newAddOwner(commandSender, doorRetriever, target, AddOwner.DEFAULT_PERMISSION_LEVEL).run();

        Assertions.assertTrue(result.get(1, TimeUnit.SECONDS));
        Mockito.verify(databaseManager, Mockito.times(1)).addOwner(door, target, AddOwner.DEFAULT_PERMISSION_LEVEL,
                                                                   commandSender.getPlayer().orElse(null));
    }
}
