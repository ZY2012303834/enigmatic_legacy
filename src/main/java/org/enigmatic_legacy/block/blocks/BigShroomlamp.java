package org.enigmatic_legacy.block.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;

public class BigShroomlamp extends LanternBlock {

    public BigShroomlamp() {
        super(Properties.ofFullCopy(Blocks.SHROOMLIGHT)
                .lightLevel(state -> 15)
                .noOcclusion());
    }
}