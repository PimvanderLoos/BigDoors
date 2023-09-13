package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.ChatMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtilities {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(HttpUtilities.LOGGER)).setNameFormat("Downloader %d").build()));

    private HttpUtilities() {}

    public static String a(Map<String, Object> map) {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, Object> entry = (Entry) iterator.next();

            if (stringbuilder.length() > 0) {
                stringbuilder.append('&');
            }

            try {
                stringbuilder.append(URLEncoder.encode((String) entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
                unsupportedencodingexception.printStackTrace();
            }

            if (entry.getValue() != null) {
                stringbuilder.append('=');

                try {
                    stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException unsupportedencodingexception1) {
                    unsupportedencodingexception1.printStackTrace();
                }
            }
        }

        return stringbuilder.toString();
    }

    public static String a(URL url, Map<String, Object> map, boolean flag, @Nullable Proxy proxy) {
        return a(url, a(map), flag, proxy);
    }

    private static String a(URL url, String s, boolean flag, @Nullable Proxy proxy) {
        try {
            if (proxy == null) {
                proxy = Proxy.NO_PROXY;
            }

            HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection(proxy);

            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpurlconnection.setRequestProperty("Content-Length", s.getBytes().length.makeConcatWithConstants < invokedynamic > (s.getBytes().length));
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());

            dataoutputstream.writeBytes(s);
            dataoutputstream.flush();
            dataoutputstream.close();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuilder stringbuilder = new StringBuilder();

            String s1;

            while ((s1 = bufferedreader.readLine()) != null) {
                stringbuilder.append(s1);
                stringbuilder.append('\r');
            }

            bufferedreader.close();
            return stringbuilder.toString();
        } catch (Exception exception) {
            if (!flag) {
                HttpUtilities.LOGGER.error("Could not post to {}", url, exception);
            }

            return "";
        }
    }

    public static CompletableFuture<?> a(File file, String s, Map<String, String> map, int i, @Nullable IProgressUpdate iprogressupdate, Proxy proxy) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection httpurlconnection = null;
            InputStream inputstream = null;
            DataOutputStream dataoutputstream = null;

            if (iprogressupdate != null) {
                iprogressupdate.b(new ChatMessage("resourcepack.downloading"));
                iprogressupdate.c(new ChatMessage("resourcepack.requesting"));
            }

            try {
                byte[] abyte = new byte[4096];
                URL url = new URL(s);

                httpurlconnection = (HttpURLConnection) url.openConnection(proxy);
                httpurlconnection.setInstanceFollowRedirects(true);
                float f = 0.0F;
                float f1 = (float) map.entrySet().size();
                Iterator iterator = map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry) iterator.next();

                    httpurlconnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    if (iprogressupdate != null) {
                        iprogressupdate.a((int) (++f / f1 * 100.0F));
                    }
                }

                inputstream = httpurlconnection.getInputStream();
                f1 = (float) httpurlconnection.getContentLength();
                int j = httpurlconnection.getContentLength();

                if (iprogressupdate != null) {
                    iprogressupdate.c(new ChatMessage("resourcepack.progress", new Object[]{String.format(Locale.ROOT, "%.2f", f1 / 1000.0F / 1000.0F)}));
                }

                if (file.exists()) {
                    long k = file.length();

                    if (k == (long) j) {
                        if (iprogressupdate != null) {
                            iprogressupdate.a();
                        }

                        Object object = null;

                        return object;
                    }

                    HttpUtilities.LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", file, j, k);
                    FileUtils.deleteQuietly(file);
                } else if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }

                dataoutputstream = new DataOutputStream(new FileOutputStream(file));
                if (i > 0 && f1 > (float) i) {
                    if (iprogressupdate != null) {
                        iprogressupdate.a();
                    }

                    throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + i + ")");
                } else {
                    int l;

                    while ((l = inputstream.read(abyte)) >= 0) {
                        f += (float) l;
                        if (iprogressupdate != null) {
                            iprogressupdate.a((int) (f / f1 * 100.0F));
                        }

                        if (i > 0 && f > (float) i) {
                            if (iprogressupdate != null) {
                                iprogressupdate.a();
                            }

                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + i + ")");
                        }

                        if (Thread.interrupted()) {
                            HttpUtilities.LOGGER.error("INTERRUPTED");
                            if (iprogressupdate != null) {
                                iprogressupdate.a();
                            }

                            Object object1 = null;

                            return object1;
                        }

                        dataoutputstream.write(abyte, 0, l);
                    }

                    if (iprogressupdate != null) {
                        iprogressupdate.a();
                    }

                    return null;
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                if (httpurlconnection != null) {
                    InputStream inputstream1 = httpurlconnection.getErrorStream();

                    try {
                        HttpUtilities.LOGGER.error(IOUtils.toString(inputstream1));
                    } catch (IOException ioexception) {
                        ioexception.printStackTrace();
                    }
                }

                if (iprogressupdate != null) {
                    iprogressupdate.a();
                }

                return null;
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(dataoutputstream);
            }
        }, HttpUtilities.DOWNLOAD_EXECUTOR);
    }

    public static int a() {
        try {
            ServerSocket serversocket = new ServerSocket(0);

            int i;

            try {
                i = serversocket.getLocalPort();
            } catch (Throwable throwable) {
                try {
                    serversocket.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            serversocket.close();
            return i;
        } catch (IOException ioexception) {
            return 25564;
        }
    }
}
