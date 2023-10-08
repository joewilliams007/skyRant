package com.dev.engineerrant.classes.kbin;

public class Magazine {
    Integer magazineId;
    String name;
    Icon icon;
    Boolean isUserSubscribed;
    Boolean isBlockedByUser;
    String apId;
    String apProfileId;

    public Integer getMagazineId() {
        return magazineId;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    public Boolean getUserSubscribed() {
        return isUserSubscribed;
    }

    public Boolean getBlockedByUser() {
        return isBlockedByUser;
    }

    public String getApId() {
        return apId;
    }

    public String getApProfileId() {
        return apProfileId;
    }
}
