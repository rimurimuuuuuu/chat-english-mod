package com.chatenglish.mixin;

import com.chatenglish.ChatEnglishMod;
import com.chatenglish.TranslationService;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    /**
     * sendMessageメソッドにインジェクト
     * プレイヤーがEnterを押してチャットを送信する直前に翻訳します
     */
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        // OFFの場合はそのまま送信
        if (!ChatEnglishMod.enabled) return;

        // スキップすべきメッセージ（コマンドなど）
        if (TranslationService.shouldSkip(chatText)) return;

        // すでに英語なら翻訳不要
        if (TranslationService.isLikelyEnglish(chatText)) return;

        // 元の送信をキャンセルして翻訳後に送信
        ci.cancel();

        MinecraftClient client = MinecraftClient.getInstance();

        TranslationService.translateToEnglish(chatText, translatedText -> {
            client.execute(() -> {
                try {
                    // 翻訳されたテキストを送信
                    client.getNetworkHandler().sendChatMessage(translatedText);

                    // 履歴に追加
                    if (addToHistory) {
                        client.inGameHud.getChatHud().addToMessageHistory(chatText);
                    }

                    ChatEnglishMod.LOGGER.debug("[ChatEnglish] 送信: {} -> {}", chatText, translatedText);
                } catch (Exception e) {
                    ChatEnglishMod.LOGGER.warn("[ChatEnglish] 送信エラー: " + e.getMessage());
                    // 失敗したら元のテキストで送信
                    client.getNetworkHandler().sendChatMessage(chatText);
                }
            });
        });
    }
}
