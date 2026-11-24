package us.kattosaur.changedextras.item;

import us.kattosaur.changedextras.client.access.ArtistBrushClientAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** The Artist's Brush: still a texture editor off-hand tool, now also a hexing sword. */
public class ArtistBrushItem extends SwordItem {
    public static final String BRUSH_DATA_TAG = "ArtistBrushData";
    public static final String TEXTURE_PATH_TAG = "TexturePath";
    public static final String HEX_COLOR_TAG = "HexColor";
    public static final String TARGET_FORM_TAG = "TargetForm";
    public static final String TARGET_FORM_ID = "changed:custom_latex";
    public static final String SELECTED_TARGET_NAME_TAG = "SelectedTargetName";
    public static final String SELECTED_TARGET_UUID_TAG = "SelectedTargetUuid";
    public static final String SELECTED_TARGET_TYPE_TAG = "SelectedTargetType";

    // Weapon tunables.
    /** Durability of the brush. */
    private static final int BRUSH_DURABILITY = 850;
    /** Total melee damage: tier bonus (6) + sword modifier (1). */
    private static final int BRUSH_DAMAGE = 7;
    /** Attack speed modifier: 1.1 attacks/sec, between an axe and a sword (trident speed). */
    private static final float BRUSH_ATTACK_SPEED = -2.9F;
    /** How many random negative effects a hit applies. */
    private static final int BRUSH_EFFECT_COUNT = 1;
    private static final int BRUSH_EFFECT_DURATION = 120;
    private static final int BRUSH_EFFECT_AMPLIFIER = 0;

    private static final Tier BRUSH_TIER = new Tier() {
        @Override
        public int getUses() {
            return BRUSH_DURABILITY;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 6.0F;
        }

        @Override
        public int getLevel() {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.NETHERITE_SCRAP);
        }
    };

    public ArtistBrushItem(Properties properties) {
        super(BRUSH_TIER, BRUSH_DAMAGE - 6, BRUSH_ATTACK_SPEED, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ArtistBrushClientAccess.openEditor(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide) {
            for (int i = 0; i < BRUSH_EFFECT_COUNT; i++) {
                target.addEffect(rollBrushEffect(attacker.getRandom()));
            }
        }
        return result;
    }

    private MobEffectInstance rollBrushEffect(RandomSource random) {
        return switch (random.nextInt(8)) {
            case 0 -> new MobEffectInstance(MobEffects.WITHER, BRUSH_EFFECT_DURATION, BRUSH_EFFECT_AMPLIFIER);
            case 1 -> new MobEffectInstance(MobEffects.POISON, BRUSH_EFFECT_DURATION, BRUSH_EFFECT_AMPLIFIER);
            case 2 -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BRUSH_EFFECT_DURATION, BRUSH_EFFECT_AMPLIFIER + 1);
            case 3 -> new MobEffectInstance(MobEffects.WEAKNESS, BRUSH_EFFECT_DURATION, BRUSH_EFFECT_AMPLIFIER);
            case 4 -> new MobEffectInstance(MobEffects.BLINDNESS, BRUSH_EFFECT_DURATION / 2, BRUSH_EFFECT_AMPLIFIER);
            case 5 -> new MobEffectInstance(MobEffects.HUNGER, BRUSH_EFFECT_DURATION, BRUSH_EFFECT_AMPLIFIER);
            case 6 -> new MobEffectInstance(MobEffects.HARM, 1, 0);
            default -> new MobEffectInstance(MobEffects.CONFUSION, BRUSH_EFFECT_DURATION, BRUSH_EFFECT_AMPLIFIER);
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag brushData = stack.getTagElement(BRUSH_DATA_TAG);
        tooltip.add(Component.translatable("item.changedextras.artist_brush.tooltip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.changedextras.artist_brush.tooltip.weapon").withStyle(ChatFormatting.RED));
        if (brushData == null) {
            tooltip.add(Component.translatable("item.changedextras.artist_brush.tooltip.empty").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltip.add(Component.translatable("item.changedextras.artist_brush.texture", brushData.getString(TEXTURE_PATH_TAG)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.changedextras.artist_brush.hex", brushData.getString(HEX_COLOR_TAG)).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.changedextras.artist_brush.target", brushData.getString(TARGET_FORM_TAG)).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("item.changedextras.artist_brush.selected", brushData.getString(SELECTED_TARGET_NAME_TAG)).withStyle(ChatFormatting.BLUE));
    }

    public static CompoundTag getOrCreateBrushData(ItemStack stack) {
        CompoundTag brushData = stack.getTagElement(BRUSH_DATA_TAG);
        if (brushData == null) {
            brushData = new CompoundTag();
            brushData.putString(TEXTURE_PATH_TAG, "");
            brushData.putString(HEX_COLOR_TAG, "#FFFFFF");
            brushData.putString(TARGET_FORM_TAG, TARGET_FORM_ID);
            brushData.putString(SELECTED_TARGET_NAME_TAG, "None");
            brushData.putString(SELECTED_TARGET_UUID_TAG, "");
            brushData.putString(SELECTED_TARGET_TYPE_TAG, "none");
            stack.getOrCreateTag().put(BRUSH_DATA_TAG, brushData);
        }
        return brushData;
    }

    public static void setSelectedTarget(ItemStack stack, String name, String uuid, String type) {
        CompoundTag brushData = getOrCreateBrushData(stack);
        brushData.putString(SELECTED_TARGET_NAME_TAG, name);
        brushData.putString(SELECTED_TARGET_UUID_TAG, uuid);
        brushData.putString(SELECTED_TARGET_TYPE_TAG, type);
    }
}
