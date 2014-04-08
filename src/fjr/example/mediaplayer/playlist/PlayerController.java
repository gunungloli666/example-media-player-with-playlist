package fjr.example.mediaplayer.playlist;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;


import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 *
 * @author fajar
 */
public class PlayerController extends AnchorPane implements Initializable {

    @FXML
    Button playButton;
    @FXML
    Button pauseButton;
    @FXML
    Button stopButton;
    @FXML
    Button nextButton;
    @FXML
    Button prevButton;

    @FXML
    Button shuffleButton;
    @FXML
    Button repeatButton;

    @FXML
    ListView<File> playListView;

    FileChooser chooser;
    
    MediaPlayer media; 

    FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter(
            "PNG FIle Files", "*.mp3");

    List<File> listFile;

    Stage mainStage;

    File fileToOpen;

    Group root;

    @FXML Slider sliderVolume; 
    
    @FXML Slider sliderProgres; 
    
    AudioSpectrumListener audioListener; 
    
    XYChart.Data<String, Number> dataChart[];
    
    @FXML BarChart<String, Number> barChart;
    
    @FXML CategoryAxis xAxis;
    
    @FXML NumberAxis yAxis;
    
    
    boolean animationRunning = false;
    
    Timeline animasi; 
    
    ObservableList<File> listObservableFile; 
    
    int currentIndex= 0 ; 
    boolean continuePlaying = true; 
    
    
    HashMap<File, Integer > mapFile;
    
//    XYChart.Data<String, Number> series; 
    
    public PlayerController(Group root) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Player.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
        root.getChildren().add(this);
        this.root = root;
        this.mainStage = (Stage) root.getScene().getWindow();
    }

    @FXML
    public void onPlayPressed() {
        if(listObservableFile != null){
            File f = listObservableFile.get(currentIndex);
            playFile(f);
        }
    }

    @FXML
    public void onPausePressed() {

    }

    @FXML
    public void onStopPressed() {

    }

    @FXML
    public void onOpenPressed() {
        listFile = chooser.showOpenMultipleDialog(mainStage);
        if(listFile!= null && listFile.size()>0){
            listObservableFile = FXCollections.observableArrayList(listFile); 
            playListView.setItems(listObservableFile);
            
            mapFile = new HashMap<>();
            int index = 0;
            for (File f : listObservableFile) {
                mapFile.put(f, index++);
            }

        }
    }

    
    public void checkNextFile(){
        if(listObservableFile != null){
            
        }
    }
    
    
    private class CurrentPlayList{
        private ObservableList<File> listFile;
        
        public CurrentPlayList(ObservableList<File> listFile){
            this.listFile = listFile; 
        }
        
    }
    
    
    public void playFile(File f){
        if(media!=null){
            media.stop();
        }
        String URI = f.toURI().toString();
        Media m = new Media(URI);
        media = new MediaPlayer(m);
        media.setVolume(sliderVolume.getValue()/100.);
        
        
        // sesuaikan Posisi slider dengan posisi bagian mp3 yang dimainkan...
        media.currentTimeProperty().addListener(new ChangeListener<Duration>(){
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                double waktuPutar = media.getTotalDuration().toMillis();
                double waktuSekarang = media.getCurrentTime().toMillis();
                double persentasi = (waktuSekarang/waktuPutar) *100;
                sliderProgres.setValue(persentasi);
            }
        });
        
        media.setAudioSpectrumListener(new AudioSpectrumListener() {
            {
                audioListener = this;
            }
            
            @Override
            public void spectrumDataUpdate(double d, double d1, float[] magnitudo, float[] fasa) {
               for(int i=0; i< dataChart.length; i++){
                    dataChart[i].setYValue(magnitudo[i*2]+60);
               }
            }
        });
        
        media.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if(continuePlaying){
                    int  index = mapFile.get(f); 
                    int indexplussatu = index + 1; 
                    if(indexplussatu  < listObservableFile.size()){
                        playFile(listObservableFile.get(indexplussatu));
                        currentIndex = indexplussatu; 
                    }else{
                        playFile(listObservableFile.get(0));
                        currentIndex = 0;
                    }
                }
            }
        });
        media.play();
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert playButton != null : "playButton gagal injeksi"; // pastikan VM argumen -ea untuk memastikan saat compile time bahwa  eror bukan karena gagal injek
        assert playListView != null : "playListView null";
        assert  sliderProgres != null : "slider progress gagal injek"; 
        assert  sliderVolume != null : "slider volume gagal injeksi"; 
        assert  barChart!= null: "bar chart gagal injek"; 
        assert  xAxis != null : "x axis null.. gagal Injek"; 
        assert  yAxis != null : "y axis null... gagal injek"; 
        
        sliderVolume.setValue(20);
                
        sliderVolume.valueProperty().addListener((ObservableValue<? extends Number> observable, 
                Number oldValue, Number newValue) -> {
            if(media != null){
                media.setVolume(sliderVolume.getValue()/100.);
            }
        });
       
        // Rubah posisi mp3 yang dimainkan sesuai dengan perubahan posisi slider... 
        sliderProgres.valueProperty().addListener((Observable observable) -> {
            if (sliderProgres.isValueChanging()) {
                if (media != null) {
                    double durasi = media.getMedia().getDuration().toMillis();
                    durasi = durasi * (sliderProgres.getValue() / 100);
                    media.seek(Duration.millis(durasi));
                }
            }
        });
        
        chooser = new FileChooser();
        chooser.setInitialDirectory(new File("F:/"));
        chooser.getExtensionFilters().add(filter2);
        playListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        playListView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
            @Override
            public ListCell<File> call(ListView<File> lstFile) {
                return new ListCell<File>() {
                    FlowPane flow;
                    File f;
                    {
                        flow = new FlowPane();
                        flow.setHgap(1);
                        flow.setOrientation(Orientation.HORIZONTAL);
                    }

                    @Override
                    public void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            flow.getChildren().clear();
                            this.f = item; 
                            String name[] = item.getName().split("");
                            for (String nameFile : name) {
                                Text t = new Text(nameFile);
                                flow.getChildren().add(t);
                            }
                            setText(null);
                            setGraphic(flow);
                        }
                    }

                    {// respon mouse
                        flow.setOnMouseClicked((javafx.scene.input.MouseEvent event) -> {
                            if(f!= null){
                                int index = mapFile.get(f); 
                                currentIndex = index; 
                                playFile(f);
                            }
                        });
                    }
                };
            }
        });
        dataChart = new XYChart.Data[64]; 
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.setName("");
        for(int i=0; i< dataChart.length; i++){
            dataChart[i] = new XYChart.Data<String, Number>(Integer.toString(i),0); 
            series.getData().add(dataChart[i]); 
        }
        barChart.getData().add(series);
        barChart.setAnimated(false);
    }
    
    public void animated(FlowPane pane){
        
    }
}
