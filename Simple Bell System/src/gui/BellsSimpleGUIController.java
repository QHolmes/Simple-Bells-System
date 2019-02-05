/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dataStructures.EventSegment;
import dataStructures.SegmentType;
import dataStructures.SoundFile;
import dataStructures.schedules.ScheduledEvent;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
import helperClasses.Helper;
import helperClasses.Save;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Quinten Holmes
 */
public class BellsSimpleGUIController implements Initializable {

    @FXML private Button loadWeek;
    @FXML private Button saveWeek;
    @FXML private Button addMusicFiles;
    @FXML private Button addBellSound;
    @FXML private Button stopBells;
    @FXML private Button saveDay;
    @FXML private Button loadDay;
    @FXML private Button nextWeek;
    @FXML private Button prevWeek;
    @FXML private Button newEvent;
    @FXML private Button loadEvent;
    @FXML private Button saveEvent;
    @FXML private Button deleteEvent;
    @FXML private Button addEvent;
    @FXML private Button messageButton;
    @FXML private Button templateDelete;
    @FXML private Button templateSelect;
    @FXML private Button templateDefault;
    @FXML private Button templateClose;
    @FXML private Button saveClose;
    @FXML private Button saveButton;
    
    @FXML private MenuButton dayOfWeek;
    @FXML private MenuItem monday;
    @FXML private MenuItem tuesday;
    @FXML private MenuItem wednesday;
    @FXML private MenuItem thursday;
    @FXML private MenuItem friday;
    @FXML private MenuItem saturday;
    @FXML private MenuItem sunday;
    
    @FXML private ComboBox hourEvent;
    @FXML private ComboBox minEvent;
    @FXML private ComboBox secEvent;
    @FXML private ComboBox templateCombo;
   
    @FXML private SplitMenuButton ringBell;
    
    @FXML private TableView<ScheduledEvent> eventTable;
    @FXML private TableColumn<ScheduledEvent, String> startColumn;
    @FXML private TableColumn<ScheduledEvent, String> endColumn;    
    
    @FXML private Label dayOfWeekLabel;
    @FXML private Label dateLabel;
    @FXML private Label messageLabel;
    @FXML private Label templateLabel;
    @FXML private Label saveLabel;
    @FXML private Label timeLabel;
    
    @FXML private TextField saveField;
    
    @FXML private ScrollPane eventInfo;
    
    @FXML private HBox messagePane;
    @FXML private HBox templatePane;
    @FXML private HBox savePane;
    
    private final String timePattern = "hh:mm:ss a";
    private final String timeLabelPattern = "hh:mm:ss a";
    private final String dayPattern = "MMM dd yyyy";
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dayFormat;
    private final SimpleDateFormat timeLabelFormat;
    private ScheduledEvent editingEvent;
    private TemplateType templateType;
    private Stage stage;
    
    GUICore core;
    private final String FILEPATH = "D:\\Users\\dholmes\\OneDrive\\Music\\OneRepublic\\Native\\01 Counting Stars.m4a";

    
    public BellsSimpleGUIController(Stage stage){
        timeFormat = new SimpleDateFormat(timePattern);
        dayFormat = new SimpleDateFormat(dayPattern);
        timeLabelFormat = new SimpleDateFormat(timeLabelPattern);
        this.stage = stage;
        
        try{
            GUICore loadedCore = (GUICore) Save.loadObject("GUICore");
            if(loadedCore != null)
                core = loadedCore;
            else
                core = new GUICore();
        }catch (Exception e){
            core = new GUICore();
        }
        
        core.initialize();
        
        /*
        if(core.bellSounds.isEmpty()){
            SoundFile song = new SoundFile(FILEPATH, "Test Song");
            core.bellSounds.add(song);
            core.musicFiles.add(song);
            core.defaultBell = song;
        }
        */
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     //----------------------
     //--------Message-------
     //----------------------
        messageButton.setOnAction((ActionEvent e) -> {
            Platform.runLater(() -> { 
                messagePane.setVisible(false);
            });  
        });
        
     //----------------------
     //---Player Controll----
     //----------------------  
        stopBells.setOnAction((ActionEvent e) -> {
            core.region.stopMedia();
            message("Bells stopped.");
        });
     
     //----------------------
     //-------Add Files------
     //---------------------- 
        addMusicFiles.setOnAction((ActionEvent e) -> {
            addMusic();
        });
        
        addBellSound.setOnAction((ActionEvent e) -> {
            addBells();
        });
     //----------------------
     //-----Week Editing-----
     //----------------------
        nextWeek.setOnAction((ActionEvent e) -> {
            core.incWeek();
            setDay();
        });
        
        prevWeek.setOnAction((ActionEvent e) -> {
            core.decWeek();
            setDay();
        });
        
        
     //----------------------
     //-----Event Editing----
     //----------------------
        getComboBoxRange(24, hourEvent);
        getComboBoxRange(60, minEvent);
        getComboBoxRange(60, secEvent);
        
        hourEvent.setOnAction((Event event1) -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(editingEvent.getStartTime());
            cal.set(Calendar.HOUR_OF_DAY, (int) hourEvent.getValue());
            editingEvent.setStartTime(cal.getTime());
        });
        
