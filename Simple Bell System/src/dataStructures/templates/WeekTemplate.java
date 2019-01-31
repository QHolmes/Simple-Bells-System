/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures.templates;

/**
 *
 * @author Quinten Holmes
 */
public class WeekTemplate {
    private DayTemplate[] days = new DayTemplate[7];
    private String weekName;

    public WeekTemplate() {
        
    }
    
    public WeekTemplate(String weekName){
        this.weekName = weekName;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
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
    
    
}
