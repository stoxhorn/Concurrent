/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp;

import java.nio.file.Path;
import java.util.ArrayList;
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
    //contains the index of most frequent number 
    int mostFrequent = -1;
    
    int leastFrequent = -1;
    
    private ArrayList<Integer> occurences = new ArrayList();
    
    private ArrayList<Integer> occured = new ArrayList();
    
    private List<statNode> paths = new ArrayList();
    
    public StatsExam()
    {
        
    }
            
            
    
    public void add(Path dir)
    {

    }
    
    
    public void calcOcc()
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
    
    
    
    // The stream will bne needed for each node, as i need to go through each number from the path
    public void calcOcc(IntStream Stream)
    {
        
        Stream.forEach( x ->
                setOcc(addOneOcc(x)));
        
    }
        
    
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
    
    
    public void setPaths(List<statNode> list)
    {
        paths = list;
        printOcc();
        System.out.println(this.mostFrequent());
        System.out.println(this.leastFrequent());
        System.out.println(this.paths);
    }
    
    public String toString()
    {
        String ret = "";
        for(statNode x: paths)
        {
            
        }
    }
    
    
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
    
    

    private void setSum(Path dir)
    {
        
        
        
    }
    
}
