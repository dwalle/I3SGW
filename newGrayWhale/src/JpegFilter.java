import java.io.*;

public class JpegFilter implements FileFilter {

    //Accept gif, jpg files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return false;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.gif) ||
                extension.equals(Utils.jpeg) ||
                extension.equals(Utils.jpg)) {
                    return true;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Just JPEG & GIF images.";
    }
}
