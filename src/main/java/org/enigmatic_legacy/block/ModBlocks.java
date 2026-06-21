package org.enigmatic_legacy.block;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.enigmatic_legacy.EnigmaticLegacy;
import org.enigmatic_legacy.block.blocks.AstralDustSack;
import org.enigmatic_legacy.block.blocks.EtheriumBlock;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(EnigmaticLegacy.MODID);

    public static final DeferredBlock<AstralDustSack> ASTRAL_DUST_SACK = BLOCKS.register("astral_dust_sack", AstralDustSack::new);
    public static final DeferredBlock<EtheriumBlock> ETHERIUM_BLOCK = BLOCKS.register("etherium_block", EtheriumBlock::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
