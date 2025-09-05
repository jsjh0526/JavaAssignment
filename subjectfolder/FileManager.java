
package subjectfolder;

import timetable.*;
import java.io.*;

public class FileManager {

    public static String getSaveFileName(Subject subject) {
        return subject.getTitle() + "_data.ser";
    }

    public static void save(Folder folder) {
        String fileName = getSaveFileName(folder.getSubject());
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  
    
    public static Folder load(Subject subject) {
        String fileName = getSaveFileName(subject);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Folder) in.readObject();
        } catch (Exception e) {
            return new Folder("루트", subject);
        }
    }
}
