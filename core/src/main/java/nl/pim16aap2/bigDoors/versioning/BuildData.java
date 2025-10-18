package nl.pim16aap2.bigDoors.versioning;

public final class BuildData
{
    public static BuildData EMPTY = new BuildData(
        false,
        "unknown",
        -1,
        -1,
        -1
    );

    private final boolean isRelease;
    private final String git;
    private final long buildNumber;
    private final long buildId;
    private final int buildSeqNumber;

    public BuildData(boolean isRelease, String git, long buildNumber, long buildId, int buildSeqNumber)
    {
        this.isRelease = isRelease;
        this.git = git;
        this.buildNumber = buildNumber;
        this.buildId = buildId;
        this.buildSeqNumber = buildSeqNumber;
    }

    public boolean isDevBuild()
    {
        return !isRelease;
    }

    public String getGit()
    {
        return git;
    }

    public long getBuildNumber()
    {
        return buildNumber;
    }

    public long getBuildId()
    {
        return buildId;
    }

    public int getBuildSeqNumber()
    {
        return buildSeqNumber;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Release:              %b\n" +
            "Commit:               %s\n" +
            "Actions Build number: %d\n" +
            "Actions Build id:     %d\n" +
            "Build sequence num.:  %d",
            isRelease,
            git,
            buildNumber,
            buildId,
            buildSeqNumber
        );
    }
}
