package com.icypunishing.client;

import com.icypunishing.IcyPunishing;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = IcyPunishing.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SkillKeyMappings {

    public static final KeyMapping COMBAT = new KeyMapping("key.icy_punishing.combat",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z,
            "key.categories.icy_punishing");
    public static final KeyMapping ULT = new KeyMapping("key.icy_punishing.ult",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X,
            "key.categories.icy_punishing");

    private SkillKeyMappings() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(COMBAT);
        event.register(ULT);
    }
}
