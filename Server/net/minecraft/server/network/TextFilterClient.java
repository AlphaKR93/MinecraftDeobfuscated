/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.internal.Streams
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.OutputStreamWriter
 *  java.io.Reader
 *  java.io.Writer
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.net.HttpURLConnection
 *  java.net.MalformedURLException
 *  java.net.URI
 *  java.net.URL
 *  java.nio.charset.StandardCharsets
 *  java.util.Base64
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.ExecutorService
 *  java.util.concurrent.Executors
 *  java.util.concurrent.ThreadFactory
 *  java.util.concurrent.atomic.AtomicInteger
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.TextFilter;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class TextFilterClient
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
    private static final ThreadFactory THREAD_FACTORY = $$0 -> {
        Thread $$1 = new Thread($$0);
        $$1.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
        return $$1;
    };
    private static final String DEFAULT_ENDPOINT = "v1/chat";
    private final URL chatEndpoint;
    private final MessageEncoder chatEncoder;
    final URL joinEndpoint;
    final JoinOrLeaveEncoder joinEncoder;
    final URL leaveEndpoint;
    final JoinOrLeaveEncoder leaveEncoder;
    private final String authKey;
    final IgnoreStrategy chatIgnoreStrategy;
    final ExecutorService workerPool;

    private TextFilterClient(URL $$0, MessageEncoder $$1, URL $$2, JoinOrLeaveEncoder $$3, URL $$4, JoinOrLeaveEncoder $$5, String $$6, IgnoreStrategy $$7, int $$8) {
        this.authKey = $$6;
        this.chatIgnoreStrategy = $$7;
        this.chatEndpoint = $$0;
        this.chatEncoder = $$1;
        this.joinEndpoint = $$2;
        this.joinEncoder = $$3;
        this.leaveEndpoint = $$4;
        this.leaveEncoder = $$5;
        this.workerPool = Executors.newFixedThreadPool((int)$$8, (ThreadFactory)THREAD_FACTORY);
    }

    private static URL getEndpoint(URI $$0, @Nullable JsonObject $$1, String $$2, String $$3) throws MalformedURLException {
        String $$4 = TextFilterClient.getEndpointFromConfig($$1, $$2, $$3);
        return $$0.resolve("/" + $$4).toURL();
    }

    private static String getEndpointFromConfig(@Nullable JsonObject $$0, String $$1, String $$2) {
        return $$0 != null ? GsonHelper.getAsString($$0, $$1, $$2) : $$2;
    }

    @Nullable
    public static TextFilterClient createFromConfig(String $$0) {
        if (Strings.isNullOrEmpty((String)$$0)) {
            return null;
        }
        try {
            MessageEncoder $$18;
            JsonObject $$1 = GsonHelper.parse($$0);
            URI $$22 = new URI(GsonHelper.getAsString($$1, "apiServer"));
            String $$32 = GsonHelper.getAsString($$1, "apiKey");
            if ($$32.isEmpty()) {
                throw new IllegalArgumentException("Missing API key");
            }
            int $$42 = GsonHelper.getAsInt($$1, "ruleId", 1);
            String $$5 = GsonHelper.getAsString($$1, "serverId", "");
            String $$6 = GsonHelper.getAsString($$1, "roomId", "Java:Chat");
            int $$7 = GsonHelper.getAsInt($$1, "hashesToDrop", -1);
            int $$8 = GsonHelper.getAsInt($$1, "maxConcurrentRequests", 7);
            JsonObject $$9 = GsonHelper.getAsJsonObject($$1, "endpoints", null);
            String $$10 = TextFilterClient.getEndpointFromConfig($$9, "chat", DEFAULT_ENDPOINT);
            boolean $$11 = $$10.equals((Object)DEFAULT_ENDPOINT);
            URL $$12 = $$22.resolve("/" + $$10).toURL();
            URL $$13 = TextFilterClient.getEndpoint($$22, $$9, "join", "v1/join");
            URL $$14 = TextFilterClient.getEndpoint($$22, $$9, "leave", "v1/leave");
            JoinOrLeaveEncoder $$15 = $$2 -> {
                JsonObject $$3 = new JsonObject();
                $$3.addProperty("server", $$5);
                $$3.addProperty("room", $$6);
                $$3.addProperty("user_id", $$2.getId().toString());
                $$3.addProperty("user_display_name", $$2.getName());
                return $$3;
            };
            if ($$11) {
                MessageEncoder $$16 = ($$3, $$4) -> {
                    JsonObject $$5 = new JsonObject();
                    $$5.addProperty("rule", (Number)Integer.valueOf((int)$$42));
                    $$5.addProperty("server", $$5);
                    $$5.addProperty("room", $$6);
                    $$5.addProperty("player", $$3.getId().toString());
                    $$5.addProperty("player_display_name", $$3.getName());
                    $$5.addProperty("text", $$4);
                    $$5.addProperty("language", "*");
                    return $$5;
                };
            } else {
                String $$17 = String.valueOf((int)$$42);
                $$18 = ($$3, $$4) -> {
                    JsonObject $$5 = new JsonObject();
                    $$5.addProperty("rule_id", $$17);
                    $$5.addProperty("category", $$5);
                    $$5.addProperty("subcategory", $$6);
                    $$5.addProperty("user_id", $$3.getId().toString());
                    $$5.addProperty("user_display_name", $$3.getName());
                    $$5.addProperty("text", $$4);
                    $$5.addProperty("language", "*");
                    return $$5;
                };
            }
            IgnoreStrategy $$19 = IgnoreStrategy.select($$7);
            String $$20 = Base64.getEncoder().encodeToString($$32.getBytes(StandardCharsets.US_ASCII));
            return new TextFilterClient($$12, $$18, $$13, $$15, $$14, $$15, $$20, $$19, $$8);
        }
        catch (Exception $$21) {
            LOGGER.warn("Failed to parse chat filter config {}", (Object)$$0, (Object)$$21);
            return null;
        }
    }

    void processJoinOrLeave(GameProfile $$0, URL $$1, JoinOrLeaveEncoder $$2, Executor $$3) {
        $$3.execute(() -> {
            JsonObject $$3 = $$2.encode($$0);
            try {
                this.processRequest($$3, $$1);
            }
            catch (Exception $$4) {
                LOGGER.warn("Failed to send join/leave packet to {} for player {}", new Object[]{$$1, $$0, $$4});
            }
        });
    }

    CompletableFuture<FilteredText> requestMessageProcessing(GameProfile $$0, String $$1, IgnoreStrategy $$2, Executor $$3) {
        if ($$1.isEmpty()) {
            return CompletableFuture.completedFuture((Object)((Object)FilteredText.EMPTY));
        }
        return CompletableFuture.supplyAsync(() -> {
            JsonObject $$3 = this.chatEncoder.encode($$0, $$1);
            try {
                JsonObject $$4 = this.processRequestResponse($$3, this.chatEndpoint);
                boolean $$5 = GsonHelper.getAsBoolean($$4, "response", false);
                if ($$5) {
                    return FilteredText.passThrough($$1);
                }
                String $$6 = GsonHelper.getAsString($$4, "hashed", null);
                if ($$6 == null) {
                    return FilteredText.fullyFiltered($$1);
                }
                JsonArray $$7 = GsonHelper.getAsJsonArray($$4, "hashes");
                FilterMask $$8 = this.parseMask($$1, $$7, $$2);
                return new FilteredText($$1, $$8);
            }
            catch (Exception $$9) {
                LOGGER.warn("Failed to validate message '{}'", (Object)$$1, (Object)$$9);
                return FilteredText.fullyFiltered($$1);
            }
        }, (Executor)$$3);
    }

    private FilterMask parseMask(String $$0, JsonArray $$1, IgnoreStrategy $$2) {
        if ($$1.isEmpty()) {
            return FilterMask.PASS_THROUGH;
        }
        if ($$2.shouldIgnore($$0, $$1.size())) {
            return FilterMask.FULLY_FILTERED;
        }
        FilterMask $$3 = new FilterMask($$0.length());
        for (int $$4 = 0; $$4 < $$1.size(); ++$$4) {
            $$3.setFiltered($$1.get($$4).getAsInt());
        }
        return $$3;
    }

    public void close() {
        this.workerPool.shutdownNow();
    }

    private void drainStream(InputStream $$0) throws IOException {
        byte[] $$1 = new byte[1024];
        while ($$0.read($$1) != -1) {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonObject processRequestResponse(JsonObject $$0, URL $$1) throws IOException {
        HttpURLConnection $$2 = this.makeRequest($$0, $$1);
        try (InputStream $$3 = $$2.getInputStream();){
            JsonObject jsonObject;
            if ($$2.getResponseCode() == 204) {
                JsonObject jsonObject2 = new JsonObject();
                return jsonObject2;
            }
            try {
                jsonObject = Streams.parse((JsonReader)new JsonReader((Reader)new InputStreamReader($$3, StandardCharsets.UTF_8))).getAsJsonObject();
            }
            catch (Throwable throwable) {
                this.drainStream($$3);
                throw throwable;
            }
            this.drainStream($$3);
            return jsonObject;
        }
    }

    private void processRequest(JsonObject $$0, URL $$1) throws IOException {
        HttpURLConnection $$2 = this.makeRequest($$0, $$1);
        try (InputStream $$3 = $$2.getInputStream();){
            this.drainStream($$3);
        }
    }

    private HttpURLConnection makeRequest(JsonObject $$0, URL $$1) throws IOException {
        HttpURLConnection $$2 = (HttpURLConnection)$$1.openConnection();
        $$2.setConnectTimeout(15000);
        $$2.setReadTimeout(2000);
        $$2.setUseCaches(false);
        $$2.setDoOutput(true);
        $$2.setDoInput(true);
        $$2.setRequestMethod("POST");
        $$2.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        $$2.setRequestProperty("Accept", "application/json");
        $$2.setRequestProperty("Authorization", "Basic " + this.authKey);
        $$2.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
        try (OutputStreamWriter $$3 = new OutputStreamWriter($$2.getOutputStream(), StandardCharsets.UTF_8);
             JsonWriter $$4 = new JsonWriter((Writer)$$3);){
            Streams.write((JsonElement)$$0, (JsonWriter)$$4);
        }
        int $$5 = $$2.getResponseCode();
        if ($$5 < 200 || $$5 >= 300) {
            throw new RequestFailedException($$5 + " " + $$2.getResponseMessage());
        }
        return $$2;
    }

    public TextFilter createContext(GameProfile $$0) {
        return new PlayerContext($$0);
    }

    @FunctionalInterface
    public static interface IgnoreStrategy {
        public static final IgnoreStrategy NEVER_IGNORE = ($$0, $$1) -> false;
        public static final IgnoreStrategy IGNORE_FULLY_FILTERED = ($$0, $$1) -> $$0.length() == $$1;

        public static IgnoreStrategy ignoreOverThreshold(int $$0) {
            return ($$1, $$2) -> $$2 >= $$0;
        }

        public static IgnoreStrategy select(int $$0) {
            return switch ($$0) {
                case -1 -> NEVER_IGNORE;
                case 0 -> IGNORE_FULLY_FILTERED;
                default -> IgnoreStrategy.ignoreOverThreshold($$0);
            };
        }

        public boolean shouldIgnore(String var1, int var2);
    }

    @FunctionalInterface
    static interface MessageEncoder {
        public JsonObject encode(GameProfile var1, String var2);
    }

    @FunctionalInterface
    static interface JoinOrLeaveEncoder {
        public JsonObject encode(GameProfile var1);
    }

    public static class RequestFailedException
    extends RuntimeException {
        RequestFailedException(String $$0) {
            super($$0);
        }
    }

    class PlayerContext
    implements TextFilter {
        private final GameProfile profile;
        private final Executor streamExecutor;

        PlayerContext(GameProfile $$0) {
            this.profile = $$0;
            ProcessorMailbox<Runnable> $$1 = ProcessorMailbox.create((Executor)TextFilterClient.this.workerPool, "chat stream for " + $$0.getName());
            this.streamExecutor = $$1::tell;
        }

        @Override
        public void join() {
            TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, TextFilterClient.this.joinEncoder, this.streamExecutor);
        }

        @Override
        public void leave() {
            TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, TextFilterClient.this.leaveEncoder, this.streamExecutor);
        }

        @Override
        public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> $$02) {
            List $$1 = (List)$$02.stream().map($$0 -> TextFilterClient.this.requestMessageProcessing(this.profile, (String)$$0, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor)).collect(ImmutableList.toImmutableList());
            return Util.sequenceFailFast($$1).exceptionally($$0 -> ImmutableList.of());
        }

        @Override
        public CompletableFuture<FilteredText> processStreamMessage(String $$0) {
            return TextFilterClient.this.requestMessageProcessing(this.profile, $$0, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
        }
    }
}