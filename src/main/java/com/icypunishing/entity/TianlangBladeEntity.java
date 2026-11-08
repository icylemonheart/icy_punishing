package com.icypunishing.entity;

import com.icypunishing.item.ObjKatanaItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/** 天狼星副手悬浮体：跟随主人停在左后上方，纯表现实体，不参与碰撞与伤害。 */
public class TianlangBladeEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(TianlangBladeEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_OWNER =
            SynchedEntityData.defineId(TianlangBladeEntity.class, EntityDataSerializers.INT);
    private static final String TAG_OWNER = "owner";

    public static final double BACK_OFFSET = 1.40;
    public static final double LEFT_OFFSET = 0.90;
    public static final double HEIGHT = 2.30;

    private UUID ownerId;

    public TianlangBladeEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM, ItemStack.EMPTY);
        builder.define(DATA_OWNER, 0);
    }

    /** 主人实体 id（同步到客户端，供渲染端做逐帧跟随）。 */
    public int getOwnerEntityId() {
        return this.getEntityData().get(DATA_OWNER);
    }

    public void setOwnerEntity(int entityId) {
        this.getEntityData().set(DATA_OWNER, entityId);
    }

    public ItemStack getDisplayStack() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setDisplayStack(ItemStack stack) {
        this.getEntityData().set(DATA_ITEM, stack);
    }

    public void setOwnerId(UUID id) {
        this.ownerId = id;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Player owner = ownerId != null ? serverLevel.getPlayerByUUID(ownerId) : null;
        if (owner == null || !holdsTianlang(owner)) {
            this.discard();
            return;
        }
        if (this.tickCount % 20 == 0) {
            this.setDisplayStack(owner.getOffhandItem().copy());
        }
        if (getOwnerEntityId() != owner.getId()) {
            setOwnerEntity(owner.getId());
        }

        double yawRad = Math.toRadians(owner.getYRot());
        Vec3 back = new Vec3(Math.sin(yawRad), 0.0, -Math.cos(yawRad));
        Vec3 left = new Vec3(Math.cos(yawRad), 0.0, Math.sin(yawRad));
        double bob = Math.sin((owner.tickCount + this.tickCount) * 0.08) * 0.08;
        Vec3 target = owner.position()
                .add(back.scale(BACK_OFFSET))
                .add(left.scale(LEFT_OFFSET))
                .add(0.0, HEIGHT + bob, 0.0);

        Vec3 current = this.position();
        this.setPos(current.add(target.subtract(current).scale(0.6)));
        this.setDeltaMovement(Vec3.ZERO);
        this.setYRot(owner.getYRot());
        this.yRotO = owner.yRotO;
    }

    private static boolean holdsTianlang(Player owner) {
        ItemStack stack = owner.getOffhandItem();
        return stack.getItem() instanceof ObjKatanaItem katana && "tianlang".equals(katana.getModelId());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID(TAG_OWNER)) {
            ownerId = tag.getUUID(TAG_OWNER);
        }
        setDisplayStack(ItemStack.parseOptional(this.registryAccess(), tag.getCompound("item")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerId != null) {
            tag.putUUID(TAG_OWNER, ownerId);
        }
        if (!getDisplayStack().isEmpty()) {
            tag.put("item", getDisplayStack().save(this.registryAccess(), new CompoundTag()));
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}
