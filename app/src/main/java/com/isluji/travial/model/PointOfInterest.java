package com.isluji.travial.model;

import android.location.Location;

import java.util.ArrayList;

public class PointOfInterest {

    private String name;
    private PoiType type;
    private ArrayList<String> authors;
    private int openingYear;
    private Location location;
    private boolean openToPublic;

    public boolean isOpenToPublic() {
        return openToPublic;
    }

    // -------------------------------------

    public boolean hasSeveralAuthors() {
        return (authors.size() > 1);
    }

    public boolean hasAnyTrivia() {
        return false;   // TODO
    }
}
