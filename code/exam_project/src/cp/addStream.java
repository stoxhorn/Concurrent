/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Stoxhorn
 */
public class addStream {
     // Takes a path and returns an intstream
    public static IntStream addStream(Path path)
    {
        // creating the Builder to build the IntStream that is created after
        IntStream.Builder resultBuilder = IntStream.builder();
        
        // The try with resources block for getting the stream of the file, in case the file does not exist an error will be printed, and -1 will be returned
        // -1 cannot be a number in the given numbers, and as such represents a fail
        // Fairly sure the print statement is redundant, as this method won't even get called, if a false directory is used, however keeping it is just nice in case shit goes south.
            try
            (
                Stream<String> s = Files.lines( path )
            ){
                // Calls the method addlist on each line of the file, using the builder from earlier
                // only one line is given, however, it's easy to read and understand
                // Also allows for potential useage on multple lines in case an update is needed
                s.forEach( x -> 
                        addList(x, resultBuilder));
                
            }
            catch( IOException e )
            {
                System.out.println(e); 
            }
            
            return resultBuilder.build();
            
            
    }
    
    
    
    
    /**
     * Adds a string of numbers, seperated by a "," to an Intstream, and adds it to the given Builder
     * 
     * @param x             A string conatining numbers seperated by ","
     * @param resultBuilder An IntStream.Builder, that needs be build
     */
    private static void addList(String x, IntStream.Builder resultBuilder)
    {
        
        // Turning the String into a list of Int's
        String[] tmp = x.split(",");
        int[] returnList = Arrays.stream(tmp).mapToInt(Integer::parseInt).toArray();
        
        // Adding the int[] to the builder
        for(int z : returnList)
        {
            resultBuilder.add(z);
        }
    }
       
}
