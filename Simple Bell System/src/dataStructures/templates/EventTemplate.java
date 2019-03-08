/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.RegionDataCore;
import dataStructures.Types.EventType;
import dataStructures.schedules.EventScheduled;
import exceptions.IncorrectVersionException;
import exceptions.TimeOutOfBounds;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public class EventTemplate extends EventType{
    
    private LocalTime startTime;
    private String eventName;

    public EventTemplate(long ID, LocalTime startTime) {
        super(ID);
        this.startTime = startTime;
        segments = new ArrayList();
    }

    public int getHour() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime.getHour();
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setHour(int version, int hour) throws TimeOutOfBounds, InterruptedException, IncorrectVersionException {
        if(hour > 23 || hour < 0)
            throw new TimeOutOfBounds("Hour needs to be between 0-23");
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            startTime = startTime.minusHours(-hour);
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
        
    }
    
    public int getMinute() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime.getMinute();
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setMinute(int version, int minute) throws TimeOutOfBounds, InterruptedException, IncorrectVersionException {
        if(minute > 59 || minute < 0)
            throw new TimeOutOfBounds("Minutes needs to be between 0-59");
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            startTime = startTime.minusMinutes(-minute);
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public int getSecond() throws InterruptedException {
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime.getSecond();
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setSecond(int version, int second) throws TimeOutOfBounds, InterruptedException, IncorrectVersionException {        
         if(second > 59 || second < 0)
            throw new TimeOutOfBounds("Seconds needs to be between 0-59");
        
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            startTime = startTime.minusSeconds(-second);
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public LocalTime getStartTime() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public LocalTime getStopTime(RegionDataCore data) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            return startTime.minusSeconds(- ((long) Math.ceil(getDuration(data))));
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public String getEventName() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return eventName;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setEventName(int version, String eventName) throws InterruptedException, IncorrectVersionException {
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            this.eventName = eventName;
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    /**
     * Creates a ScheduledEvent for today's date at the given time.
     * @param newEventID
     * @param data
     * @param day Day the event should happen
     * @return 
     */
    public EventScheduled getScheduledEvent(long newEventID, RegionDataCore data, LocalDate day) throws InterruptedException{
        EventScheduled event;
        
        while(true)
            try{
                event =  new EventScheduled(newEventID, startTime.atDate(day));
                data.addEventScheduled(event);
                for(int i = 0; i < segments.size(); i++)
                    event.addSegment(event.getVersion(), segments.get(i), i);

                return event;
            } catch (IncorrectVersionException ex) {
            }
    }
    
    public int compareTo(EventType e){
        if(!(e instanceof EventTemplate))
            return 0;
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return startTime.compareTo(((EventTemplate)e).getStartTime());
        } catch (InterruptedException ex) {
            return 0;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    @Override
    public boolean checkOverlap(EventType e, RegionDataCore data) throws InterruptedException{
        if(!(e instanceof EventTemplate))
            return false;
        
        LocalTime endTime = getStopTime(data);
        
        //Ends before start
        if(((EventTemplate) e).getStopTime(data).compareTo(startTime) <= 0)
            return false;
        
        //Starts after end
        if(((EventTemplate) e).getStartTime().compareTo(endTime) >= 0)
            return false;
        
        return true;           
    }
    
    @Override
    public String toString(){
        return eventName;
    }
    
}
