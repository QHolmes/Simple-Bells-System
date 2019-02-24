/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.Types;

import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.templates.EventTemplate;
import helperClasses.Helper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quinten Holmes
 */
public abstract class DayType implements Serializable{
    protected static final long serialVersionUID = 1;
    
    protected ArrayList<EventType> events;
    protected String name;
    
    protected StampedLock eventLock;
    protected StampedLock varLock;
    protected StampedLock timeLock;
    
    public DayType(){
        check();
    }
    
    final public void check(){
        if(varLock == null)
            varLock= new StampedLock();
        
        if(timeLock == null)
            timeLock = new StampedLock();
        
        if(eventLock == null)
            eventLock = new StampedLock();
    }
    
    abstract public boolean addEvent(EventType ev) throws InterruptedException;
    
    public void removeFile(SoundFile file) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(eventLock);
            events.forEach(s -> s.removeFile(file));
        }finally{
            if(stamp != 0)
                eventLock.unlockWrite(stamp);
        }
    }
    
    public void removePlayList(PlayList list) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(eventLock);
            events.forEach(s -> s.removePlayList(list));
        }finally{
            if(stamp != 0)
                eventLock.unlockWrite(stamp);
        }
    }
        
    public void sortEvents() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(eventLock);
            events.sort(new eventComparator());
        }finally{
            if(stamp != 0)
                eventLock.unlockWrite(stamp);
        }
    }
       
    class eventComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if(!(o1 instanceof EventTemplate))
                return 0;
            
            return ((EventType) o1).compareTo((EventType) o2);
        }        
    }
    
    public String getName() throws InterruptedException {
        long stamp = 0;
        try{
           stamp = Helper.getReadLock(varLock);
           return name; 
        }finally{
            if(stamp !=0)
                varLock.unlockRead(stamp);
        }
    }

    public void setName(String name) throws InterruptedException {
        long stamp = 0;
        try{
           stamp = Helper.getWriteLock(varLock);
            this.name = name;
        }finally{
            if(stamp !=0)
                varLock.unlockWrite(stamp);
        }
    }
    
    
    @Override
    public String toString(){
        int gate = 0;
        while(gate < 3){
            try {
                return getName();
            } catch (InterruptedException ex) {gate++;}
        } 
        
        return "Error fetching name";
    }
    

}
