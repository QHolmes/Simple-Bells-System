/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import dataStructures.RegionDataCore;
import dataStructures.Generator;
import dataStructures.PlayList;
import dataStructures.SoundFile;
import dataStructures.Types.WeekType;
import dataStructures.templates.WeekTemplate;
import exceptions.IncorrectVersionException;
import helperClasses.Helper;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 *
 * @author Quinten Holmes
 */
public class WeekScheduled extends WeekType{
    
    private final DayScheduled[] days = new DayScheduled[7];

    public WeekScheduled(long ID, Generator gen, RegionDataCore data, LocalDate date) {
        super(ID);
        this.date = date;
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate local = date.with(weekFields.dayOfWeek(), 1);
        
        
        for(int i = 0; i < 7; i++){
            days[i] = new DayScheduled(gen.getNewDayID(), local);
            data.addDayScheduled(days[i]);
            local = local.minusDays(-1);
        }
        check();
    }
    
    public WeekScheduled(long ID, Generator gen, RegionDataCore data, String weekName, LocalDate date){
        this(ID, gen, data, date);
        this.name = weekName;
    }
    
    public DayScheduled[] getDays() throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            DayScheduled[] array = new DayScheduled[7];
            System.arraycopy(days, 0, array, 0, 7);
            return array;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public void setDay(int version, DayScheduled day) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            int index = day.getDay().get(WeekFields.of(Locale.getDefault()).dayOfWeek()) - 1;
            days[index] = day;
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        }
    }
    
    public DayScheduled getDay(int i) throws InterruptedException{
        long stamp = 0;
        if(i >= 1 && i <= 7){
            try{
                stamp = Helper.getReadLock(lock);
                return days[i - 1];
            }finally{
                if(stamp != 0)
                    lock.unlockRead(stamp);
            }
        }
        
        throw new java.lang.IndexOutOfBoundsException("Day needs to be between 1-7");
    }    
     
    public LocalDate getDate() throws InterruptedException{
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            return date;
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
    }
    
    public WeekTemplate getTemplate(long newWeekID, RegionDataCore data, Generator gen) throws InterruptedException{
        WeekTemplate wTemp = new WeekTemplate(newWeekID);
        data.addWeekTemplate(wTemp);
        
        long stamp = 0;
        try{
            stamp = Helper.getReadLock(lock);
            for(int i = 0; i < 7; i++){
                while(true)
               try{
                    wTemp.setDay(wTemp.getVersion(), days[i].getTemplate(gen.getNewDayID(), data, gen), i + 1); 
                    break;
               }catch (IncorrectVersionException ex) {}
            }
                
        }finally{
            if(stamp != 0)
                lock.unlockRead(stamp);
        }
        
        return wTemp;
    }
    
    public void removeFile(int version, long fileID, RegionDataCore data) throws InterruptedException, IncorrectVersionException{
        long stamp = 0;
        try{
            stamp = Helper.getWriteLock(lock);
            checkVersion(version);
            for(int i = 0; i < 7; i++){
                while(true)
                    try{
                        days[i].removeFile(days[i].getVersion(), fileID, data);
                        break;
                    }catch (IncorrectVersionException ex) {}
            }  
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
            boolean b;
            for(int i = 0; i < 7; i++){
                b= false;
                while(!b)
                    try{
                        days[i].removePlayList(days[i].getVersion(), listID, data); 
                        b = true;
                    }catch (IncorrectVersionException ex) {}
            }  
            this.version++;
        }finally{
            if(stamp != 0)
                lock.unlockWrite(stamp);
        } 
    }
    
}