        minEvent.setOnAction((Event event1) -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(editingEvent.getStartTime());
            cal.set(Calendar.MINUTE, (int) minEvent.getValue());
            editingEvent.setStartTime(cal.getTime());
        });
        
        secEvent.setOnAction((Event event1) -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(editingEvent.getStartTime());
            cal.set(Calendar.SECOND, (int) secEvent.getValue());
            editingEvent.setStartTime(cal.getTime());
        });
        
        newEvent.setOnAction((ActionEvent e) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            ScheduledEvent event = new ScheduledEvent(cal.getTime());
            displayEvent(event, true);
        });
        
        addEvent.setOnAction((ActionEvent e) -> {
            addEvent();
        });
        
        deleteEvent.setOnAction((ActionEvent e) -> {
            removeEvent();
        });
        
        
     //----------------------
     //-----Day Selection----
     //----------------------        
        sunday.setOnAction((ActionEvent event) -> {
            core.setDay(1);
            setDay();
        });
        monday.setOnAction((ActionEvent event) -> {
            core.setDay(2);
            setDay();
        });
        tuesday.setOnAction((ActionEvent event) -> {
            core.setDay(3);
            setDay();
        });
        wednesday.setOnAction((ActionEvent event) -> {
            core.setDay(4);
            setDay();
        });
        thursday.setOnAction((ActionEvent event) -> {
            core.setDay(5);
            setDay();
        });
        friday.setOnAction((ActionEvent event) -> {
            core.setDay(6);
            setDay();
        });
        saturday.setOnAction((ActionEvent event) -> {
            core.setDay(7);
            setDay();
        });
        
     //----------------------
     //------Table Setup-----
     //----------------------
        eventTable.setPlaceholder(new Label("No events scheduled"));
        eventTable.setRowFactory(tv -> {
            TableRow<ScheduledEvent> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ScheduledEvent rowData = row.getItem();
                    displayEvent(rowData, false);
                }
            });
            return row ;
        });
                
        startColumn.setCellValueFactory(data -> {
            try{
                Date date = data.getValue().getStartTime();
                return new SimpleStringProperty(timeFormat.format(date));
            }catch (NullPointerException ex){
                return new SimpleStringProperty("");
            }
        });
        
        endColumn.setCellValueFactory(data -> {
            try{
                Date date = data.getValue().getStopTime();
                return new SimpleStringProperty(timeFormat.format(date));
            }catch (NullPointerException ex){
                return new SimpleStringProperty("");
            }
        });
        
        setDay();
        
     //----------------------
     //----Template Setup----
     //----------------------   
        templateDelete.setOnAction((ActionEvent event) -> {
            deleteTemplate();
        });
        templateSelect.setOnAction((ActionEvent event) -> {
            selectTemplate();
        });
        templateDefault.setOnAction((ActionEvent event) -> {
            defaultTemplate();
        });
        templateClose.setOnAction((ActionEvent event) -> {
            closeTemplate();
        });
        loadDay.setOnAction((ActionEvent event) -> {
            openTemplate(TemplateType.DAY);
        });
        loadEvent.setOnAction((ActionEvent event) -> {
            openTemplate(TemplateType.EVENT);
        });
        loadWeek.setOnAction((ActionEvent event) -> {
            openTemplate(TemplateType.WEEK);
        });
        saveClose.setOnAction((ActionEvent event) -> {
            closeSave();
        });
        saveButton.setOnAction((ActionEvent event) -> {
            acceptSave();
        });
        saveDay.setOnAction((ActionEvent event) -> {
            openSave(TemplateType.DAY);
        });
        saveWeek.setOnAction((ActionEvent event) -> {
            openSave(TemplateType.WEEK);
        });
        saveEvent.setOnAction((ActionEvent event) -> {
            openSave(TemplateType.EVENT);
        });
       
     //----------------------
     //------Time Label------
     //---------------------- 
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timeUpdate(), 0, 50);
        
     //----------------------
     //-----Quick Bells------
     //---------------------- 
        ringBell.setOnAction((ActionEvent event) -> {
            ringBell();
        });
        updateBell();
    }    
    
    private void setDay(){      
        displayEvent(null, false);
        Platform.runLater(() -> { 
            
            eventTable.getItems().clear();
            eventTable.getItems().addAll(core.getCurrentDay().getEvents());
            dayOfWeekLabel.setText(Helper.dayOfWeekName(core.currentDayOfWeek));
            dateLabel.setText(dayFormat.format(core.getCurrentDay().getDay()));
        });                
    }
    
    private void updateTable(){
        Platform.runLater(() -> { 
            eventTable.getItems().clear();
            eventTable.getItems().addAll(core.getCurrentDay().getEvents());
        });
    }
    
    private void addEvent(){
        Calendar cal = Calendar.getInstance();
            cal.setTime(editingEvent.getStartTime());
            cal.set(Calendar.HOUR_OF_DAY, (int) hourEvent.getValue());
            cal.set(Calendar.MINUTE, (int) minEvent.getValue());
            cal.set(Calendar.SECOND, (int) secEvent.getValue());
        
        Date day = cal.getTime();
            
        editingEvent.setStartTime(cal.getTime());
        Object o = core.getCurrentDay();
        boolean b =  core.getCurrentDay().addEvent(editingEvent);
        
        if(b == true){
        Platform.runLater(() -> { 
            eventTable.getItems().add(editingEvent);
        });
        
        displayEvent(editingEvent, false);
        
        }else{
            message("The event could not be added, please check for time conflicts.");
        }
    } 
    
    private void displayEvent(ScheduledEvent event, boolean newEvent){
        editingEvent = event;
        
        if(event == null){
            Platform.runLater(() -> { 
                eventInfo.setContent(null);
                hourEvent.setDisable(true);
                minEvent.setDisable(true);
                secEvent.setDisable(true);
                deleteEvent.setDisable(true);
                addEvent.setDisable(true);
                saveEvent.setDisable(true);
            }); 
            return;
        }
        
        GridPane grid = new GridPane();
            grid.setVgap(10);
            grid.setHgap(20);
            grid.setPadding(new Insets(10, 10, 10, 10));
        
            Label type = new Label("Segment Type");
            Label selectionL = new Label("File");
            Label durationL = new Label("Duration (seconds)");
            
            grid.addRow(0, type, selectionL, durationL);
            
        int rowCount = 0;
        for(int i = 0; i < event.getSegments().size(); i++){
            ComboBox selection = getEventTypeSelection();
            EventSegment seg = event.getSegments().get(i);
            
            
            ComboBox files = new ComboBox();
            files.setOnAction((Event event1) -> {
                seg.setFile((SoundFile) files.getValue());
                updateTable();
            });
            
            
            TextField duration = new TextField();
            duration.setText(String.format("%d",(int) Math.ceil(seg.getDuration())));
            duration.textProperty().addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
               String s = newValue;
                s = s.replaceAll("[^\\d]", "");
                duration.setText(s);   
                try{seg.setDuration(Integer.parseInt(s));}catch(Exception e){}
                updateTable();
            });
            
            selection.setOnAction((Event event1) -> {
                String st = selection.getValue().toString();
                files.getItems().clear();
                
                switch(st.toLowerCase()){
                    case ("music"):
                        Platform.runLater(() -> { 
                            files.getItems().addAll(core.musicFiles);
                            files.setDisable(false);
                            seg.setType(SegmentType.SOUND);
                            if(seg.getFile() == null)
                                seg.setFile(core.musicFiles.iterator().next());
                        });
                        break;
                    case ("bell"):
                        Platform.runLater(() -> { 
                            files.getItems().addAll(core.bellSounds);
                            files.setDisable(false);
                            seg.setType(SegmentType.SOUND);
                            if(seg.getFile() == null)
                                seg.setFile(core.defaultBell);
                        });
                        break;
                    case ("silence"):
                        Platform.runLater(() -> { 
                            files.getItems().clear();
                            files.setDisable(true);
                            seg.setType(SegmentType.SILENCE);
                        });
                        break;
                    default:
                        
                }
                updateTable();                
            });
            
            String segType;
            switch(seg.getType()){
                case SOUND:
                    segType = "Music";
                    files.getItems().addAll(core.musicFiles);
                    files.setDisable(false);
                    files.setValue(seg.getFile());
                    break;
                case BELL:
                    files.getItems().addAll(core.bellSounds);
                    files.setDisable(false);
                    files.setValue(seg.getFile());
                    segType = "Bell";
                    break;
                default:
                    segType = "Silence";
                    files.setDisable(true);
            }
            
            selection.setValue(segType);
            
           Button rmSection = new Button("-");
           
           rmSection.setOnAction((ActionEvent event1)-> {
               event.removeSegment(seg);
               displayEvent(event, newEvent);
               updateTable();               
            });
           
           
           grid.addRow(i + 1, selection, files, duration, rmSection);
           rowCount = i + 1;
        }//End for loop
        
       Button addSection = new Button("+");
       addSection.setOnAction((ActionEvent event1)-> {
           EventSegment newSeg = new EventSegment(SegmentType.BELL, core.defaultBell, 3);
           event.addSegment(newSeg);
           displayEvent(event, newEvent);
           updateTable();
        });
       
       grid.add(addSection, 3, rowCount + 1);
       
        Calendar cal = Calendar.getInstance();
        cal.setTime(event.getStartTime());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        
        Platform.runLater(() -> { 
            eventInfo.setContent(grid);
            
            hourEvent.setDisable(!newEvent);
                hourEvent.setValue(hour);
            minEvent.setDisable(!newEvent);
                minEvent.setValue(min);
            secEvent.setDisable(!newEvent);
                secEvent.setValue(sec);
                
            deleteEvent.setDisable(false);
            
            addEvent.setDisable(!newEvent);  
            saveEvent.setDisable(!newEvent);
        }); 
    }
    
    
    private ComboBox getComboBoxRange(int range, ComboBox box){
        box.getItems().clear();
        for(int i = 0; i < range; i++)
            box.getItems().add(i);
        
        return box;
    }
    
    private ComboBox getEventTypeSelection(){
        ComboBox box = new ComboBox();
        box.getItems().addAll(
                "Silence",
                "Music",
                "Bell"
        );
        
        return box;
    }
    
    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }

    public void shutDown() {
        
    }

    private void removeEvent() {
        editingEvent.cancelEvent();
        core.getCurrentDay().removeEvent(editingEvent);
        setDay();
        
        editingEvent = null;
        
        displayEvent(null, false);
    }
    
    private void message(String m){
        Platform.runLater(() -> { 
            messageLabel.setText(m);
            messagePane.setVisible(true);
        });  
        System.out.println("Message: " + m);
    }
    
    private void openTemplate(TemplateType type){
        templateType = type;
        Platform.runLater(() -> { 
            templateDefault.setVisible(type == TemplateType.WEEK);
            templateCombo.getItems().clear();
            switch(templateType){
            case WEEK:
                templateCombo.getItems().addAll(core.region.getWeekTemplates());
                templateLabel.setText("Week Templates");
                break; 
            case DAY:
                templateCombo.getItems().addAll(core.region.getDayTemplates());
                templateLabel.setText("Day Templates");
                break;
            case EVENT:
                templateCombo.getItems().addAll(core.region.getEventTemplates());
                templateLabel.setText("Event Templates");
                break;
            }
            
            templatePane.setVisible(true);
        });
    }
    
    private void closeTemplate(){
        Platform.runLater(() -> { 
            templatePane.setVisible(false);
        });
    }

    private void selectTemplate() {
        Object ob = templateCombo.getValue();
        if(ob == null)
            return;
        
        try{
        switch(templateType){
            case WEEK:
                WeekTemplate tempWeek = (WeekTemplate) ob;
                core.region.addScheduledWeek(tempWeek.createScheduledWeek(core.currentWeek, core.currentYear));
                break; 
            case DAY:
                DayTemplate tempDay = (DayTemplate) ob;
                core.getSelectedWeek().setDay(tempDay.createScheduledDay(core.getCurrentDay().getDay()));
                break;
            case EVENT:
                EventTemplate tempEvent = (EventTemplate) ob;
                displayEvent(tempEvent.createScheduledEvent(new Date()), true);
                break;
        }
        }catch(ClassCastException e){
            e.printStackTrace();
        }
        
        closeTemplate();
        setDay();
    }

    private void deleteTemplate() {
        Object ob = templateCombo.getValue();
        if(ob == null)
            return;
        
        try{
        switch(templateType){
            case WEEK:
                core.region.removeTemplateWeek((WeekTemplate) ob);
                break; 
            case DAY:
                core.region.removeTemplateDay((DayTemplate) ob);
                break;
            case EVENT:
                core.region.removeTemplateEvent((EventTemplate) ob);
                break;
        }
        }catch(ClassCastException e){
            e.printStackTrace();
        }
        
        closeTemplate();
    }

    private void defaultTemplate() {
        if(templateType != TemplateType.WEEK)
            return;
        
        WeekTemplate w = (WeekTemplate) templateCombo.getValue();
        if(w != null)
            core.setDefaultWeek(w);
        
        closeTemplate();
    }
    
    private void openSave(TemplateType type){
        templateType = type;
        Platform.runLater(() -> { 
            saveField.setText("");
            switch(templateType){
                case WEEK:
                    saveField.setText("Week " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                    templateLabel.setText("Save Week");
                    break; 
                case DAY:
                    saveField.setText("Day " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                    templateLabel.setText("Save Day");
                    break;
                case EVENT:
                    if(editingEvent == null)
                        return;
                    
                    saveField.setText("Event " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                    templateLabel.setText("Save Event");
                    break;
                }
            
            savePane.setVisible(true);
        });
    }
    
    private void acceptSave(){
        switch(templateType){
            case WEEK:
                WeekTemplate w = core.getSelectedWeek().getTemplate();
                w.setWeekName(saveField.getText());
                core.region.addTemplateWeek(w);
                break; 
            case DAY:
                DayTemplate d = core.getCurrentDay().createTemplate();
                d.setName(saveField.getText());
                core.region.addTemplateDay(d);
                break;
            case EVENT:
                if(editingEvent != null){                
                    EventTemplate e = editingEvent.createTemplate();
                    e.setEventName(saveField.getText());
                    core.region.addTemplateEvent(e);
                }
                break;
        }
        
       closeSave();
    }
    
    private void closeSave(){
        Platform.runLater(() -> { 
            savePane.setVisible(false);
        });
    }
    
    private void addMusic(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Music Files");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aif", "*.aiff", "*.AAC", "*.pcm")
        );
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        
        SoundFile s;
        if(list != null){
            for(File f: list){
                s = new SoundFile(f);
                core.musicFiles.add(s);
            }
            setDay();
        }
    }
    
    private void addBells(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Bell Sound Files");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aif", "*.aiff", "*.AAC", "*.pcm")
        );
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        
        SoundFile s;
        if(list != null){
            for(File f: list){
                s = new SoundFile(f);
                core.bellSounds.add(s);
            }
            setDay();
            updateBell();
        }
    }
    
    private void updateBell(){
        Platform.runLater(() -> { 
            MenuItem item;
            ringBell.getItems().clear();
            for(SoundFile f: core.bellSounds){
                item = new MenuItem(f.toString());
                 item.setOnAction((ActionEvent event) -> {
                    core.defaultBell = f;
                });
                 ringBell.getItems().add(item);
            }
        });
    }
    
    private void ringBell(){
        if(core.defaultBell != null)
            core.region.quickPlay(core.defaultBell, core.defaultBell.getPlayTime());
        else
            message("No bell sound has been selected to play");
    }
    
    private TimerTask timeUpdate(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                Platform.runLater(() -> { 
                    timeLabel.setText(timeLabelFormat.format(new Date()));
                });
            }
        };
        
        return th;
    } 
    
    
}
