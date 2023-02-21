package dk.easv.presentation.controller;

import dk.easv.entities.*;
import dk.easv.presentation.model.AppModel;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.media.*;
import javafx.util.*;

import java.io.*;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {

    @FXML
    private Button playButton;

    @FXML
    private ListView<User> lvUsers;
    @FXML
    private ListView<Movie> lvTopForUser;
    @FXML
    private ListView<Movie> lvTopAvgNotSeen;
    @FXML
    private ListView<UserSimilarity> lvTopSimilarUsers;
    @FXML
    private ListView<TopMovie> lvTopFromSimilar;

    @FXML
    private MediaView mediaViewWindow;


    private MediaPlayer mediaPlayer;
    private Media media;


    private AppModel model;
    private long timerStartMillis = 0;
    private String timerMsg = "";
    private double current;
    private double end;
    private boolean running;

    private void startTimer(String message){
        timerStartMillis = System.currentTimeMillis();
        timerMsg = message;
    }

    private void stopTimer(){
        System.out.println(timerMsg + " took : " + (System.currentTimeMillis() - timerStartMillis) + "ms");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File trailer = new File("src/resources/vecteezy-trailer-ex.mp4");
        media = new Media(trailer.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaViewWindow.setMediaPlayer(mediaPlayer);
    }

    public void setModel(AppModel model) {
        this.model = model;
        //lvUsers.setItems(model.getObsUsers());
        //lvTopForUser.setItems(model.getObsTopMovieSeen());
        //lvTopAvgNotSeen.setItems(model.getObsTopMovieNotSeen());
        //lvTopSimilarUsers.setItems(model.getObsSimilarUsers());
        //lvTopFromSimilar.setItems(model.getObsTopMoviesSimilarUsers());

        startTimer("Load users");
        model.loadUsers();
        stopTimer();

        //lvUsers.getSelectionModel().selectedItemProperty().addListener(
        //        (observableValue, oldUser, selectedUser) -> {
        //            startTimer("Loading all data for user: " + selectedUser);
        //            model.loadData(selectedUser);
        //        });

        // Select the logged-in user in the listview, automagically trigger the listener above
        //lvUsers.getSelectionModel().select(model.getObsLoggedInUser());
    }

    @FXML
    private void playMedia (ActionEvent actionEvent){
        if (!running){
            running = true;
            mediaPlayer.play();
            beginTrailerTimer();
        } else
        {
            running = false;
            mediaPlayer.pause();
        }
    }

    private void beginTrailerTimer()
    {
        Timer playTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                current = mediaPlayer.getCurrentTime().toMillis();
                end = media.getDuration().toMillis();

                playButton.setVisible(false);

                if (current / end == 1) {
                    mediaPlayer.seek(Duration.seconds(0));
                    mediaPlayer.stop();
                    playTimer.cancel();
                    running = false;
                    playButton.setVisible(true);
                }
            }
        };
        playTimer.scheduleAtFixedRate(task, 0, 1000);
    }
}
