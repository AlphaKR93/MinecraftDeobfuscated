/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.net.HttpURLConnection
 *  java.net.MalformedURLException
 *  java.net.Proxy
 *  java.net.URL
 *  java.nio.charset.StandardCharsets
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.client;

import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public abstract class Request<T extends Request<T>> {
    protected HttpURLConnection connection;
    private boolean connected;
    protected String url;
    private static final int DEFAULT_READ_TIMEOUT = 60000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    public Request(String $$0, int $$1, int $$2) {
        try {
            this.url = $$0;
            Proxy $$3 = RealmsClientConfig.getProxy();
            this.connection = $$3 != null ? (HttpURLConnection)new URL($$0).openConnection($$3) : (HttpURLConnection)new URL($$0).openConnection();
            this.connection.setConnectTimeout($$1);
            this.connection.setReadTimeout($$2);
        }
        catch (MalformedURLException $$4) {
            throw new RealmsHttpException($$4.getMessage(), (Exception)((Object)$$4));
        }
        catch (IOException $$5) {
            throw new RealmsHttpException($$5.getMessage(), (Exception)((Object)$$5));
        }
    }

    public void cookie(String $$0, String $$1) {
        Request.cookie(this.connection, $$0, $$1);
    }

    public static void cookie(HttpURLConnection $$0, String $$1, String $$2) {
        String $$3 = $$0.getRequestProperty("Cookie");
        if ($$3 == null) {
            $$0.setRequestProperty("Cookie", $$1 + "=" + $$2);
        } else {
            $$0.setRequestProperty("Cookie", $$3 + ";" + $$1 + "=" + $$2);
        }
    }

    public T header(String $$0, String $$1) {
        this.connection.addRequestProperty($$0, $$1);
        return (T)this;
    }

    public int getRetryAfterHeader() {
        return Request.getRetryAfterHeader(this.connection);
    }

    public static int getRetryAfterHeader(HttpURLConnection $$0) {
        String $$1 = $$0.getHeaderField("Retry-After");
        try {
            return Integer.valueOf((String)$$1);
        }
        catch (Exception $$2) {
            return 5;
        }
    }

    public int responseCode() {
        try {
            this.connect();
            return this.connection.getResponseCode();
        }
        catch (Exception $$0) {
            throw new RealmsHttpException($$0.getMessage(), $$0);
        }
    }

    public String text() {
        try {
            String $$1;
            this.connect();
            if (this.responseCode() >= 400) {
                String $$0 = this.read(this.connection.getErrorStream());
            } else {
                $$1 = this.read(this.connection.getInputStream());
            }
            this.dispose();
            return $$1;
        }
        catch (IOException $$2) {
            throw new RealmsHttpException($$2.getMessage(), (Exception)((Object)$$2));
        }
    }

    private String read(@Nullable InputStream $$0) throws IOException {
        if ($$0 == null) {
            return "";
        }
        InputStreamReader $$1 = new InputStreamReader($$0, StandardCharsets.UTF_8);
        StringBuilder $$2 = new StringBuilder();
        int $$3 = $$1.read();
        while ($$3 != -1) {
            $$2.append((char)$$3);
            $$3 = $$1.read();
        }
        return $$2.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dispose() {
        byte[] $$0 = new byte[1024];
        try {
            InputStream $$1 = this.connection.getInputStream();
            while ($$1.read($$0) > 0) {
            }
            $$1.close();
        }
        catch (Exception $$2) {
            InputStream $$3;
            block13: {
                $$3 = this.connection.getErrorStream();
                if ($$3 != null) break block13;
                return;
            }
            try {
                while ($$3.read($$0) > 0) {
                }
                $$3.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        finally {
            if (this.connection != null) {
                this.connection.disconnect();
            }
        }
    }

    protected T connect() {
        if (this.connected) {
            return (T)this;
        }
        T $$0 = this.doConnect();
        this.connected = true;
        return $$0;
    }

    protected abstract T doConnect();

    public static Request<?> get(String $$0) {
        return new Get($$0, 5000, 60000);
    }

    public static Request<?> get(String $$0, int $$1, int $$2) {
        return new Get($$0, $$1, $$2);
    }

    public static Request<?> post(String $$0, String $$1) {
        return new Post($$0, $$1, 5000, 60000);
    }

    public static Request<?> post(String $$0, String $$1, int $$2, int $$3) {
        return new Post($$0, $$1, $$2, $$3);
    }

    public static Request<?> delete(String $$0) {
        return new Delete($$0, 5000, 60000);
    }

    public static Request<?> put(String $$0, String $$1) {
        return new Put($$0, $$1, 5000, 60000);
    }

    public static Request<?> put(String $$0, String $$1, int $$2, int $$3) {
        return new Put($$0, $$1, $$2, $$3);
    }

    public String getHeader(String $$0) {
        return Request.getHeader(this.connection, $$0);
    }

    public static String getHeader(HttpURLConnection $$0, String $$1) {
        try {
            return $$0.getHeaderField($$1);
        }
        catch (Exception $$2) {
            return "";
        }
    }

    public static class Get
    extends Request<Get> {
        public Get(String $$0, int $$1, int $$2) {
            super($$0, $$1, $$2);
        }

        @Override
        public Get doConnect() {
            try {
                this.connection.setDoInput(true);
                this.connection.setDoOutput(true);
                this.connection.setUseCaches(false);
                this.connection.setRequestMethod("GET");
                return this;
            }
            catch (Exception $$0) {
                throw new RealmsHttpException($$0.getMessage(), $$0);
            }
        }
    }

    public static class Post
    extends Request<Post> {
        private final String content;

        public Post(String $$0, String $$1, int $$2, int $$3) {
            super($$0, $$2, $$3);
            this.content = $$1;
        }

        @Override
        public Post doConnect() {
            try {
                if (this.content != null) {
                    this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.connection.setDoInput(true);
                this.connection.setDoOutput(true);
                this.connection.setUseCaches(false);
                this.connection.setRequestMethod("POST");
                OutputStream $$0 = this.connection.getOutputStream();
                OutputStreamWriter $$1 = new OutputStreamWriter($$0, "UTF-8");
                $$1.write(this.content);
                $$1.close();
                $$0.flush();
                return this;
            }
            catch (Exception $$2) {
                throw new RealmsHttpException($$2.getMessage(), $$2);
            }
        }
    }

    public static class Delete
    extends Request<Delete> {
        public Delete(String $$0, int $$1, int $$2) {
            super($$0, $$1, $$2);
        }

        @Override
        public Delete doConnect() {
            try {
                this.connection.setDoOutput(true);
                this.connection.setRequestMethod("DELETE");
                this.connection.connect();
                return this;
            }
            catch (Exception $$0) {
                throw new RealmsHttpException($$0.getMessage(), $$0);
            }
        }
    }

    public static class Put
    extends Request<Put> {
        private final String content;

        public Put(String $$0, String $$1, int $$2, int $$3) {
            super($$0, $$2, $$3);
            this.content = $$1;
        }

        @Override
        public Put doConnect() {
            try {
                if (this.content != null) {
                    this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.connection.setDoOutput(true);
                this.connection.setDoInput(true);
                this.connection.setRequestMethod("PUT");
                OutputStream $$0 = this.connection.getOutputStream();
                OutputStreamWriter $$1 = new OutputStreamWriter($$0, "UTF-8");
                $$1.write(this.content);
                $$1.close();
                $$0.flush();
                return this;
            }
            catch (Exception $$2) {
                throw new RealmsHttpException($$2.getMessage(), $$2);
            }
        }
    }
}