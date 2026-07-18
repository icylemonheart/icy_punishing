package com.icypunishing.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class ModAwarenessSlots {
    public static final String AWARENESS_1 = "awareness_1";
    public static final String AWARENESS_2 = "awareness_2";
    public static final String AWARENESS_3 = "awareness_3";
    public static final String AWARENESS_4 = "awareness_4";
    public static final String AWARENESS_5 = "awareness_5";
    public static final String AWARENESS_6 = "awareness_6";

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModAwarenessSlots::enqueueIMC);
    }

    private static void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(AWARENESS_1)
                        .size(1)
                        .icon(new ResourceLocation("icy_punishing", "slot/empty_awareness_slot"))
                        .build());

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(AWARENESS_2)
                        .size(1)
                        .icon(new ResourceLocation("icy_punishing", "slot/empty_awareness_slot"))
                        .build());

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(AWARENESS_3)
                        .size(1)
                        .icon(new ResourceLocation("icy_punishing", "slot/empty_awareness_slot"))
                        .build());

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(AWARENESS_4)
                        .size(1)
                        .icon(new ResourceLocation("icy_punishing", "slot/empty_awareness_slot"))
                        .build());

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(AWARENESS_5)
                        .size(1)
                        .icon(new ResourceLocation("icy_punishing", "slot/empty_awareness_slot"))
                        .build());

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(AWARENESS_6)
                        .size(1)
                        .icon(new ResourceLocation("icy_punishing", "slot/empty_awareness_slot"))
                        .build());
    }
}