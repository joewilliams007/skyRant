package com.dev.engineerrant.classes.kbin;

public class Domain {
    String name;
    Integer entryCount;
    Integer subscriptionsCount;
    Boolean isUserSubscribed;
    Boolean isBlockedByUser;
    Integer domainId;

    public String getName() {
        return name;
    }

    public Integer getEntryCount() {
        return entryCount;
    }

    public Integer getSubscriptionsCount() {
        return subscriptionsCount;
    }

    public Boolean getUserSubscribed() {
        return isUserSubscribed;
    }

    public Boolean getBlockedByUser() {
        return isBlockedByUser;
    }

    public Integer getDomainId() {
        return domainId;
    }
}
