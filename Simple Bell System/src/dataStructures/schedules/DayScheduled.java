/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.RegionDataCore;
import dataStructures.Generator;
import dataStructures.Types.DayType;
import dataStructures.templates.DayTemplate;
import dataStructures.templates.EventTemplate;
import exceptions.IncorrectVersionException;
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
    
    public DayScheduled(long ID, LocalDate day){
        super(ID);
        events = new ArrayList();
        this.day = day;
    }
    
    @Override
    public void removeFile(int version, long fileID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            events.forEach(id -> {
                while(true)
                    try {
                        EventScheduled e = data.getEventScheduled(id);
                        if(e != null)
                        e.removeFile(e.getVersion(), fileID, data);
                        return;
                    } catch (InterruptedException | IncorrectVersionException ex) {} 
            });
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public ArrayList<Long> getEvents() throws InterruptedException{
        ArrayList<Long> ev = new ArrayList();
        
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            events.forEach(e -> {
                ev.add(e);
            });
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
        
        return ev;
    }
    
    @Override
    public boolean addEvent(int version, long eventID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        
        EventScheduled ev = data.getEventScheduled(eventID);
        
        if(!(ev instanceof EventScheduled))
            return false;
        
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.version++;
            return events.add(eventID);
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
        
    }
    
    public void removeEvent(int version, EventScheduled ev) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            events.remove(ev.getID());
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }

    public LocalDate getDay() throws InterruptedException { 
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return day;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public String getName() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return name;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setName(int version, String name) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.name = name;
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    /**
     * Returns the next event that should be played.If there is no more events to 
     * play it will return a null value.
     * @param data
     * @return 
     * @throws java.lang.InterruptedException 
     */
    public long getNextEvent(RegionDataCore data) throws InterruptedException{
        long event = -1;
        LocalDateTime now = LocalDateTime.now();
        
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            EventScheduled ev;
            for(int i = 0; i < events.size(); i++){
                ev = data.getEventScheduled(events.get(i));
                if(ev == null){
                    events.remove(i);
                    continue;
                }
                
                if(!ev.isCancelled() && ev.getStopTime(data).compareTo(now) > 0){
                    event = events.get(i);
                    break;
                }
            }
        }finally{
            if (stamp != 0)
                lock.unlockRead(stamp);
        }
        
        return event;
    }
    
    public DayTemplate getTemplate(long newDayID, RegionDataCore data, Generator gen) throws InterruptedException{
        DayTemplate dayTemp = new DayTemplate(newDayID);
        
        long stamp = 0;
        while(true)
            try{
                stamp = Helper.getReadLock(lock);
                for(long e: events){
                    EventScheduled ev = data.getEventScheduled(e);
                    EventTemplate template = ev.getTemplate(gen.getNewEventID(), data);
                    data.addEventTemplate(template);
                    
                    dayTemp.addEvent(dayTemp.getVersion(), 
                            template.getID(), data);
                }
                
                return dayTemp;
            } catch (IncorrectVersionException ex) {}finally{
                if(stamp != 0)
                    lock.unlockRead(stamp);
            }
    }
    
}
