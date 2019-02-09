/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dataStructures.BellRegion;
import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.schedules.ScheduledDay;
import dataStructures.schedules.ScheduledWeek;
import dataStructures.templates.WeekTemplate;
import helperClasses.Save;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;

/**
 *
 * @author Quinten Holmes
 */
public class GUICore implements Serializable{
    private static final long serialVersionUID = 4;
    
    protected BellRegion region;
    protected transient HashSet<SoundFile> musicFiles;
    protected transient HashSet<SoundFile> bellSounds;
    protected transient HashSet<PlayList> playLists;
    protected int currentDayOfWeek;
    protected int currentWeek;
    protected int currentYear;
    protected SoundFile defaultBell;
    
    public GUICore(){
        region = new BellRegion();
        musicFiles = new HashSet();
        bellSounds = new HashSet();
        playLists = new HashSet();
        defaultBell = null;
        initialize();
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
    }
    
    public void setDay(int i){
        if( i >= 1 && i <= 7)
            currentDayOfWeek = i;
    }
    
    public ScheduledDay getCurrentDay(){
        return getSelectedWeek().getDay(currentDayOfWeek);
    }
    
    public ScheduledWeek getSelectedWeek(){
        return region.getWeek(currentWeek, currentYear);
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

    void setDefaultWeek(WeekTemplate w) {
        region.setDefaultWeekTemplate(w);
    }
    
    public void removeBellFile(SoundFile f){
        boolean b = bellSounds.remove(f);
        
        if(b){
            region.removeFile(f);        
        }
    }
    
    public void removeMusicFile(SoundFile f){
        boolean b = musicFiles.remove(f);
        
        if(b)
            region.removeFile(f);  
    }
    
    public void removePlayList(PlayList list){
        boolean b = playLists.remove(list);
        
        if(b)
            region.removePlayList(list);
    }
    
}
