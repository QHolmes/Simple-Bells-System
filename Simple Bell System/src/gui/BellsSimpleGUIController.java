/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dataStructures.EventSegment;
import dataStructures.PlayList;
import dataStructures.SegmentType;
import dataStructures.SoundFile;
import dataStructures.schedules.ScheduledEvent;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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

    @FXML private Button addSoundFile;   
    @FXML private Button stopBells;
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
    @FXML private Button soundClose;
    @FXML private Button soundDelete;
    @FXML private Button soundDefault;
    @FXML private Button ringBell;
    @FXML private Button playNew;
    @FXML private Button playDelete;
    @FXML private Button playClose;
    @FXML private Button deleteYes;
    @FXML private Button deleteNo;
    
    @FXML private MenuItem loadWeek;
    @FXML private MenuItem saveWeek;
    @FXML private MenuItem saveEventM;
    @FXML private MenuItem nextWeek;
    @FXML private MenuItem prevWeek;
    @FXML private MenuItem saveDay;
    @FXML private MenuItem loadDay;
    @FXML private MenuItem editBells;
    @FXML private MenuItem editMusic;
    @FXML private MenuItem editPlayList;
    
    @FXML private MenuItem nextDay;
    @FXML private MenuItem prevDay;
    
    @FXML private ComboBox hourEvent;
    @FXML private ComboBox minEvent;
    @FXML private ComboBox secEvent;
    @FXML private ComboBox templateCombo;
    @FXML private ComboBox playCombo;
   
    
    @FXML private TableView<ScheduledEvent> eventTable;
    @FXML private TableColumn<ScheduledEvent, String> startColumn;
    @FXML private TableColumn<ScheduledEvent, String> endColumn;   
    @FXML private TableView<SoundFile> soundsTable;
    @FXML private TableColumn<SoundFile, String> soundsColumn;
    @FXML private TableView<SoundFile> playListTable;
    @FXML private TableColumn<SoundFile, String> playListColumn;
    @FXML private TableView<SoundFile> playAllTable;
    @FXML private TableColumn<SoundFile, String> playAllColumn;
    
    @FXML private Label dateLabel;
    @FXML private Label messageLabel;
    @FXML private Label templateLabel;
    @FXML private Label saveLabel;
    @FXML private Label timeLabel;    
    @FXML private Label soundTitleLabel;
    @FXML private Label soundLocationLabel;
    @FXML private Label soundDurationLabel;
    
    @FXML private TextField soundNameField;    
    @FXML private TextField saveField;
    @FXML private TextField playName;
    
    @FXML private ScrollPane eventInfo;
    
    @FXML private HBox messagePane;
    @FXML private HBox templatePane;
    @FXML private HBox savePane;
    @FXML private HBox soundPane;
    @FXML private HBox playPane;
    @FXML private HBox deletePane;
    
    private final String timePattern = "hh:mm:ss a";
    private final String timeDatePattern = "hh:mm:ss a";
    private final String timeLabelPattern = "hh:mm:ss a";
    private final String dayPattern = "EEEE, MMM dd yyyy";
    private final String namingPattern = "yyyy.MM.dd.HH.mm.ss";
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dayFormat;
    private final SimpleDateFormat timeLabelFormat;
    private final SimpleDateFormat nameingFormat;
    private final SimpleDateFormat timeDateFormat;
    private ScheduledEvent editingEvent;
    private SoundFile selectedSound;
    private TemplateType templateType;
    private PlayList playList;
    private Stage stage;
    private boolean deleteChoice;
    private final Object deleteNotice;
    
    GUICore core;
    private final String FILEPATH = "D:\\Users\\dholmes\\OneDrive\\Music\\OneRepublic\\Native\\01 Counting Stars.m4a";

    
    public BellsSimpleGUIController(Stage stage){
        timeFormat = new SimpleDateFormat(timePattern);
        dayFormat = new SimpleDateFormat(dayPattern);
        timeLabelFormat = new SimpleDateFormat(timeLabelPattern);
        nameingFormat = new SimpleDateFormat(namingPattern);
        timeDateFormat = new SimpleDateFormat(timeDatePattern);
        this.stage = stage;
        deleteChoice = false;
        deleteNotice = new Object();
        
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
                core.getLog().log(Level.INFO,"Hiding message window");
            });  
        });
        
     //----------------------
     //---Player Control-----
     //----------------------  
        stopBells.setOnAction((ActionEvent e) -> {
            core.getLog().log(Level.INFO,"Stopping media player.");
            stopMedia();
            core.getLog().log(Level.INFO,"Media player stopped.");
        });
     
     //----------------------
     //--Delete Comfirmation-
     //---------------------- 
        
        setupDeleteComf();
        
     //----------------------
     //-------Add Files------
     //---------------------- 
        setupSounds();
     
     //----------------------
     //-----Week Editing-----
     //----------------------
        nextWeek.setOnAction((ActionEvent e) -> {
            
            boolean b = core.incWeek();
            
            if(b)
                setDay();
            else
                message("Cannot go more then 3 weeks in the future");
        });
        
        prevWeek.setOnAction((ActionEvent e) -> {
            boolean b = core.decWeek();
            
            if(b)
                setDay();
            else
                message("Cannot go into the past");
        });        
        
     //----------------------
     //-----Event Editing----
     //----------------------
        setupEvents();
     //----------------------
     //-----Day Selection----
     //----------------------  
     
        nextDay.setOnAction((ActionEvent event) -> {
            nextDay();
        });
        
        prevDay.setOnAction((ActionEvent event) -> {
            prevDay();
        });
        
        
     //----------------------
     //------Table Setup-----
     //----------------------
        setupTable();
     //----------------------
     //---Play List Setup----
     //---------------------- 
        setupPlayList();
     //----------------------
     //----Template Setup----
     //----------------------   
        setupTemplates();
       
     //----------------------
     //------Time Label------
     //---------------------- 
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timeUpdate(), 0, 50);
        timer.scheduleAtFixedRate(cleanUp(), 0, 360000);
        
        
     //----------------------
     //-----Quick Bells------
     //---------------------- 
        ringBell.setOnAction((ActionEvent event) -> {
            core.getLog().log(Level.INFO,"Quick ring activated");
            ringBell();
        });
        
        setupShortCuts();
        setDay();
    }    
    
    private void setDay(){      
        displayEvent(null, false);
        Platform.runLater(() -> {             
            eventTable.getItems().clear();
            eventTable.getItems().addAll(core.getCurrentDay().getEvents());
            dateLabel.setText(dayFormat.format(core.getCurrentDay().getDay()));
        });                
    }
    
    private void updateEventTable(){
        Platform.runLater(() -> { 
            eventTable.getItems().clear();
            eventTable.getItems().addAll(core.getCurrentDay().getEvents());
        });
    }
    
    private void addEvent(){
        try{
            Calendar cal = Calendar.getInstance();
                cal.setTime(editingEvent.getStartTime());
                int hour = (int) hourEvent.getValue();
                int min = (int) minEvent.getValue();
                int second = (int) secEvent.getValue();

                cal.set(Calendar.HOUR_OF_DAY, (int) hourEvent.getValue());
                cal.set(Calendar.MINUTE, (int) minEvent.getValue());
                cal.set(Calendar.SECOND, (int) secEvent.getValue());

            editingEvent.setStartTime(cal.getTime());
            Object o = core.getCurrentDay();
            boolean b =  core.getCurrentDay().addEvent(editingEvent);

            if(b == true){
                Platform.runLater(() -> { 
                    eventTable.getItems().add(editingEvent);
                });

                displayEvent(editingEvent, false);
                core.save();
                core.getLog().log(Level.INFO,"Adding new event for {0}", timeDateFormat.format(cal.getTime()));
            }else{
                core.getLog().log(Level.WARNING,"New event could not be added at {0}", timeDateFormat.format(cal));
                message("The event could not be added, please check for time conflicts.");
            }
        }catch(Exception e){
            message("Error adding event: " + e.getMessage());
            core.getLog().log(Level.WARNING, "Error adding event: {0}", e.getMessage());
            e.printStackTrace();
        }
    } 
    
    private void displayEvent(ScheduledEvent event, boolean newEvent){
        editingEvent = event;
        
        if(event == null){
            core.getLog().log(Level.INFO,"Clearing displayed event");
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
        
        if(newEvent)
            core.getLog().log(Level.INFO,"Displaying new event...");
        else
            core.getLog().log(Level.INFO,"Displaying event {0}...", timeDateFormat.format(event.getStartTime()));
        
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
                files.setTooltip(new Tooltip());
                ComboBoxAutoComplete temp = new ComboBoxAutoComplete<>(files);
                
            files.setOnAction((Event event1) -> {
                if(seg.getType() == SegmentType.BELL || seg.getType() == SegmentType.SOUND)
                    seg.setFile((SoundFile) files.getValue());
                else if (seg.getType() == SegmentType.PLAYLIST)
                    seg.setPlayList((PlayList) files.getValue());
                
                updateEventTable();
            });
            
            
            TextField duration = new TextField();
            duration.setPrefColumnCount(10);
            duration.setText(String.format("%d",(int) Math.ceil(seg.getDuration())));
            
            duration.textProperty().addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
               String s = newValue;
               if(s.length() <= 9)
                   s = s.replaceAll("[^\\d]", "");
               else
                   s = oldValue;
               
                  
                try{
                    int num = Integer.parseInt(s);
                    seg.setDuration(num);
                    if(num == 0){
                        s = String.format("%d", (int) Math.ceil(seg.getDuration()));
                    }
                } catch(Exception e){
                    core.getLog().log(Level.WARNING, "Failed to change duration: {0}", e.getMessage());
                }
                
                duration.setText(s); 
                updateEventTable();
            });
            
            selection.setOnAction((Event event1) -> {
                try{
                    String st = selection.getValue().toString();
                    files.getItems().clear();

                    switch(st.toLowerCase()){
                        case ("music"):
                            Platform.runLater(() -> { 
                                files.getItems().addAll(core.getMusicFiles());
                                temp.updateOrginial(core.getMusicFiles());
                                files.setDisable(false);
                                seg.setType(SegmentType.SOUND);
                                if(seg.getFile() == null)
                                    seg.setFile(core.getMusicFiles().iterator().next());
                                files.setValue(seg.getFile());
                            });
                            break;
                        case ("bell"):
                            Platform.runLater(() -> { 
                                files.getItems().addAll(core.getBellSounds());
                                temp.updateOrginial(core.getBellSounds());
                                files.setDisable(false);
                                seg.setType(SegmentType.SOUND);
                                if(seg.getFile() == null)
                                    seg.setFile(core.defaultBell);
                                files.setValue(seg.getFile());
                            });
                            break;
                        case ("play list"):
                            Platform.runLater(() -> { 
                                files.getItems().addAll(core.playLists);
                                temp.updateOrginial(core.playLists);
                                files.setDisable(false);
                                seg.setType(SegmentType.PLAYLIST);
                                if(seg.getPlayList() == null){
                                    if(core.playLists.isEmpty())
                                        files.setValue(null);
                                    else{
                                        seg.setPlayList(core.playLists.iterator().next());
                                        files.setValue(seg.getPlayList());
                                    }
                                }else{
                                    files.setValue(seg.getPlayList());
                                }
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
                    updateEventTable();       
                }catch(Exception e){
                    core.getLog().log(Level.INFO, "Error changing event type: {0}", e.getMessage());
                }
            });
            
            String segType;
            switch(seg.getType()){
                case SOUND:
                    segType = "Music";
                    files.getItems().addAll(core.getMusicFiles());
                    temp.updateOrginial(core.getMusicFiles());
                    files.setDisable(false);
                    files.setValue(seg.getFile());
                    break;
                case PLAYLIST:
                    segType = "Play List";
                    files.getItems().addAll(core.playLists);
                    temp.updateOrginial(core.playLists);
                    files.setDisable(false);
                    files.setValue(seg.getPlayList());
                    break;
                case BELL:
                    files.getItems().addAll(core.getBellSounds());
                    temp.updateOrginial(core.getBellSounds());
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
               try{
                event.removeSegment(seg);
                displayEvent(event, newEvent);
                updateEventTable(); 
               }catch(Exception e){
                   core.getLog().log(Level.WARNING, "Failed to remove section: {0}", e.getMessage());
               }
            });
           
           
           grid.addRow(i + 1, selection, files, duration, rmSection);
           rowCount = i + 1;
        }//End for loop
        
       Button addSection = new Button("+");
       addSection.setOnAction((ActionEvent event1)-> {
           try{
                EventSegment newSeg = new EventSegment(SegmentType.BELL, core.defaultBell, 3);
                event.addSegment(newSeg);
                displayEvent(event, newEvent);
                updateEventTable();
           }catch(Exception e){
               core.getLog().log(Level.WARNING, "Failed to add a new section: {0}", e.getMessage());
           }
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
            saveEvent.setDisable(false);
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
                "Play List",
                "Bell"
        );        
        return box;
    }

    public void shutDown() {
        core.getLog().log(Level.INFO, "Safely shutting down...");
        core.save();
    }

    private void removeEvent() {
        try{
            editingEvent.cancelEvent();
            core.getCurrentDay().removeEvent(editingEvent);
            setDay();

            editingEvent = null;

            displayEvent(null, false);
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Error removing event: {0}", e.getMessage());
        }
    }
    
    private void message(String m){
        Platform.runLater(() -> { 
            messageLabel.setText(m);
            messagePane.setVisible(true);
        });  
        core.getLog().log(Level.INFO,"Message being displayed: \"{0}\"", m);
    }
    
    private void openTemplate(TemplateType type){
        templateType = type;
        Platform.runLater(() -> { 
            try{
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
            } catch(Exception e){
                core.getLog().log(Level.WARNING, "Failed to open template Window: {0}", e.getMessage());
            }
            core.getLog().log(Level.INFO, "Template window opening [{0}]", type.toString());
        });
    }
    
    private void closeTemplate(){
        Platform.runLater(() -> { 
            templatePane.setVisible(false);
            core.getLog().log(Level.INFO, "template Window closed");
        });
        core.save();
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
                    setDay();
                    break; 
                case DAY:
                    DayTemplate tempDay = (DayTemplate) ob;
                    core.getSelectedWeek().setDay(tempDay.createScheduledDay(core.getCurrentDay().getDay()));
                    setDay();
                    break;
                case EVENT:
                    EventTemplate tempEvent = (EventTemplate) ob;
                    displayEvent(tempEvent.createScheduledEvent(new Date()), true);
                    break;
            }

            closeTemplate();
            core.getLog().log(Level.INFO, "Template selected: {1} [{0}]", new Object[] {ob.toString(), templateType.toString()});
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Failed to select Template: {0}", e.getMessage());
            message("Failed to open template: " + e.getMessage());
        }
        
    }

    private void deleteTemplate() {
        Thread th = new Thread(){
            @Override
            public void run(){
                Object ob = templateCombo.getValue();
                if(ob == null)
                    return;

                if(!deleteComfirmation())
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
                }catch(Exception e){
                    core.getLog().log(Level.WARNING, "Failed to delete Template: {0}", e.getMessage());
                    message("Failed to delete template: " + e.getMessage());
                }

                openTemplate(templateType);
                core.getLog().log(Level.INFO, "Deleted template: {1} [{0}]", new Object[] {ob.toString(), templateType.toString()});
            }
        }; 
        
        th.setDaemon(true);
        th.start();
    }

    private void defaultTemplate() {
        if(templateType != TemplateType.WEEK)
            return;
        
        WeekTemplate w = (WeekTemplate) templateCombo.getValue();
        if(w != null){
            core.setDefaultWeek(w);
            message("Default template now set to: " + w.toString());
            core.getLog().log(Level.INFO, "Default template now set to: {0}", w.toString());
        }
    }
    
    private void openSave(TemplateType type){
        templateType = type;
        core.getLog().log(Level.INFO, "Opening save window [{0}]", type.toString());
        
        Platform.runLater(() -> { 
            try{
                saveField.setText("");
                switch(templateType){
                    case WEEK:
                        saveField.setText("Week " + nameingFormat.format(new Date()));
                        templateLabel.setText("Save Week");
                        break; 
                    case DAY:
                        saveField.setText("Day " + nameingFormat.format(new Date()));
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
            }catch (Exception e){
                core.getLog().log(Level.INFO, "Failed to open save window: {0}", e.getMessage());
            }
        });
    }
    
    private void acceptSave(){
        try{
            Object ob;
            switch(templateType){
                case WEEK:
                    WeekTemplate w = core.getSelectedWeek().getTemplate();
                    ob = w;
                    w.setWeekName(saveField.getText());
                    core.region.addTemplateWeek(w);
                    break; 
                case DAY:
                    DayTemplate d = core.getCurrentDay().createTemplate();
                    ob = d;
                    d.setName(saveField.getText());
                    core.region.addTemplateDay(d);
                    break;
                case EVENT:
                    if(editingEvent != null){                
                        EventTemplate e = editingEvent.createTemplate();
                        ob = e;
                        e.setEventName(saveField.getText());
                        core.region.addTemplateEvent(e);
                    }else{
                        message("There is no event to save");
                        closeSave();
                        return;
                    }
                    break;
                default:
                    message("An unknown error as occured: Type unknown");
                    core.getLog().log(Level.WARNING, "Type unkown trying to save, quitting");
                    closeSave();
                    return;
                    
            }
            
            closeSave();
            core.getLog().log(Level.INFO, "Accepted saved template {1} [{0}]", new Object[] {ob.toString(), templateType.toString()});
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Failed to accept save: {0}", e.getMessage());
        }
        
    }
    
    private void closeSave(){
        Platform.runLater(() -> { 
            savePane.setVisible(false);
        });
        core.getLog().log(Level.INFO, "Closing save");
        core.save();
    }
    
    private void addMusic(){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Add Music Files");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aif", "*.aiff", "*.AAC", "*.pcm")
            );

            core.getLog().log(Level.INFO, "Showing add music file chooser");
            List<File> list = fileChooser.showOpenMultipleDialog(stage);
            core.getLog().log(Level.INFO, "Add music file chooser returned");

            SoundFile s;
            int i = 0;
            boolean b;
            if(list != null){
                for(File f: list){
                    s = new SoundFile(f);
                    b = core.addMusicFile(s);
                    if(b)
                        i++;
                    
                }
                
                if(i > 0)
                    message( i + "/" + list.size() + " new files added. Any duplicates were skipped");
                else
                   message("All selected files were already added"); 
                
                setDay();
                core.save();
            }
            
            core.getLog().log(Level.INFO, "Add music file complete");
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Add music failed: {0}", e.getMessage());
            message("An error occured trying to add new music files: " + e.getMessage());
        }        
    }
    
    private void addBells(){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Add Bell Sound Files");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aif", "*.aiff", "*.AAC", "*.pcm")
            );

            core.getLog().log(Level.INFO, "Showing add bell sounds chooser");
            List<File> list = fileChooser.showOpenMultipleDialog(stage);
            core.getLog().log(Level.INFO, "Add bell sounds chooser returned");

            SoundFile s;
            int i = 0;
            boolean b;
            if(list != null){
                for(File f: list){
                    s = new SoundFile(f);
                    b = core.addBellSound(s);
                    if(b)
                        i++;
                    
                }
                
                if(i > 0)
                    message( i + "/" + list.size() + " new sounds added. Any duplicates were skipped");
                else
                   message("All selected files were already added"); 
                
                setDay();
                core.save();
            }
            
            core.getLog().log(Level.INFO, "Add bell sounds complete");
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Add bell sounds failed: {0}", e.getMessage());
            message("An error occured trying to add new bell sounds: " + e.getMessage());
        } 
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
    
    private TimerTask cleanUp(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                core.region.cleanSchedule();
                core.setupLogger();
            }
        };
        
        return th;
    } 
    
    private void displaySoundFile(SoundFile s){
        try{
            selectedSound = s;

            if(s == null){
                Platform.runLater(() -> { 
                    soundDelete.setDisable(true);
                    soundNameField.setText("");
                    soundLocationLabel.setText("");
                    soundDurationLabel.setText("");
                }); 
                return;
            }

            Platform.runLater(() -> { 
                soundDelete.setDisable(false);
                soundNameField.setText(s.getFileName());
                soundLocationLabel.setText(s.getFilePath());

                try{
                    soundDurationLabel.setText(s.getPlayTime() + " seconds");
                }catch (Exception e){
                    soundDurationLabel.setText("Error " + e.getMessage());
                }
            });
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Error displaying sound file: {0}", e.getMessage());
        }
    }
    
    private void openSound(TemplateType type){
        try{
            core.getLog().log(Level.INFO, "Opening sound window");
            templateType = type; 
            if(type ==null || (type != TemplateType.BELLS && type != TemplateType.MUSIC))
                return;

            updateSoundTable();
            displaySoundFile(null);
            if(type == TemplateType.BELLS){            
                Platform.runLater(() -> { 
                    soundTitleLabel.setText("Bells");
                    soundPane.setVisible(true);
                    soundDefault.setVisible(true);
                }); 
            }else if (type == TemplateType.MUSIC){
                Platform.runLater(() -> { 
                    soundTitleLabel.setText("Music");
                    soundPane.setVisible(true);
                    soundDefault.setVisible(false);
                }); 
            }
        } catch(Exception e){
            core.getLog().log(Level.WARNING, "Error opening sound window: {0}", e.getMessage());
        }
        
    }
    
    private void updateSoundTable(){
        try{
            if(templateType == TemplateType.BELLS)
                Platform.runLater(() -> { 
                    soundsTable.getItems().clear();
                    soundsTable.getItems().addAll(core.getBellSounds());
                }); 
            else if(templateType == TemplateType.MUSIC)
                Platform.runLater(() -> { 
                    soundsTable.getItems().clear();
                    soundsTable.getItems().addAll(core.getMusicFiles());
                }); 
        }catch (Exception e){
           core.getLog().log(Level.WARNING, "Error opening sound window: {0}", e.getMessage()); 
        }
    }
    
    public void openPlayList(){
        try{
            core.getLog().log(Level.INFO, "Opening play list window");
            updatePlayListCombo();
            displayPlayList(null);
            Platform.runLater(() -> { 
                playPane.setVisible(true);
            }); 
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Error opening play list window: {0}", e.getMessage()); 
        }
    }
    
    public void displayPlayList(PlayList list){
        try{
            core.getLog().log(Level.INFO, "Displaying play list");
            playList = list;
            if(list == null){
                Platform.runLater(() -> { 
                    playListTable.getItems().clear();
                    playAllTable.getItems().clear();
                    playName.setText("");
                }); 
            }else{
                Platform.runLater(() -> { 

                    playListTable.getItems().clear();
                    playListTable.getItems().addAll(list.getSoundFiles());

                    playAllTable.getItems().clear();
                    playAllTable.getItems().addAll(core.getMusicFiles());
                    playAllTable.getItems().removeAll(list.getSoundFiles());

                    playName.setText(list.getPlayListName());
                });
            }
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Error trying to display a play list: {0}", e.getMessage()); 
        }
    }
    
    public void updatePlayListCombo(){
        Platform.runLater(() -> { 
            playCombo.getItems().clear();
            playCombo.getItems().addAll(core.playLists);
        }); 
    }
    
    private void addToPlayList(SoundFile file){
        try{
            if(playList == null){
                message("No play list selected.");
                return;
            }
            
            if(file == null){
                message("No song selected.");
                return;
            }
            
            boolean b = playList.addSoundFile(file);
            
            if(b != true){
                message("Song already added");
                return;
            }
            
            displayPlayList(playList);
        }catch (Exception e){
            message("Error adding song to play list: " + e.getMessage());
            core.getLog().log(Level.WARNING, "Error adding song to play list {0}: {1}",
                    new Object[]{playList.getPlayListName(), e.getMessage()}); 
        }
    }
    
    private void removeFromPlayList(SoundFile file){
        try{
            if(playList == null){
                message("No play list selected.");
                return;
            }
            
            if(file == null){
                message("No song selected.");
                return;
            }
            
            boolean b = playList.removeSoundFile(file);
            
            if(b != true){
                message("Song was not able to be removed");
                return;
            }
            
            displayPlayList(playList);
        }catch (Exception e){
            message("Error removing song from play list: " + e.getMessage());
            core.getLog().log(Level.WARNING, "Error removing song from play list {0}: {1}",
                    new Object[]{playList.getPlayListName(), e.getMessage()}); 
        }
    }

    private void stopMedia() {
        core.region.stopMedia();
        message("Bells stopped.");
        core.getLog().log(Level.WARNING, "Bells manually stopped"); 
    }

    private void setupPlayList() {
        playListTable.setPlaceholder(new Label("No Songs Added"));
        playListTable.setRowFactory(tv -> {
            TableRow<SoundFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((! row.isEmpty()) ) { 
                    removeFromPlayList(row.getItem());
                }
            });
            return row ;
        });     
        
        playListColumn.setCellValueFactory(data -> {
            try{
                String file = data.getValue().getFileName();
                return new SimpleStringProperty(file);
            }catch (NullPointerException ex){
                return new SimpleStringProperty("");
            }
        });
        
        playAllTable.setPlaceholder(new Label("No Songs Added"));
        playAllTable.setRowFactory(tv -> {
            TableRow<SoundFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((! row.isEmpty()) ) { 
                    addToPlayList(row.getItem());
                }
            });
            return row ;
        });  
                
        playAllColumn.setCellValueFactory(data -> {
            try{
                String file = data.getValue().getFileName();
                return new SimpleStringProperty(file);
            }catch (NullPointerException ex){
                return new SimpleStringProperty("");
            }
        });
        
        playCombo.setOnAction((Event event1) -> {
            if(playCombo.getValue() != null)
                displayPlayList((PlayList) playCombo.getValue());
            else if(playList != null)
                playCombo.setValue(playList);
        });
        
        playDelete.setOnAction((ActionEvent event) -> {
            deletePlayList();
        });
        
        playNew.setOnAction((ActionEvent event) -> {
            PlayList play = new PlayList(nameingFormat.format(new Date()));
            core.playLists.add(play);
            updatePlayListCombo();
            displayPlayList(play);
        });
        
        playClose.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                playPane.setVisible(false);
            });
            core.save();
        });
        
        editPlayList.setOnAction((ActionEvent e) -> {
            openPlayList();
        });  
        
        playName.textProperty().addListener(
        (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(playList != null){
                playList.setPlayListName(newValue);
                updatePlayListCombo();
                Platform.runLater(() -> {
                    playCombo.setValue(playList);
                });
            }
        });
    }

    private void setupSounds() {
        soundNameField.textProperty().addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                if(selectedSound != null){
                    selectedSound.setFileName(newValue);
                    Platform.runLater(()-> {
                        //Refresh table text
                        soundsTable.getColumns().get(0).setVisible(false);
                        soundsTable.getColumns().get(0).setVisible(true);
                    });
                }
            });
        
        addSoundFile.setOnAction((ActionEvent e) -> {
            
            if(null == templateType)
                message ("An error as occured: Wrong template type");
            
            else switch (templateType) {
                case MUSIC:
                    addMusic();
                    updateSoundTable();
                    break;
                case BELLS:
                    addBells();
                    updateSoundTable();
                    break;
                default:
                    message ("An error as occured: Wrong template type");
                    break;
            }
        });
        
        soundsTable.setPlaceholder(new Label("No files added"));
        soundsTable.setRowFactory(tv -> {
            TableRow<SoundFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((! row.isEmpty()) ) { 
                    SoundFile rowData = row.getItem();
                    displaySoundFile(rowData);
                }
            });
            return row ;
        });
                
        soundsColumn.setCellValueFactory(data -> {
            try{
                String file = data.getValue().getFileName();
                return new SimpleStringProperty(file);
            }catch (NullPointerException ex){
                return new SimpleStringProperty("");
            }
        });
        
        editMusic.setOnAction((ActionEvent e) -> {
            openSound(TemplateType.MUSIC);
        });  
        
        editBells.setOnAction((ActionEvent e) -> {
            openSound(TemplateType.BELLS);
        });  
        soundClose.setOnAction((ActionEvent e) -> {
            Platform.runLater(()-> {
                soundPane.setVisible(false);
            });
            
            displayEvent(editingEvent, false);
            core.save();
        });
        
        soundDelete.setOnAction((ActionEvent e) -> {
            deleteSound();
        });
        
        soundDefault.setOnAction((ActionEvent e) -> {
            if (templateType == TemplateType.BELLS){
                core.defaultBell = selectedSound;
            }
        });
    }

    private void setupEvents() {
        getComboBoxRange(24, hourEvent);
        getComboBoxRange(60, minEvent);
        getComboBoxRange(60, secEvent);
        
        hourEvent.setOnAction((Event event1) -> {
            if(editingEvent == null)
                return;
            Calendar cal = Calendar.getInstance();
            cal.setTime(editingEvent.getStartTime());
            cal.set(Calendar.HOUR_OF_DAY, (int) hourEvent.getValue());
            editingEvent.setStartTime(cal.getTime());
        });
        
        saveEventM.setOnAction((ActionEvent event) -> {
            openSave(TemplateType.DAY);
        });
        
        minEvent.setOnAction((Event event1) -> {
            if(editingEvent == null)
                return;
            Calendar cal = Calendar.getInstance();
            cal.setTime(editingEvent.getStartTime());
            cal.set(Calendar.MINUTE, (int) minEvent.getValue());
            editingEvent.setStartTime(cal.getTime());
        });
        
        secEvent.setOnAction((Event event1) -> {
            if(editingEvent == null)
                return;
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
    }

    private void setupTemplates() {
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
    }
    
    private boolean deleteComfirmation(){
        Platform.runLater(() -> { 
                deletePane.setVisible(true);
            });  
            synchronized(deleteNotice){
                try {
                    deleteNotice.wait();
                } catch (InterruptedException ex) {}
            }
        return deleteChoice;
    }

    private void deleteSound() {
        Thread th = new Thread(){
            @Override
            public void run(){
                try{
                if(selectedSound == null)
                    return;
                
                if(!deleteComfirmation())
                    return;
            
                if(templateType == TemplateType.BELLS){
                    core.removeBellFile(selectedSound);
                    if(core.defaultBell == selectedSound)
                        core.defaultBell = null;
                }else if (templateType == TemplateType.MUSIC){
                    core.removeMusicFile(selectedSound);
                }

                selectedSound = null;
                displaySoundFile(null);
                updateSoundTable();
                }catch(Exception e){
                    core.getLog().log(Level.WARNING, "Error deleting song file {0}: {1}",
                    new Object[]{selectedSound.getFileName(), e.getMessage()}); 
                }
            }
        };
        
        th.setDaemon(true);
        th.start();
    }

    private void deletePlayList() {
        Thread th = new Thread(){
            
            public void run(){
                try{
                    if(playList == null){
                        message("No playlist selected");
                        return;
                    }

                    if(!deleteComfirmation())
                        return;

                    core.removePlayList(playList);
                    updatePlayListCombo();
                    displayPlayList(null);
                }catch(Exception e){
                    core.getLog().log(Level.WARNING, "Error deleting playList {0}: {1}",
                        new Object[]{playList.getPlayListName(), e.getMessage()}); 
                }
            }
        };
        
        th.setDaemon(true);
        th.start();
    }
    
    private void setupShortCuts(){
        nextDay.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        prevDay.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        editPlayList.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        nextWeek.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        prevWeek.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        editBells.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
        editMusic.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
        loadDay.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHIFT_DOWN));
        loadWeek.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHIFT_DOWN));
    }
    
    private void nextDay(){
        try{
            int day = core.currentDayOfWeek;
            if(day >= 7){
                boolean b = core.incWeek();
                if(b){
                    core.setDay(1);
                    setDay();
                }else
                    message("Cannot go more then 3 weeks in the future");
            }else{
                core.setDay(++day);
                setDay();
            }
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Error getting next day: {0}", e.getMessage()); 
        }
    }

    private void prevDay(){
        try{
        int day = core.currentDayOfWeek;
            if(day <= 1){
                boolean b = core.decWeek();

                if(b){
                    core.setDay(7);
                    setDay();
                }else
                    message("Cannot go into the past");
            }else{
                core.setDay(--day);
                setDay();
            }
        }catch(Exception e){
            core.getLog().log(Level.WARNING, "Error getting previous day: {0}", e.getMessage()); 
        }
    }

    private void setupDeleteComf() {
        deleteYes.setOnAction((ActionEvent e) -> {
            Platform.runLater(() -> { 
                deletePane.setVisible(false);
            });  
            
            deleteChoice = true;
            synchronized(deleteNotice){
                deleteNotice.notifyAll();
            }
        });
        
        deleteNo.setOnAction((ActionEvent e) -> {
            Platform.runLater(() -> { 
                deletePane.setVisible(false);
            });  
            
            deleteChoice = false;
            synchronized(deleteNotice){
                deleteNotice.notifyAll();
            }
        });
    }

    private void setupTable() {
        eventTable.setPlaceholder(new Label("No events scheduled"));
        eventTable.setRowFactory(tv -> {
            TableRow<ScheduledEvent> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if ((! row.isEmpty()) ) { //event.getClickCount() == 2 && 
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
    }
    
    
}
