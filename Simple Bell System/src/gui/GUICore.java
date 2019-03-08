/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dataStructures.BellRegion;
import dataStructures.Generator;
import dataStructures.PlayList;
import dataStructures.RegionDataCore;
import dataStructures.SoundFile;
import dataStructures.schedules.DayScheduled;
import dataStructures.schedules.WeekScheduled;
import dataStructures.templates.WeekTemplate;
import helperClasses.MyLogger;
import helperClasses.Save;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class GUICore implements Serializable{
    private static final long serialVersionUID = 7;
    
    protected BellRegion region;
    protected int currentDayOfWeek;
    protected int currentWeek;
    protected int currentYear;
    protected long defaultBell = 0;
    private Generator gen;
    private RegionDataCore data;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    
    public GUICore(){
        Save.setUp();
        gen = new Generator();
        region = new BellRegion(gen);
        defaultBell = 0;
        
        initialize();
    }
    
    public boolean addMusicFile(SoundFile s){
        if(s != null){
            return data.addMusicFile(s);
        }
        
        LOGGER.log(Level.INFO, "null music file was attempted to be added");
        
        return false;
    }

    public RegionDataCore getData() {
        return data;
    }
    
    public int getMusicFileSize(){
        return data.getBellFiles().size();
    }
    
    public ArrayList<SoundFile> getMusicFiles(){
        ArrayList<Long> ids = data.getMusicFiles();
        ArrayList<SoundFile> music = new ArrayList(ids.size());
        
        ids.forEach(id -> {
            SoundFile m = data.getMusicFile(id);
            if(m != null)
                music.add(m);
        });
        
        return music;
    }
    
    public boolean addBellSound(SoundFile s){
        if(s != null)
            return data.addBellFile(s);
       
        
        LOGGER.log(Level.INFO, "null bell file was attempted to be added");
        
        return false;
    }
    
    public int getBellSoundsSize(){
        return data.getBellFiles().size();
    }
    
    public ArrayList<SoundFile> getBellSounds(){
        ArrayList<Long> ids = data.getBellFiles();
        ArrayList<SoundFile> bells = new ArrayList(ids.size());
        
        ids.forEach(id -> {
            SoundFile m = data.getBellFile(id);
            if(m != null)
                bells.add(m);
        });
        
        return bells;
    }
    
    public final void initialize(){
        region.initialize(gen);
        Calendar cal = Calendar.getInstance();
        currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        gen = (Generator) Save.loadObject("Generator");
        if(gen == null)
            gen = new Generator();
        data = region.getData();
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
                Save.saveObject(gen, "Generator");
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
        boolean b = data.removeBellFile(f.getID());
        
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
        boolean b = data.removeMusicFile(f.getID());
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
        boolean b = data.removePlayList(list.getID());
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
    
    public Generator getGenerator(){
        return gen;
    }
    
}
