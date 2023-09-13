package net.minecraft.network.chat;

import javax.annotation.Nullable;

public class ChatPreviewCache {

    @Nullable
    private ChatPreviewCache.a result;

    public ChatPreviewCache() {}

    public void set(String s, IChatBaseComponent ichatbasecomponent) {
        this.result = new ChatPreviewCache.a(s, ichatbasecomponent);
    }

    @Nullable
    public IChatBaseComponent pull(String s) {
        ChatPreviewCache.a chatpreviewcache_a = this.result;

        if (chatpreviewcache_a != null && chatpreviewcache_a.matches(s)) {
            this.result = null;
            return chatpreviewcache_a.preview();
        } else {
            return null;
        }
    }

    private static record a(String query, IChatBaseComponent preview) {

        public boolean matches(String s) {
            return this.query.equals(s);
        }
    }
}
