package nl.pim16aap2.bigDoors.util;

import com.google.common.io.BaseEncoding;

import java.util.Locale;

/**
 * Represents the details of the resource pack.
 * <p>
 * Each resource pack has a URL and a minimum and maximum version for which it is suitable.
 * <p>
 * The URL is the URL to the resource pack file.
 * <p>
 * The minimum and maximum version are the taken from the <a
 * href="https://minecraft.fandom.com/wiki/Pack_format">wiki page</a>.
 */
public enum ResourcePackDetails
{
    FORMAT_3(
        "https://www.dropbox.com/s/6zdkg4jr90pc1mi/BigDoorsResourcePack-1_11.zip?dl=1",
        new MinecraftVersion(1, 11),
        new MinecraftVersion(1, 12, 2)
    ),

    FORMAT_4(
        "https://www.dropbox.com/scl/fi/bm29osi71pb70njyu5st8/BigDoorsResourcePack-Format4.zip?rlkey=cxolgh4bw4iiucnjkca789ofd&st=klrqaohl&dl=1",
        new MinecraftVersion(1, 13),
        new MinecraftVersion(1, 14, 4)
    ),

    FORMAT_5(
        "https://www.dropbox.com/scl/fi/gg6bf8e30k89n7obvlpxc/BigDoorsResourcePack-Format5.zip?rlkey=l80rbfebfzb7zjnv2p814csxs&st=yeeu8ix4&dl=1",
        new MinecraftVersion(1, 15),
        new MinecraftVersion(1, 16, 1)
    ),

    FORMAT_6(
        "https://www.dropbox.com/scl/fi/frc758gboe5xk1iz2jb4v/BigDoorsResourcePack-Format6.zip?rlkey=tp45b9m6dtgdka23mml535zid&st=zi7c5d2m&dl=6",
        new MinecraftVersion(1, 16, 2),
        new MinecraftVersion(1, 16, 5)
    ),

    FORMAT_7(
        "https://www.dropbox.com/s/frkik8qpv3jep9v/BigDoorsResourcePack-Format7.zip?dl=1",
        new MinecraftVersion(1, 17),
        new MinecraftVersion(1, 17, 1)
    ),

    FORMAT_8(
        "https://www.dropbox.com/s/4pkvrpb9kmrq590/BigDoorsResourcePack-Format8.zip?dl=1",
        new MinecraftVersion(1, 18),
        new MinecraftVersion(1, 18, 2)
    ),

    FORMAT_9(
        "https://www.dropbox.com/s/mrft439gckhz2cw/BigDoorsResourcePack-Format9.zip?dl=1",
        new MinecraftVersion(1, 19),
        new MinecraftVersion(1, 19, 2)
    ),

    FORMAT_12(
        "https://www.dropbox.com/s/8vpwzjkd9jnp1xu/BigDoorsResourcePack-Format12.zip?dl=1",
        new MinecraftVersion(1, 19, 3),
        new MinecraftVersion(1, 19, 3)
    ),

    FORMAT_13(
        "https://www.dropbox.com/s/3b6ohu02ueq5no0/BigDoorsResourcePack-Format13.zip?dl=1",
        new MinecraftVersion(1, 19, 4),
        new MinecraftVersion(1, 19, 4)
    ),

    FORMAT_15(
        "https://www.dropbox.com/scl/fi/bnfpjvmk9gm1470iohjdx/BigDoorsResourcePack-Format15.zip?rlkey=us8jfsq9cqqz5zy2c2bb7ufuc&st=cii8luqv&dl=1",
        "759f22b5dc02edaf33e70fd89556248e85be92cf",
        new MinecraftVersion(1, 20),
        new MinecraftVersion(1, 20, 1)
    ),

