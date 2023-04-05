package com.dev.engineerrant.models;

import com.dev.engineerrant.adapters.ChangelogItem;
import com.dev.engineerrant.classes.Changelog;
import com.dev.engineerrant.classes.Rants;

import java.util.List;

// works for register & login
public class ModelUpdate {

    String version;
    int build;
    List<Changelog> changelog;

    public List<Changelog> getChangelog() {
        return changelog;
    }

    public String getVersion() {
        return version;
    }

    public int getBuild() {
        return build;
    }

}

