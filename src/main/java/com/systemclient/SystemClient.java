package com.systemclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import java.util.HashSet;
import java.util.Set;
import org.lwjgl.glfw.GLFW;

public final class SystemClient implements ClientModInitializer {
    private boolean wasDown;
    private final Set<Integer> held = new HashSet<>();
    @Override public void onInitializeClient() {
        Config.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() == null) return;
            boolean down = GLFW.glfwGetKey(client.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
            if (down && !wasDown && client.screen == null) client.setScreen(new SystemScreen());
            wasDown = down;
            if (client.screen == null) {
                for (var entry : Config.MODULES.entrySet()) {
                    int key = entry.getValue().bind;
                    if (key < 0) continue;
                    boolean pressed = GLFW.glfwGetKey(client.getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
                    if (pressed && !held.contains(key)) {
                        entry.getValue().enabled = !entry.getValue().enabled;
                        Config.save();
                    }
                    if (pressed) held.add(key); else held.remove(key);
                }
            }
        });
    }
}
