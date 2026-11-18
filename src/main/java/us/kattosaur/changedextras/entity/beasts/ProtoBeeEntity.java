package us.kattosaur.changedextras.entity.beasts;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ProtoBeeEntity extends AbstractWhiteCatEntity {
    public ProtoBeeEntity(EntityType<? extends ProtoBeeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.NONE.get();
    }
}
