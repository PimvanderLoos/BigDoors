package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.IChatBaseComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HttpUtilities {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(HttpUtilities.LOGGER)).setNameFormat("Downloader %d").build()));

    private HttpUtilities() {}

    public static CompletableFuture<?> downloadTo(File file, URL url, Map<String, String> map, int i, @Nullable IProgressUpdate iprogressupdate, Proxy proxy) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection httpurlconnection = null;
            InputStream inputstream = null;
            DataOutputStream dataoutputstream = null;

            if (iprogressupdate != null) {
                iprogressupdate.progressStart(IChatBaseComponent.translatable("resourcepack.downloading"));
                iprogressupdate.progressStage(IChatBaseComponent.translatable("resourcepack.requesting"));
            }

            try {
                byte[] abyte = new byte[4096];

                httpurlconnection = (HttpURLConnection) url.openConnection(proxy);
                httpurlconnection.setInstanceFollowRedirects(true);
                float f = 0.0F;
                float f1 = (float) map.entrySet().size();
                Iterator iterator = map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry) iterator.next();

                    httpurlconnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    if (iprogressupdate != null) {
                        iprogressupdate.progressStagePercentage((int) (++f / f1 * 100.0F));
                    }
                }

                inputstream = httpurlconnection.getInputStream();
                f1 = (float) httpurlconnection.getContentLength();
                int j = httpurlconnection.getContentLength();

                if (iprogressupdate != null) {
                    iprogressupdate.progressStage(IChatBaseComponent.translatable("resourcepack.progress", String.format(Locale.ROOT, "%.2f", f1 / 1000.0F / 1000.0F)));
                }

                if (file.exists()) {
                    long k = file.length();

                    if (k == (long) j) {
                        if (iprogressupdate != null) {
                            iprogressupdate.stop();
                        }

                        Object object = null;

                        return object;
                    }

                    HttpUtilities.LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", new Object[]{file, j, k});
                    FileUtils.deleteQuietly(file);
                } else if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }

                dataoutputstream = new DataOutputStream(new FileOutputStream(file));
                if (i > 0 && f1 > (float) i) {
                    if (iprogressupdate != null) {
                        iprogressupdate.stop();
                    }

                    throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + i + ")");
                } else {
                    int l;

                    while ((l = inputstream.read(abyte)) >= 0) {
                        f += (float) l;
                        if (iprogressupdate != null) {
                            iprogressupdate.progressStagePercentage((int) (f / f1 * 100.0F));
                        }

                        if (i > 0 && f > (float) i) {
                            if (iprogressupdate != null) {
                                iprogressupdate.stop();
                            }

                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + i + ")");
                        }

                        if (Thread.interrupted()) {
                            HttpUtilities.LOGGER.error("INTERRUPTED");
                            if (iprogressupdate != null) {
                                iprogressupdate.stop();
                            }

                            Object object1 = null;

                            return object1;
                        }

                        dataoutputstream.write(abyte, 0, l);
                    }

                    if (iprogressupdate != null) {
                        iprogressupdate.stop();
                    }

                    return null;
                }
            } catch (Throwable throwable) {
                HttpUtilities.LOGGER.error("Failed to download file", throwable);
                if (httpurlconnection != null) {
                    InputStream inputstream1 = httpurlconnection.getErrorStream();

                    try {
                        HttpUtilities.LOGGER.error("HTTP response error: {}", IOUtils.toString(inputstream1, StandardCharsets.UTF_8));
                    } catch (IOException ioexception) {
                        HttpUtilities.LOGGER.error("Failed to read response from server");
                    }
                }

                if (iprogressupdate != null) {
                    iprogressupdate.stop();
                }

                return null;
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(dataoutputstream);
            }
        }, HttpUtilities.DOWNLOAD_EXECUTOR);
    }

    public static int getAvailablePort() {
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

    public static boolean isPortAvailable(int i) {
        if (i >= 0 && i <= 65535) {
            try {
                ServerSocket serversocket = new ServerSocket(i);

                boolean flag;

                try {
                    flag = serversocket.getLocalPort() == i;
                } catch (Throwable throwable) {
                    try {
                        serversocket.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                serversocket.close();
                return flag;
            } catch (IOException ioexception) {
                return false;
            }
        } else {
            return false;
        }
    }
}
