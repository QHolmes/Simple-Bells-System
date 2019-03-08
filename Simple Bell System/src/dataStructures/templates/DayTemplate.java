/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.RegionDataCore;
import dataStructures.Generator;
import dataStructures.Types.DayType;
import dataStructures.schedules.DayScheduled;
import dataStructures.schedules.EventScheduled;
import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author Quinten Holmes
 */
public class DayTemplate extends DayType{
    
    
    public DayTemplate(long ID){
        super(ID);
        events = new ArrayList();
        name = "";
    }
    
    public DayTemplate(long ID, String name){
        this(ID);
        this.name = name;
    }
    
    /**
     * Returns a scheduled day for the given date
     * @param newDayID
     * @param gen
     * @param day The day the scheduled event would be used.
     * @param data
     * @return 
     */
    public DayScheduled getScheduledDay(long newDayID, Generator gen, LocalDate day, RegionDataCore data) throws InterruptedException{
        DayScheduled scDay;
        
        long stamp = 0;
        
        while(true)
            try{
                stamp = Helper.getReadLock(lock);
                scDay = new DayScheduled(newDayID, day);
                data.addDayScheduled(scDay);
                
                EventTemplate e;
                for(long id: events){
                    e = data.getEventTemplate(id);
                    EventScheduled newEvent = e.getScheduledEvent(gen.getNewEventID(), data, day);
                    data.addEventScheduled(newEvent);
                    scDay.addEvent(scDay.getVersion(), newEvent.getID(), data);
                }

                scDay.setName(scDay.getVersion(), name);

                return scDay;
            } catch (IncorrectVersionException ex) {}finally{
                if(stamp != 0)
                    lock.unlockRead(stamp);
            }
    }
    

    @Override
    public boolean addEvent(int version, long eventID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        EventTemplate ev = data.getEventTemplate(eventID);
        
        if(ev == null || !(ev instanceof EventTemplate))
            return false;
        
        boolean valid = true;
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            EventTemplate e;
            for(long id: events){
                e = data.getEventTemplate(id);
                if(e == null)
                    continue;
                valid = !e.checkOverlap(ev, data);
                if(valid == false)
                    break;
            }

            if(valid){
                events.add(eventID);
                sortEvents();
            }
            this.version++;
            return valid;
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
    
}
