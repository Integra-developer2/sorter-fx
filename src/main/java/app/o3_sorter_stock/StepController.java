package app.o3_sorter_stock;

import java.io.File;
import java.util.ArrayList;

import static app.functions.mkdir;
import app.objGlobals;

public class StepController extends functions{
    public int count;
    public int countFile;
    public int emptyAt;
    public int minEmptyAt;
    public ArrayList<String> stepList;
    public String workingPrefix;

    public StepController(){
        minEmptyAt=objGlobals.PRINT_AT;
        count=0;
        countFile=0;
        stepList=new ArrayList<>();
        workingPrefix="";

        mkdir(objGlobals.logStock);
    }
    public void checkLimit(int test){
        if((test/10)<minEmptyAt&&test>0){
            emptyAt=(test/10);
        }else{
            emptyAt=minEmptyAt;
        }
    }

    public void stepAdd(String text){
        stepList.add(text);
        count++;
    }

    public boolean listIsFull(){
        if(emptyAt==0){
            checkLimit(0);
        }
        return count>=emptyAt;
    }

    public void end(String prefix){
        File stepFile = new File(objGlobals.logStock, prefix+"_end.txt");
        printStepControl(stepFile.toString(),"1");
        stepList = new ArrayList<>();
        countFile=0;
        count=0;
    }

    public void printProgress(String text, int listSize){
        objGlobals.progressStock = ((double)countFile*(double)emptyAt)/(double)listSize;
    }

}