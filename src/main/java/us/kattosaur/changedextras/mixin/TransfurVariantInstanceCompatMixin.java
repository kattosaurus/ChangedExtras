package us.kattosaur.changedextras.mixin;

import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.KeyReference;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Restores the no-argument {@code getSelectedAbility()} that Changed Addon Plus
 * 2.9.2c was compiled against. Changed 0.16.0 replaced it with the keyed
 * overload, so the addon's ability checks (carry animation, wing animation,
 * glow layers and transfur cancellation) throw NoSuchMethodError when called.
 */
@Mixin(value = TransfurVariantInstance.class, remap = false)
public abstract class TransfurVariantInstanceCompatMixin {
    public AbstractAbilityInstance getSelectedAbility() {
        TransfurVariantInstance<?> self = (TransfurVariantInstance<?>) (Object) this;
        return self.getSelectedAbilityInstance(KeyReference.ABILITY);
    }
}
