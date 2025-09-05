//ui 패키지
/* 메인 창 생성, 작동 프레임
 * 
*/
package ui;

import javax.swing.*; //JReame, JLable Swing GUI

public class MainFrame extends JFrame {
	//메인 윈도우
    public MainFrame() {
        setTitle("시간표 시스템"); // 창 제목
        setDefaultCloseOperation(EXIT_ON_CLOSE); //창 닫으면 종료
        setSize(700, 800); //가로세로
        setLocationRelativeTo(null); //화면 중앙에 창
        
        add(new TimetablePanel(this)); //메인 시간표 패널 부착
        setVisible(true); //창 보여주기
    }

    public static void main(String[] args) {
    	//실행
        new MainFrame();
    }
}
