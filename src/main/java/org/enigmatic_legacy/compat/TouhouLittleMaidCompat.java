package org.enigmatic_legacy.compat;

/**
 * Touhou Little Maid 兼容逻辑。
 *
 * <p>当前兼容通过数据生成完成：给女仆实体声明与玩家一致的 Curios 栏位，
 * 让女仆可以佩戴本项目的戒指、护符、术石与卷轴。</p>
 */
public final class TouhouLittleMaidCompat {
    public static final String MODID = "touhou_little_maid";
    public static final String MAID_ENTITY_ID = MODID + ":maid";
    public static final String MAID_CURIOS_ENTITY_FILE = MODID + "_maid.json";

    private TouhouLittleMaidCompat() {
    }
}
