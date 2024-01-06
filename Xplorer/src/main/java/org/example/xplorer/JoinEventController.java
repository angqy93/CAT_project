package org.example.xplorer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import objects.Card;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JoinEventController implements Initializable {

    @FXML
    private VBox cardContainer;
    List<Card> cards;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cards = new ArrayList<>(getCards());
        try {
            for(Card card:cards){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("org/example/xplorer/EventCard.fxml"));

                VBox vbox = fxmlLoader.load();
                EventCardController eventCardController = fxmlLoader.getController();
                eventCardController.setData(card);
                cardContainer.getChildren().add(vbox);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<Card> getCards(){
        List<Card> ls = new ArrayList<>();

        Card card;

        for(int i=0; i<50; i++){
             card = new Card();
            card.setImage("/img/Waddle.png");
            card.setName("Ang");
            card.setDate("date");
            card.setTime("time");
            card.setVenue("penang");
            card.setDescription("description");
            ls.add(card);
        }

        return ls;
    }
}
