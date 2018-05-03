package cp;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;




/**
 * This class is present only for helping you in testing your software.
 * It will be completely ignored in the evaluation.
 * 
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Main
{
    public static String testString = "C:\\Users\\Stoxhorn\\Desktop\\CurrentProjects\\Concurrent\\code\\data_example";

	public static void main( String[] args )
	{
            List asd = Exam.m1(Paths.get(testString));
            for(Object x : asd)
            {
                System.out.println(x.toString());
            }
            
            
            
            
	}
}
