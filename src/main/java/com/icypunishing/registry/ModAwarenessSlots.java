package com.icypunishing.registry;

import net.neoforged.bus.api.IEventBus;

/**
 * 意识饰品栏位定义。
 *
 * NeoForge 1.21.1 + Curios 9.5.1 中，饰品栏位通过数据驱动 JSON 文件注册：
 * - 槽位定义：data/icy_punishing/curios/slots/awareness_N.json
 * - 实体映射：data/icy_punishing/curios/entities/awareness_entity.json
 * - 物品标签：data/curios/tags/item/awareness_N.json
 *
 * 不再使用已废弃的 SlotTypeMessage IMC 方式。
 */
public class ModAwarenessSlots {
    public static final String AWARENESS_1 = "awareness_1";
    public static final String AWARENESS_2 = "awareness_2";
    public static final String AWARENESS_3 = "awareness_3";
    public static final String AWARENESS_4 = "awareness_4";
    public static final String AWARENESS_5 = "awareness_5";
    public static final String AWARENESS_6 = "awareness_6";

    public static void register(IEventBus eventBus) {
        // 数据驱动注册，无需代码
    }
}
