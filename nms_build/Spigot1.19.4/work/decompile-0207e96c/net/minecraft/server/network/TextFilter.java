package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.thread.ThreadedMailbox;
import org.slf4j.Logger;

public class TextFilter implements AutoCloseable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
    private static final ThreadFactory THREAD_FACTORY = (runnable) -> {
        Thread thread = new Thread(runnable);

        thread.setName("Chat-Filter-Worker-" + TextFilter.WORKER_COUNT.getAndIncrement());
        return thread;
    };
    private static final String DEFAULT_ENDPOINT = "v1/chat";
    private final URL chatEndpoint;
    private final TextFilter.c chatEncoder;
    final URL joinEndpoint;
    final TextFilter.b joinEncoder;
    final URL leaveEndpoint;
    final TextFilter.b leaveEncoder;
    private final String authKey;
    final TextFilter.a chatIgnoreStrategy;
    final ExecutorService workerPool;

    private TextFilter(URL url, TextFilter.c textfilter_c, URL url1, TextFilter.b textfilter_b, URL url2, TextFilter.b textfilter_b1, String s, TextFilter.a textfilter_a, int i) {
        this.authKey = s;
        this.chatIgnoreStrategy = textfilter_a;
        this.chatEndpoint = url;
        this.chatEncoder = textfilter_c;
        this.joinEndpoint = url1;
        this.joinEncoder = textfilter_b;
        this.leaveEndpoint = url2;
        this.leaveEncoder = textfilter_b1;
        this.workerPool = Executors.newFixedThreadPool(i, TextFilter.THREAD_FACTORY);
    }

    private static URL getEndpoint(URI uri, @Nullable JsonObject jsonobject, String s, String s1) throws MalformedURLException {
        String s2 = getEndpointFromConfig(jsonobject, s, s1);

        return uri.resolve("/" + s2).toURL();
    }

    private static String getEndpointFromConfig(@Nullable JsonObject jsonobject, String s, String s1) {
        return jsonobject != null ? ChatDeserializer.getAsString(jsonobject, s, s1) : s1;
    }

    @Nullable
    public static TextFilter createFromConfig(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return null;
        } else {
            try {
                JsonObject jsonobject = ChatDeserializer.parse(s);
                URI uri = new URI(ChatDeserializer.getAsString(jsonobject, "apiServer"));
                String s1 = ChatDeserializer.getAsString(jsonobject, "apiKey");

                if (s1.isEmpty()) {
                    throw new IllegalArgumentException("Missing API key");
                } else {
                    int i = ChatDeserializer.getAsInt(jsonobject, "ruleId", 1);
                    String s2 = ChatDeserializer.getAsString(jsonobject, "serverId", "");
                    String s3 = ChatDeserializer.getAsString(jsonobject, "roomId", "Java:Chat");
                    int j = ChatDeserializer.getAsInt(jsonobject, "hashesToDrop", -1);
                    int k = ChatDeserializer.getAsInt(jsonobject, "maxConcurrentRequests", 7);
                    JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "endpoints", (JsonObject) null);
                    String s4 = getEndpointFromConfig(jsonobject1, "chat", "v1/chat");
                    boolean flag = s4.equals("v1/chat");
                    URL url = uri.resolve("/" + s4).toURL();
                    URL url1 = getEndpoint(uri, jsonobject1, "join", "v1/join");
                    URL url2 = getEndpoint(uri, jsonobject1, "leave", "v1/leave");
                    TextFilter.b textfilter_b = (gameprofile) -> {
                        JsonObject jsonobject2 = new JsonObject();

                        jsonobject2.addProperty("server", s2);
                        jsonobject2.addProperty("room", s3);
                        jsonobject2.addProperty("user_id", gameprofile.getId().toString());
                        jsonobject2.addProperty("user_display_name", gameprofile.getName());
                        return jsonobject2;
                    };
                    TextFilter.c textfilter_c;

                    if (flag) {
                        textfilter_c = (gameprofile, s5) -> {
                            JsonObject jsonobject2 = new JsonObject();

                            jsonobject2.addProperty("rule", i);
                            jsonobject2.addProperty("server", s2);
                            jsonobject2.addProperty("room", s3);
                            jsonobject2.addProperty("player", gameprofile.getId().toString());
                            jsonobject2.addProperty("player_display_name", gameprofile.getName());
                            jsonobject2.addProperty("text", s5);
                            jsonobject2.addProperty("language", "*");
                            return jsonobject2;
                        };
                    } else {
                        String s5 = String.valueOf(i);

                        textfilter_c = (gameprofile, s6) -> {
                            JsonObject jsonobject2 = new JsonObject();

                            jsonobject2.addProperty("rule_id", s5);
                            jsonobject2.addProperty("category", s2);
                            jsonobject2.addProperty("subcategory", s3);
                            jsonobject2.addProperty("user_id", gameprofile.getId().toString());
                            jsonobject2.addProperty("user_display_name", gameprofile.getName());
                            jsonobject2.addProperty("text", s6);
                            jsonobject2.addProperty("language", "*");
                            return jsonobject2;
                        };
                    }

                    TextFilter.a textfilter_a = TextFilter.a.select(j);
                    String s6 = Base64.getEncoder().encodeToString(s1.getBytes(StandardCharsets.US_ASCII));

                    return new TextFilter(url, textfilter_c, url1, textfilter_b, url2, textfilter_b, s6, textfilter_a, k);
                }
            } catch (Exception exception) {
                TextFilter.LOGGER.warn("Failed to parse chat filter config {}", s, exception);
                return null;
            }
        }
    }

    void processJoinOrLeave(GameProfile gameprofile, URL url, TextFilter.b textfilter_b, Executor executor) {
        executor.execute(() -> {
            JsonObject jsonobject = textfilter_b.encode(gameprofile);

            try {
                this.processRequest(jsonobject, url);
            } catch (Exception exception) {
                TextFilter.LOGGER.warn("Failed to send join/leave packet to {} for player {}", new Object[]{url, gameprofile, exception});
            }

        });
    }

    CompletableFuture<FilteredText> requestMessageProcessing(GameProfile gameprofile, String s, TextFilter.a textfilter_a, Executor executor) {
        return s.isEmpty() ? CompletableFuture.completedFuture(FilteredText.EMPTY) : CompletableFuture.supplyAsync(() -> {
            JsonObject jsonobject = this.chatEncoder.encode(gameprofile, s);

            try {
                JsonObject jsonobject1 = this.processRequestResponse(jsonobject, this.chatEndpoint);
                boolean flag = ChatDeserializer.getAsBoolean(jsonobject1, "response", false);

                if (flag) {
                    return FilteredText.passThrough(s);
                } else {
                    String s1 = ChatDeserializer.getAsString(jsonobject1, "hashed", (String) null);

                    if (s1 == null) {
                        return FilteredText.fullyFiltered(s);
                    } else {
                        JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject1, "hashes");
                        FilterMask filtermask = this.parseMask(s, jsonarray, textfilter_a);

                        return new FilteredText(s, filtermask);
                    }
                }
            } catch (Exception exception) {
                TextFilter.LOGGER.warn("Failed to validate message '{}'", s, exception);
                return FilteredText.fullyFiltered(s);
            }
        }, executor);
    }

    private FilterMask parseMask(String s, JsonArray jsonarray, TextFilter.a textfilter_a) {
        if (jsonarray.isEmpty()) {
            return FilterMask.PASS_THROUGH;
        } else if (textfilter_a.shouldIgnore(s, jsonarray.size())) {
            return FilterMask.FULLY_FILTERED;
        } else {
            FilterMask filtermask = new FilterMask(s.length());

            for (int i = 0; i < jsonarray.size(); ++i) {
                filtermask.setFiltered(jsonarray.get(i).getAsInt());
            }

            return filtermask;
        }
    }

    public void close() {
        this.workerPool.shutdownNow();
    }

    private void drainStream(InputStream inputstream) throws IOException {
        byte[] abyte = new byte[1024];

        while (inputstream.read(abyte) != -1) {
            ;
        }

    }

    private JsonObject processRequestResponse(JsonObject jsonobject, URL url) throws IOException {
        HttpURLConnection httpurlconnection = this.makeRequest(jsonobject, url);
        InputStream inputstream = httpurlconnection.getInputStream();

        JsonObject jsonobject1;
        label91:
        {
            try {
                if (httpurlconnection.getResponseCode() != 204) {
                    try {
                        jsonobject1 = Streams.parse(new JsonReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))).getAsJsonObject();
                        break label91;
                    } finally {
                        this.drainStream(inputstream);
                    }
                }

                jsonobject1 = new JsonObject();
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

            return jsonobject1;
        }

        if (inputstream != null) {
            inputstream.close();
        }

        return jsonobject1;
    }

    private void processRequest(JsonObject jsonobject, URL url) throws IOException {
        HttpURLConnection httpurlconnection = this.makeRequest(jsonobject, url);
        InputStream inputstream = httpurlconnection.getInputStream();

        try {
            this.drainStream(inputstream);
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

    }

    private HttpURLConnection makeRequest(JsonObject jsonobject, URL url) throws IOException {
        HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();

        httpurlconnection.setConnectTimeout(15000);
        httpurlconnection.setReadTimeout(2000);
        httpurlconnection.setUseCaches(false);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setDoInput(true);
        httpurlconnection.setRequestMethod("POST");
        httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        httpurlconnection.setRequestProperty("Accept", "application/json");
        httpurlconnection.setRequestProperty("Authorization", "Basic " + this.authKey);
        httpurlconnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(httpurlconnection.getOutputStream(), StandardCharsets.UTF_8);

        try {
            JsonWriter jsonwriter = new JsonWriter(outputstreamwriter);

            try {
                Streams.write(jsonobject, jsonwriter);
            } catch (Throwable throwable) {
                try {
                    jsonwriter.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            jsonwriter.close();
        } catch (Throwable throwable2) {
            try {
                outputstreamwriter.close();
            } catch (Throwable throwable3) {
                throwable2.addSuppressed(throwable3);
            }

            throw throwable2;
        }

        outputstreamwriter.close();
        int i = httpurlconnection.getResponseCode();

        if (i >= 200 && i < 300) {
            return httpurlconnection;
        } else {
            throw new TextFilter.e(i + " " + httpurlconnection.getResponseMessage());
        }
    }

    public ITextFilter createContext(GameProfile gameprofile) {
        return new TextFilter.d(gameprofile);
    }

    @FunctionalInterface
    public interface a {

        TextFilter.a NEVER_IGNORE = (s, i) -> {
            return false;
        };
        TextFilter.a IGNORE_FULLY_FILTERED = (s, i) -> {
            return s.length() == i;
        };

        static TextFilter.a ignoreOverThreshold(int i) {
            return (s, j) -> {
                return j >= i;
            };
        }

        static TextFilter.a select(int i) {
            TextFilter.a textfilter_a;

            switch (i) {
                case -1:
                    textfilter_a = TextFilter.a.NEVER_IGNORE;
                    break;
                case 0:
                    textfilter_a = TextFilter.a.IGNORE_FULLY_FILTERED;
                    break;
                default:
                    textfilter_a = ignoreOverThreshold(i);
            }

            return textfilter_a;
        }

        boolean shouldIgnore(String s, int i);
    }

    @FunctionalInterface
    private interface c {

        JsonObject encode(GameProfile gameprofile, String s);
    }

    @FunctionalInterface
    private interface b {

        JsonObject encode(GameProfile gameprofile);
    }

    public static class e extends RuntimeException {

        e(String s) {
            super(s);
        }
    }

    private class d implements ITextFilter {

        private final GameProfile profile;
        private final Executor streamExecutor;

        d(GameProfile gameprofile) {
            this.profile = gameprofile;
            ThreadedMailbox<Runnable> threadedmailbox = ThreadedMailbox.create(TextFilter.this.workerPool, "chat stream for " + gameprofile.getName());

            Objects.requireNonNull(threadedmailbox);
            this.streamExecutor = threadedmailbox::tell;
        }

        @Override
        public void join() {
            TextFilter.this.processJoinOrLeave(this.profile, TextFilter.this.joinEndpoint, TextFilter.this.joinEncoder, this.streamExecutor);
        }

        @Override
        public void leave() {
            TextFilter.this.processJoinOrLeave(this.profile, TextFilter.this.leaveEndpoint, TextFilter.this.leaveEncoder, this.streamExecutor);
        }

        @Override
        public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> list) {
            List<CompletableFuture<FilteredText>> list1 = (List) list.stream().map((s) -> {
                return TextFilter.this.requestMessageProcessing(this.profile, s, TextFilter.this.chatIgnoreStrategy, this.streamExecutor);
            }).collect(ImmutableList.toImmutableList());

            return SystemUtils.sequenceFailFast(list1).exceptionally((throwable) -> {
                return ImmutableList.of();
            });
        }

        @Override
        public CompletableFuture<FilteredText> processStreamMessage(String s) {
            return TextFilter.this.requestMessageProcessing(this.profile, s, TextFilter.this.chatIgnoreStrategy, this.streamExecutor);
        }
    }
}
