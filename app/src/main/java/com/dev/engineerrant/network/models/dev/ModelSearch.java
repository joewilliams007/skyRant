package com.dev.engineerrant.network.models.dev;

import com.dev.engineerrant.classes.dev.Rants;

import java.util.List;

// works for register & login
public class ModelSearch {
    List<Rants> results;
    Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public List<Rants> getRants() {
        return results;
    }


}

