/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.Types.DayType;
import dataStructures.Types.EventType;
import dataStructures.templates.DayTemplate;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Quinten Holmes
 */
public class DayScheduled extends DayType{
    
    private final LocalDate day;
    
    public DayScheduled(LocalDate day){
        super();
        events = new ArrayList();
        this.day = day;
    }
    
    public ArrayList<EventScheduled> getEvents() throws InterruptedException{
        ArrayList<EventScheduled> ev = new ArrayList();
        
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(eventLock);
            events.forEach(e -> {
                ev.add((EventScheduled) e);
            });
        }finally{
            if(stamp != 0)
                eventLock.unlockRead(stamp);
        }
        
        return ev;
    }
    
    @Override
    public boolean addEvent(EventType ev) throws InterruptedException{
        if(ev == null)
            return false;
        
        if(!(ev instanceof EventScheduled))
            return false;
        
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(eventLock);
            return events.add((EventScheduled) ev);
        }finally{
            if(stamp != 0)
                eventLock.unlockWrite(stamp);
        }
        
    }
    
    public void removeEvent(EventScheduled ev) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(eventLock);
            events.remove(ev);
        }finally{
            if(stamp != 0)
                eventLock.unlockWrite(stamp);
        }
    }

    public LocalDate getDay() throws InterruptedException { 
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(timeLock);
            return day;
        }finally{
            if(stamp != 0)
                timeLock.unlockRead(stamp);
        }
    }

    public String getName() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(varLock);
            return name;
        }finally{
            if(stamp != 0)
                varLock.unlockRead(stamp);
        }
    }

    public void setName(String name) throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(varLock);
            this.name = name;
        }finally{
            if(stamp != 0)
                varLock.unlockWrite(stamp);
        }
    }
    
    /**
     * Returns the next event that should be played.If there is no more events to 
     * play it will return a null value.
     * @return 
     * @throws java.lang.InterruptedException 
     */
    public EventScheduled getNextEvent() throws InterruptedException{
        EventScheduled event = null;
        LocalDateTime now = LocalDateTime.now();
        
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(eventLock);
            for(int i = 0; i < events.size(); i++){
                if(!((EventScheduled) events.get(i)).isCancelled() && ((EventScheduled) events.get(i)).getStopTime().compareTo(now) > 0){
                    event = ((EventScheduled) events.get(i));
                    break;
                }
            }
        }finally{
            if (stamp != 0)
                eventLock.unlockRead(stamp);
        }
        
        return event;
    }
    
    public DayTemplate createTemplate() throws InterruptedException{
        DayTemplate dayTemp = new DayTemplate();
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(eventLock);
            for(EventType e: events)
                dayTemp.addEvent(((EventScheduled) e).getTemplate());
        }finally{
            if(stamp != 0)
                eventLock.unlockRead(stamp);
        }
        
        return dayTemp;
    }
    
}