    FORMAT_18(
        "https://www.dropbox.com/scl/fi/6antz5z6qw0213st3qw81/BigDoorsResourcePack-Format18.zip?rlkey=oz4ghyajlhg7kkfma0iwveyph&st=p07goie9&dl=1",
        "5d457693e69f26bd7657db0b7033ad68dd465409",
        new MinecraftVersion(1, 20, 2),
        new MinecraftVersion(1, 20, 2)
    ),

    FORMAT_23(
        "https://www.dropbox.com/scl/fi/lv8uiy5kfryhqugtih53c/BigDoorsResourcePack-Format23.zip?rlkey=esxnipm5zmytp9pnfszrd6ims&st=tv7vs9ch&dl=1",
        "c1e2037aa354ace6e929e448c9f288150f8aa646",
        new MinecraftVersion(1, 20, 3),
        new MinecraftVersion(1, 20, 4)
    ),

    FORMAT_32(
        "https://www.dropbox.com/scl/fi/xh01ikoowsn4t82hgb06d/BigDoorsResourcePack-Format32.zip?rlkey=d9m1rb80dt1xq7giypxif3e4i&st=rjh7uskr&dl=1",
        "4cab78f1f2e0183039ce252effe8ecb3f202632f",
        new MinecraftVersion(1, 20, 5),
        new MinecraftVersion(1, 20, 6)
    ),

    ;

    private static final ResourcePackDetails[] VALUES = values();
    private static final ResourcePackDetails LATEST = VALUES[VALUES.length - 1];

    private final String url;
    private final byte[] hash;
    private final MinecraftVersion minVersion;
    private final MinecraftVersion maxVersion;

    ResourcePackDetails(String url, byte[] hash, MinecraftVersion minVersion, MinecraftVersion maxVersion)
    {
        this.url = url;
        this.hash = hash;

        this.minVersion = minVersion;
        this.maxVersion = maxVersion;

        if (this.hash.length != 0 && this.hash.length != 20)
            throw new IllegalArgumentException("The hash must be empty or 20 bytes long! Got: " + this.hash.length + " bytes.");
    }

    ResourcePackDetails(String url, String hash, MinecraftVersion minVersion, MinecraftVersion maxVersion)
    {
        this(url, decodeHash(hash), minVersion, maxVersion);
    }

    ResourcePackDetails(String url, MinecraftVersion minVersion, MinecraftVersion maxVersion)
    {
        this(url, new byte[0], minVersion, maxVersion);
    }

    /**
     * Decodes the hash from a hexadecimal string to a byte array.
     *
     * @param hash The hash to decode.
     * @return The decoded hash.
     */
    private static byte[] decodeHash(String hash)
    {
        if (hash.isEmpty())
            return new byte[0];

        if (hash.length() != 40)
            throw new IllegalArgumentException(
                "The hash must be 40 characters long! Got: " + hash.length() + " characters.");

        //noinspection UnstableApiUsage
        return BaseEncoding.base16().decode(hash.toUpperCase(Locale.ROOT));
    }

    /**
     * Finds the resource pack data most suitable for the given version.
     * <p>
     * If no suitable resource pack data is found, the latest resource pack data is returned.
     *
     * @param version The version for which to find the most suitable resource pack data.
     * @return The resource pack data most suitable for the given version.
     */
    public static ResourcePackDetails getForVersion(MinecraftVersion version)
    {
        if (version.isOlderThan(LATEST.minVersion))
            return LATEST;

        for (int idx = VALUES.length - 1; idx >= 0; idx--)
        {
            final ResourcePackDetails data = VALUES[idx];
            if (version.isBetween(data.minVersion, data.maxVersion))
                return data;
        }

        return LATEST;
    }

    /**
     * Gets the URL to the resource pack.
     *
     * @return The URL to the resource pack.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Gets the hash of the resource pack.
     * <p>
     * If no hash is set, an empty byte array is returned.
     * <p>
     * If provided, the hash is an SHA-1 hash of the resource pack (20 bytes).
     *
     * @return The hash of the resource pack.
     */
    public byte[] getHash()
    {
        return hash;
    }
}
