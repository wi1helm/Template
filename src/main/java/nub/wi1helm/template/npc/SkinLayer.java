package nub.wi1helm.template.npc;

import net.minestom.server.entity.metadata.PlayerMeta;

public enum SkinLayer {
    FULL(true, true, true, true, true, true, true),
    NO_CAPE(false, true, true, true, true, true, true),
    NO_JACKET(true, false, true, true, true, true, true),
    NO_HAT(true, true, true, true, true, true, false),
    ONLY_HAT(false, false, false, false, false, false, true),
    NONE(false, false, false, false, false, false, false);

    private final boolean cape;
    private final boolean jacket;
    private final boolean leftSleeve;
    private final boolean rightSleeve;
    private final boolean leftLeg;
    private final boolean rightLeg;
    private final boolean hat;

    SkinLayer(boolean cape, boolean jacket, boolean leftSleeve, boolean rightSleeve, boolean leftLeg, boolean rightLeg, boolean hat) {
        this.cape = cape;
        this.jacket = jacket;
        this.leftSleeve = leftSleeve;
        this.rightSleeve = rightSleeve;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.hat = hat;
    }

    public void apply(PlayerMeta meta) {
        meta.setCapeEnabled(cape);
        meta.setJacketEnabled(jacket);
        meta.setLeftSleeveEnabled(leftSleeve);
        meta.setRightSleeveEnabled(rightSleeve);
        meta.setLeftLegEnabled(leftLeg);
        meta.setRightLegEnabled(rightLeg);
        meta.setHatEnabled(hat);
    }
}

