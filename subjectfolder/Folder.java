
package subjectfolder;

import java.util.ArrayList;
import java.util.List;

import timetable.*;

public class Folder extends BaseFolder {
    private List<Folder> subfolders = new ArrayList<>();

    public Folder(String name, Subject subject) {
        super(name, subject);
    }

    public void addSubfolder(Folder fd) { 
    	subfolders.add(fd); 
    }
    
    public void removeSubfolder(Folder fd) { 
    	subfolders.remove(fd); 
    }
    
    public List<Folder> getSubfolders() { 
    	return subfolders; 
    }

    @Override
    public String toString() {
        return this.name;
    }
}
/*
 * BaseFolder 상속 
 * 하위 폴더 list 관리(add remove get)
 */
