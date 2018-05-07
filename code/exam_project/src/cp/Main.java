package cp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;




/**
 * This class is present only for helping you in testing your software.
 * It will be completely ignored in the evaluation.
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Main
{
    public static String testString = "C:\\Users\\Stoxhorn\\Desktop\\CurrentProjects\\Concurrent\\code\\data_example";

    static long startTime = System.currentTimeMillis();;
    
    static long endTime = System.currentTimeMillis();;
            
    // Method for comparing results
    public static void compare(List<PathResultMin> list)
    {
        
        
        
        List<PathResultMin> check = seq(Paths.get(testString));
        
        
        
        
        ListIterator<PathResultMin> itr;
        
        int sameResults = 0;
        
        for(PathResultMin v: check)
        {
            itr = list.listIterator();

            while(itr.hasNext())
            {
                PathResultMin z = itr.next();


                if(equals(v, z))
                {
                    
                    sameResults ++;
            
                }
            }
            
            
        }        
        int dif = check.size()-sameResults;
        System.out.println("The amount of results not equal or missing " + dif);
        System.out.println("The goal: " + check.size());
        System.out.println("The result: " + sameResults);
        
        
    }
    
    public static void main( String[] args )
    {
        
        
        /*startTime = System.currentTimeMillis();
        List list = Exam.m1(Paths.get(testString));
        
        endTime = System.currentTimeMillis();
        System.out.println("concurrent: " + (endTime - startTime));
        
        System.out.println("size; " + list.size());
        
        for(Object x : list)
        {
            System.out.println(x.toString());
        }
        
        compare(list);*/
        startTime = System.currentTimeMillis();
        Stats res = Exam.m3(Paths.get(testString));
        endTime = System.currentTimeMillis();
        System.out.println("concurrent: " + (endTime - startTime));
        
        System.out.println(res);
        
        



    }
    
    // Sequential m1:
    public static List< PathResultMin > seq( Path dir )
    {
        
        
        
       // Creates the list to be returned
       List<PathResultMin> returnList = new LinkedList();            

       // In case the given path is a directory and not a txt file:
       if (Files.isDirectory(dir))
       {    
           // Creates an array of Files, representing the files in the given irectory
           File[] dirFiles = dir.toFile().listFiles();

           // Loops through each file, and calls this method upon the loop
           for(File file : dirFiles)
           {
               returnList.addAll(seq(file.toPath()));
           }
       }

       // In case the given path is a txt file and not a directory:
       else if(dir.toString().toLowerCase().endsWith(".txt"))
       {
           // Adds a PathResultMin for the given file to the lsit
           returnList.add(new PathResultMin(dir));
       } 

       // Returns the build List
       // If the given path was neither a txt file or directory,
       // this will be empty, and thus cause no chaos
       return returnList;            
    }
    
    public static boolean equals(PathResultMin x, PathResultMin y)
    {
     
        
        int xNum = x.number();
        
        int yNum = y.number();
        
        String xStr = x.path().toString();
        
        String yStr = y.path().toString();
        
        if(xNum != yNum)
        {
            return false;
        }
        
        return xStr.equals(yStr);
    }
    
}
