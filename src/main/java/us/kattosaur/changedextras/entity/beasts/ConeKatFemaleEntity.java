package us.kattosaur.changedextras.entity.beasts;

import net.ltxprogrammer.changed.entity.Gender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ConeKatFemaleEntity extends AbstractConeKatEntity {
    public ConeKatFemaleEntity(EntityType<? extends ConeKatFemaleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}
