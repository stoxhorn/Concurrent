/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp;

import java.nio.file.Path;

/**
 *
 * @author Stoxhorn
 */
public class statNode {
    
    private int sum;
    
    private Path dir;
    
    private int index;
    
    public statNode(int newSum,  Path newDir, int newIndex)
    {        
        sum = newSum;
        dir = newDir;
        index = newIndex;
        
        
    }

    statNode() {
        
    }
    
    public int getSum()
    {
        int tmp = sum;
        return tmp;
    }


    public Path getPath()
    {
        Path tmp = dir;
        return tmp;
    }
    
    public int getInd()
    {
        int tmp = index;
        return tmp;
    }
    
    public void setInd(int newIndex)
    {
        index = newIndex;
    }
    
    public String toString()
    {
        String ret = "";
        ret += index;
        ret += " - index | ";
        ret += sum;
        ret += " is the sum \n";
        ret += dir;
        ret += "\n";
        
        return ret;
        
    }
    
    
    
}
