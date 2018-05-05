package cp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exam
{
    // an executor that is used for all the curent methods
    // as only one executor should be running at a time
    static ScheduledThreadPoolExecutor Serv = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    
    static ConcurrentLinkedDeque<Future<Result>> m1Fut = new ConcurrentLinkedDeque<>();
    
    static LinkedList<Result> m1List = new LinkedList<>();
    
    static AtomicInteger m1Int = new AtomicInteger();
    
    static CountDownLatch m1Latch = new CountDownLatch(0);
    
    
    
    
    
    private static int decrementM1()
    {
        int x = m1Int.decrementAndGet();
        return x;
    }
    
    private static  int incrementM1()
    {
        int x = m1Int.incrementAndGet();
        return x;
    }
    
    public static void add()
    {
        
        
        
        for(Future<Result> x : m1Fut)
        {
            
                Serv.execute(()-> {
                    try {
                        m1List.add(x.get());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            
            
            
        }
        Serv.shutdown();
                
    }
    
    
    
    /**
    * This method recursively visits a directory to find all the text
    * files contained in it and its subdirectories.
    * 
    * You must consider only files ending with a ".txt" suffix.
    * You are guaranteed that they will be text files.
    * 
    * System.out.println();You can assume that each text file contains a (non-empty)
    * comma-separated sequence of
    * numbers. For example: 100,200,34,25
    * There won't be any new lines, spaces, etc., and the sequence never
    * ends with a comma.
    * You are guaranteed that each number will be at least or equal to
    * 0 (zero), i.e., no negative numbers.
    * 
    * The search is recursive: if the directory contains subdirectories,
    * these are also searched and so on so forth (until there are no more
    * subdirectories).
    * 
    * This method returns a list of results.
    * The list contains a result for each text file that you find.
    * Each {@link Result} stores the path of its text file,
    * and the lowest number (minimum) found inside of the text file.
    * 
    * @param dir the directory to search
    * @return a list of results ({@link Result}), each giving the lowest number found in a file
    */
    public static List< Result > m1( Path dir )
    {
       
         // Creates the list to be returned
        List<Result> returnList = new LinkedList();            
       
        // Integer to keep track of call number
        int callBlock = incrementM1();
       
        // In case the given path is a txt file and not a directory:
        if(dir.toString().toLowerCase().endsWith(".txt"))
        {
            // Adds a PathResultMin for the given file to the future list
            m1Fut.add(Serv.submit(() -> new PathResultMin(dir)));
        }  
       
        // In case the given path is a directory and not a txt file:
        else if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
               incrementM1();
               // add to files 
               m1(file.toPath());
            
            }
        }
       
       

        // Returns the build List
        // If the given path was neither a txt file or directory,
        // this will be empty, and thus cause no chaos
       
        // if this is first call, return the list of futures
        if(callBlock == 1)
        {
            // Start the creation
            add();
            return m1List;
        }
        else
        {
            
        }
        return null;
    }

    
    /**
     * This method recursively visits a directory for text files with suffix
     * ".dat" (notice that it is different than the one before)
     * contained in it and its subdirectories.
     * 
     * You must consider only files ending with a .dat suffix.
     * You are guaranteed that they will be text files.
     * 
     * Each .dat file contains some lines of text,
     * separated by the newline character "\n".
     * You can assume that each line contains a (non-empty)
     * comma-separated sequence of
     * numbers. For example: 100,200,34,25
     * 
     * This method looks for a .dat file that contains a line whose numbers,
     * when added together (total), amount to at least (>=) parameter min.
     * Once this is found, the method can return immediately
     * (without waiting to analyse also the other files).
     * The return value is a result that contains:
     *	- path: the path to the text file that contains the line that respects the condition;
     *  - number: the line number, starting from 1 (e.g., 1 if it is the first line, 3 if it is the third, etc.)
     * @param dir the directory to search
     * @param min the int to compare a sum to
     * @return A result that contains the path c:\ and number -1, if no sum is larger than min
     * 
     */
    public static Result m2( Path dir, int min )
    {       
        Result tmp = new PathResultSum(false); 
        // In case the given path is a directory and not a txt file:
        if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
                int i = m2(file.toPath(), min).number();
                if(i > -1)
                {
                    return m2(file.toPath(), min);
                }
            }
        }

        // In case the given path is a txt file and not a directory:
        else if(dir.toString().toLowerCase().endsWith(".dat"))
        {
            int tmpSum = new PathResultSum(dir, min).number();
            if(tmpSum > -1)
            {
                return new PathResultSum(dir, min);
            }
        }
        return tmp; 
    }

	
    /**
     * Computes overall statistics about the occurrences of numbers in a directory.
     * 
     * This method recursively searches the directory for all numbers in all lines of .txt and .dat files and returns
     * a {@link Stats} object containing the statistics of interest. See the
     * documentation of {@link Stats}.
     */
    public static Stats m3( Path dir )
    {
            throw new UnsupportedOperationException();
    }
}
