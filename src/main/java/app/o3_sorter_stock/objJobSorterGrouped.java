package app.o3_sorter_stock;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static app.o3_sorter_stock.functions.strPad;

public class objJobSorterGrouped {
    public static HashMap<String, HashMap<String, String[]>> list = new HashMap<>();
    public static HashMap<String, String> alternative = new HashMap<>();
    public static HashMap<String, String> parent = new HashMap<>();
    public static HashMap<String, String> stock = new HashMap<>();
    public static HashMap<String, String> groupCustomer = new HashMap<>();
    public static HashMap<String, Boolean> isFirst = new HashMap<>();

    public static String stock(String customer){
        String agency = groupCustomer.get(customer);
        String agencyStock = stock.get(agency);
        String ret;
        if(isFirst.get(agency)){
            isFirst.put(agency,false);
            ret = stockPad(agencyStock);
        }
        else{
            ret = nextStock(agencyStock);
        }
        stock.put(agency,ret);
        return ret;
    }

    public static void add(String group,String barcode, String[] row){
        if(list.containsKey(group)){
            list.get(group).put(barcode, row);
        }
        else{
            HashMap<String, String[]> _row = new HashMap<>();
            _row.put(barcode, row);
            list.put(group, _row);
        }
    }

    public static void alternative(String a, String b){
        if(!a.isEmpty()){
            alternative.put(a, b);
        }
        if(!b.isEmpty()){
            parent.put(b,a);
        }
    }

    public static String getAlternative(String barcode){
        String ret = "";
        if(alternative.containsKey(barcode)){
            ret = alternative.get(barcode);
        }else if(parent.containsKey(barcode)){
            ret = parent.get(barcode);
        }
        return ret;
    }

    public static String getParent(String barcode){
        String ret = "";
        if(alternative.containsKey(barcode)){
            ret = alternative.get(barcode);
        }
        return ((ret.isEmpty())?barcode:ret);
    }

    public static void clear(){
        list.clear();
        groupCustomer.clear();
        alternative.clear();
        parent.clear();
        stock.clear();
        isFirst.clear();
    }

    private static String nextStock(String stock) {
        Pattern pattern = Pattern.compile("([A-Za-z]+)(\\d+)");
        Matcher matcher = pattern.matcher(stock);
        matcher.find();
        Integer next = Integer.parseInt(matcher.group(2))+1;
        return matcher.group(1)+strPad(next.toString(),4,"0");
    }

    private static String stockPad(String stock){
        Pattern pattern = Pattern.compile("([A-Za-z]+)(\\d+)");
        Matcher matcher = pattern.matcher(stock);
        matcher.find();
        return matcher.group(1)+strPad(matcher.group(2),4,"0");
    }
}
