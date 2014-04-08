/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fjr.example.mediaplayer.playlist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 *
 * @author fajar
 */
public class MediaPLayerWithPlayList extends Application {

    MediaPlayer mp;

    double width = 500;
    double height = 500;

    Group root;

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        this.root = root;
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(root));
        PlayerController player = new PlayerController(root);
        primaryStage.show();
    }

    public MediaPlayer getMediaPlayer() {
        Media media = new Media(getClass().getResource("resource/taylor.mp3").toExternalForm());
        mp = new MediaPlayer(media);
        return mp;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
