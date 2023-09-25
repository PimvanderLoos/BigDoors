package nl.pim16aap2.bigDoors.util;

import org.apache.commons.lang.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A functional interface to compare two version Strings with similar version schemes.
 */
@FunctionalInterface
public interface VersionScheme
{
    /**
     * Compares two versions and return the higher of the two. If null is returned, it is assumed that at least one of
     * the two versions are unsupported by this version scheme parser.
     *
     * @param first
     *     The first version to check.
     * @param second
     *     The second version to check.
     * @return The greater of the two versions or null if the format of at least one of the two versions is not valid.
     */
    @Nullable String compareVersions(String first, String second);

    /**
     * A version scheme that uses the decimal system to compare versions.
     * <p>
     * For example, "1.2.3" is considered a higher version than "1.2.2" and "1.2" but lower than "1.12".
     */
    VersionScheme DECIMAL = new Decimal();

    /**
     * A version scheme that uses the decimal system to compare versions.
     * <p>
     * For example, "1.2.3" is considered a higher version than "1.2.2" and "1.2" but lower than "1.12".
     * <p>
     * An instance of this class can be obtained through {@link #DECIMAL}.
     */
    final class Decimal implements VersionScheme
    {
        private static final Pattern DECIMAL_SCHEME_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)*");

        private Decimal()
        {
            // Should be obtained through the static field.
        }

        @Override
        public @Nullable String compareVersions(String first, String second)
        {
            String[] firstSplit = splitVersionInfo(first), secondSplit = splitVersionInfo(second);
            if (firstSplit == null || secondSplit == null)
                return null;

            for (int i = 0; i < Math.min(firstSplit.length, secondSplit.length); i++)
            {
                int currentValue = NumberUtils.toInt(firstSplit[i]), newestValue = NumberUtils.toInt(secondSplit[i]);

                if (newestValue > currentValue)
                    return second;
                else if (newestValue < currentValue)
                    return first;
            }

            return (secondSplit.length > firstSplit.length) ? second : first;
        }

        private static String[] splitVersionInfo(String version)
        {
            Matcher matcher = DECIMAL_SCHEME_PATTERN.matcher(version);
            if (!matcher.find())
                return null;

            return matcher.group().split("\\.");
        }
    }
}
