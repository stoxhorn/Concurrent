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
import java.util.LinkedList;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Stoxhorn
 */
public class PathResultMin implements Result {
    private final int number;
    
    private final Path path;
    
    /**
     * Getter for the path
     * 
     * @return the path of this
     */
    @Override
    public Path path()
    {
        return path;
    }
    
    /**
     * Getter for the number of this
     * 
     * @return the lowest number of this path
     */
    @Override
    public int number()
    {
        return number;
    }
    
    
    /**
     * The constructor taking a path to construct a result with
     * 
     * @param newPath the path leading to the .txt file
     */
    public PathResultMin(Path newPath)
    {
        path = newPath;
        number = getNumber();
    }
    
    /**
     * The toString() method that returns two line, path at the top, number bottom
     * 
     * @return a string representation of this object
     */
    public String toString()
    {
        // The string to build upon
        String returnString = "";
        
        // Adding the path and the number on top of each other
        returnString += path.toString();
        returnString += "\n " + Integer.toString(number);
        
        return returnString;
    }
    
    /**
     * A method that returns the lowest number of the path of thos object
     * 
     * @return int lowest number of this path
     */
    private int getNumber()
    {
        
        // creating the Builder to build the IntStream that is created after
        IntStream.Builder resultBuilder = IntStream.builder();
        IntStream resultList;
        
        
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

            // Build the Instream from the Builder
            // if the stream is empty, the OptionalInt will be empty, or not present
            // And it will return the int -1
            resultList = resultBuilder.build();
            OptionalInt minVal = resultList.min();
            if(minVal.isPresent())
            {

                return minVal.getAsInt();
            }
            else
            {

                return -1;
            }
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
    
    public boolean equals(Result x)
    {
        
        
        int z = x.number();
        
        String c = x.path().toString();
        
        String v = path().toString();
        
        if(z != number())
        {
            return false;
        }
        return c.equals(v);
    }
}
