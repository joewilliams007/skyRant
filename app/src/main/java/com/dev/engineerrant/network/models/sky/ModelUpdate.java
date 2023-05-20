package com.dev.engineerrant.network.models.sky;

import com.dev.engineerrant.classes.dev.Changelog;

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

