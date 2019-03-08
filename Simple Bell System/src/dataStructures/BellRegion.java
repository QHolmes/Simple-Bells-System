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
import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
    private static final long serialVersionUID = 3;
    
    private final RegionDataCore data;
    private WeekTemplate defaultWeekTemplate;
    
    private final StampedLock scheduleLock;
    private final StampedLock arrayLock;
    private transient AudioPlayer player;
    private transient TimerTask schedulerTask;
    private transient Timer timer;
    private Generator gen;
    
    public BellRegion(Generator gen){
        this.gen = gen;
        data = new RegionDataCore();
        scheduleLock = new StampedLock();
        arrayLock = new StampedLock();
        defaultWeekTemplate = new WeekTemplate(gen.getNewWeekID(), "Empty Week");
        data.addWeekTemplate(defaultWeekTemplate);
    } 
    
    public void initialize(Generator gen){
        this.gen = gen;
        player = AudioPlayer.getPlayer(data);
        schedulerTask = scheduler();
        timer = new Timer();
        
        timer.scheduleAtFixedRate(schedulerTask, 0, 100);
    }
    
    public boolean addScheduledWeek(WeekScheduled newWeek) throws InterruptedException{
        if(newWeek == null)
            return false;
        
        long stamp = scheduleLock.writeLock();
        
        try{
            return data.addWeekScheduled(newWeek);
                
        }finally{
            if(stamp != 0)
                scheduleLock.unlockWrite(stamp);
        }
    }
    
    public boolean addTemplateEvent(EventTemplate event){
        if(event == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        try{
            return data.addEventTemplate(event);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean removeTemplateEvent(EventTemplate event){
        if(event == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        while(true)
        try{
            return data.removeEventTemplate(event.getID());
        } catch (InterruptedException ex) {
            Logger.getLogger(BellRegion.class.getName()).log(Level.SEVERE, null, ex);
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
            return data.addDayTemplate(day);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean removeTemplateDay(DayTemplate day){
        if(day == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        while(true)
        try{
            return data.removeDayTemplate(day.getID());
                
        }   catch (InterruptedException ex) {
                Logger.getLogger(BellRegion.class.getName()).log(Level.SEVERE, null, ex);
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
            return data.addWeekTemplate(week);
                
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    public boolean removeTemplateWeek(WeekTemplate week){
        if(week == null)
            return false;
        
        long stamp = arrayLock.writeLock();
        
        while(true)
        try{
            return data.removeWeekTemplate(week.getID());
                
        } catch (InterruptedException ex) {
            Logger.getLogger(BellRegion.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
    /**
     * Removes past weeks from the schedule.
     */
    public void cleanSchedule() throws InterruptedException{
        
        ArrayList<Long> remove = new ArrayList();
        ArrayList<Long> schedule = data.getWeeksScheduled();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstOfWeek = LocalDate.now().minusWeeks(1).with(weekFields.dayOfWeek(), 1);
        WeekScheduled w;
        
        long stamp = scheduleLock.readLock();
        try{
            for(Long id: schedule){
                w = data.getWeekScheduled(id);
                if(w == null || w.getDate().isBefore(firstOfWeek))
                    remove.add(id);
            }
        }finally{
            scheduleLock.unlockRead(stamp);
        }
        
        stamp = scheduleLock.writeLock();
        try{
           remove.forEach(id -> {
               while(true)
               try {
                   data.removeWeekScheduled(id);
                   return;
               } catch (InterruptedException ex) {}
           });
        }finally{
           scheduleLock.unlockWrite(stamp); 
        }
    }
    
    public RegionDataCore getData(){
        return data;
    }
    
    /**
     * Returns the scheduledWeek for the current week.If there is no 
     * scheduled week for this week, this method will create an empty week.
     * @return The scheduled week for the current week
     * @throws java.lang.InterruptedException
     */
    public WeekScheduled getCurrentWeek() throws InterruptedException{
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate today = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        ArrayList<Long> schedule = data.getWeeksScheduled();
        WeekScheduled w;
        
        long stamp = scheduleLock.readLock();
        try{
            for(long id: schedule){
                w = data.getWeekScheduled(id);
                LocalDate date = w.getDate();
                if(w == null){
                    data.removeWeekScheduled(id);
                }else if(w.getDate().isEqual(today))
                    return w;              
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        //Create new week since there is no current week
        Calendar cal = Calendar.getInstance();
       
        WeekScheduled week = defaultWeekTemplate.getScheduledWeek(gen.getNewWeekID(), gen, data, cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
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
        
        ArrayList<Long> schedule = data.getWeeksScheduled();
        WeekScheduled w;
        
        try{
            for(long id: schedule){
                w = data.getWeekScheduled(id);
                
                LocalDate date = w.getDate();
                if(w == null)
                    data.removeWeekScheduled(id);
                else if(w.getDate().isEqual(date))
                    return w;             
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        WeekScheduled week = defaultWeekTemplate.getScheduledWeek(gen.getNewWeekID(), gen, data, weekOfYear, year);
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
        
        if(e == null)
            return;
        
        while(true)
            try {
                if(e.isRunning())
                    e.cancelEvent(e.getVersion(), true);
                player.fullStop();
                return;
            } catch (InterruptedException | IncorrectVersionException ex) {}
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
                    EventScheduled event = data.getEventScheduled(day.getNextEvent(data));
                    if(event == null || event.isCancelled())
                        return;

                    LocalDateTime sec3 = LocalDateTime.now().plusSeconds(3);

                    boolean past = event.getStopTime(data).isBefore(LocalDateTime.now());
                    boolean toSoon = event.getStartTime().isAfter(sec3);

                    //If the event is within the next 3 seconds, see if it sould be scheduled
                    if(past || toSoon)
                        return;

                    //If the event is not the current event, and the current event is done schedule event
                    if(player.getBellEvent() == null 
                            || (player.getBellEvent().compareTo(event) != 0 
                            && player.getBellEvent().getStopTime(data).isBefore(LocalDateTime.now())))
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
        long stamp = 0;
        
        while(true)
        try{
            stamp = Helper.getReadLock(arrayLock);
            ArrayList<Long> dayIDs = data.getDaysTemplates();
            ArrayList<DayTemplate> templates = new ArrayList();
            
            dayIDs.forEach(l -> templates.add(data.getDayTemplate(l)));

            return templates;
        } catch (InterruptedException ex) {}finally{
            if(stamp != 0)
                arrayLock.unlockRead(stamp);
        }
    }

    public ArrayList<EventTemplate> getEventTemplates() {
        long stamp = 0;
        
        while(true)
        try{
            stamp = Helper.getReadLock(arrayLock);
            ArrayList<Long> eventIDs = data.getEventTemplates();
            ArrayList<EventTemplate> templates = new ArrayList();
            
            eventIDs.forEach(l -> templates.add(data.getEventTemplate(l)));

            return templates;
        } catch (InterruptedException ex) {}finally{
            if(stamp != 0)
                arrayLock.unlockRead(stamp);
        }
    }

    public ArrayList<WeekTemplate> getWeekTemplates() {
        long stamp = 0;
        
        while(true)
        try{
            stamp = Helper.getReadLock(arrayLock);
            ArrayList<Long> weekIDs = data.getWeekTemplates();
            ArrayList<WeekTemplate> templates = new ArrayList();
            
            weekIDs.forEach(l -> templates.add(data.getWeekTemplate(l)));

            return templates;
        } catch (InterruptedException ex) {}finally{
            if(stamp != 0)
                arrayLock.unlockRead(stamp);
        }
    }
    
    public void quickPlay(SoundFile f, double duration){
        player.quickPlay(f, duration);
    }
    
    public void removeFile(SoundFile f) throws InterruptedException{
        long stamp = 0;
        
        while(true)
        try{
            stamp = Helper.getWriteLock(arrayLock);
            data.removeBellFile(f.getID());
            data.removeMusicFile(f.getID());

            return;
        } catch (InterruptedException ex) {}finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    

    public void removePlayList(PlayList list) throws InterruptedException {
        long stamp = 0;
        
        while(true)
        try{
            stamp = Helper.getWriteLock(arrayLock);
            data.removePlayList(list.getID());

            return;
        } catch (InterruptedException ex) {}finally{
            if(stamp != 0)
                arrayLock.unlockWrite(stamp);
        }
    }
    
}
