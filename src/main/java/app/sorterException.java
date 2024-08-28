package app;

public class sorterException extends Exception{
    public String category;
    public String text;
    public sorterException(String category, String text){
        super(text);
        this.category = category;
        this.text = text;
    }
}
