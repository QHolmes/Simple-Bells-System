/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.Types;

import dataStructures.RegionDataCore;
import dataStructures.PlayList;
import dataStructures.templates.EventTemplate;
import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.StampedLock;

/**
 *
 * @author Quinten Holmes
 */
public abstract class DayType implements Serializable{
    protected static final long serialVersionUID = 3;
    
    protected ArrayList<Long> events;
    protected String name;
    protected int version = 0;
    protected StampedLock lock;
    protected final long ID;
    
    public DayType(long ID){
        this.ID = ID;
        check();
    }
    
    final public void check(){
        if(lock == null)
            lock = new StampedLock();
    }
    
    /**
     * Adds the event ID to the day.
     * @param version last known version of the day
     * @param eventID ID of event to add
     * @return true if added, else false
     * @throws InterruptedException
     * @throws IncorrectVersionException 
     */
    abstract public boolean addEvent(int version, long eventID, RegionDataCore data) throws InterruptedException, IncorrectVersionException;
    
    public void removeFile(int version, long fileID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            events.forEach(id -> {
                while(true)
                    try {
                        EventType e = data.getEventScheduled(id);
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
    
    public void removePlayList(int version, long listID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            events.forEach(id -> {
                while(true)
                    try {
                        EventType e = data.getEventScheduled(id);
                        if(e != null)
                            e.removePlayList(e.getVersion(), listID, data);
                        return;
                    } catch (InterruptedException | IncorrectVersionException ex) {}
            });
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
        
    public void sortEvents() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            events.sort(new eventComparator());
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
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
           stamp = Helper.getReadLock(lock);
           return name; 
        }finally{
            if(stamp !=0)
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
            if(stamp !=0)
                lock.unlockWrite(stamp);
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
    
    public int getVersion() throws InterruptedException {
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return version;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    protected void checkVersion(int version) throws IncorrectVersionException{
        if(version != this.version)
                throw new IncorrectVersionException(String.format("The given version %d does not match the current version %d.%n", 
                        version, this.version));
    }
    
    public long getID(){
        return ID;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (int) (this.ID ^ (this.ID >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DayType other = (DayType) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
    
    
    

}
