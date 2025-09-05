
package subjectfolder;

import java.io.Serializable;
import interfacepkg.*;

public class Memo implements Serializable, Title {
    private String title;
    private InsideMemo insideMemo;

    public Memo(String title) {
    	this.title = title;
        this.insideMemo = new InsideMemo();
    }

    public InsideMemo getInsideMemo() {
        return this.insideMemo;
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }
    
    @Override
	public String toString() {
		if  (title.length() > StaticLength.maxlen)
    		return title.substring(0, StaticLength.maxlen) + "...";
    	else
    		return title;
	}
}
/*
 * 메모 관련 클래스
 * title 인터페이스 : gettitle
 * insidememo 객체 사용
 */