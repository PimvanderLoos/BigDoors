package nl.pim16aap2.bigDoors.util;

import com.google.common.io.BaseEncoding;
import org.semver4j.Semver;

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
        Semver.of(1, 11, 0),
        Semver.of(1, 12, 2)
    ),

    FORMAT_4(
        "https://www.dropbox.com/scl/fi/bm29osi71pb70njyu5st8/BigDoorsResourcePack-Format4.zip?rlkey=cxolgh4bw4iiucnjkca789ofd&st=klrqaohl&dl=1",
        Semver.of(1, 13, 0),
        Semver.of(1, 14, 4)
    ),

    FORMAT_5(
        "https://www.dropbox.com/scl/fi/gg6bf8e30k89n7obvlpxc/BigDoorsResourcePack-Format5.zip?rlkey=l80rbfebfzb7zjnv2p814csxs&st=yeeu8ix4&dl=1",
        Semver.of(1, 15, 0),
        Semver.of(1, 16, 1)
    ),

    FORMAT_6(
        "https://www.dropbox.com/scl/fi/frc758gboe5xk1iz2jb4v/BigDoorsResourcePack-Format6.zip?rlkey=tp45b9m6dtgdka23mml535zid&st=zi7c5d2m&dl=1",
        Semver.of(1, 16, 2),
        Semver.of(1, 16, 5)
    ),

    FORMAT_7(
        "https://www.dropbox.com/s/frkik8qpv3jep9v/BigDoorsResourcePack-Format7.zip?dl=1",
        Semver.of(1, 17, 0),
        Semver.of(1, 17, 1)
    ),

    FORMAT_8(
        "https://www.dropbox.com/s/4pkvrpb9kmrq590/BigDoorsResourcePack-Format8.zip?dl=1",
        Semver.of(1, 18, 0),
        Semver.of(1, 18, 2)
    ),

    FORMAT_9(
        "https://www.dropbox.com/s/mrft439gckhz2cw/BigDoorsResourcePack-Format9.zip?dl=1",
        Semver.of(1, 19, 0),
        Semver.of(1, 19, 2)
    ),

    FORMAT_12(
        "https://www.dropbox.com/s/8vpwzjkd9jnp1xu/BigDoorsResourcePack-Format12.zip?dl=1",
        Semver.of(1, 19, 3),
        Semver.of(1, 19, 3)
    ),

    FORMAT_13(
        "https://www.dropbox.com/s/3b6ohu02ueq5no0/BigDoorsResourcePack-Format13.zip?dl=1",
        Semver.of(1, 19, 4),
        Semver.of(1, 19, 4)
    ),

    FORMAT_15(
        "https://www.dropbox.com/scl/fi/bnfpjvmk9gm1470iohjdx/BigDoorsResourcePack-Format15.zip?rlkey=us8jfsq9cqqz5zy2c2bb7ufuc&st=cii8luqv&dl=1",
        "759f22b5dc02edaf33e70fd89556248e85be92cf",
        Semver.of(1, 20, 0),
        Semver.of(1, 20, 1)
    ),

    FORMAT_18(
        "https://www.dropbox.com/scl/fi/6antz5z6qw0213st3qw81/BigDoorsResourcePack-Format18.zip?rlkey=oz4ghyajlhg7kkfma0iwveyph&st=p07goie9&dl=1",
        "5d457693e69f26bd7657db0b7033ad68dd465409",
        Semver.of(1, 20, 2),
        Semver.of(1, 20, 2)
    ),

    FORMAT_22(
        "https://www.dropbox.com/scl/fi/iid6k41zigbk7lr6q4k9p/BigDoorsResourcePack-Format22.zip?rlkey=rxa0yew6pzjelqqmwjno7mta0&st=5taz85qm&dl=1",
        "c05d5e2e4c327cb40c623b6a91456b0e86ab2987",
        Semver.of(1, 20, 3),
        Semver.of(1, 20, 4)
    ),

    FORMAT_32(
        "https://www.dropbox.com/scl/fi/xh01ikoowsn4t82hgb06d/BigDoorsResourcePack-Format32.zip?rlkey=d9m1rb80dt1xq7giypxif3e4i&st=rjh7uskr&dl=1",
        "4cab78f1f2e0183039ce252effe8ecb3f202632f",
        Semver.of(1, 20, 5),
        Semver.of(1, 20, 6)
    ),

    FORMAT_34(
        "https://www.dropbox.com/scl/fi/71jyxjeqfyrcn8hokn6cy/BigDoorsResourcePack-Format34.zip?rlkey=05afk8t5yxdil1w6zm34akzzo&st=9xxocl78&dl=1",
        "4d81904572cdf0ef7b313f87012b7230a2d2ac12",
        Semver.of(1, 21, 0),
        Semver.of(1, 21, 3)
    ),

    FORMAT_46(
        "https://www.dropbox.com/scl/fi/5totedxlfixk7dm7cnukz/BigDoorsResourcePack-Format46.zip?rlkey=fhy152g0rrh42ucu7uu3hgwyh&st=1w7yzm1a&dl=1",
        "ad5c666f2d37c8b992058fae5e6706b2883baab1",
        Semver.of(1, 21, 4),
        Semver.of(1, 21, 4)
    ),

    FORMAT_51(
        "https://www.dropbox.com/scl/fi/c6yv4ellokzp8blf91nlu/BigDoorsResourcePack-Format51.zip?rlkey=db4qkpjnxeziw9h2jzpjzp9o2&st=hhv1xigd&dl=1",
        "eacaccec6e755f871ef420a336bacafa0526eeca",
        Semver.of(1, 21, 5),
        Semver.of(1, 21, 5)
    ),

    ;

    private static final ResourcePackDetails[] VALUES = values();
    private static final ResourcePackDetails LATEST = VALUES[VALUES.length - 1];

    private final String url;
    private final byte[] hash;
    private final Semver minVersion;
    private final Semver maxVersion;

    /**
     * @param url The URL to the resource pack.
     * @param hash The SHA1 hash of the resource pack.
     * @param minVersion The minimum version for which the resource pack is suitable (inclusive).
     * @param maxVersion The maximum version for which the resource pack is suitable (inclusive).
     */
    ResourcePackDetails(String url, byte[] hash, Semver minVersion, Semver maxVersion)
    {
        this.url = url;
        this.hash = hash;

        this.minVersion = minVersion;
        this.maxVersion = maxVersion;

        if (this.hash.length != 0 && this.hash.length != 20)
            throw new IllegalArgumentException("The hash must be empty or 20 bytes long! Got: " + this.hash.length + " bytes.");
    }

    ResourcePackDetails(String url, String hash, Semver minVersion, Semver maxVersion)
    {
        this(url, decodeHash(hash), minVersion, maxVersion);
    }

    ResourcePackDetails(String url, Semver minVersion, Semver maxVersion)
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
    public static ResourcePackDetails getForVersion(Semver version)
    {
        if (version.isGreaterThan(LATEST.minVersion))
            return LATEST;

        for (int idx = VALUES.length - 1; idx >= 0; idx--)
        {
            final ResourcePackDetails data = VALUES[idx];
            if (version.isGreaterThanOrEqualTo(data.minVersion) && version.isLowerThanOrEqualTo(data.maxVersion))
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
