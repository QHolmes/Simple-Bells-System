/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dataStructures.BellRegion;
import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.schedules.DayScheduled;
import dataStructures.schedules.WeekScheduled;
import dataStructures.templates.WeekTemplate;
import helperClasses.MyLogger;
import helperClasses.Save;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class GUICore implements Serializable{
    private static final long serialVersionUID = 5;
    
    protected BellRegion region;
    private transient HashSet<SoundFile> musicFiles;
    private transient HashSet<SoundFile> bellSounds;
    protected transient HashSet<PlayList> playLists;
    protected int currentDayOfWeek;
    protected int currentWeek;
    protected int currentYear;
    protected SoundFile defaultBell;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    
    public GUICore(){
        Save.setUp();
        region = new BellRegion();
        musicFiles = new HashSet();
        bellSounds = new HashSet();
        playLists = new HashSet();
        defaultBell = null;
        initialize();
    }
    
    public boolean addMusicFile(SoundFile s){
        if(s != null){
            LOGGER.log(Level.INFO, "Adding music file [{0}]", s.getFileName());
             if (!musicFiles.stream().noneMatch((f) -> (f.equals(s)))) {
                return false;
            }
            return musicFiles.add(s);
        }
        
        LOGGER.log(Level.INFO, "null music file was attempted to be added");
        
        return false;
    }
    
    public int getMusicFileSize(){
        return musicFiles.size();
    }
    
    public HashSet<SoundFile> getMusicFiles(){
        return musicFiles;
    }
    
    public boolean addBellSound(SoundFile s){
        if(s != null){
            LOGGER.log(Level.INFO, "Adding bell sound [{0}]", s.getFileName());
            
            if (!bellSounds.stream().noneMatch((f) -> (f.equals(s)))) {
                return false;
            }                
            
            return bellSounds.add(s);
        }
        
        LOGGER.log(Level.INFO, "null bell file was attempted to be added");
        
        return false;
    }
    
    public int getBellSoundsSize(){
        return bellSounds.size();
    }
    
    public HashSet<SoundFile> getBellSounds(){
        return bellSounds;
    }
    
    public final void initialize(){
        region.initialize();
        Calendar cal = Calendar.getInstance();
        currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        musicFiles = (HashSet<SoundFile>) Save.loadObject("Music_Library");
        if(musicFiles == null)
            musicFiles = new HashSet();
        bellSounds = (HashSet<SoundFile>) Save.loadObject("Bells_Library");
        if(bellSounds == null)
            bellSounds = new HashSet();
        playLists = (HashSet<PlayList>) Save.loadObject("Play_Lists");
        if(playLists == null)
            playLists = new HashSet();
        setupLogger();
    }
    
    public void setDay(int i){
        if( i >= 1 && i <= 7)
            currentDayOfWeek = i;
    }
    
    public DayScheduled getCurrentDay(){
        while(true)
            try {
                return getSelectedWeek().getDay(currentDayOfWeek);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, "Interrupted Exception while getting the current Day of the week-- {0}",
                        ex.getMessage());
            }
    }
    
    public WeekScheduled getSelectedWeek(){
        while(true)
            try {
                return region.getWeek(currentWeek, currentYear);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, "Interrupted Exception while getting the selected week-- {0}",
                        ex.getMessage());
            }
    }
    
    public boolean incWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(currentYear, currentWeek, currentDayOfWeek);
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        Calendar maxWeek = Calendar.getInstance();
            maxWeek.set(Calendar.DAY_OF_WEEK, 1);
            maxWeek.set(Calendar.HOUR_OF_DAY, 0);
            maxWeek.set(Calendar.MINUTE, 0);
            maxWeek.set(Calendar.SECOND, 0);
            maxWeek.set(Calendar.MILLISECOND, 0);
            maxWeek.add(Calendar.DATE, 28);
            
        if(maxWeek.getTime().after(cal.getTime())){
            currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
            currentYear = cal.get(Calendar.YEAR);
            return true;
        }
        
        return false;
    }
    
    public boolean decWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(currentYear, currentWeek, currentDayOfWeek);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Calendar thisWeek = Calendar.getInstance();
            thisWeek.set(Calendar.DAY_OF_WEEK, 1);
            thisWeek.set(Calendar.HOUR_OF_DAY, 0);
            thisWeek.set(Calendar.MINUTE, 0);
            thisWeek.set(Calendar.SECOND, 0);
            thisWeek.set(Calendar.MILLISECOND, 0);
        
        if(thisWeek.getTime().before(cal.getTime())){
            currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
            currentYear = cal.get(Calendar.YEAR);
            return true;
        }
            
        return false;
    }
    
    public void save(){
        GUICore core = this;
        
        Thread th = new Thread(){
            public void run(){
                Save.saveObject(core, "GUICore");
                Save.saveObject(musicFiles, "Music_Library");
                Save.saveObject(bellSounds, "Bells_Library");
                Save.saveObject(playLists, "Play_Lists");
            }
        };
        th.setDaemon(true);
        th.start();
        
    }

    public void setDefaultWeek(WeekTemplate w) {
        region.setDefaultWeekTemplate(w);
        String name = "";
        while(name.isEmpty()){
            try {
                name = w.getWeekName();
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, "Interrupted Exception while getting the name of the newly selected week-- {0}",
                        ex.getMessage());
            }
        }//End while
        LOGGER.log(Level.INFO,"Changing default week [{0}]", name);
    }
    
    
    public WeekTemplate getDefaultWeek(){
        return region.getDefaultWeekTemplate();
    }
    
    public void removeBellFile(SoundFile f){
        boolean b = bellSounds.remove(f);
        
        if(b){
            while(true)
                try {
                    region.removeFile(f);
                    LOGGER.log(Level.INFO,"Removing bell file [{0}]", f.getFileName());
                    return;
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "Interrupted Exception while removing bell file-- {0}",
                        ex.getMessage());
                }
        }
    }
    
    public void removeMusicFile(SoundFile f){
        boolean b = musicFiles.remove(f);
        if(b){
            while(true)
                try {
                    region.removeFile(f); 
                    LOGGER.log(Level.INFO,"Removing music file [{0}]", f.getFileName());
                    return;
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "Interrupted Exception while removing music file-- {0}",
                        ex.getMessage());
                }
        }
    }
    
    public void removePlayList(PlayList list){
        boolean b = playLists.remove(list);
        if(b){
            while(true)
                try {
                    region.removePlayList(list);
                    LOGGER.log(Level.INFO,"Removing play list [{0}]", list.getPlayListName());
                    return;
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "Interrupted Exception while removing play list-- {0}",
                        ex.getMessage());
                }
        }
    }
    
    public void setupLogger(){
        try {
           MyLogger.setup();
        } catch (IOException ex) {
           LOGGER.log(Level.SEVERE, "Error setting up log, IOException ", ex.getMessage());
           ex.printStackTrace();
        }
    }
    
    public Logger getLog(){
        return LOGGER;
    }
    
}
