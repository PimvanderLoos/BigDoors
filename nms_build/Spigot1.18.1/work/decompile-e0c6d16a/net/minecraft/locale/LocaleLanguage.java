package net.minecraft.locale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatFormatted;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.FormattedString;
import net.minecraft.util.StringDecomposer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LocaleLanguage {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    public static final String DEFAULT = "en_us";
    private static volatile LocaleLanguage instance = loadDefault();

    public LocaleLanguage() {}

    private static LocaleLanguage loadDefault() {
        Builder<String, String> builder = ImmutableMap.builder();

        Objects.requireNonNull(builder);
        BiConsumer<String, String> biconsumer = builder::put;
        String s = "/assets/minecraft/lang/en_us.json";

        try {
            InputStream inputstream = LocaleLanguage.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");

            try {
                loadFromJson(inputstream, biconsumer);
            } catch (Throwable throwable) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (inputstream != null) {
                inputstream.close();
            }
        } catch (JsonParseException | IOException ioexception) {
            LocaleLanguage.LOGGER.error("Couldn't read strings from {}", "/assets/minecraft/lang/en_us.json", ioexception);
        }

        final Map<String, String> map = builder.build();

        return new LocaleLanguage() {
            @Override
            public String getOrDefault(String s1) {
                return (String) map.getOrDefault(s1, s1);
            }

            @Override
            public boolean has(String s1) {
                return map.containsKey(s1);
            }

            @Override
            public boolean isDefaultRightToLeft() {
                return false;
            }

            @Override
            public FormattedString getVisualOrder(IChatFormatted ichatformatted) {
                return (formattedstringempty) -> {
                    return ichatformatted.visit((chatmodifier, s1) -> {
                        return StringDecomposer.iterateFormatted(s1, chatmodifier, formattedstringempty) ? Optional.empty() : IChatFormatted.STOP_ITERATION;
                    }, ChatModifier.EMPTY).isPresent();
                };
            }
        };
    }

    public static void loadFromJson(InputStream inputstream, BiConsumer<String, String> biconsumer) {
        JsonObject jsonobject = (JsonObject) LocaleLanguage.GSON.fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonObject.class);
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry) iterator.next();
            String s = LocaleLanguage.UNSUPPORTED_FORMAT_PATTERN.matcher(ChatDeserializer.convertToString((JsonElement) entry.getValue(), (String) entry.getKey())).replaceAll("%$1s");

            biconsumer.accept((String) entry.getKey(), s);
        }

    }

    public static LocaleLanguage getInstance() {
        return LocaleLanguage.instance;
    }

    public static void inject(LocaleLanguage localelanguage) {
        LocaleLanguage.instance = localelanguage;
    }

    public abstract String getOrDefault(String s);

    public abstract boolean has(String s);

    public abstract boolean isDefaultRightToLeft();

    public abstract FormattedString getVisualOrder(IChatFormatted ichatformatted);

    public List<FormattedString> getVisualOrder(List<IChatFormatted> list) {
        return (List) list.stream().map(this::getVisualOrder).collect(ImmutableList.toImmutableList());
    }
}
