/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import audioSystem.AudioPlayer;
import dataStructures.schedules.DayScheduled;
import dataStructures.schedules.EventScheduled;
import dataStructures.schedules.WeekScheduled;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
import helperClasses.Helper;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class BellRegion implements Serializable{
    private static final long serialVersionUID = 2;
    
    private final ArrayList<DayTemplate> dayTemplates;
    private final ArrayList<EventTemplate> eventTemplates;
    private final ArrayList<WeekTemplate> weekTemplates;
    private final ArrayList<WeekScheduled> schedule;
    private WeekTemplate defaultWeekTemplate;
    
    private final StampedLock scheduleLock;
    private final StampedLock arrayLock;
    private transient AudioPlayer player;
    private transient TimerTask schedulerTask;
    private transient Timer timer;
    
    public BellRegion(){
        dayTemplates = new ArrayList();
        eventTemplates = new ArrayList();
        weekTemplates = new ArrayList();
        schedule = new ArrayList();
        scheduleLock = new StampedLock();
        arrayLock = new StampedLock();
        defaultWeekTemplate = new WeekTemplate();
    } 
    
    public void initialize(){
        player = AudioPlayer.getPlayer();
        schedulerTask = scheduler();
        timer = new Timer();
        
        timer.scheduleAtFixedRate(schedulerTask, 0, 100);
    }
    
    public boolean addScheduledWeek(WeekScheduled newWeek) throws InterruptedException{
        if(newWeek == null)
            return false;
        
        long stamp = scheduleLock.writeLock();
        
        try{
            ArrayList<WeekScheduled> oldWeeks = new ArrayList();
            for(WeekScheduled w: schedule){
                if(newWeek.getDate().isEqual(w.getDate()))
                    oldWeeks.add(w);
            }
            
                oldWeeks.forEach(w -> schedule.remove(w));
            
                schedule.add(newWeek);
                
        }finally{
            if(stamp != 0)
                scheduleLock.unlockWrite(stamp);
        }
        
        sortSchedule();
        return true;
    }
    
    public boolean addTemplateEvent(EventTemplate event){
        if(event == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return eventTemplates.add(event);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean removeTemplateEvent(EventTemplate event){
        if(event == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return eventTemplates.remove(event);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean addTemplateDay(DayTemplate day){
        if(day == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return dayTemplates.add(day);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean removeTemplateDay(DayTemplate day){
        if(day == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return dayTemplates.remove(day);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean addTemplateWeek(WeekTemplate week){
        if(week == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return weekTemplates.add(week);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean removeTemplateWeek(WeekTemplate week){
        if(week == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return weekTemplates.remove(week);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public void sortSchedule(){
        if(schedule.size() < 2)
            return;
        
        long stamp = scheduleLock.writeLock();
        schedule.sort(new scheduleComparator());        
        scheduleLock.unlockWrite(stamp);
    }
    
    /**
     * Removes past weeks from the schedule.
     */
    public void cleanSchedule() throws InterruptedException{
        
        ArrayList<WeekScheduled> remove = new ArrayList();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstOfWeek = LocalDate.now().minusWeeks(1).with(weekFields.dayOfWeek(), 1);
        
        long stamp = scheduleLock.readLock();
        try{
            for(WeekScheduled w: schedule){
                if(w.getDate().isBefore(firstOfWeek))
                    remove.add(w);
            }
        }finally{
            scheduleLock.unlockRead(stamp);
        }
        
        stamp = scheduleLock.writeLock();
        try{
           remove.forEach(w -> schedule.remove(w));
        }finally{
           scheduleLock.unlockWrite(stamp); 
        }
    }
    
    /**
     * Returns the scheduledWeek for the current week. If there is no 
     * scheduled week for this week, this method will create an empty week.
     * @return The scheduled week for the current week
     */
    public WeekScheduled getCurrentWeek() throws InterruptedException{
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate today = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        long stamp = scheduleLock.readLock();
        try{
            for(WeekScheduled w: schedule){
                if(w.getDate().isEqual(today))
                    return w;
                else if (w.getDate().isAfter(today))
                    break;                
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        //Create new week since there is no current week
        Calendar cal = Calendar.getInstance();
       
        WeekScheduled week = defaultWeekTemplate.getScheduledWeek(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
        addScheduledWeek(week);
        
        return week;
    }
    
    /**
     * Returns the week of the given year and week number. If no week is scheduled
     * a null is returned.
     * @param weekOfYear week number of the year
     * @param year year the week is in
     * @return 
     */
    public WeekScheduled getWeek(int weekOfYear, int year) throws InterruptedException{
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate local = LocalDate.now()
                            .withYear(year)
                            .with(weekFields.weekOfYear(), weekOfYear)
                            .with(weekFields.dayOfWeek(), 1);
        
        long stamp = scheduleLock.readLock();
        
        try{
            for(WeekScheduled w: schedule){
                if(w.getDate().isEqual(local))
                    return w;
                else if (w.getDate().isAfter(local))
                    break;                
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        WeekScheduled week = defaultWeekTemplate.getScheduledWeek(weekOfYear, year);
        addScheduledWeek(week);
        
        return week;
        
    }

    public WeekTemplate getDefaultWeekTemplate() {
        return defaultWeekTemplate;
    }

    public void setDefaultWeekTemplate(WeekTemplate defaultWeekTemplate) {
        this.defaultWeekTemplate = defaultWeekTemplate;
    }

    public void stopMedia() {
        EventScheduled e = player.getBellEvent();
        
        while(true)
            try {
                if(e.isRunning())
                    e.cancelEvent(true);
                player.fullStop();
                return;
            } catch (InterruptedException ex) {}
    }

    public void removePlayList(PlayList list) throws InterruptedException {
        for(WeekTemplate w: weekTemplates)
            w.removePlayList(list);
        
        for(WeekScheduled w: schedule)
            w.removePlayList(list);
    }
    
    
    class scheduleComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if(!(o1 instanceof WeekScheduled))
                return 0;
            
            if(o2 == null)
                return 0;
            
            try {
                return ((WeekScheduled) o1).getDate().compareTo(((WeekScheduled) o2).getDate());
            } catch (InterruptedException ex) {
                return 0;
            }
        }        
    }
    
    private TimerTask scheduler(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                try{
                    //Get the current week and ensure its not null
                    WeekScheduled w = getCurrentWeek();
                    if(w == null)
                        return;

                    //Get the current day and ensure its not null
                    DayScheduled day = w.getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));                
                    if( day == null)
                        return;

                    //Get the next event, ensure its not null
                    EventScheduled event = day.getNextEvent();
                    if(event == null || event.isCancelled())
                        return;

                    LocalDateTime sec3 = LocalDateTime.now().plusSeconds(3);

                    boolean past = event.getStopTime().isBefore(LocalDateTime.now());
                    boolean toSoon = event.getStartTime().isAfter(sec3);

                    //If the event is within the next 3 seconds, see if it sould be scheduled
                    if(past || toSoon)
                        return;

                    //If the event is not the current event, and the current event is done schedule event
                    if(player.getBellEvent() == null 
                            || (player.getBellEvent().compareTo(event) != 0 
                            && player.getBellEvent().getStopTime().isBefore(LocalDateTime.now())))
                        player.setBellEvent(event);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BellRegion.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    
                }
            }
        };
        
        return th;
    } 

    public ArrayList<DayTemplate> getDayTemplates() {
        return dayTemplates;
    }

    public ArrayList<EventTemplate> getEventTemplates() {
        return eventTemplates;
    }

    public ArrayList<WeekTemplate> getWeekTemplates() {
        return weekTemplates;
    }
    
    public void quickPlay(SoundFile f, double duration){
        player.quickPlay(f, duration);
    }
    
    public void removeFile(SoundFile f) throws InterruptedException{
        for(WeekTemplate w: weekTemplates)
            w.removeFile(f);
        
        for(WeekScheduled w: schedule)
            w.removeFile(f);
    }
    
}
