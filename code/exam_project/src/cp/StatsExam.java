/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * Should take a path, and add the respective stats to the respective objects
 * 
 * Need an "adder" for each getter, that uses the txt of the given files
 * 
 * Each method uses the currently stored IntStream
 * 
 */
public class StatsExam implements Stats
{
    //contains the index of most frequent number 
    int mostFrequent = -1;
    
    int leastFrequent = -1;
    
    private ArrayList<Integer> occurences = new ArrayList();
    
    private ArrayList<Integer> occured = new ArrayList();
    
    private ArrayList<statNode> paths = new ArrayList();
    
    
    public void add(Path dir)
    {

    }
    
    
    private void calcOcc()
    {
        // for each occureance:
        for(int occ : occured)
        {
            
            int index = occured.indexOf(occ);
            
            // If first occurance:        
            if(mostFrequent == -1)
            {
                mostFrequent = index;
            }
            // if previes freq is lower than the freq of occ
            else if(occ > occurences.get(mostFrequent))
            {
                mostFrequent = index;
            }
            // if first occurance
            if(leastFrequent == -1)
            {
                leastFrequent = index;
            }
            // if previous freq is hiehger
            else if(occ < occurences.get(leastFrequent))
            {
                mostFrequent = index;
            }
        }
            
    
    }
        
    
    
    
    private void setOcc(ArrayList<ArrayList<Integer>> Occurences)
    {
        occurences = Occurences.get(0);
        occured = Occurences.get(1);
        
    }
    
    public void calcOcc(IntStream Stream)
    {
        
        Stream.forEach( x ->
                setOcc(addOneOcc(x)));
        
    }
        
    
    private ArrayList<ArrayList<Integer>> addOneOcc(Integer x)
    {
        ArrayList<Integer> occurences = new ArrayList<>();
        ArrayList<Integer> occured = new ArrayList<>();
        ArrayList<ArrayList<Integer>> returnArr3 = new ArrayList<>();
        returnArr3.add(occurences);
        returnArr3.add(occured);
        
        
        // check if occured once
        boolean check = false;
        int index = 0;
        for(Integer y : occured)
        {
            
            if(y.equals(x))
            {
                // if x has occured once do:
                index = occured.indexOf(y);
                // has appeared
                check = true;
            }
        }
        if(check)
        {
                occurences.add(index, occurences.get(index)+1);
        }else
        {
            // if has not ocured
            occured.add(x);
            occurences.add(1);
        }
        return returnArr3;
    }
    
    
    
    private IntStream Stream;
    
    private IntStream getStream()
    {
        IntStream tmp = Stream;
        return tmp;
    }
    
    private void closeStream()
    {
        Stream.close();
    }
    
    private void addStream(Path path)
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
            
            Stream = resultBuilder.build();
    }
    
    
    
    
    public void printOcc()
    {
        
        int index = 0;
        for(int x : occured)
        {
            index = occured.indexOf(x);
            int result = occurences.get(index);
            
            if(result == 0)
            {
                
            }else
            {
                System.out.println("the nubmer: " + x + " had " + result + " occurences");
            }
        }
        
        System.out.println("most Frequent: " + occured.get(mostFrequent) + " and it's occurences " + occurences.get(mostFrequent));
        System.out.println("least Frequent: " + occured.get(leastFrequent) + " and it's occurences " + occurences.get(leastFrequent));
    }
    
    

    
    
    
    @Override
    public int occurrences(int number) {
        int y = 0;
        for(int x : occured)
        {
            if(x == number)
            {
                return occurences.get(y);
            }
            y++;
        }
        return 0;
    }

    @Override
    public int mostFrequent() {
        int tmp = mostFrequent;
        return tmp;
    }

    @Override
    public int leastFrequent() {
        int tmp = mostFrequent;
        return tmp;
    }

    @Override
    public List<Path> byTotals() {
        ArrayList<Path> tmp = new ArrayList();
        for(statNode x : paths)
        {
            tmp.add(x.getPath());
        }
        
        return tmp;
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

    private void setSum(Path dir)
    {
        
        
        
    }
    
}
