package Ash_Hollow_Requiem.bounty;

public enum BountyType {
    STANDARD("Bounty", false),
    BOSS("Boss Bounty", true),
    HORDE("Horde Bounty", false),
    ELITE("Elite Bounty", false);

    private final String displayName;
    private final boolean isBossType;

    BountyType (String displayName, boolean isBossType) {
        this.displayName = displayName;
        this.isBossType = isBossType;
    }

    public String getDisplayName() { return displayName; }
    public boolean isBossType() { return isBossType; }
}
