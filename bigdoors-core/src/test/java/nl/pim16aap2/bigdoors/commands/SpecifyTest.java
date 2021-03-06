package nl.pim16aap2.bigdoors.commands;

import lombok.SneakyThrows;
import nl.pim16aap2.bigdoors.UnitTestUtil;
import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.localization.ILocalizer;
import nl.pim16aap2.bigdoors.managers.DoorSpecificationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;


class SpecifyTest
{
    @Mock
    private IPPlayer commandSender;

    @Mock
    private DoorSpecificationManager doorSpecificationManager;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private Specify.IFactory factory;

    @BeforeEach
    void init()
    {
        MockitoAnnotations.openMocks(this);

        CommandTestingUtil.initCommandSenderPermissions(commandSender, true, true);

        final ILocalizer localizer = UnitTestUtil.initLocalizer();

        Mockito.when(factory.newSpecify(Mockito.any(ICommandSender.class), Mockito.anyString()))
               .thenAnswer(invoc -> new Specify(invoc.getArgument(0, ICommandSender.class), localizer,
                                                invoc.getArgument(1, String.class), doorSpecificationManager));
    }

    @Test
    @SneakyThrows
    void testServer()
    {
        final IPServer server = Mockito.mock(IPServer.class, Answers.CALLS_REAL_METHODS);
        Assertions.assertTrue(factory.newSpecify(server, "newDoor").run().get(1, TimeUnit.SECONDS));
        Mockito.verify(doorSpecificationManager, Mockito.never()).handleInput(Mockito.any(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void testExecution()
    {
        Mockito.when(doorSpecificationManager.handleInput(Mockito.any(), Mockito.any())).thenReturn(true);
        final String input = "newDoor";
        Assertions.assertTrue(factory.newSpecify(commandSender, input).run().get(1, TimeUnit.SECONDS));
        Mockito.verify(doorSpecificationManager).handleInput(commandSender, input);
        Mockito.verify(commandSender, Mockito.never()).sendMessage(Mockito.any());

        // Test again, but now the command sender is not an active tool user.
        Mockito.when(doorSpecificationManager.handleInput(Mockito.any(), Mockito.any())).thenReturn(false);
        Assertions.assertTrue(factory.newSpecify(commandSender, input).run().get(1, TimeUnit.SECONDS));
        Mockito.verify(doorSpecificationManager, Mockito.times(2)).handleInput(commandSender, input);
        Mockito.verify(commandSender).sendMessage(Mockito.any());
    }
}
