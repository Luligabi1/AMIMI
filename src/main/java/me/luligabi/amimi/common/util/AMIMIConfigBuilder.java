package me.luligabi.amimi.common.util;

import com.google.errorprone.annotations.CheckReturnValue;
import me.luligabi.amimi.common.AMIMI;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AMIMIConfigBuilder {
    public static final Map<String, String> configTranslations = new ConcurrentHashMap<>();

    private static String configTranslationKey(String key) {
        return AMIMI.ID + ".configuration." + key;
    }

    private final ModConfigSpec.Builder builder;

    public AMIMIConfigBuilder() {
        this.builder = new ModConfigSpec.Builder();
    }

    public void pushSection(String key, String title) {
        String sectionTranslation = configTranslationKey(key);
        configTranslations.put(sectionTranslation, title);
        builder.push(key);
    }

    public void popSection() {
        builder.pop();
    }

    @CheckReturnValue
    public ModConfigSpec.Builder start(String key, String title, String... comment) {
        if (comment.length == 0) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        var translationKey = configTranslationKey(key);
        configTranslations.put(translationKey, title);
        configTranslations.put(translationKey + ".tooltip", String.join(" ", comment));

        return builder.translation(translationKey)
                .comment(comment);
    }

    public ModConfigSpec build() {
        return builder.build();
    }
}