/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.schedules;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class ScheduledWeek implements Serializable{
    private static final long serialVersionUID = 1;
    
    private ScheduledDay[] days = new ScheduledDay[7];
    private String weekName;
    private Date date;

    public ScheduledWeek(Date date) {
        this.date = date;
    }
    
    public ScheduledWeek(String weekName){
        this.weekName = weekName;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }
    
    public ScheduledDay[] getDays(){
        ScheduledDay[] array = new ScheduledDay[7];
        System.arraycopy(days, 0, array, 0, 7);
        return array;
    }
    
    public void setDay(ScheduledDay day, int i){
        if(i >= 0 && i < 7)
            days[i] = day;
        else
            throw new java.lang.IndexOutOfBoundsException("Day needs to be between 0-6");
    }
    
    public ScheduledDay getDay(int i){
        if(i >= 0 && i < 7)
            return days[i];
        
        throw new java.lang.IndexOutOfBoundsException("Day needs to be between 0-6");
    }    
     
    public Date getDate(){
        return date;
    }
    
}
