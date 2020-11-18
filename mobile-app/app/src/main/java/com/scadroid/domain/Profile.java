package com.scadroid.domain;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

/**
 * Created by viniciusthiengo on 5/10/15.
 */
public class Profile {
    private ProfileDrawerItem profile;
    private int background;
    private String serverId;

    public Profile(){}

    public ProfileDrawerItem getProfile() {
        return profile;
    }

    public void setProfile(ProfileDrawerItem profile) {
        this.profile = profile;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getServerId(){
        return serverId;
    }

    public void setServerId(String url){
        this.serverId = url;
    }
}
