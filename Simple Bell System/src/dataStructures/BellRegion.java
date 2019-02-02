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
    private static final long serialVersionUID = 1;
    
    private final ArrayList<DayTemplate> dayTemplates;
    private final ArrayList<EventTemplate> eventTemplates;
    private final ArrayList<WeekTemplate> weekTemplates;
    private final ArrayList<ScheduledWeek> schedule;
    
    private final StampedLock scheduleLock;
    private transient AudioPlayer player;
    private transient TimerTask schedulerTask;
    private transient Timer timer;;
    
    public BellRegion(){
        dayTemplates = new ArrayList();
        eventTemplates = new ArrayList();
        weekTemplates = new ArrayList();
        schedule = new ArrayList();
        scheduleLock = new StampedLock();
    } 
    
    public void initialize(){
        player = AudioPlayer.getPlayer();
        schedulerTask = scheduler();
        timer = new Timer();
        
        timer.scheduleAtFixedRate(schedulerTask, 0, 100);
    }
    
    public boolean addScheduledWeek(ScheduledWeek newWeek){
        
        long stamp = scheduleLock.readLock();
        
        try{
            if (!schedule.stream().noneMatch((w) -> (Helper.isSameWeek(newWeek.getDate(), w.getDate())))) {
                return false;
            }
            schedule.add(newWeek);
        }finally{
            if(stamp != 0)
                scheduleLock.unlockRead(stamp);
        }
        
        sortSchedule();
        return true;
    }
    
    public void sortSchedule(){
        long stamp = scheduleLock.writeLock();
        schedule.sort(new scheduleComparator());
        scheduleLock.unlockWrite(stamp);
    }
    
    /**
     * Returns the scheduledWeek for the current week. If there is no 
     * scheduled week for this week, this will return null.
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
                    return null;
            }
        }finally {
            scheduleLock.unlockRead(stamp);
        }
        
        return null;
    }
    
    class scheduleComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if(!(o1 instanceof ScheduledWeek))
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
                ScheduledDay day = w.getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);                
                if( day == null)
                    return;
                
                //Get the next event, ensure its not null
                ScheduledEvent event = day.getNextEvent();
                if(event == null)
                    return;
                
                Calendar sec3 = Calendar.getInstance();
                sec3.add(Calendar.SECOND, 3);
                
                //If the event is within the next 3 seconds, see if it sould be scheduled
                if(event.getStartTime().before(new Date()) || event.getStartTime().after(sec3.getTime()))
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
    
}
