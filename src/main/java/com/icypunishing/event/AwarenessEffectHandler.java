package com.icypunishing.event;

import com.icypunishing.core.AwarenessItem;
import com.icypunishing.core.AwarenessSet;
import com.icypunishing.registry.ModEffects;
import com.icylib.damage.DamageTypeHelper;
import com.icylib.damage.IcyDamageTypes;
import com.icylib.init.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AwarenessEffectHandler {

    public static final Map<Player, Integer> curieCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> cunninghamCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Boolean> cunninghamShield = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> aliceBuffTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> edisonCritTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> archimedesTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Boolean> archimedesEmpowered = new ConcurrentHashMap<>();
    
    public static final Map<Player, Integer> aifuCritStacks = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> aifuStackTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> aifuUltimateCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> aikeElementalCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> voltaireCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> gorodaCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> voltaireDefenseReduction = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> voltaireDefenseTimer = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> gorodaElementalResistanceTimer = new ConcurrentHashMap<>();
    
    public static final Map<Player, Integer> richelieuHealCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> richelieuCritCount = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> richelieuCritCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> richelieuDamageBuffTimer = new ConcurrentHashMap<>();
    
    public static final Map<Player, Integer> samandaHealCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> samandaAuraCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, net.minecraft.world.phys.AABB> samandaAuraArea = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> samandaAuraTimer = new ConcurrentHashMap<>();
    
    public static final Map<Player, Boolean> mozartHadHyperSpace = new ConcurrentHashMap<>();
    
    public static final Map<Player, Integer> boudicaStacks = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> boudicaStackTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Boolean> boudicaStacksReset = new ConcurrentHashMap<>();

    public static final Map<Player, Integer> einsteinnaMinionCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> einsteinnaElementResistTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> einsteinnaAuraCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> einsteinnaAuraTimer = new ConcurrentHashMap<>();

    public static final Map<Player, Integer> kantlinaRageCooldown = new ConcurrentHashMap<>();

    public static final Map<UUID, Integer> shakespeareScorchingTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> shakespeareScorchingCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> shakespeareExplosionCooldown = new ConcurrentHashMap<>();

    public static final Map<UUID, Integer> heisenbergElectrocutionTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> heisenbergElectrocutionCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> heisenbergStormCooldown = new ConcurrentHashMap<>();

    public static final Set<UUID> shakespeareExplosionProcessing = ConcurrentHashMap.newKeySet();
    public static final Set<UUID> heisenbergStormProcessing = ConcurrentHashMap.newKeySet();

    public static final Map<Player, Integer> darwinCritStacks = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> darwinStackTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Set<String>> darwinDamageTypes = new ConcurrentHashMap<>();
    public static final Set<UUID> darwinShockwaveProcessing = ConcurrentHashMap.newKeySet();

    public static final Map<Player, Integer> hannaUltimateCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Boolean> hannaNextCritHyperSpace = new ConcurrentHashMap<>();

    public static final Map<Player, Integer> daVinciMinionCount = new ConcurrentHashMap<>();

    public static final Map<Player, Integer> catherineResistCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> catherineShieldCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> catherineShieldTimer = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> catherinePhysicalResistTimer = new ConcurrentHashMap<>();
    public static final Map<UUID, Double> catherineOriginalPhysicalResist = new ConcurrentHashMap<>();

    public static final Map<Player, Integer> ferietHealCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> ferietBuffTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> ferietChargeCount = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> ferietAuraCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> ferietAuraTimer = new ConcurrentHashMap<>();

    public static final Map<Player, Integer> guineyHealCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> guineyElementalTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> guineyChargeCount = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> guineyAuraCooldown = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> guineyAuraTimer = new ConcurrentHashMap<>();

    public static final Map<UUID, Integer> pattonPiercingTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> pattonPiercingCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> pattonExplosionCooldown = new ConcurrentHashMap<>();
    public static final Set<UUID> pattonExplosionProcessing = ConcurrentHashMap.newKeySet();

    public static final Map<UUID, Integer> basiloneCorrosionTimer = new ConcurrentHashMap<>();
    public static final Map<Player, Integer> basiloneCorrosionCooldown = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> basiloneExplosionCooldown = new ConcurrentHashMap<>();
    public static final Set<UUID> basiloneExplosionProcessing = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        if (event.getEntity() instanceof Player && !event.getEntity().level().isClientSide) {
            Player player = (Player) event.getEntity();
            boolean isVanillaCrit = event.isVanillaCritical();
            
            Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
            for (AwarenessSet equippedSet : equippedSets) {
                int count = equippedSet.countEquipped(player);
                
                if (isVanillaCrit) {
                    if (equippedSet.getId().getPath().equals("edison")) {
                        if (count >= 2) {
                            var addAttr = player.getAttribute(ModAttributes.CRITICAL_STRIKE_MULTIPLIER_ADD);
                            if (addAttr != null) {
                                addAttr.setBaseValue(addAttr.getBaseValue() + 0.05);
                            }
                        }
                        if (count >= 4) {
                            edisonCritTimer.put(player, 5 * 20);
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("nimitiz")) {
                        if (count >= 4) {
                            var multAttr = player.getAttribute(ModAttributes.CRITICAL_STRIKE_MULTIPLIER_MULTIPLY);
                            if (multAttr != null) {
                                multAttr.setBaseValue(multAttr.getBaseValue() * 1.05);
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("richelieu")) {
                        if (count >= 4) {
                            int critCooldown = richelieuCritCooldown.getOrDefault(player, 0);
                            if (critCooldown <= 0) {
                                int critCount = richelieuCritCount.getOrDefault(player, 0);
                                critCount++;
                                if (critCount >= 8) {
                                    critCount = 0;
                                    richelieuCritCooldown.put(player, 5 * 20);
                                }
                                richelieuCritCount.put(player, critCount);
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("darwin")) {
                        if (count >= 2) {
                            int stacks = darwinCritStacks.getOrDefault(player, 0);
                            stacks = Math.min(stacks + 1, 5);
                            darwinCritStacks.put(player, stacks);
                            darwinStackTimer.put(player, 4 * 20);
                        }
                        if (count >= 4) {
                            Set<String> types = darwinDamageTypes.computeIfAbsent(player, k -> new HashSet<>());
                            types.add("vanilla_crit");
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("catherine")) {
                        if (count >= 2) {
                            Entity targetEntity = event.getTarget();
                            if (targetEntity instanceof LivingEntity) {
                                LivingEntity target = (LivingEntity) targetEntity;
                                int cooldown = catherineResistCooldown.getOrDefault(player, 0);
                                if (cooldown <= 0) {
                                    var resistanceAttr = target.getAttribute(com.icylib.init.ModAttributes.PHYSICAL_RESISTANCE);
                                    if (resistanceAttr != null) {
                                        double originalResist = resistanceAttr.getBaseValue();
                                        catherineOriginalPhysicalResist.put(target.getUUID(), originalResist);
                                        resistanceAttr.setBaseValue(Math.max(0.1, originalResist - 0.2));
                                        catherinePhysicalResistTimer.put(target.getUUID(), 8 * 20);
                                        catherineResistCooldown.put(player, 10 * 20);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (equippedSet.getId().getPath().equals("darwin")) {
                        if (count >= 2) {
                            int stacks = darwinCritStacks.getOrDefault(player, 0);
                            stacks = Math.min(stacks + 1, 5);
                            darwinCritStacks.put(player, stacks);
                            darwinStackTimer.put(player, 4 * 20);
                        }
                        if (count >= 4) {
                            Set<String> types = darwinDamageTypes.computeIfAbsent(player, k -> new HashSet<>());
                            types.add("icy_crit");
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("aifu")) {
                        if (count >= 2) {
                            int stacks = aifuCritStacks.getOrDefault(player, 0);
                            stacks = Math.min(stacks + 1, 5);
                            aifuCritStacks.put(player, stacks);
                            aifuStackTimer.put(player, 3 * 20);
                        }
                        if (count >= 4) {
                            int cooldown = aifuUltimateCooldown.getOrDefault(player, 0);
                            if (cooldown <= 0) {
                                int stacks = aifuCritStacks.getOrDefault(player, 0);
                                if (stacks >= 5) {
                                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                            com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE, 3 * 20));
                                    aifuCritStacks.put(player, 0);
                                    aifuUltimateCooldown.put(player, 60 * 20);
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("hanna")) {
                        if (count >= 4) {
                            if (hannaNextCritHyperSpace.getOrDefault(player, false)) {
                                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                        com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE, 3 * 20));
                                hannaNextCritHyperSpace.put(player, false);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getContainer().getSource().getEntity() instanceof Player player && !player.level().isClientSide) {
            LivingEntity target = event.getEntity();
            
            Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
            
            for (AwarenessSet equippedSet : equippedSets) {
                int count = equippedSet.countEquipped(player);
                
                if (equippedSet.getId().getPath().equals("curie_cohet")) {
                    if (count >= 2) {
                        tryCallArtillery(player, target);
                    }
                }
                
                if (equippedSet.getId().getPath().equals("cunningham")) {
                    if (count >= 4) {
                        handleCunninghamShield(player);
                    }
                }
                
                if (equippedSet.getId().getPath().equals("archimedes")) {
                    if (count >= 4) {
                        handleArchimedes(player);
                    }
                }
                
                if (equippedSet.getId().getPath().equals("voltaire")) {
                    if (count >= 2) {
                        int cooldown = voltaireCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            var defenseAttr = target.getAttribute(ModAttributes.DEFENSE);
                            if (defenseAttr != null) {
                                defenseAttr.setBaseValue(defenseAttr.getBaseValue() * 0.9);
                            }
                            voltaireDefenseTimer.put(target.getUUID(), 5 * 20);
                            voltaireCooldown.put(player, 12 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("goroda")) {
                    if (count >= 2) {
                        int cooldown = gorodaCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            var elementalResistAttr = target.getAttribute(ModAttributes.ELEMENTAL_RESISTANCE);
                            if (elementalResistAttr != null) {
                                elementalResistAttr.setBaseValue(Math.max(0.1, elementalResistAttr.getBaseValue() - 0.06));
                            }
                            gorodaElementalResistanceTimer.put(target.getUUID(), 5 * 20);
                            gorodaCooldown.put(player, 12 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("einsteinna")) {
                    if (count >= 2) {
                        int cooldown = einsteinnaMinionCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            DamageSource source = event.getContainer().getSource();
                            if (DamageTypeHelper.isFireDamage(source)) {
                                var fireResist = target.getAttribute(ModAttributes.FIRE_RESISTANCE);
                                if (fireResist != null) {
                                    fireResist.setBaseValue(Math.max(0.1, fireResist.getBaseValue() - 0.15));
                                }
                            } else if (DamageTypeHelper.isLightDamage(source)) {
                                var lightResist = target.getAttribute(ModAttributes.LIGHT_RESISTANCE);
                                if (lightResist != null) {
                                    lightResist.setBaseValue(Math.max(0.1, lightResist.getBaseValue() - 0.15));
                                }
                            } else if (DamageTypeHelper.isIceDamage(source)) {
                                var iceResist = target.getAttribute(ModAttributes.ICE_RESISTANCE);
                                if (iceResist != null) {
                                    iceResist.setBaseValue(Math.max(0.1, iceResist.getBaseValue() - 0.15));
                                }
                            } else if (DamageTypeHelper.isShadowDamage(source)) {
                                var shadowResist = target.getAttribute(ModAttributes.SHADOW_RESISTANCE);
                                if (shadowResist != null) {
                                    shadowResist.setBaseValue(Math.max(0.1, shadowResist.getBaseValue() - 0.15));
                                }
                            }
                            einsteinnaElementResistTimer.put(target.getUUID(), 8 * 20);
                            einsteinnaMinionCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("kantlina")) {
                    if (count >= 4) {
                        int cooldown = kantlinaRageCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0 && player.getRandom().nextFloat() < 0.5) {
                            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    com.icylib.init.ModEffects.RAGE, 5 * 20));
                            kantlinaRageCooldown.put(player, 8 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("shakespeare")) {
                    if (count >= 4) {
                        int cooldown = shakespeareScorchingCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    com.icylib.init.ModEffects.SCORCHING, 8 * 20));
                            shakespeareScorchingTimer.put(target.getUUID(), 8 * 20);
                            shakespeareScorchingCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("heisenberg")) {
                    if (count >= 4) {
                        int cooldown = heisenbergElectrocutionCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    com.icylib.init.ModEffects.ELECTROCUTION, 8 * 20));
                            heisenbergElectrocutionTimer.put(target.getUUID(), 8 * 20);
                            heisenbergElectrocutionCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("darwin")) {
                    if (count >= 4) {
                        Set<String> types = darwinDamageTypes.computeIfAbsent(player, k -> new HashSet<>());
                    }
                }
                
                if (equippedSet.getId().getPath().equals("catherine")) {
                    if (count >= 2) {
                        int cooldown = catherineResistCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            var physicalResistAttr = target.getAttribute(ModAttributes.PHYSICAL_RESISTANCE);
                            if (physicalResistAttr != null) {
                                catherineOriginalPhysicalResist.put(target.getUUID(), physicalResistAttr.getBaseValue());
                                physicalResistAttr.setBaseValue(Math.max(0.1, physicalResistAttr.getBaseValue() - 0.2));
                            }
                            catherinePhysicalResistTimer.put(target.getUUID(), 8 * 20);
                            catherineResistCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("patton")) {
                    if (count >= 4) {
                        int cooldown = pattonPiercingCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    com.icylib.init.ModEffects.PIERCING, 8 * 20));
                            pattonPiercingTimer.put(target.getUUID(), 8 * 20);
                            pattonPiercingCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("basilone")) {
                    if (count >= 4) {
                        int cooldown = basiloneCorrosionCooldown.getOrDefault(player, 0);
                        if (cooldown <= 0) {
                            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    com.icylib.init.ModEffects.CORROSION, 8 * 20));
                            basiloneCorrosionTimer.put(target.getUUID(), 8 * 20);
                            basiloneCorrosionCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("da_vinci")) {
                    if (count >= 4) {
                        net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                                player.getX() - 5.5, player.getY() - 5.5, player.getZ() - 5.5,
                                player.getX() + 5.5, player.getY() + 5.5, player.getZ() + 5.5);
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                            if (entity != player && entity != target && entity.isAlliedTo(player) && entity instanceof net.minecraft.world.entity.Mob mob) {
                                mob.setTarget(target);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!event.getEntity().level().isClientSide) {
            LivingEntity target = event.getEntity();
            
            
            
            if (event.getContainer().getSource().getEntity() instanceof Player player) {
                handleMinionDamage(event, player);
            } else if (event.getContainer().getSource().getEntity() instanceof LivingEntity attacker) {
                Player owner = getOwnerOfEntity(attacker);
                if (owner != null) {
                    handleMinionDamage(event, owner);
                }
            }
            
            if (event.getContainer().getSource().getEntity() instanceof Player) {
                Player player = (Player) event.getContainer().getSource().getEntity();
                float damage = event.getNewDamage();
                
                Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
                
                for (AwarenessSet equippedSet : equippedSets) {
                    int count = equippedSet.countEquipped(player);
                    
                    if (equippedSet.getId().getPath().equals("edison")) {
                        if (count >= 4) {
                            if (edisonCritTimer.getOrDefault(player, 0) > 0) {
                                damage *= 1.05f;
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("cunningham")) {
                        if (count >= 2) {
                            if (target.hasEffect(com.icypunishing.registry.ModEffects.FORTITUDE)) {
                                DamageSource source = event.getContainer().getSource();
                                if (DamageTypeHelper.isPhysicalDamage(source) || DamageTypeHelper.isElementalDamage(source)) {
                                    damage *= 1.05f;
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("alice")) {
                        if (count >= 4) {
                            if (aliceBuffTimer.getOrDefault(player, 0) > 0) {
                                damage *= 1.04f;
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("archimedes")) {
                        if (count >= 2) {
                            damage *= 1.05f;
                        }
                        if (count >= 4) {
                            if (archimedesEmpowered.getOrDefault(player, false)) {
                                damage *= 1.1f;
                                archimedesEmpowered.put(player, false);
                                archimedesTimer.put(player, 0);
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("aifu")) {
                        if (count >= 2) {
                            int stacks = aifuCritStacks.getOrDefault(player, 0);
                            damage *= 1.0f + stacks * 0.02f;
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("aike")) {
                        if (count >= 2) {
                            damage *= 1.02f;
                            DamageSource source = event.getContainer().getSource();
                            if (DamageTypeHelper.isElementalDamage(source)) {
                                damage *= 1.04f;
                            }
                        }
                        if (count >= 4) {
                            DamageSource source = event.getContainer().getSource();
                            if (DamageTypeHelper.isElementalDamage(source)) {
                                int cooldown = aikeElementalCooldown.getOrDefault(target.getUUID(), 0);
                                if (cooldown <= 0 && player.getRandom().nextFloat() < 0.15f) {
                                    float elementalDamage = damage * 0.4f;
                                    target.hurt(source, elementalDamage);
                                    aikeElementalCooldown.put(target.getUUID(), 8 * 20);
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("richelieu")) {
                        if (count >= 2) {
                            if (richelieuDamageBuffTimer.getOrDefault(player, 0) > 0 && DamageTypeHelper.isPhysicalDamage(event.getContainer().getSource())) {
                                damage *= 1.08f;
                            }
                        }
                        if (count >= 4) {
                            int critCount = richelieuCritCount.getOrDefault(player, 0);
                            if (critCount == 0 && richelieuCritCooldown.getOrDefault(player, 0) > 0) {
                                float extraDamage = damage;
                                target.hurt(player.damageSources().playerAttack(player), extraDamage);
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("mozart")) {
                        if (count >= 2) {
                            if (player.hasEffect(com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE)) {
                                damage *= 1.15f;
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("boudica")) {
                        if (count >= 6) {
                            int stacks = boudicaStacks.getOrDefault(player, 0);
                            damage *= 1.0f + stacks * 0.03f;
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("einsteinna")) {
                        if (count >= 2) {
                            damage *= 1.05f;
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("guiney")) {
                        if (count >= 2 && guineyElementalTimer.containsKey(player)) {
                            DamageSource source = event.getContainer().getSource();
                            if (DamageTypeHelper.isFireDamage(source) || DamageTypeHelper.isLightDamage(source) || DamageTypeHelper.isIceDamage(source) || DamageTypeHelper.isShadowDamage(source)) {
                                damage *= 1.1f;
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("kantlina")) {
                        if (count >= 2) {
                            damage *= 1.05f;
                        }
                        if (count >= 4) {
                            if (player.hasEffect(com.icylib.init.ModEffects.RAGE)) {
                                damage *= 1.2f;
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("shakespeare")) {
                        if (count >= 2) {
                            damage *= 1.03f;
                            if (DamageTypeHelper.isFireDamage(event.getContainer().getSource())) {
                                damage *= 1.07f;
                            }
                        }
                        if (count >= 4) {
                            damage *= 1.1f;
                            if (target.hasEffect(com.icylib.init.ModEffects.SCORCHING)) {
                                int explosionCooldown = shakespeareExplosionCooldown.getOrDefault(target.getUUID(), 0);
                                if (explosionCooldown <= 0 && !shakespeareExplosionProcessing.contains(target.getUUID())) {
                                    shakespeareExplosionProcessing.add(target.getUUID());
                                    float explosionDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 6.0f;
                                    // 莎士比亚4件套爆炸：icy_fire火焰伤害，且携带玩家为来源（火系增益/远航星聚爆引爆等攻击链路才能识别）
                                    DamageSource fireSource = DamageTypeHelper.createDamageSource(player, IcyDamageTypes.FIRE);
                                    target.hurt(fireSource != null ? fireSource : player.damageSources().onFire(), explosionDamage);
                                    shakespeareExplosionProcessing.remove(target.getUUID());
                                    shakespeareExplosionCooldown.put(target.getUUID(), 6 * 20);
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("heisenberg")) {
                        if (count >= 2) {
                            damage *= 1.03f;
                            if (DamageTypeHelper.isLightDamage(event.getContainer().getSource())) {
                                damage *= 1.07f;
                            }
                        }
                        if (count >= 4) {
                            damage *= 1.1f;
                            if (target.hasEffect(com.icylib.init.ModEffects.ELECTROCUTION)) {
                                int stormCooldown = heisenbergStormCooldown.getOrDefault(target.getUUID(), 0);
                                if (stormCooldown <= 0 && !heisenbergStormProcessing.contains(target.getUUID())) {
                                    heisenbergStormProcessing.add(target.getUUID());
                                    float stormDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 6.0f;
                                    target.hurt(player.damageSources().lightningBolt(), stormDamage);
                                    heisenbergStormProcessing.remove(target.getUUID());
                                    heisenbergStormCooldown.put(target.getUUID(), 6 * 20);
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("darwin")) {
                        if (!darwinShockwaveProcessing.contains(target.getUUID())) {
                            if (count >= 2) {
                                int stacks = darwinCritStacks.getOrDefault(player, 0);
                                damage *= Math.pow(1.03, stacks);
                            }
                            if (count >= 4) {
                                Set<String> types = darwinDamageTypes.getOrDefault(player, new HashSet<>());
                                types.add("normal_damage");
                                if (types.contains("icy_crit") && types.contains("vanilla_crit") && types.contains("normal_damage")) {
                                    float shockwaveDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 6.0f;
                                    net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                                            player.getX() - 6, player.getY() - 6, player.getZ() - 6,
                                            player.getX() + 6, player.getY() + 6, player.getZ() + 6);
                                    for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                                        if (!(entity instanceof Player)) {
                                            darwinShockwaveProcessing.add(entity.getUUID());
                                            entity.hurt(player.damageSources().playerAttack(player), shockwaveDamage);
                                            darwinShockwaveProcessing.remove(entity.getUUID());
                                        }
                                    }
                                    types.clear();
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("patton")) {
                        if (count >= 2) {
                            damage *= 1.03f;
                            if (DamageTypeHelper.isPhysicalDamage(event.getContainer().getSource())) {
                                damage *= 1.07f;
                            }
                        }
                        if (count >= 4) {
                            damage *= 1.1f;
                            if (target.hasEffect(com.icylib.init.ModEffects.PIERCING)) {
                                int explosionCooldown = pattonExplosionCooldown.getOrDefault(target.getUUID(), 0);
                                if (explosionCooldown <= 0 && !pattonExplosionProcessing.contains(target.getUUID())) {
                                    pattonExplosionProcessing.add(target.getUUID());
                                    float explosionDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 6.0f;
                                    target.hurt(player.damageSources().playerAttack(player), explosionDamage);
                                    pattonExplosionProcessing.remove(target.getUUID());
                                    pattonExplosionCooldown.put(target.getUUID(), 6 * 20);
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("basilone")) {
                        if (count >= 2) {
                            damage *= 1.03f;
                            if (DamageTypeHelper.isShadowDamage(event.getContainer().getSource())) {
                                damage *= 1.07f;
                            }
                        }
                        if (count >= 4) {
                            damage *= 1.1f;
                            if (target.hasEffect(com.icylib.init.ModEffects.CORROSION)) {
                                int explosionCooldown = basiloneExplosionCooldown.getOrDefault(target.getUUID(), 0);
                                if (explosionCooldown <= 0 && !basiloneExplosionProcessing.contains(target.getUUID())) {
                                    basiloneExplosionProcessing.add(target.getUUID());
                                    float explosionDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 6.0f;
                                    target.hurt(player.damageSources().wither(), explosionDamage);
                                    basiloneExplosionProcessing.remove(target.getUUID());
                                    basiloneExplosionCooldown.put(target.getUUID(), 6 * 20);
                                }
                            }
                        }
                    }
                }
                
                event.setNewDamage(damage);
            }

            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (cunninghamShield.getOrDefault(player, false)) {
                    float reducedDamage = event.getNewDamage() * 0.7f;
                    event.setNewDamage(reducedDamage);
                    cunninghamShield.put(player, false);
                }
                
                Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
                for (AwarenessSet equippedSet : equippedSets) {
                    int count = equippedSet.countEquipped(player);
                    
                    if (equippedSet.getId().getPath().equals("catherine")) {
                        if (count >= 4) {
                            int cooldown = catherineShieldCooldown.getOrDefault(player, 0);
                            if (cooldown <= 0) {
                                com.icylib.shield.ShieldSystem.setShield(player, 1000.0f);
                                catherineShieldTimer.put(player, 5 * 20);
                                catherineShieldCooldown.put(player, 8 * 20);
                                
                                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                    ModEffects.CATHERINE_SHIELD, 5 * 20, 0, false, true, true));
                            }
                        }
                    }
                }
                for (AwarenessSet equippedSet : equippedSets) {
                    int count = equippedSet.countEquipped(player);
                    
                    if (equippedSet.getId().getPath().equals("voltaire")) {
                        if (count >= 4) {
                            net.minecraft.world.entity.Entity attackerEntity = event.getContainer().getSource().getDirectEntity();
                            if (attackerEntity instanceof LivingEntity) {
                                LivingEntity attacker = (LivingEntity) attackerEntity;
                                UUID attackerUUID = attacker.getUUID();
                                int stacks = voltaireDefenseReduction.getOrDefault(attackerUUID, 0);
                                stacks = Math.min(stacks + 1, 2);
                                voltaireDefenseReduction.put(attackerUUID, stacks);
                                voltaireDefenseTimer.put(attackerUUID, 7 * 20);
                                
                                var resistanceAttr = attacker.getAttribute(ModAttributes.UNIVERSAL_RESISTANCE);
                                if (resistanceAttr != null) {
                                    resistanceAttr.setBaseValue(Math.max(0.1, resistanceAttr.getBaseValue() - 0.15 * stacks));
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("goroda")) {
                        if (count >= 4) {
                            net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                                    player.getX() - 5, player.getY() - 5, player.getZ() - 5,
                                    player.getX() + 5, player.getY() + 5, player.getZ() + 5);
                            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                                if (!(entity instanceof Player)) {
                                    var elementalResistAttr = entity.getAttribute(ModAttributes.ELEMENTAL_RESISTANCE);
                                    if (elementalResistAttr != null) {
                                        elementalResistAttr.setBaseValue(Math.max(0.1, elementalResistAttr.getBaseValue() - 0.06));
                                    }
                                    gorodaElementalResistanceTimer.put(entity.getUUID(), 7 * 20);
                                }
                            }
                        }
                    }
                    
                    if (equippedSet.getId().getPath().equals("einsteinna")) {
                        if (count >= 4) {
                            int cooldown = einsteinnaAuraCooldown.getOrDefault(player, 0);
                            if (cooldown <= 0) {
                                var fireResist = player.getAttribute(ModAttributes.FIRE_RESISTANCE);
                                var lightResist = player.getAttribute(ModAttributes.LIGHT_RESISTANCE);
                                var iceResist = player.getAttribute(ModAttributes.ICE_RESISTANCE);
                                var shadowResist = player.getAttribute(ModAttributes.SHADOW_RESISTANCE);
                                
                                if (fireResist != null) {
                                    fireResist.setBaseValue(fireResist.getBaseValue() + 0.2);
                                }
                                if (lightResist != null) {
                                    lightResist.setBaseValue(lightResist.getBaseValue() + 0.2);
                                }
                                if (iceResist != null) {
                                    iceResist.setBaseValue(iceResist.getBaseValue() + 0.2);
                                }
                                if (shadowResist != null) {
                                    shadowResist.setBaseValue(shadowResist.getBaseValue() + 0.2);
                                }
                                
                                einsteinnaAuraTimer.put(player, 5 * 20);
                                einsteinnaAuraCooldown.put(player, 8 * 20);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
            
            for (AwarenessSet equippedSet : equippedSets) {
                int count = equippedSet.countEquipped(player);
                
                if (equippedSet.getId().getPath().equals("alice")) {
                    if (count >= 2) {
                        event.setAmount(event.getAmount() * 1.15f);
                    }
                    if (count >= 4) {
                        aliceBuffTimer.put(player, 4 * 20);
                    }
                }
                
                if (equippedSet.getId().getPath().equals("richelieu")) {
                    if (count >= 2) {
                        event.setAmount(event.getAmount() * 1.05f);
                        int healCooldown = richelieuHealCooldown.getOrDefault(player, 0);
                        if (healCooldown <= 0) {
                            richelieuDamageBuffTimer.put(player, 4 * 20);
                            richelieuHealCooldown.put(player, 5 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("samanda")) {
                    if (count >= 2) {
                        event.setAmount(event.getAmount() * 1.05f);
                    }
                    if (count >= 4) {
                        int auraCooldown = samandaAuraCooldown.getOrDefault(player, 0);
                        if (auraCooldown <= 0) {
                            samandaAuraTimer.put(player, 5 * 20);
                            samandaAuraCooldown.put(player, 10 * 20);
                        }
                    }
                }
                
                if (equippedSet.getId().getPath().equals("feriet_ii")) {
                    if (count >= 2) {
                        event.setAmount(event.getAmount() * 1.1f);
                        int healCooldown = ferietHealCooldown.getOrDefault(player, 0);
                        if (healCooldown <= 0) {
                            ferietBuffTimer.put(player, 5 * 20);
                            ferietHealCooldown.put(player, 8 * 20);
                            
                            net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                                    player.getX() - 10, player.getY() - 10, player.getZ() - 10,
                                    player.getX() + 10, player.getY() + 10, player.getZ() + 10);
                            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                                if (entity instanceof Player || entity.isAlliedTo(player)) {
                                    ferietBuffTimer.put(entity instanceof Player ? (Player) entity : null, 5 * 20);
                                }
                            }
                        }
                    }
                    if (count >= 4 && ferietAuraCooldown.getOrDefault(player, 0) <= 0) {
                        int charges = ferietChargeCount.getOrDefault(player, 0);
                        charges++;
                        if (charges >= 5) {
                            charges = 0;
                            ferietAuraTimer.put(player, 5 * 20);
                            ferietAuraCooldown.put(player, 12 * 20);
                            
                            net.minecraft.world.entity.animal.allay.Allay allay = new net.minecraft.world.entity.animal.allay.Allay(
                                    net.minecraft.world.entity.EntityType.ALLAY, player.level());
                            allay.setPos(player.position().add(0, 1, 0));
                            player.level().addFreshEntity(allay);
                        }
                        ferietChargeCount.put(player, charges);
                    }
                }
                
                if (equippedSet.getId().getPath().equals("guiney")) {
                    if (count >= 2) {
                        event.setAmount(event.getAmount() * 1.1f);
                        int healCooldown = guineyHealCooldown.getOrDefault(player, 0);
                        if (healCooldown <= 0) {
                            guineyElementalTimer.put(player, 5 * 20);
                            guineyHealCooldown.put(player, 8 * 20);
                        }
                    }
                    if (count >= 4 && guineyAuraCooldown.getOrDefault(player, 0) <= 0) {
                        int charges = guineyChargeCount.getOrDefault(player, 0);
                        charges++;
                        if (charges >= 5) {
                            charges = 0;
                            guineyAuraTimer.put(player, 5 * 20);
                            guineyAuraCooldown.put(player, 12 * 20);
                            
                            net.minecraft.world.entity.animal.allay.Allay allay = new net.minecraft.world.entity.animal.allay.Allay(
                                    net.minecraft.world.entity.EntityType.ALLAY, player.level());
                            allay.setPos(player.position().add(0, 1, 0));
                            player.level().addFreshEntity(allay);
                        }
                        guineyChargeCount.put(player, charges);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack stack = player.getMainHandItem();
            
            if (stack.is(net.minecraft.world.item.Items.SPLASH_POTION) || 
                stack.is(net.minecraft.world.item.Items.LINGERING_POTION) ||
                stack.is(net.minecraft.world.item.Items.POTION)) {
                
                net.minecraft.world.item.alchemy.PotionContents contents = stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, net.minecraft.world.item.alchemy.PotionContents.EMPTY);
                for (net.minecraft.world.effect.MobEffectInstance effect : contents.getAllEffects()) {
                    if (effect.getEffect().value().isBeneficial()) {
                        handleHealingCharge(player);
                        break;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack stack = event.getItemStack();
            
            if (stack.is(net.minecraft.world.item.Items.POTION)) {
                net.minecraft.world.item.alchemy.PotionContents contents = stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, net.minecraft.world.item.alchemy.PotionContents.EMPTY);
                for (net.minecraft.world.effect.MobEffectInstance effect : contents.getAllEffects()) {
                    if (effect.getEffect().value().isBeneficial()) {
                        handleHealingCharge(player);
                        break;
                    }
                }
            }
        }
    }
    
    private static void handleHealingCharge(Player player) {
        Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
        
        for (AwarenessSet equippedSet : equippedSets) {
            int count = equippedSet.countEquipped(player);
            
            if (equippedSet.getId().getPath().equals("feriet_ii")) {
                if (count >= 4 && ferietAuraCooldown.getOrDefault(player, 0) <= 0) {
                    int charges = ferietChargeCount.getOrDefault(player, 0);
                    charges++;
                    if (charges >= 5) {
                        charges = 0;
                        ferietAuraTimer.put(player, 5 * 20);
                        ferietAuraCooldown.put(player, 12 * 20);
                        
                        net.minecraft.world.entity.animal.allay.Allay allay = new net.minecraft.world.entity.animal.allay.Allay(
                                net.minecraft.world.entity.EntityType.ALLAY, player.level());
                        allay.setPos(player.position().add(0, 1, 0));
                        player.level().addFreshEntity(allay);
                    }
                    ferietChargeCount.put(player, charges);
                }
            }
            
            if (equippedSet.getId().getPath().equals("guiney")) {
                if (count >= 4 && guineyAuraCooldown.getOrDefault(player, 0) <= 0) {
                    int charges = guineyChargeCount.getOrDefault(player, 0);
                    charges++;
                    if (charges >= 5) {
                        charges = 0;
                        guineyAuraTimer.put(player, 5 * 20);
                        guineyAuraCooldown.put(player, 12 * 20);
                        
                        net.minecraft.world.entity.animal.allay.Allay allay = new net.minecraft.world.entity.animal.allay.Allay(
                                net.minecraft.world.entity.EntityType.ALLAY, player.level());
                        allay.setPos(player.position().add(0, 1, 0));
                        player.level().addFreshEntity(allay);
                    }
                    guineyChargeCount.put(player, charges);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide) {
            Player player = event.getEntity();
            
            curieCooldown.merge(player, 1, Integer::sum);
            
            if (aliceBuffTimer.containsKey(player)) {
                int timer = aliceBuffTimer.get(player) - 1;
                if (timer <= 0) {
                    aliceBuffTimer.remove(player);
                } else {
                    aliceBuffTimer.put(player, timer);
                }
            }
            
            Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
            for (AwarenessSet set : equippedSets) {
                int count = set.countEquipped(player);
                
                if (set.getId().getPath().equals("hanna")) {
                    if (count >= 2) {
                        AttributeModifier critMultModifier = new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("icy_punishing", "hanna_crit_multiplier"),
                                0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        var critMultAttr = player.getAttribute(com.icylib.init.ModAttributes.CRITICAL_STRIKE_MULTIPLIER_MULTIPLY);
                        if (critMultAttr != null && !critMultAttr.hasModifier(critMultModifier.id())) {
                            critMultAttr.addTransientModifier(critMultModifier);
                        }
                    }
                    if (count >= 4) {
                        AttributeModifier critChanceModifier = new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("icy_punishing", "hanna_crit_chance"),
                                0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        var critChanceAttr = player.getAttribute(com.icylib.init.ModAttributes.CRITICAL_STRIKE_CHANCE);
                        if (critChanceAttr != null && !critChanceAttr.hasModifier(critChanceModifier.id())) {
                            critChanceAttr.addTransientModifier(critChanceModifier);
                        }
                        hannaNextCritHyperSpace.putIfAbsent(player, true);
                    }
                }
                
                if (set.getId().getPath().equals("da_vinci")) {
                    net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                            player.getX() - 5.5, player.getY() - 5.5, player.getZ() - 5.5,
                            player.getX() + 5.5, player.getY() + 5.5, player.getZ() + 5.5);
                    int minionCount = 0;
                    for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                        if (entity != player && entity.isAlliedTo(player)) {
                            minionCount++;
                        }
                    }
                    
                    AttributeModifier attackModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "da_vinci_attack"),
                            0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    AttributeModifier defenseModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "da_vinci_defense"),
                            0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    AttributeModifier allAttackModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "da_vinci_all_attack"),
                            0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    
                    var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    var defenseAttr = player.getAttribute(Attributes.ARMOR);
                    
                    if (count >= 2 && minionCount > 0) {
                        if (attackAttr != null && !attackAttr.hasModifier(attackModifier.id())) {
                            attackAttr.addTransientModifier(attackModifier);
                        }
                        if (defenseAttr != null && !defenseAttr.hasModifier(defenseModifier.id())) {
                            defenseAttr.addTransientModifier(defenseModifier);
                        }
                    } else {
                        if (attackAttr != null && attackAttr.hasModifier(attackModifier.id())) {
                            attackAttr.removeModifier(attackModifier.id());
                        }
                        if (defenseAttr != null && defenseAttr.hasModifier(defenseModifier.id())) {
                            defenseAttr.removeModifier(defenseModifier.id());
                        }
                    }
                    
                    if (count >= 4 && minionCount >= 3) {
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                            if (entity.isAlliedTo(player)) {
                                var entityAttackAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                                if (entityAttackAttr != null && !entityAttackAttr.hasModifier(allAttackModifier.id())) {
                                    entityAttackAttr.addTransientModifier(allAttackModifier);
                                }
                            }
                        }
                    }
                }
                
                
                
                if (set.getId().getPath().equals("feriet_ii")) {
                    if (count >= 2 && ferietBuffTimer.containsKey(player)) {
                        AttributeModifier damageModifier = new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("icy_punishing", "feriet_ii_damage"),
                                0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        AttributeModifier defenseModifier = new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("icy_punishing", "feriet_ii_defense"),
                                0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        
                        var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                        var defenseAttr = player.getAttribute(Attributes.ARMOR);
                        
                        if (attackAttr != null && !attackAttr.hasModifier(damageModifier.id())) {
                            attackAttr.addTransientModifier(damageModifier);
                        }
                        if (defenseAttr != null && !defenseAttr.hasModifier(defenseModifier.id())) {
                            defenseAttr.addTransientModifier(defenseModifier);
                        }
                    }
                    
                    if (count >= 4 && ferietAuraTimer.containsKey(player)) {
                        boolean nearAllay = false;
                        for (net.minecraft.world.entity.animal.allay.Allay allay : player.level().getEntitiesOfClass(
                                net.minecraft.world.entity.animal.allay.Allay.class, 
                                player.getBoundingBox().inflate(5.0))) {
                            if (allay.distanceTo(player) <= 5.0) {
                                nearAllay = true;
                                break;
                            }
                        }
                        
                        AttributeModifier auraDamageModifier = new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("icy_punishing", "feriet_ii_aura_damage"),
                                0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                        if (attackAttr != null) {
                            if (nearAllay && !attackAttr.hasModifier(auraDamageModifier.id())) {
                                attackAttr.addTransientModifier(auraDamageModifier);
                            } else if (!nearAllay && attackAttr.hasModifier(auraDamageModifier.id())) {
                                attackAttr.removeModifier(auraDamageModifier.id());
                            }
                        }
                    }
                }
                
                if (set.getId().getPath().equals("guiney")) {
                    if (count >= 2 && guineyElementalTimer.containsKey(player)) {
                        AttributeModifier fireResist = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_fire_resistance"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        AttributeModifier lightResist = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_light_resistance"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        AttributeModifier iceResist = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_ice_resistance"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        AttributeModifier shadowResist = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_shadow_resistance"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        AttributeModifier elementalResist = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_elemental_resistance"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        
                        var fireAttr = player.getAttribute(com.icylib.init.ModAttributes.FIRE_RESISTANCE);
                        var lightAttr = player.getAttribute(com.icylib.init.ModAttributes.LIGHT_RESISTANCE);
                        var iceAttr = player.getAttribute(com.icylib.init.ModAttributes.ICE_RESISTANCE);
                        var shadowAttr = player.getAttribute(com.icylib.init.ModAttributes.SHADOW_RESISTANCE);
                        var elementalAttr = player.getAttribute(com.icylib.init.ModAttributes.ELEMENTAL_RESISTANCE);
                        
                        if (fireAttr != null && !fireAttr.hasModifier(fireResist.id())) fireAttr.addTransientModifier(fireResist);
                        if (lightAttr != null && !lightAttr.hasModifier(lightResist.id())) lightAttr.addTransientModifier(lightResist);
                        if (iceAttr != null && !iceAttr.hasModifier(iceResist.id())) iceAttr.addTransientModifier(iceResist);
                        if (shadowAttr != null && !shadowAttr.hasModifier(shadowResist.id())) shadowAttr.addTransientModifier(shadowResist);
                        if (elementalAttr != null && !elementalAttr.hasModifier(elementalResist.id())) elementalAttr.addTransientModifier(elementalResist);
                    }
                    
                    if (count >= 4 && guineyAuraTimer.containsKey(player)) {
                        boolean nearAllay = false;
                        for (net.minecraft.world.entity.animal.allay.Allay allay : player.level().getEntitiesOfClass(
                                net.minecraft.world.entity.animal.allay.Allay.class, 
                                player.getBoundingBox().inflate(5.0))) {
                            if (allay.distanceTo(player) <= 5.0) {
                                nearAllay = true;
                                break;
                            }
                        }
                        
                        AttributeModifier auraDamageModifier = new AttributeModifier(
                                ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_aura_damage"),
                                0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                        if (attackAttr != null) {
                            if (nearAllay && !attackAttr.hasModifier(auraDamageModifier.id())) {
                                attackAttr.addTransientModifier(auraDamageModifier);
                            } else if (!nearAllay && attackAttr.hasModifier(auraDamageModifier.id())) {
                                attackAttr.removeModifier(auraDamageModifier.id());
                            }
                        }
                    }
                }
            }
            
            if (edisonCritTimer.containsKey(player)) {
                int timer = edisonCritTimer.get(player) - 1;
                if (timer <= 0) {
                    edisonCritTimer.remove(player);
                } else {
                    edisonCritTimer.put(player, timer);
                }
            }
            
            if (archimedesTimer.containsKey(player)) {
                int timer = archimedesTimer.get(player) + 1;
                if (timer >= 10 * 20) {
                    archimedesEmpowered.put(player, true);
                    timer = 0;
                }
                archimedesTimer.put(player, timer);
            } else {
                archimedesTimer.put(player, 0);
            }
            
            if (cunninghamCooldown.containsKey(player)) {
                int cd = cunninghamCooldown.get(player) - 1;
                if (cd <= 0) {
                    cunninghamCooldown.remove(player);
                } else {
                    cunninghamCooldown.put(player, cd);
                }
            }
            
            if (aifuStackTimer.containsKey(player)) {
                int timer = aifuStackTimer.get(player) - 1;
                if (timer <= 0) {
                    aifuStackTimer.remove(player);
                    aifuCritStacks.remove(player);
                } else {
                    aifuStackTimer.put(player, timer);
                }
            }
            
            if (aifuUltimateCooldown.containsKey(player)) {
                int cd = aifuUltimateCooldown.get(player) - 1;
                if (cd <= 0) {
                    aifuUltimateCooldown.remove(player);
                } else {
                    aifuUltimateCooldown.put(player, cd);
                }
            }
            
            if (voltaireCooldown.containsKey(player)) {
                int cd = voltaireCooldown.get(player) - 1;
                if (cd <= 0) {
                    voltaireCooldown.remove(player);
                } else {
                    voltaireCooldown.put(player, cd);
                }
            }
            
            if (gorodaCooldown.containsKey(player)) {
                int cd = gorodaCooldown.get(player) - 1;
                if (cd <= 0) {
                    gorodaCooldown.remove(player);
                } else {
                    gorodaCooldown.put(player, cd);
                }
            }
            
            if (richelieuHealCooldown.containsKey(player)) {
                int cd = richelieuHealCooldown.get(player) - 1;
                if (cd <= 0) {
                    richelieuHealCooldown.remove(player);
                } else {
                    richelieuHealCooldown.put(player, cd);
                }
            }
            
            if (richelieuDamageBuffTimer.containsKey(player)) {
                int timer = richelieuDamageBuffTimer.get(player) - 1;
                if (timer <= 0) {
                    richelieuDamageBuffTimer.remove(player);
                } else {
                    richelieuDamageBuffTimer.put(player, timer);
                }
            }
            
            if (richelieuCritCooldown.containsKey(player)) {
                int cd = richelieuCritCooldown.get(player) - 1;
                if (cd <= 0) {
                    richelieuCritCooldown.remove(player);
                } else {
                    richelieuCritCooldown.put(player, cd);
                }
            }
            
            if (samandaHealCooldown.containsKey(player)) {
                int cd = samandaHealCooldown.get(player) - 1;
                if (cd <= 0) {
                    samandaHealCooldown.remove(player);
                } else {
                    samandaHealCooldown.put(player, cd);
                }
            }
            
            if (samandaAuraCooldown.containsKey(player)) {
                int cd = samandaAuraCooldown.get(player) - 1;
                if (cd <= 0) {
                    samandaAuraCooldown.remove(player);
                } else {
                    samandaAuraCooldown.put(player, cd);
                }
            }
            
            if (samandaAuraTimer.containsKey(player)) {
                int timer = samandaAuraTimer.get(player) - 1;
                if (timer <= 0) {
                    samandaAuraTimer.remove(player);
                    samandaAuraArea.remove(player);
                } else {
                    samandaAuraTimer.put(player, timer);
                    
                    if (timer % 5 == 0) {
                        net.minecraft.world.phys.AABB auraArea = new net.minecraft.world.phys.AABB(
                                player.getX() - 5, player.getY() - 5, player.getZ() - 5,
                                player.getX() + 5, player.getY() + 5, player.getZ() + 5);
                        samandaAuraArea.put(player, auraArea);
                        
                        for (int i = 0; i < 12; i++) {
                            double angle = (Math.PI * 2 / 12) * i;
                            double x = player.getX() + Math.cos(angle) * 5;
                            double z = player.getZ() + Math.sin(angle) * 5;
                            double y = player.getY() + 1;
                            player.level().addParticle(
                                    net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                                    x, y, z, 0, 0, 0);
                        }
                        
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, auraArea)) {
                            if (entity instanceof Player || entity.isAlliedTo(player)) {
                                if (!entity.hasEffect(com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE)) {
                                    entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                            com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE, 10));
                                }
                            }
                        }
                    }
                }
            }
            
            if (player.hasEffect(com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE)) {
                boolean hadHyperSpace = mozartHadHyperSpace.getOrDefault(player, false);
                
                if (!hadHyperSpace) {
                    Set<AwarenessSet> sets = getAllEquippedSets(player);
                    for (AwarenessSet set : sets) {
                        if (set.getId().getPath().equals("mozart")) {
                            int count = set.countEquipped(player);
                            if (count >= 4) {
                                float voidDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0f;
                                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class,
                                        new net.minecraft.world.phys.AABB(
                                                player.getX() - 16, player.getY() - 16, player.getZ() - 16,
                                                player.getX() + 16, player.getY() + 16, player.getZ() + 16))) {
                                    if (!(entity instanceof Player) && entity.hasEffect(com.icypunishing.registry.ModEffects.HYPER_CALCULATION_SPACE)) {
                                        entity.hurt(player.damageSources().magic(), voidDamage);
                                    }
                                }
                            }
                        }
                    }
                }
                
                mozartHadHyperSpace.put(player, true);
                
                Set<AwarenessSet> boudicaSets = getAllEquippedSets(player);
                for (AwarenessSet set : boudicaSets) {
                    if (set.getId().getPath().equals("boudica")) {
                        int count = set.countEquipped(player);
                        if (count >= 4) {
                            AttributeModifier attackModifier = new AttributeModifier(
                                    ResourceLocation.fromNamespaceAndPath("icy_punishing", "boudica_hyper_attack"),
                                    0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                            AttributeModifier speedModifier = new AttributeModifier(
                                    ResourceLocation.fromNamespaceAndPath("icy_punishing", "boudica_hyper_speed"),
                                    0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                            
                            var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                            var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
                            
                            if (attackAttr != null && !attackAttr.hasModifier(attackModifier.id())) {
                                attackAttr.addTransientModifier(attackModifier);
                            }
                            if (speedAttr != null && !speedAttr.hasModifier(speedModifier.id())) {
                                speedAttr.addTransientModifier(speedModifier);
                            }
                        }
                    }
                }
            } else {
                mozartHadHyperSpace.put(player, false);
            }
            
            if (boudicaStackTimer.containsKey(player)) {
                int timer = boudicaStackTimer.get(player) - 1;
                if (timer <= 0) {
                    boudicaStackTimer.remove(player);
                } else {
                    boudicaStackTimer.put(player, timer);
                }
            }
            
            if (einsteinnaMinionCooldown.containsKey(player)) {
                int cd = einsteinnaMinionCooldown.get(player) - 1;
                if (cd <= 0) {
                    einsteinnaMinionCooldown.remove(player);
                } else {
                    einsteinnaMinionCooldown.put(player, cd);
                }
            }
            
            if (einsteinnaAuraCooldown.containsKey(player)) {
                int cd = einsteinnaAuraCooldown.get(player) - 1;
                if (cd <= 0) {
                    einsteinnaAuraCooldown.remove(player);
                } else {
                    einsteinnaAuraCooldown.put(player, cd);
                }
            }
            
            if (einsteinnaAuraTimer.containsKey(player)) {
                int timer = einsteinnaAuraTimer.get(player) - 1;
                if (timer <= 0) {
                    einsteinnaAuraTimer.remove(player);
                    var fireResist = player.getAttribute(ModAttributes.FIRE_RESISTANCE);
                    var lightResist = player.getAttribute(ModAttributes.LIGHT_RESISTANCE);
                    var iceResist = player.getAttribute(ModAttributes.ICE_RESISTANCE);
                    var shadowResist = player.getAttribute(ModAttributes.SHADOW_RESISTANCE);
                    
                    if (fireResist != null) {
                        fireResist.setBaseValue(Math.max(0.1, fireResist.getBaseValue() - 0.2));
                    }
                    if (lightResist != null) {
                        lightResist.setBaseValue(Math.max(0.1, lightResist.getBaseValue() - 0.2));
                    }
                    if (iceResist != null) {
                        iceResist.setBaseValue(Math.max(0.1, iceResist.getBaseValue() - 0.2));
                    }
                    if (shadowResist != null) {
                        shadowResist.setBaseValue(Math.max(0.1, shadowResist.getBaseValue() - 0.2));
                    }
                } else {
                    einsteinnaAuraTimer.put(player, timer);
                }
            }
            
            if (kantlinaRageCooldown.containsKey(player)) {
                int cd = kantlinaRageCooldown.get(player) - 1;
                if (cd <= 0) {
                    kantlinaRageCooldown.remove(player);
                } else {
                    kantlinaRageCooldown.put(player, cd);
                }
            }
            
            if (shakespeareScorchingCooldown.containsKey(player)) {
                int cd = shakespeareScorchingCooldown.get(player) - 1;
                if (cd <= 0) {
                    shakespeareScorchingCooldown.remove(player);
                } else {
                    shakespeareScorchingCooldown.put(player, cd);
                }
            }
            
            if (heisenbergElectrocutionCooldown.containsKey(player)) {
                int cd = heisenbergElectrocutionCooldown.get(player) - 1;
                if (cd <= 0) {
                    heisenbergElectrocutionCooldown.remove(player);
                } else {
                    heisenbergElectrocutionCooldown.put(player, cd);
                }
            }
            
            shakespeareExplosionCooldown.forEach((p, cd) -> {
                int newCd = cd - 1;
                if (newCd <= 0) {
                    shakespeareExplosionCooldown.remove(p);
                } else {
                    shakespeareExplosionCooldown.put(p, newCd);
                }
            });
            
            heisenbergStormCooldown.forEach((p, cd) -> {
                int newCd = cd - 1;
                if (newCd <= 0) {
                    heisenbergStormCooldown.remove(p);
                } else {
                    heisenbergStormCooldown.put(p, newCd);
                }
            });
            
            if (darwinStackTimer.containsKey(player)) {
                int timer = darwinStackTimer.get(player) - 1;
                if (timer <= 0) {
                    darwinStackTimer.remove(player);
                    darwinCritStacks.remove(player);
                } else {
                    darwinStackTimer.put(player, timer);
                }
            }
            
            if (hannaUltimateCooldown.containsKey(player)) {
                int cd = hannaUltimateCooldown.get(player) - 1;
                if (cd <= 0) {
                    hannaUltimateCooldown.remove(player);
                    hannaNextCritHyperSpace.put(player, true);
                } else {
                    hannaUltimateCooldown.put(player, cd);
                }
            }
            
            if (catherineResistCooldown.containsKey(player)) {
                int cd = catherineResistCooldown.get(player) - 1;
                if (cd <= 0) {
                    catherineResistCooldown.remove(player);
                } else {
                    catherineResistCooldown.put(player, cd);
                }
            }
            
            if (catherineShieldCooldown.containsKey(player)) {
                int cd = catherineShieldCooldown.get(player) - 1;
                if (cd <= 0) {
                    catherineShieldCooldown.remove(player);
                } else {
                    catherineShieldCooldown.put(player, cd);
                }
            }
            
            if (catherineShieldTimer.containsKey(player)) {
                int timer = catherineShieldTimer.get(player) - 1;
                if (timer <= 0) {
                    catherineShieldTimer.remove(player);
                    com.icylib.shield.ShieldSystem.clearShield(player);
                    player.removeEffect(ModEffects.CATHERINE_SHIELD);
                } else {
                    catherineShieldTimer.put(player, timer);
                }
            }
            
            Set<UUID> keysToRemove = new HashSet<>();
            catherinePhysicalResistTimer.forEach((entityUUID, timer) -> {
                int newTimer = timer - 1;
                if (newTimer <= 0) {
                    Double originalResist = catherineOriginalPhysicalResist.get(entityUUID);
                    if (originalResist != null) {
                        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class,
                                new net.minecraft.world.phys.AABB(
                                        player.getX() - 32, player.getY() - 32, player.getZ() - 32,
                                        player.getX() + 32, player.getY() + 32, player.getZ() + 32))) {
                            if (entity.getUUID().equals(entityUUID)) {
                                var physicalResistAttr = entity.getAttribute(ModAttributes.PHYSICAL_RESISTANCE);
                                if (physicalResistAttr != null) {
                                    physicalResistAttr.setBaseValue(originalResist);
                                }
                                break;
                            }
                        }
                        catherineOriginalPhysicalResist.remove(entityUUID);
                    }
                    keysToRemove.add(entityUUID);
                } else {
                    catherinePhysicalResistTimer.put(entityUUID, newTimer);
                }
            });
            keysToRemove.forEach(catherinePhysicalResistTimer::remove);
            
            if (ferietHealCooldown.containsKey(player)) {
                int cd = ferietHealCooldown.get(player) - 1;
                if (cd <= 0) {
                    ferietHealCooldown.remove(player);
                } else {
                    ferietHealCooldown.put(player, cd);
                }
            }
            
            if (ferietAuraCooldown.containsKey(player)) {
                int cd = ferietAuraCooldown.get(player) - 1;
                if (cd <= 0) {
                    ferietAuraCooldown.remove(player);
                } else {
                    ferietAuraCooldown.put(player, cd);
                }
            }
            
            if (ferietAuraTimer.containsKey(player)) {
                int timer = ferietAuraTimer.get(player) - 1;
                boolean nearAllay = false;
                
                for (net.minecraft.world.entity.animal.allay.Allay allay : player.level().getEntitiesOfClass(
                        net.minecraft.world.entity.animal.allay.Allay.class, 
                        player.getBoundingBox().inflate(5.0))) {
                    if (allay.distanceTo(player) <= 5.0) {
                        nearAllay = true;
                        break;
                    }
                }
                
                if (nearAllay) {
                    if (timer % 20 == 0) {
                        float healAmount = player.getMaxHealth() * 0.02f;
                        player.heal(healAmount);
                    }
                }
                
                if (timer <= 0) {
                    ferietAuraTimer.remove(player);
                    AttributeModifier auraDamageModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "feriet_ii_aura_damage"),
                            0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (attackAttr != null && attackAttr.hasModifier(auraDamageModifier.id())) {
                        attackAttr.removeModifier(auraDamageModifier.id());
                    }
                } else {
                    ferietAuraTimer.put(player, timer);
                }
            }
            
            if (guineyHealCooldown.containsKey(player)) {
                int cd = guineyHealCooldown.get(player) - 1;
                if (cd <= 0) {
                    guineyHealCooldown.remove(player);
                } else {
                    guineyHealCooldown.put(player, cd);
                }
            }
            
            if (guineyElementalTimer.containsKey(player)) {
                int timer = guineyElementalTimer.get(player) - 1;
                if (timer <= 0) {
                    guineyElementalTimer.remove(player);
                } else {
                    guineyElementalTimer.put(player, timer);
                }
            }
            
            if (ferietBuffTimer.containsKey(player)) {
                int timer = ferietBuffTimer.get(player) - 1;
                if (timer <= 0) {
                    ferietBuffTimer.remove(player);
                } else {
                    ferietBuffTimer.put(player, timer);
                }
            }
            
            if (guineyAuraCooldown.containsKey(player)) {
                int cd = guineyAuraCooldown.get(player) - 1;
                if (cd <= 0) {
                    guineyAuraCooldown.remove(player);
                } else {
                    guineyAuraCooldown.put(player, cd);
                }
            }
            
            if (guineyAuraTimer.containsKey(player)) {
                int timer = guineyAuraTimer.get(player) - 1;
                boolean nearAllay = false;
                
                for (net.minecraft.world.entity.animal.allay.Allay allay : player.level().getEntitiesOfClass(
                        net.minecraft.world.entity.animal.allay.Allay.class, 
                        player.getBoundingBox().inflate(5.0))) {
                    if (allay.distanceTo(player) <= 5.0) {
                        nearAllay = true;
                        break;
                    }
                }
                
                if (nearAllay) {
                    if (timer % 20 == 0) {
                        float healAmount = player.getMaxHealth() * 0.02f;
                        player.heal(healAmount);
                    }
                }
                
                if (timer <= 0) {
                    guineyAuraTimer.remove(player);
                    AttributeModifier auraDamageModifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("icy_punishing", "guiney_aura_damage"),
                            0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    var attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (attackAttr != null && attackAttr.hasModifier(auraDamageModifier.id())) {
                        attackAttr.removeModifier(auraDamageModifier.id());
                    }
                } else {
                    guineyAuraTimer.put(player, timer);
                }
            }
            
            if (pattonPiercingCooldown.containsKey(player)) {
                int cd = pattonPiercingCooldown.get(player) - 1;
                if (cd <= 0) {
                    pattonPiercingCooldown.remove(player);
                } else {
                    pattonPiercingCooldown.put(player, cd);
                }
            }
            
            pattonExplosionCooldown.forEach((p, cd) -> {
                int newCd = cd - 1;
                if (newCd <= 0) {
                    pattonExplosionCooldown.remove(p);
                } else {
                    pattonExplosionCooldown.put(p, newCd);
                }
            });
            
            if (basiloneCorrosionCooldown.containsKey(player)) {
                int cd = basiloneCorrosionCooldown.get(player) - 1;
                if (cd <= 0) {
                    basiloneCorrosionCooldown.remove(player);
                } else {
                    basiloneCorrosionCooldown.put(player, cd);
                }
            }
            
            basiloneExplosionCooldown.forEach((p, cd) -> {
                int newCd = cd - 1;
                if (newCd <= 0) {
                    basiloneExplosionCooldown.remove(p);
                } else {
                    basiloneExplosionCooldown.put(p, newCd);
                }
            });
        }
    }

    private static void tryCallArtillery(Player player, LivingEntity target) {
        int cooldown = curieCooldown.getOrDefault(player, 0);
        if (cooldown >= 15 * 20 && player.getRandom().nextFloat() < 0.2) {
            float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float damage = attackDamage * 0.4f;
            target.hurt(player.damageSources().playerAttack(player), damage);
            player.level().explode(player, target.getX(), target.getY(), target.getZ(), 1.0f, Level.ExplosionInteraction.NONE);
            curieCooldown.put(player, 0);
        }
    }

    private static void handleCunninghamShield(Player player) {
        if (!cunninghamCooldown.containsKey(player)) {
            cunninghamShield.put(player, true);
            cunninghamCooldown.put(player, 5 * 20);
        }
    }

    private static void handleArchimedes(Player player) {
        archimedesTimer.putIfAbsent(player, 0);
    }

    private static Set<AwarenessSet> getAllEquippedSets(Player player) {
        Set<AwarenessSet> foundSets = new HashSet<>();
        var opt = CuriosApi.getCuriosInventory(player);
        if (opt.isPresent()) {
            List<top.theillusivec4.curios.api.SlotResult> list = opt.get().findCurios(stack -> stack.getItem() instanceof AwarenessItem);
            for (top.theillusivec4.curios.api.SlotResult result : list) {
                if (result.stack().getItem() instanceof AwarenessItem awarenessItem) {
                    foundSets.add(awarenessItem.getSet());
                }
            }
        }
        return foundSets;
    }

    

    private static void handleMinionDamage(LivingDamageEvent.Pre event, Player player) {
        Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
        for (AwarenessSet equippedSet : equippedSets) {
            int count = equippedSet.countEquipped(player);
            if (equippedSet.getId().getPath().equals("nimitiz") && count >= 6) {
                float damage = event.getNewDamage();
                damage *= 1.1f;
                event.setNewDamage(damage);
            }
            
            if (equippedSet.getId().getPath().equals("catherine") && count >= 2) {
                int cooldown = catherineResistCooldown.getOrDefault(player, 0);
                if (cooldown <= 0) {
                    LivingEntity target = event.getEntity();
                    var physicalResistAttr = target.getAttribute(ModAttributes.PHYSICAL_RESISTANCE);
                    if (physicalResistAttr != null) {
                        catherineOriginalPhysicalResist.put(target.getUUID(), physicalResistAttr.getBaseValue());
                        physicalResistAttr.setBaseValue(Math.max(0.1, physicalResistAttr.getBaseValue() - 0.2));
                    }
                    catherinePhysicalResistTimer.put(target.getUUID(), 8 * 20);
                    catherineResistCooldown.put(player, 10 * 20);
                }
            }
            
            if (equippedSet.getId().getPath().equals("da_vinci") && count >= 4) {
                LivingEntity target = event.getEntity();
                net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                        player.getX() - 5.5, player.getY() - 5.5, player.getZ() - 5.5,
                        player.getX() + 5.5, player.getY() + 5.5, player.getZ() + 5.5);
                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
                    if (entity != player && entity != target && entity.isAlliedTo(player) && entity instanceof net.minecraft.world.entity.Mob mob) {
                        mob.setTarget(target);
                    }
                }
            }
        }
    }

    private static Player getOwnerOfEntity(LivingEntity entity) {
        if (entity.getPersistentData().contains("OwnerUUID")) {
            String ownerUUID = entity.getPersistentData().getString("OwnerUUID");
            try {
                UUID uuid = UUID.fromString(ownerUUID);
                return entity.level().getPlayerByUUID(uuid);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
    
    private static void applyTrueDamage(LivingEntity target, Player attacker, float damage) {
        if (target == null || target.isDeadOrDying()) return;
        float currentHealth = target.getHealth();
        float newHealth = Math.max(0, currentHealth - damage);
        target.setHealth(newHealth);
        target.hurtDuration = 10;
        target.hurtTime = 10;
        target.invulnerableTime = 0;
        if (!target.level().isClientSide) {
            target.level().broadcastEntityEvent(target, (byte) 33);
        }
    }
    
    @SubscribeEvent
    public void onLivingDamage_Boudica(LivingDamageEvent.Pre event) {
        if (!event.getEntity().level().isClientSide && event.getContainer().getSource().getEntity() instanceof Player player) {
            Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
            for (AwarenessSet equippedSet : equippedSets) {
                int count = equippedSet.countEquipped(player);
                if (equippedSet.getId().getPath().equals("boudica") && count >= 6) {
                    int currentStacks = boudicaStacks.getOrDefault(player, 0);
                    int stackTimer = boudicaStackTimer.getOrDefault(player, 0);
                    
                    if (stackTimer <= 0) {
                        currentStacks = Math.min(currentStacks + 1, 5);
                        boudicaStacks.put(player, currentStacks);
                        boudicaStackTimer.put(player, 2 * 20);
                    }
                }
            }
        }
        
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player player) {
            boudicaStacksReset.put(player, true);
            
            Set<AwarenessSet> equippedSets = getAllEquippedSets(player);
            for (AwarenessSet equippedSet : equippedSets) {
                if (equippedSet.getId().getPath().equals("boudica")) {
                    boudicaStacks.remove(player);
                    boudicaStackTimer.remove(player);
                }
            }
        }
    }
}