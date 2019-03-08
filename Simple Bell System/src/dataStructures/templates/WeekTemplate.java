/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.RegionDataCore;
import dataStructures.Generator;
import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.Types.WeekType;
import dataStructures.schedules.WeekScheduled;
import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 *
 * @author Quinten Holmes
 */
public class WeekTemplate extends WeekType{
    
    private DayTemplate[] days = new DayTemplate[7];

    public WeekTemplate(long ID) {
        super(ID);
    }
    
    public WeekTemplate(long ID, String weekName){
        this(ID);
        this.name = weekName;
    }
    
    public DayTemplate[] getDays() throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            
            DayTemplate[] array = new DayTemplate[7];
            System.arraycopy(days, 0, array, 0, 7);
            return array;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }

    public void setDay(int version, DayTemplate day, int i) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            if(i >= 1 && i <= 7)
                days[i - 1] = day;
            else
                throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public DayTemplate getDay(int i) throws InterruptedException{
        long stamp = 0;
        
        try{
            stamp = Helper.getReadLock(lock);
            if(i >= 1 && i <= 7)
                return days[i - 1];
            else
                throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
            
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    } 
    
    /**
     * Creates a scheduledWeek using the given template for the given range
     * @param newWeekID
     * @param gen
     * @param data
     * @param weekOfYear the number of the week of the given year
     * @param year Year of the week
     * @return 
     * @throws java.lang.InterruptedException 
     */
    public WeekScheduled getScheduledWeek(long newWeekID, Generator gen, RegionDataCore data, int weekOfYear, int year) throws InterruptedException{
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate local = LocalDate.now()
                            .withYear(year)
                            .with(weekFields.weekOfYear(), weekOfYear)
                            .with(weekFields.dayOfWeek(), 1);
        
        WeekScheduled week = new WeekScheduled(newWeekID, gen, data, name, local);
        data.addWeekScheduled(week);
        
        boolean b;
        for(int i = 0; i < 7; i++){
            b = false;
            if(days[i] != null){
                while(!b)
                    try {
                        week.setDay(days[i].getVersion(), days[i].getScheduledDay(gen.getNewDayID(), gen, local, data));
                        b= true;
                    } catch (IncorrectVersionException ex) {}
            }
            local = local.minusDays(-1);
        }
        
        return week;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    public void removeFile(int version, long fileID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        boolean b;
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            for(int i = 0; i < 0; i++){
                b = false;
                while(!b)
                try {
                    days[i].removeFile(days[i].getVersion(), fileID, data);
                    b = true;
                } catch (IncorrectVersionException ex) {}   
            }
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public void removePlayList(int version, long listID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        boolean b;
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            for(int i = 0; i < 0; i++){
                b = false;
                while(!b)
                try {
                    days[i].removePlayList(days[i].getVersion(), listID, data);
                    b = true;
                } catch (IncorrectVersionException ex) {}   
            }
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
}
