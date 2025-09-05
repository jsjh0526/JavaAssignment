//시간표 패키지
/* 시간표의 한 칸 객체
 * 특정 요일과 시간의 조합 나타내는 객체
 * timetablemanager의 schedule 맵의 key
 *
 */

//package timetable; 
package timetable;

import java.util.Objects; //equals(), hashCode() 사용
import java.io.Serializable;

public class TimeSlot implements Serializable {
	//시간표 한칸 : 요일 + 시간
    private String day;   
    private String time;  

    public TimeSlot(String day, String time) { //생성 : 요일+시간
        this.day = day;
        this.time = time;
    }
    //getter 메소드
    public String getDay() {
    	return day; 
    }
    public String getTime() {
    	return time; 
    }

    //두 객체 같은지 비교
    @Override
    public boolean equals(Object o) {
        if (this == o) //같은 메모리주소
        	return true; 
        if (!(o instanceof TimeSlot)) //o의 객체 타입 확인
        	return false;
        TimeSlot other = (TimeSlot) o; //형변환
        return day.equals(other.day) && time.equals(other.time);
        //둘다 같으면 T 다르면 F
    }

    //HashMap 해쉬 코드 생성
    @Override
    public int hashCode() {
        return Objects.hash(day, time); //요일+시간 조합 : 해쉬 생성
    }

    //문자열 출력 : 요일 시간 
    @Override
    public String toString() {
        return day + " " + time;
    }
}