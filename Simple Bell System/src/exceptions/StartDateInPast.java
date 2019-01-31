/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Quinten Holmes
 */
public class StartDateInPast extends Exception{
    
    public StartDateInPast(String message) {
        super(message);
    }
    
    public StartDateInPast(){
        super("The start date is already past the current date.");
    }
    
}
