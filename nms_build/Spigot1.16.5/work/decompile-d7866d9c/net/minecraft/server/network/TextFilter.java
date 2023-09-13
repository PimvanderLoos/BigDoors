package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.thread.ThreadedMailbox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextFilter implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger b = new AtomicInteger(1);
    private static final ThreadFactory c = (runnable) -> {
        Thread thread = new Thread(runnable);

        thread.setName("Chat-Filter-Worker-" + TextFilter.b.getAndIncrement());
        return thread;
    };
    private final URL d;
    private final URL e;
    private final URL f;
    private final String g;
    private final int h;
    private final String i;
    private final TextFilter.a j;
    private final ExecutorService k;

    private void a(GameProfile gameprofile, URL url, Executor executor) {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("server", this.i);
        jsonobject.addProperty("room", "Chat");
        jsonobject.addProperty("user_id", gameprofile.getId().toString());
        jsonobject.addProperty("user_display_name", gameprofile.getName());
        executor.execute(() -> {
            try {
                this.b(jsonobject, url);
            } catch (Exception exception) {
                TextFilter.LOGGER.warn("Failed to send join/leave packet to {} for player {}", url, gameprofile, exception);
            }

        });
    }

    private CompletableFuture<Optional<String>> a(GameProfile gameprofile, String s, TextFilter.a textfilter_a, Executor executor) {
        if (s.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.of(""));
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("rule", this.h);
            jsonobject.addProperty("server", this.i);
            jsonobject.addProperty("room", "Chat");
            jsonobject.addProperty("player", gameprofile.getId().toString());
            jsonobject.addProperty("player_display_name", gameprofile.getName());
            jsonobject.addProperty("text", s);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    JsonObject jsonobject1 = this.a(jsonobject, this.d);
                    boolean flag = ChatDeserializer.a(jsonobject1, "response", false);

                    if (flag) {
                        return Optional.of(s);
                    } else {
                        String s1 = ChatDeserializer.a(jsonobject1, "hashed", (String) null);

                        if (s1 == null) {
                            return Optional.empty();
                        } else {
                            int i = ChatDeserializer.u(jsonobject1, "hashes").size();

                            return textfilter_a.shouldIgnore(s1, i) ? Optional.empty() : Optional.of(s1);
                        }
                    }
                } catch (Exception exception) {
                    TextFilter.LOGGER.warn("Failed to validate message '{}'", s, exception);
                    return Optional.empty();
                }
            }, executor);
        }
    }

    public void close() {
        this.k.shutdownNow();
    }

    private void a(InputStream inputstream) throws IOException {
        byte[] abyte = new byte[1024];

        while (inputstream.read(abyte) != -1) {
            ;
        }

    }

    private JsonObject a(JsonObject jsonobject, URL url) throws IOException {
        HttpURLConnection httpurlconnection = this.c(jsonobject, url);
        InputStream inputstream = httpurlconnection.getInputStream();
        Throwable throwable = null;

        JsonObject jsonobject1;

        try {
            if (httpurlconnection.getResponseCode() != 204) {
                try {
                    jsonobject1 = Streams.parse(new JsonReader(new InputStreamReader(inputstream))).getAsJsonObject();
                    return jsonobject1;
                } finally {
                    this.a(inputstream);
                }
            }

            jsonobject1 = new JsonObject();
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (inputstream != null) {
                if (throwable != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    inputstream.close();
                }
            }

        }

        return jsonobject1;
    }

    private void b(JsonObject jsonobject, URL url) throws IOException {
        HttpURLConnection httpurlconnection = this.c(jsonobject, url);
        InputStream inputstream = httpurlconnection.getInputStream();
        Throwable throwable = null;

        try {
            this.a(inputstream);
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (inputstream != null) {
                if (throwable != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    inputstream.close();
                }
            }

        }

    }

    private HttpURLConnection c(JsonObject jsonobject, URL url) throws IOException {
        HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();

        httpurlconnection.setConnectTimeout(15000);
        httpurlconnection.setReadTimeout(2000);
        httpurlconnection.setUseCaches(false);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setDoInput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        httpurlconnection.setRequestProperty("Accept", "application/json");
        httpurlconnection.setRequestProperty("Authorization", "Basic " + this.g);
        httpurlconnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getGameVersion().getName());
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(httpurlconnection.getOutputStream(), StandardCharsets.UTF_8);
        Throwable throwable = null;

        try {
            JsonWriter jsonwriter = new JsonWriter(outputstreamwriter);
            Throwable throwable1 = null;

            try {
                Streams.write(jsonobject, jsonwriter);
            } catch (Throwable throwable2) {
                throwable1 = throwable2;
                throw throwable2;
            } finally {
                if (jsonwriter != null) {
                    if (throwable1 != null) {
                        try {
                            jsonwriter.close();
                        } catch (Throwable throwable3) {
                            throwable1.addSuppressed(throwable3);
                        }
                    } else {
                        jsonwriter.close();
                    }
                }

            }
        } catch (Throwable throwable4) {
            throwable = throwable4;
            throw throwable4;
        } finally {
            if (outputstreamwriter != null) {
                if (throwable != null) {
                    try {
                        outputstreamwriter.close();
                    } catch (Throwable throwable5) {
                        throwable.addSuppressed(throwable5);
                    }
                } else {
                    outputstreamwriter.close();
                }
            }

        }

        int i = httpurlconnection.getResponseCode();

        if (i >= 200 && i < 300) {
            return httpurlconnection;
        } else {
            throw new TextFilter.c(i + " " + httpurlconnection.getResponseMessage());
        }
    }

    public ITextFilter a(GameProfile gameprofile) {
        return new TextFilter.b(gameprofile);
    }

    @FunctionalInterface
    public interface a {

        TextFilter.a a = (s, i) -> {
            return false;
        };
        TextFilter.a b = (s, i) -> {
            return s.length() == i;
        };

        boolean shouldIgnore(String s, int i);
    }

    class b implements ITextFilter {

        private final GameProfile b;
        private final Executor c;

        private b(GameProfile gameprofile) {
            this.b = gameprofile;
            ThreadedMailbox<Runnable> threadedmailbox = ThreadedMailbox.a((Executor) TextFilter.this.k, "chat stream for " + gameprofile.getName());

            this.c = threadedmailbox::a;
        }

        @Override
        public void a() {
            TextFilter.this.a(this.b, TextFilter.this.e, this.c);
        }

        @Override
        public void b() {
            TextFilter.this.a(this.b, TextFilter.this.f, this.c);
        }

        @Override
        public CompletableFuture<Optional<List<String>>> a(List<String> list) {
            List<CompletableFuture<Optional<String>>> list1 = (List) list.stream().map((s) -> {
                return TextFilter.this.a(this.b, s, TextFilter.this.j, this.c);
            }).collect(ImmutableList.toImmutableList());

            return SystemUtils.b(list1).thenApply((list2) -> {
                return Optional.of(list2.stream().map((optional) -> {
                    return (String) optional.orElse("");
                }).collect(ImmutableList.toImmutableList()));
            }).exceptionally((throwable) -> {
                return Optional.empty();
            });
        }

        @Override
        public CompletableFuture<Optional<String>> a(String s) {
            return TextFilter.this.a(this.b, s, TextFilter.this.j, this.c);
        }
    }

    public static class c extends RuntimeException {

        private c(String s) {
            super(s);
        }
    }
}
