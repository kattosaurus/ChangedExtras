package us.kattosaur.changedextras.item;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.item.ShirtItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import us.kattosaur.changedextras.ChangedExtras;

import java.util.List;

public class KattoJacketItem extends ShirtItem {

    public KattoJacketItem() {
        super();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.literal("This jacket looks a bit worn out, I wonder who owned it before")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, Entity entity) {
        return ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/models/accessory/katto_jacket.png");
    }

    @Override
    public boolean allowedInSlot(ItemStack stack, LivingEntity wearer, AccessorySlotType slotType) {
        // This is Changed clothing rendered in the chest accessory slot, not vanilla armor.
        return slotType.getEquivalentSlot() == EquipmentSlot.CHEST;
    }
}
