package subjectfolder;
import java.time.LocalDateTime;//Memo 클래스
import java.io.Serializable;
/* 커서위치 조정 가능, 자동저장, 복붙 및 실행취소 기능
 */
public class InsideMemo implements Serializable {
    private StringBuilder content; // 메모 내용
    private int cursorPosition;    // 현재 커서 위치
    private LocalDateTime updatedAt; // 마지막 수정 시각

    public InsideMemo() {
        this.content = new StringBuilder(); // 빈 메모 초기화
        this.cursorPosition = 0;            // 커서 초기 위치 0
        this.updatedAt = LocalDateTime.now(); // 생성 시점 기록
    }

    public String getContent() { // 메모 전체 내용 반환
        return content.toString();
    }

    public int getCursorPosition() { // 현재 커서 위치 반환
        return cursorPosition;
    }

    public void moveCursor(int newPosition) { // 커서 위치 이동
        if (newPosition < 0) newPosition = 0;
        if (newPosition > content.length()) newPosition = content.length();
        this.cursorPosition = newPosition;
    }

    public void moveCursorLeft() { // 커서를 왼쪽으로 한 칸 이동
        moveCursor(cursorPosition - 1);
    }

    public void moveCursorRight() { // 커서를 오른쪽으로 한 칸 이동
        moveCursor(cursorPosition + 1);
    }

    public void insertText(String text) { // 커서 위치에 텍스트 삽입
        content.insert(cursorPosition, text);
        cursorPosition += text.length(); // 커서 위치 갱신
        updatedAt = LocalDateTime.now(); // 수정 시각 갱신
    }

    public void deleteBeforeCursor() { // 커서 앞 문자 삭제 (Backspace 역할)
        if (cursorPosition > 0) {
            content.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            updatedAt = LocalDateTime.now();
        }
    }

    public void deleteAfterCursor() { // 커서 뒤 문자 삭제 (Delete 키 역할)
        if (cursorPosition < content.length()) {
            content.deleteCharAt(cursorPosition);
            updatedAt = LocalDateTime.now();
        }
    }

    public void replaceText(int start, int end, String newText) { // 드래그 영역 대체
        if (start < 0 || end > content.length() || start > end) return;
        content.replace(start, end, newText);
        cursorPosition = start + newText.length(); // 커서 위치 갱신
        updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdatedTime() { // 마지막 수정 시간 반환
        return updatedAt;
    }

    public void setContent(String newContent) { // 전체 메모 내용 덮어쓰기
        this.content = new StringBuilder(newContent);
        cursorPosition = content.length(); // 커서를 끝으로 이동
        updatedAt = LocalDateTime.now();
    }
}

/*
 * StringBuilder 사용 > 메모내용 관리
 * (커서위치, 수정 시각, 편집 기능) > MemoEditor
 */

