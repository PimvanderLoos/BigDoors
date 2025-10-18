package nl.pim16aap2.bigDoors.versioning;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalLong;

public final class BuildDataReader
{
    private static final BuildDataReader INSTANCE = new BuildDataReader();

    private final BuildData buildData;

    private BuildDataReader()
    {
        this.buildData = readBuildData();
    }

    public static BuildData getBuildData()
    {
        return getInstance().buildData;
    }

    public static BuildDataReader getInstance()
    {
        return INSTANCE;
    }

    private BuildData readBuildData()
    {
        try
        {
            return readBuildData0();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return BuildData.EMPTY;
        }
    }

    private BuildData readBuildData0()
        throws Exception
    {
        try (
            InputStream inputStream = Objects.requireNonNull(this
                .getClass()
                .getClassLoader()
                .getResourceAsStream("build_data"));
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
        {
            final Object[] lines = bufferedReader.lines().toArray();
            if (lines.length != 4)
                throw new IllegalArgumentException("Failed to parse build data from input: " + Arrays.toString(lines));
            return new BuildData(
                parseBoolean(lines[0]),
                (String) lines[1],
                parseLong("build number", (String) lines[2]),
                parseLong("build id", (String) lines[3]),
                readBuildNumber()
            );
        }
    }

    private int readBuildNumber()
    {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/build.number")))))
        {
            for (int idx = 0; idx != 2; ++idx)
                reader.readLine();
            return Integer.parseInt(reader.readLine().replace("build.number=", ""));
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    private static OptionalLong parseLong(String str)
    {
        if (str == null)
            return OptionalLong.empty();

        try
        {
            return OptionalLong.of(Long.parseLong(str));
        }
        catch (NumberFormatException e)
        {
            return OptionalLong.empty();
        }
    }

    private long parseLong(String name, String str)
    {
        final OptionalLong ret = parseLong(str);
        if (!ret.isPresent())
            System.out.printf("Failed to parse %s from input: '%s'\n", name, str);
        return ret.orElse(-1);
    }

    private boolean parseBoolean(Object obj)
    {
        if (!(obj instanceof String))
        {
            System.out.printf("Failed to parse boolean from input: '%s'\n", obj);
            return false;
        }

        final String str = (String) obj;
        if ("true".equalsIgnoreCase(str))
            return true;
        if ("false".equalsIgnoreCase(str))
            return false;
        System.out.printf("Failed to parse boolean from input: '%s'\n", str);
        return false;
    }
}
