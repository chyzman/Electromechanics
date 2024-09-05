package com.chyzman.electromechanics.data;

import com.chyzman.electromechanics.Electromechanics;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class SlimeTags {

    private static final String COMMON_NAMESPACE = "c";

    public static class Blocks {

        public static final TagKey<Block> SLIME_SLABS = registerEM("slime_slabs");
        public static final TagKey<Block> C_SLIME_SLABS = registerCommon("slime_slabs");
        public static final TagKey<Block> COLORED_SLIME_SLABS = registerEM("colored_slime_slabs");

        public static final TagKey<Block> SLIME_BLOCKS = registerEM("slime_blocks");
        public static final TagKey<Block> C_SLIME_BLOCKS = registerCommon("slime_blocks");
        public static final TagKey<Block> COLORED_SLIME_BLOCKS = registerEM("colored_slime_blocks");

        public static final TagKey<Block> STICKY_BLOCKS = registerCommon("sticky_blocks");

        private static TagKey<Block> registerCommon(String path) {
            return register(common(path));
        }

        private static TagKey<Block> registerEM(String path) {
            return register(Electromechanics.id(path));
        }

        private static TagKey<Block> register(Identifier id) {
            return TagKey.of(RegistryKeys.BLOCK, id);
        }
    }

    private static Identifier common(String path){
        return Identifier.of(COMMON_NAMESPACE, path);
    }
}
