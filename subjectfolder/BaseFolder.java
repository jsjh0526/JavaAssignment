
package subjectfolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import timetable.*;

public class BaseFolder implements Serializable {
	protected String name;
    protected Subject subject;
    protected List<Schedule> schedules = new ArrayList<>();
    protected List<Memo> memos = new ArrayList<>();
    protected List<Material> materials = new ArrayList<>();

    public BaseFolder(String name, Subject subject) {
    	this.name = name;
        this.subject = subject;
    }

    public Subject getSubject() { 
    	return subject; 
    }

    public List<Schedule> getSchedules() {
    	return schedules;
    }
    
    public List<Memo> getMemos() { 
    	return memos; 
    }
    
    public List<Material> getMaterials() { 
    	return materials; 
    }

    public void addSchedule(Schedule s) { 
    	schedules.add(s); 
    }
    
    public void removeSchedule(Schedule s) { 
    	schedules.remove(s); 
    }
    
    public void addMemo(Memo m) { 
    	memos.add(m); 
    }
    
    public void removeMemo(Memo m) { 
    	memos.remove(m); 
    }
    
    public void addMaterial(Material m) { 
    	materials.add(m); 
    }
    
    public void removeMaterial(Material m) { 
    	materials.remove(m); 
    }
    
}

/*
 * Subject 객체 멤버로 가짐, 과목Subject에 연관
 * Schedule, Memo, Material 객체
 * > List를 가져서 다양한 종류의 수업 자료를 관리(get add remove)
 */


