package com.chatenglish;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TranslationService {

    private static final String TRANSLATE_URL =
        "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=";

    /**
     * テキストを非同期で英語に翻訳します
     */
    public static void translateToEnglish(String text, Consumer<String> callback) {
        // すでに英語っぽい場合はスキップ
        if (isLikelyEnglish(text)) {
            callback.accept(text);
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
                URL url = new URL(TRANSLATE_URL + encodedText);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return parseTranslation(response.toString());
                }
            } catch (Exception e) {
                ChatEnglishMod.LOGGER.warn("[ChatEnglish] 翻訳エラー: " + e.getMessage());
            }
            return text;
        }).thenAccept(translated -> {
            callback.accept(translated != null && !translated.isEmpty() ? translated : text);
        });
    }

    private static String parseTranslation(String json) {
        try {
            int start = json.indexOf("\"") + 1;
            int end = json.indexOf("\"", start);
            if (start > 0 && end > start) {
                return json.substring(start, end)
                    .replace("\\u003d", "=")
                    .replace("\\u0026", "&")
                    .replace("\\u003c", "<")
                    .replace("\\u003e", ">");
            }
        } catch (Exception e) {
            ChatEnglishMod.LOGGER.warn("[ChatEnglish] パースエラー: " + e.getMessage());
        }
        return null;
    }

    /**
     * テキストが英語っぽいか確認（日本語・中国語・韓国語が含まれていなければ英語とみなす）
     */
    public static boolean isLikelyEnglish(String text) {
        for (char c : text.toCharArray()) {
            if ((c >= '\u3040' && c <= '\u309F') ||
                (c >= '\u30A0' && c <= '\u30FF') ||
                (c >= '\u4E00' && c <= '\u9FFF') ||
                (c >= '\uAC00' && c <= '\uD7AF')) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldSkip(String text) {
        if (text == null || text.trim().isEmpty()) return true;
        if (text.startsWith("/")) return true;
        if (text.length() < 2) return true;
        return false;
    }
}
