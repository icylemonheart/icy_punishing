package com.icypunishing.skill;

import com.icypunishing.item.ObjKatanaItem;
import com.icypunishing.skill.honglian.HonglianSkillSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SkillSetRegistry {
    private static final Map<String, CharacterSkillSet> BY_MODEL = new ConcurrentHashMap<>();

    private SkillSetRegistry() {
    }

    public static void bootstrap() {
        register(new HonglianSkillSet());
    }

    public static void register(CharacterSkillSet set) {
        BY_MODEL.put(set.getModelId(), set);
    }

    public static boolean hasSkillSet(String modelId) {
        return BY_MODEL.containsKey(modelId);
    }

    public static CharacterSkillSet find(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ObjKatanaItem katana) {
            return BY_MODEL.get(katana.getModelId());
        }
        return null;
    }
}
