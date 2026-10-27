package com.icypunishing.registry;

import com.icypunishing.IcyPunishing;
import com.icypunishing.skill.SkillState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, IcyPunishing.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillState>> SKILL_STATE =
            ATTACHMENTS.register("skill_state",
                    () -> AttachmentType.builder(SkillState::new).serialize(SkillState.CODEC).build());

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}
