package org.example.xplorer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import objects.Card;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EventCardController implements Initializable {
    @FXML
    private Text dateText;

    @FXML
    private ImageView imageBox;

    @FXML
    private Text nameText;

    @FXML
    private Text timeText;

    @FXML
    private Text venueTest;

    @FXML
    private Text descriptionText;
    public void setData(Card card){
        Image img;
        img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImage())));
        imageBox.setImage(img);
        imageBox.setFitHeight(300);;
        nameText.setText((card.getName()));
        dateText.setText(card.getDate());
        timeText.setText(card.getTime());
        venueTest.setText(card.getVenue());
        descriptionText.setText(card.getDescription());
    }
    private Card getCard(){
        Card card = new Card();
        card.setImage("/img/Waddle.png");
        card.setName("Ang");
        card.setDate("date");
        card.setTime("time");
        card.setVenue("penang");
        card.setDescription("description");

        return card;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setData(getCard());
    }
}
