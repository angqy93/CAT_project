package org.example.xplorer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import objects.Card;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JoinEventController implements Initializable {

    @FXML
    private GridPane cardContainer;

    private List<Card> cards = new ArrayList<>();

    public List<Card> getData(){
        List<Card> cards = new ArrayList<>();

        Card card;

        for(int i=0; i<4; i++){
            card = new Card();
            card.setImage("/img/Waddle.png");
            card.setName("Ang");
            card.setDate("date");
            card.setTime("time");
            card.setVenue("penang");
            card.setDescription("description");
            cards.add(card);
        }

        return cards;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cards.addAll(getData());
        int column = 0;
        int row = 0;
        try {
            for (Card card : cards) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("EventCard.fxml"));

                AnchorPane anchorPane = fxmlLoader.load();

                EventCardController eventCardController = fxmlLoader.getController();
                eventCardController.setData(card);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                cardContainer.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
                cardContainer.setMinWidth(Region.USE_COMPUTED_SIZE);
                cardContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
                cardContainer.setMaxWidth(Region.USE_COMPUTED_SIZE);

                cardContainer.setMinHeight(Region.USE_COMPUTED_SIZE);
                cardContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
                cardContainer.setMaxWidth(Region.USE_COMPUTED_SIZE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//        try {
//            for (Card card : cards) {
//                FXMLLoader fxmlLoader = new FXMLLoader();
//                fxmlLoader.setLocation(getClass().getResource("EventCard.fxml"));
//
//                GridPane gridPane = fxmlLoader.load();
//                EventCardController eventCardController = fxmlLoader.getController();
//                eventCardController.setData(card);
//                cardContainer.getChildren().add(gridPane);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    public List<Card> getCards(){
//        List<Card> ls = new ArrayList<>();
//
//        Card card;
//
//        for(int i=0; i<50; i++){
//            card = new Card();
//            card.setImage("/img/Waddle.png");
//            card.setName("Ang");
//            card.setDate("date");
//            card.setTime("time");
//            card.setVenue("penang");
//            card.setDescription("description");
//            ls.add(card);
//        }
//
//        return ls;
//    }
}
