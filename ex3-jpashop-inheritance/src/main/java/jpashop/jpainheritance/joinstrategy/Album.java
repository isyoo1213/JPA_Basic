package jpashop.jpainheritance.joinstrategy;

import javax.persistence.Entity;

@Entity
public class Album extends ItemH {
    private String artist;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
