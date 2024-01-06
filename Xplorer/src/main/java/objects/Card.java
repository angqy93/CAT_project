package objects;

import javafx.scene.image.Image;

public class Card{
    private String image;

    private String name;
    private String date;
    private String time;
    private String venue;
    private String description;

    public String getImage(){return image;}
    public void setImage(String image) {this.image = image;}
    public String getName(){return name;}
    public void setName(String name) {this.name = name;}
    public String getDate(){return date;}
    public void setDate(String date){this.date = date;}
    public String getTime(){return time;}
    public void setTime(String time) {this.time = time;}
    public String getVenue(){return venue;}
    public void setVenue(String venue) {this.venue = venue;}
    public String getDescription(){return description;}
    public void setDescription(String description) {this.description = description;}
}


