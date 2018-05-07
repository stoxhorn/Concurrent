/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Stoxhorn
 */
public class PathResultSum implements Result {

    // The path of the result
    private final Path path;
    
    // The lineNumber of the line that contains a sum
    private final int lineNumber;
    
    // The sum of the line specified by lineNumber
    private final int sum;
    private boolean bool;
    
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
     * Getter for the line of this object
     * 
     * @return the line that contains the sum of this object
     */
    @Override
    public int number()
    {
        return lineNumber;
    }
    
    /**
     * getter for the sum value
     * 
     * @return an int representing the sum of this object
     */
    public int getSum()
    {
        return sum;
    }
    
    /**
     * The constructor taking a path to construct a result with
     * 
     * @param newPath the path leading to the .txt file
     */
    public PathResultSum(Path newPath, int check)
    {
        path = newPath;
        int[] tmp = checkSum(check);
        sum = tmp[0];
        lineNumber = tmp[1]; 
    }
    
     /**
     * A second constructor for creating an empty result
     * 
     * @param bool
     */
    public PathResultSum(boolean bool)
    {
        path = Paths.get("c:\\");
        sum = -1;
        lineNumber = -1; 
        this.bool = bool;
    }
    
    
    /**
     * The toString() method that returns three lines, path at the top, lineNumber next, and lastly the sum of this line
     * 
     * @return a string representation of this object
     */
    public String toString()
    {
        // The string to build upon
        String returnString = "";
        
        // Adding the path and the lineNumber on top of each other
        returnString += path.toString();
        returnString += "\n " + Integer.toString(lineNumber);
        returnString += "\n " + Integer.toString(sum);
        
        return returnString;
    }
    
    /**
     * Takes a an int that is used to compare sums, and returns an int[] with the line and sum that is above the check
     * 
     * return {-1,-1} if none is found
     * 
     * @param check an integer to compare sums with
     * @return int[] an array that contains two values representing sum and line of the sum
     */
    private int[] checkSum(int check)
    {
        
        // Creating the builder for an intstream, to store the sums of the different lines
        IntStream.Builder resultBuilder = IntStream.builder();
        IntStream resultList;
        
        
        // The try with resources block for getting the stream of the file, in case the file does not exist an error will be printed, and -1 will be returned
        // -1 cannot be a lineNumber in the given numbers, and as such represents a fail
        // Fairly sure the print statement is redundant, as this method won't even get called, if a false directory is used, however keeping it is just nice in case shit goes south.
            try
            (
                Stream<String> s = Files.lines( path )
            ){
                // Calls the method addSum() on each line of the file, using the builder from earlier
                s.forEach( x -> 
                        addSum(x, resultBuilder));
            }
            catch( IOException e )
            {
                System.out.println(e); 
            }

            // Build the Instream from the Builder
            // if the stream is empty, the OptionalInt will be empty, or not present
            // And it will return the int -1
            resultList = resultBuilder.build();
            
            // A for loop that checks if a sum is larger than the given check,
            // and returning as soon as it's found, with the number of the line and the sum
            int i = 0;
            for(int sum : resultList.toArray())
            {
                if(sum > check-1)
                {
                    return new int[] {sum, i};
                }
            }
            
            return new int[] {-1,-1};

    }
    
    /**
     * Adds a string of numbers, seperated by a "," to an Intstream, and adds it to the given Builder
     * 
     * @param x             A string conatining numbers seperated by ","
     * @param resultBuilder An IntStream.Builder, that needs be build
     */
    private static void addSum(String x, IntStream.Builder resultBuilder)
    {
        
        // Turning the String into a list of Int's
        String[] tmp = x.split(",");
        int[] returnList = Arrays.stream(tmp).mapToInt(Integer::parseInt).toArray();
        
        // Creating a local builder to get the sum of another stream
        IntStream.Builder localBuilder = IntStream.builder();
        
        
        // Adding the int[] to the local builder
        for(int z : returnList)
        {
            localBuilder.add(z);
        }
        
        // Building a local IntStream
        IntStream localIntStream = localBuilder.build();
        

        
        // Adding the sum of the local IntStream to the given builder
        resultBuilder.add(localIntStream.sum());
    }
}



    
    
    
    
    
    
    
    
    
        
    
    
    