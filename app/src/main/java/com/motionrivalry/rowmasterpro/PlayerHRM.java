package com.motionrivalry.rowmasterpro;

public class PlayerHRM {
    private int imageID;
    private String name;
    private String polarID;
    private boolean selected;

    public int getImageID() {
        return imageID;
    }
    public String getName() {
        return name;
    }
    public String getPolarID() {
        return polarID;
    }

    public PlayerHRM(int imageID, String name, String polarID, boolean selected) {
        this.imageID = imageID;
        this.name = name;
        this.polarID = polarID;
        this.selected = selected;
    }

    public Boolean getSelected ()
    {
        return selected;
    }

    public void setSelected (Boolean s_selected)
    {
        this.selected = s_selected;
    }


}
