package nl.pim16aap2.bigdoors.extensions;

import nl.pim16aap2.bigdoors.api.IConfigLoader;
import nl.pim16aap2.bigdoors.api.restartable.RestartableHolder;
import nl.pim16aap2.bigdoors.managers.DoorTypeManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

class DoorTypeLoaderTest
{
    @Mock
    private RestartableHolder restartableHolder;

    @Mock
    private DoorTypeManager doorTypeManager;

    @Mock
    private IConfigLoader configLoader;

    private AutoCloseable mocks;

    @BeforeEach
    public void init()
    {
        mocks = MockitoAnnotations.openMocks(this);
        Mockito.when(configLoader.debug()).thenReturn(true);
    }

    @AfterEach
    void cleanup()
        throws Exception
    {
        mocks.close();
    }

    @Test
    void test()
        throws IOException
    {
        final Path basePath = Path.of("");
        final String extensionsPath =
            basePath.toAbsolutePath().getParent().getParent().resolve("bigdoors-doors")
                    .resolve("DoorTypes").toAbsolutePath().toString();
        final int inputCount = Objects.requireNonNull(new File(extensionsPath).list()).length;

        final var doorTypeLoader = new DoorTypeLoader(restartableHolder, doorTypeManager, configLoader, basePath);
        doorTypeLoader.initialize();
        Assertions.assertEquals(inputCount, doorTypeLoader.loadDoorTypesFromDirectory(Path.of(extensionsPath)).size());
    }
}
