/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp;

import java.nio.file.Path;
import java.util.ArrayList;
import static java.util.EnumSet.copyOf;
import java.util.List;
import java.util.stream.IntStream;

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
    
    // index of the most frequent number
    int mostFrequent = -1;
    
    // index of the lease frequent number
    int leastFrequent = -1;
    
    // The list of occurrences, with the index corresponding to the index of occured
    private ArrayList<Integer> occurences = new ArrayList();
    
    // The list of occured numbers, with their index linked to a number of occurences
    private ArrayList<Integer> occured = new ArrayList();
    
    // A list of paths 
    private List<statNode> paths = new ArrayList();
    
    // constructor
    public StatsExam()
    {
        
    }
            
            
    
    public void add(Path dir)
    {

    }
    
    
    public void calcOcc()
    {
        // for each occureance loops, and checks if higher og lower than previous storage of frquencies
        for(int occ : occured)
        {
            int index = 0;
            while(occured.get(index)!= occ)
            {
                index ++;
            }
            
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
        
    
    
    
    // sets the occurency lists
    private void setOcc(ArrayList<ArrayList<Integer>> Occurences)
    {
        occurences = Occurences.get(0);
        occured = Occurences.get(1);
        
    }
    
    
    
    // Takes a stream of numbers and adds the proper set of occurenies and occured numbers
    public void calcOcc(IntStream Stream)
    {
        
        Stream.forEach( x ->
                setOcc(addOneOcc(x)));
        
    }
        
    
    // adds the right occurence from a given numbere
    private ArrayList<ArrayList<Integer>> addOneOcc(Integer x)
    {
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
    
    // Takes a list of paths and sets the paths of this object to be the same
    public void setPaths(List<statNode> list)
    {
        List<statNode> tmp = list;
        paths = tmp;
    }
    
   
    // Prints the occurences, the lowest and thehighest frequency at least
    public void printOcc()
    {
        System.out.println("aouihjsdojuikhnas");
        
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
    
    

    
    
    // returns the nubmer of of the given number
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

    // returns the most number of the most frquently occurred number
    @Override
    public int mostFrequent() {
        int tmp = mostFrequent;
        return tmp;
    }
    
    // returns the most number of the least frquently occurred number
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
    
}
