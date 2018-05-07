package cp;

import static cp.Main.startTime;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exam
{
    // an executor that is used for all the curent methods
    // as only one executor should be running at a time
    static ForkJoinPool Serv;
    
    static ExecutorCompletionService Results1;
    
    static ExecutorCompletionService Results2;
    
    static ExecutorCompletionService Results3;
    
    static ConcurrentLinkedDeque<FutureTask<Callable<Result>>> m1Fut = new ConcurrentLinkedDeque<>();
    
    static LinkedList<Result> m1Return = new LinkedList<>();
    
    static Result m2Return = new PathResultSum(false);
    
    static AtomicInteger m1Int = new AtomicInteger();
    
    static AtomicInteger m2Int = new AtomicInteger();
    
    static AtomicInteger m3Int = new AtomicInteger();
    
    static CountDownLatch m2Latch = new CountDownLatch(1);
    
    static StatsExam locals = new StatsExam();
    
    
    
    private static  int incrementM1()
    {
        int x = m1Int.incrementAndGet();
        return x;
    }
    
    private static  int incrementM2()
    {
        int x = m2Int.incrementAndGet();
        return x;
    }
    
    private static  int incrementM3()
    {
        int x = m3Int.incrementAndGet();
        return x;
    }
    
    

    
    public static void add()
    {
        Future<Result> tmp = Results1.poll();
        while((Serv.hasQueuedSubmissions()) || tmp != null || Serv.getActiveThreadCount() > 0)
        {
            if(tmp == null)
            {
            }
            else{
            try {
                m1Return.add(tmp.get());
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            }
            tmp = Results1.poll();
        }
        
    }
    
    
    
    /**
    * This method recursively visits a directory to find all the text
    * files contained in it and its subdirectories.
    * 
    * You must consider only files ending with a ".txt" suffix.
    * You are guaranteed that they will be text files.
    * 
    * You can assume that each text file contains a (non-empty)
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
        // Integer to keep track of call number
        int callBlock = incrementM1();
       
        if(callBlock == 1)
        {
            Serv = new ForkJoinPool();
            Results1 = new ExecutorCompletionService(Serv);
        }
        
        // In case the given path is a txt file and not a directory:
        if(dir.toString().toLowerCase().endsWith(".txt"))
        {
            
            // Adds a PathResultMin for the given file to the future list
            
            Results1.submit(() ->
            {
                return new PathResultMin(dir);
            });
        }  
       
        // In case the given path is a directory and not a txt file:
        else if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
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
            try{
                return m1Return;
            }finally{
                Serv.shutdown();
            }
            
        }
        else
        {
            
        }
        return null;
    }

    public static void add2One()
    {
        Future<Result> tmp = Results2.poll();
            
            
            try {
                if(tmp.get() == null)
                {
                }
                else if(tmp.get().number() != -1)
                {
                    m2Return = tmp.get();
                    m2Latch.countDown();
                }
            } catch (InterruptedException | ExecutionException | NullPointerException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
                
    }
    
    public static void add2()
    {
        
        
        while((Serv.hasQueuedSubmissions()) || Serv.getActiveThreadCount() > 0)
        {
            Serv.execute(()-> add2One());
        }
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
        
        int local = incrementM2();
        if(local == 1)
        {
            Serv = new ForkJoinPool();
            Results2 = new ExecutorCompletionService(Serv);
        }
        
        // In case the given path is a directory and not a txt file:
        if (Files.isDirectory(dir))
        {    
            // Creates an array of Files, representing the files in the given irectory
            File[] dirFiles = dir.toFile().listFiles();

            // Loops through each file, and calls this method upon the loop
            for(File file : dirFiles)
            {
                m2(file.toPath(), min);
            }
        }

        // In case the given path is a txt file and not a directory:
        else if(dir.toString().toLowerCase().endsWith(".dat"))
        {
            int tmpSum = new PathResultSum(dir, min).number();
            if(tmpSum > -1)
            {
                Results2.submit(() ->
                {
                    Result tmp = new PathResultSum(dir, min);
                    if(tmp.number() != -1)
                    {
                        m2Return = tmp;
                        m2Latch.countDown();
                        return null;
                    }
                    return new PathResultSum(dir, min);
                });
                return null;
            }
        }
        if(local == 1)
        {
            Serv.execute(() -> add2());
            try {
                m2Latch.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exam.class.getName()).log(Level.SEVERE, null, ex);
            }
            return m2Return; 
        }
        return null;
    }

    public static void add3()
    {
        
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
        int local = incrementM3();
        File[] dirFiles = dir.toFile().listFiles();

       for(File x : dirFiles)
       {
        String tmp = x.getAbsolutePath();
        if(tmp.toLowerCase().endsWith(".txt") || tmp.toLowerCase().endsWith(".dat"))
        {
            locals.add(Paths.get(tmp));
        }
        else if(x.isDirectory())
        {
            m3(Paths.get(tmp));
        }   
       }
        
    
            
        return locals;
    }
    
    // each time i meet a txdt/ dat file i need to run following sequence:
    /*
    
        Get a stream
        Calculate the a sum
        sum takes a path and a new sum
        takes a list of statNodes and build upon this

        Calls method addStream
                    needs changing:
                        neds to return the stream, and to be a part of exam
    
        
        
    */
    
    // split up into two, so one method returns a list of nodes, and another reutns one node only
    private static List<statNode> calcSum(List<Path> dirs)
    {
        ArrayList<statNode> tmp = new ArrayList<>();
        for(Path x : dirs)
        {
            tmp.add(getNode(x));
        }
        
        ArrayList<statNode> temp = new ArrayList<>();
        int y = 0;
        for(statNode x : tmp)
        {
            x.setInd(y);
            y++;
            temp.add(x);   
        }
        
        return temp;
        
    }
    
    
    // Takes a path, and a list of Nodes. and returns a list with one new Node
    private static statNode getNode(Path dir)
    {
        IntStream loco;
        loco = addStream(dir);
        
        int newSum;
        newSum = loco.sum();
        
        loco.close();
        
        statNode newNode = new statNode(newSum, dir, -1);
        
        
        return newNode;

    }
    
    
    
    
    // Takes a path and returns an intstream
    private static IntStream addStream(Path path)
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
