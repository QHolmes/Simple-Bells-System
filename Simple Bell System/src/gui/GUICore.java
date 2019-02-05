/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dataStructures.BellRegion;
import dataStructures.SoundFile;
import dataStructures.schedules.ScheduledDay;
import dataStructures.schedules.ScheduledWeek;
import dataStructures.templates.WeekTemplate;
import helperClasses.Save;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import javafx.scene.media.Media;

/**
 *
 * @author Quinten Holmes
 */
public class GUICore implements Serializable{
    private static final long serialVersionUID = 1;
    
    protected BellRegion region;
    protected HashSet<SoundFile> musicFiles;
    protected HashSet<SoundFile> bellSounds;
    protected int currentDayOfWeek;
    protected int currentWeek;
    protected int currentYear;
    protected SoundFile defaultBell;
    
    public GUICore(){
        region = new BellRegion();
        musicFiles = new HashSet();
        bellSounds = new HashSet();
        defaultBell = null;
        
        Media m = new Media(getClass().getClassLoader().getResource("bells/4_Beeps.mp3").toExternalForm());
        
        SoundFile bell = new SoundFile(getClass().getClassLoader().getResource("bells/4_Beeps.mp3"), "4 Beeps");
        bellSounds.add(bell);
        
        bell = new SoundFile(getClass().getClassLoader().getResource("bells/Big_Ben_Bells.mp3"), "Big Ben");
        bellSounds.add(bell);
        
        bell = new SoundFile(getClass().getClassLoader().getResource("bells/Classic_bell.mp3"), "Classic");
        bellSounds.add(bell);
        
        bell = new SoundFile(getClass().getClassLoader().getResource("bells/Computer_Magic.mp3"), "Computer Magic");
        bellSounds.add(bell);
        
        bell = new SoundFile(getClass().getClassLoader().getResource("bells/Soft_Bells.mp3"), "Soft Bells");
        bellSounds.add(bell);
        
        initialize();
    }
    
    public final void initialize(){
        region.initialize();
        Calendar cal = Calendar.getInstance();
        currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
    }
    
    public void setDay(int i){
        if( i >= 1 && i <= 7)
            currentDayOfWeek = i;
        
        save();
    }
    
    public ScheduledDay getCurrentDay(){
        return getSelectedWeek().getDay(currentDayOfWeek);
    }
    
    public ScheduledWeek getSelectedWeek(){
        save();
        return region.getWeek(currentWeek, currentYear);
    }
    
    public void incWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(currentYear, currentWeek, currentDayOfWeek);
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        save();
    }
    
    public void decWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(currentYear, currentWeek, currentDayOfWeek);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentYear = cal.get(Calendar.YEAR);
        save();
    }
    
    public void save(){
        Save.saveObject(this, "GUICore");
    }

    void setDefaultWeek(WeekTemplate w) {
        region.setDefaultWeekTemplate(w);
    }
    
}
