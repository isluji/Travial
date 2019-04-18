package com.isluji.travial.model;

import com.isluji.travial.enums.PoiType;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PointOfInterest {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    private PoiType type;

    // TODO: Can be null for unknown authors
    private String author;

    @ColumnInfo(name = "finishing_year")
    private int finishingYear;

    // TODO: If it's null, we'll take it as 'false'
    @ColumnInfo(name = "open_to_public")
    private boolean openToPublic;

    private String image;

    @Embedded
    private PoiLocation location;


    public PointOfInterest(@NonNull String name, PoiType type, String author,
                           int finishingYear, boolean openToPublic,
                           String image, PoiLocation location) {
        this.name = name;
        this.type = type;
        this.author = author;
        this.finishingYear = finishingYear;
        this.openToPublic = openToPublic;
        this.image = image;
        this.location = location;
    }

    /** Getters and setters */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public PoiType getType() {
        return type;
    }

    public void setType(PoiType type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getFinishingYear() {
        return finishingYear;
    }

    public void setFinishingYear(int finishingYear) {
        this.finishingYear = finishingYear;
    }

    public boolean isOpenToPublic() {
        return openToPublic;
    }

    public void setOpenToPublic(boolean openToPublic) {
        this.openToPublic = openToPublic;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public PoiLocation getLocation() {
        return location;
    }

    public void setLocation(PoiLocation location) {
        this.location = location;
    }


    /** Implemented methods */

    public boolean hasAnyTrivia() {
        return false;   // TODO
    }
}
