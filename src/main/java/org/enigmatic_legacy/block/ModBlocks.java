package org.enigmatic_legacy.block;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.blocks.AstralDustSack;
import org.enigmatic_legacy.block.blocks.BigLamp;
import org.enigmatic_legacy.block.blocks.EtheriumBlock;
import org.enigmatic_legacy.block.blocks.MassiveLamp;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(EnigmaticLegacy.MODID);

    public static final DeferredBlock<AstralDustSack> ASTRAL_DUST_SACK = BLOCKS.register("astral_dust_sack", AstralDustSack::new);    // 袋装星尘
    public static final DeferredBlock<EtheriumBlock> ETHERIUM_BLOCK = BLOCKS.register("etherium_block", EtheriumBlock::new);    // 以太块
    public static final DeferredBlock<BigLamp> BIG_LAMP = BLOCKS.register("big_lamp", BigLamp::new);    // 大灯笼
    public static final DeferredBlock<MassiveLamp> MASSIVE_LAMP = BLOCKS.register("massive_lamp", MassiveLamp::new); // 封装的大灯笼

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
