package interfacepkg;

public interface Title {
	public abstract String getTitle();
}

/* 인터페이스
 * getTitle()메소드 :역할: 객체들(과목, 자료, 메모, 일정)로부터 공통적으로 과목명을 가져올 수 있는 표준 방법을 제공.
 * 객체의 실제 타입에 관계없이 getTitle()을 호출하여 화면에 보여줄 이름을 얻을 수 있어 편리
 */