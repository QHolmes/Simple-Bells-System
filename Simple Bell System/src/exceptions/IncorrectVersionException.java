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
public class IncorrectVersionException extends Exception{
    
    public IncorrectVersionException(String message) {
        super(message);
    }
    
    public IncorrectVersionException(){
        super("The given version number does not match");
    }
    
}
