package com.dev.engineerrant.network.models.git;

import com.dev.engineerrant.classes.dev.Projects;

import java.util.List;

// works for register & login
public class ModelCommunity {

    long last_updated;
    List<Projects> projects;

    public long getLast_updated() {
        return last_updated;
    }

    public List<Projects> getProjects() {
        return projects;
    }
}

