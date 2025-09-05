
package subjectfolder;

import java.io.Serializable;
import interfacepkg.*;

public class Material implements Serializable, Title {
    private String title;
    private String content;

    public Material(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    public String getContent() { 
    	return this.content; 
    }

    @Override
	public String getTitle() {
		return this.title;
	}
    
    @Override
    public String toString() {
        return this.title;
    }
    
}

/*
 * 메모 내용 관리
 */
