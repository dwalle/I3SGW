import java.io.*;

public class DirFilter implements FileFilter {
    public boolean accept(File f) {
    	System.out.println("DirFilter");//Daniel Remove
        if (f.isDirectory()) {
            return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Only directories";
    }
}
