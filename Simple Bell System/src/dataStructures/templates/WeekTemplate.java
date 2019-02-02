/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

import dataStructures.schedules.ScheduledWeek;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Quinten Holmes
 */
public class WeekTemplate implements Serializable{
    private static final long serialVersionUID = 1;
    
    private DayTemplate[] days = new DayTemplate[7];
    private String name;

    public WeekTemplate() {
        
    }
    
    public WeekTemplate(String weekName){
        this.name = weekName;
    }

    public String getWeekName() {
        return name;
    }

    public void setWeekName(String weekName) {
        this.name = weekName;
    }
    
    public DayTemplate[] getDays(){
        DayTemplate[] array = new DayTemplate[7];
        System.arraycopy(days, 0, array, 0, 7);
        return array;
    }
    
    public void setDay(DayTemplate day, int i){
        if(i >= 0 && i < 7)
            days[i] = day;
        else
            throw new java.lang.IndexOutOfBoundsException("Day needs to be between 0-6");
    }
    
    public DayTemplate getDay(int i){
        if(i >= 0 && i < 7)
            return days[i];
        
        throw new java.lang.IndexOutOfBoundsException("Day needs to be between 0-6");
    } 
    
    /**
     * Creates a scheduledWeek using the given template for the given range
     * @param weekOfYear the number of the week of the given year
     * @param year Year of the week
     * @return 
     */
    public ScheduledWeek createScheduledWeek(int weekOfYear, int year){
        Calendar cal = Calendar.getInstance();
        cal.setWeekDate(year, weekOfYear, 1);
        Date date = cal.getTime();        
        
        ScheduledWeek week = new ScheduledWeek(name);
        
        for(int i = 0; i < 7; i++){
            if(days[i] != null)
                week.setDay(days[i].createScheduledDay(cal.getTime()), i);
            cal.add(Calendar.DATE, 1);
        }
        
        return week;
    }
    
}
