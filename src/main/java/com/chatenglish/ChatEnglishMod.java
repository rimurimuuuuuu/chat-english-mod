package com.chatenglish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatEnglishMod implements ClientModInitializer {

    public static final String MOD_ID = "chatenglish";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // ON/OFFの状態（デフォルトはON）
    public static boolean enabled = true;

    // キーバインド（デフォルト: Kキー）
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // キーバインドを登録（Kキーで切り替え）
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.chatenglish.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.chatenglish"
        ));

        // 毎フレームキー入力をチェック
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                // チャットに状態を通知
                if (client.player != null) {
                    String status = enabled ? "§a[Chat English] ON §7- チャットが英語に翻訳されます" 
                                           : "§c[Chat English] OFF §7- 翻訳が無効になりました";
                    client.player.sendMessage(Text.literal(status), false);
                }
                LOGGER.info("[ChatEnglish] 翻訳: {}", enabled ? "ON" : "OFF");
            }
        });

        LOGGER.info("[ChatEnglish] Mod initialized! Kキーで翻訳のON/OFFを切り替えられます。");
    }
}
