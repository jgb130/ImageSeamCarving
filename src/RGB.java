
public class RGB
   {

    private int red;
    private int green;
    private int blue;

    public RGB(int red, int green, int blue)
       {
        this.red = red;
        this.green = green;
        this.blue = blue;
       }

    public int red()
       {
        return red;
       }

    public int green()
       {
        return green;
       }

    public int blue()
       {
        return blue;
       }

    public String toString()
       {
        return "R: " + red() + " G: " + green() + " B: " + blue();
       }
}
