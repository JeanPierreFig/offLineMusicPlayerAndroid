/*
  Created by jeanpierre on 4/1/18.
  // jean pierre
  // JAV2 - 1804
  //
 */
package com.example.jeanpierre.figaredojeanpierre_ce02;


public class Music {

    private final String title;
    private final int resorce;
    private final int image;


    public String getTitle() {
        return title;
    }

    public int getResorce() {
        return resorce;
    }

    public int getImage() {
        return image;
    }

    public Music(String title, int resorce, int image) {
        this.title = title;
        this.resorce = resorce;
        this.image = image;
    }
}
