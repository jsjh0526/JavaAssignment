package subjectfolder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import interfacepkg.*;

public class Schedule implements Serializable, Title {
    private String title;
    private LocalDate date;

    public Schedule(String title, LocalDate date) {
        this.title = title;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr, contentStr;
        
        if (date != null) 
            dateStr = date.format(dtf);
        else
            dateStr = "날짜 없음";
        
        if (title.length() > StaticLength.maxlen)
            contentStr = title.substring(0, StaticLength.maxlen) + "...";
        else
            contentStr = title;

        return "[" + dateStr + "] " + contentStr;
    }

	
	@Override
    public String getTitle() {
        return title;
    }
	
}
/*
 * 일정 관련 클래스
 * title 인터페이스 : gettitle
 * date 객체 사용
 */
