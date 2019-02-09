/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import audioSystem.AudioPlayer;
import dataStructures.schedules.ScheduledDay;
import dataStructures.schedules.ScheduledEvent;
import dataStructures.schedules.ScheduledWeek;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import dataStructures.templates.WeekTemplate;
import helperClasses.Helper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public class BellRegion implements Serializable{
    private static final long serialVersionUID = 2;
    
    private final ArrayList<DayTemplate> dayTemplates;
    private final ArrayList<EventTemplate> eventTemplates;
    private final ArrayList<WeekTemplate> weekTemplates;
    private final ArrayList<ScheduledWeek> schedule;
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
    
    public boolean addScheduledWeek(ScheduledWeek newWeek){
        if(newWeek == null)
            return false;
        
        long stamp = scheduleLock.writeLock();
        
        try{
            ArrayList<ScheduledWeek> oldWeeks = new ArrayList();
            schedule.forEach((w) -> {
                if(Helper.isSameWeek(newWeek.getDate(), w.getDate()))
                    oldWeeks.add(w);
                });
            
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
    public void cleanSchedule(){
        
        ArrayList<ScheduledWeek> remove = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR), 1);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date firstOfWeek = cal.getTime();
        
        long stamp = scheduleLock.readLock();
        try{
            schedule.stream().filter((w) -> (w.getDate().before(firstOfWeek))).forEachOrdered((w) -> {
                remove.add(w);
            });
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
    public ScheduledWeek getCurrentWeek(){
        Date today = new Date();
        long stamp = scheduleLock.readLock();
        
        try{
            for(ScheduledWeek w: schedule){
                if(Helper.isSameWeek(w.getDate(), today))
                    return w;
                else if (w.getDate().after(today))
                    break;                
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        //Create new week since there is no current week
        Calendar cal = Calendar.getInstance();
       
        ScheduledWeek week = defaultWeekTemplate.createScheduledWeek(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
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
    public ScheduledWeek getWeek(int weekOfYear, int year){
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(year, weekOfYear, 1);
        Date weekDate = cal.getTime();
        
        long stamp = scheduleLock.readLock();
        
        try{
            for(ScheduledWeek w: schedule){
                if(Helper.isSameWeek(w.getDate(), weekDate ))
                    return w;
                else if (w.getDate().after(weekDate))
                    break;                
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        ScheduledWeek week = defaultWeekTemplate.createScheduledWeek(weekOfYear, year);
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
        player.fullStop();
    }

    public void removePlayList(PlayList list) {
        weekTemplates.forEach(w -> w.removePlayList(list));
        schedule.forEach(w -> w.removePlayList(list));
    }
    
    
    class scheduleComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if(!(o1 instanceof ScheduledWeek))
                return 0;
            
            if(o2 == null)
                return 0;
            
            return ((ScheduledWeek) o1).getDate().compareTo(((ScheduledWeek) o2).getDate());
        }        
    }
    
    private TimerTask scheduler(){
        TimerTask th = new TimerTask(){
            @Override
            public void run() {
                //Get the current week and ensure its not null
                ScheduledWeek w = getCurrentWeek();
                if(w == null)
                    return;
                
                //Get the current day and ensure its not null
                ScheduledDay day = w.getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));                
                if( day == null)
                    return;
                
                //Get the next event, ensure its not null
                ScheduledEvent event = day.getNextEvent();
                if(event == null)
                    return;
                
                Calendar sec3 = Calendar.getInstance();
                sec3.add(Calendar.SECOND, 3);
                
                boolean past = event.getStopTime().before(new Date());
                boolean toSoon = event.getStartTime().after(sec3.getTime());
                Date dat = sec3.getTime();
                
                //If the event is within the next 3 seconds, see if it sould be scheduled
                if(past || toSoon)
                    return;
                
                //If the event is not the current event, and the current event is done schedule event
                if(player.getBellEvent() == null 
                        || (player.getBellEvent().compareTo(event) != 0 
                        && player.getBellEvent().getStopTime().before(new Date())))
                    player.setBellEvent(event);
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
    
    public void removeFile(SoundFile f){
        weekTemplates.forEach(w -> w.removeFile(f));
        schedule.forEach(w -> w.removeFile(f));
    }
    
}
