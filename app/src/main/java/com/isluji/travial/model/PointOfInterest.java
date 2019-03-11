package com.isluji.travial.model;

import android.location.Location;

import java.util.ArrayList;

public class PointOfInterest {

    private String name;
    private PoiType type;
    private ArrayList<String> authors;
    private int openingYear;
    private boolean openToPublic;
    private String image;
    private Location location;

    public PointOfInterest(String name, PoiType type, ArrayList<String> authors, int openingYear, boolean openToPublic, String image, Location location) {
        this.name = name;
        this.type = type;
        this.authors = authors;
        this.openingYear = openingYear;
        this.openToPublic = openToPublic;
        this.image = image;
        this.location = location;
    }

    public String getImage() {
        return image;
    }

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
