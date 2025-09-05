// timetable 패키지
/* 과목 정보 클래스
 * 과목 이름 기준 구현 
 * 과목 객체 비교
 * 이름 기준 해쉬코드 생성
*/

package timetable;

import java.util.Objects; // Objects.hash, Objects.equals 사용
import java.io.Serializable;
import interfacepkg.*;

public class Subject implements Serializable, Title {
    private String title; //private 과목 이름

    //생성자 : 이름
    public Subject() {
    	this.title = "";
    }
    
    public Subject(String title) {
        this.title = title; //null 이면 빈 문자열
    }
    
    @Override
    public String getTitle() { 
    	return title; 
    }
    
    @Override
    public String toString() {
        return title; //과목 이름 반환
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) //같은 객체 주소면 
        	return true; 
        if (o == null || getClass() != o.getClass()) //null 이거나 타입이 다르면
        	return false; 
        Subject subject = (Subject) o; //o를 형변환
        return Objects.equals(title, subject.title); //이름 끼리 비교
    }

    @Override
    public int hashCode() { //과목명을 기준으로 해시 코드
        return Objects.hash(title);
    }
}