// timetable 패키지 
/* 시간표 데이터 저장, 과목 등록 삭제, 시간 중복 체크
 * 데이터 작업
*/

package timetable;

import java.awt.Color; //색상
import java.util.List;  // 리스트
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.Map; //map 사용
import java.util.HashMap;
import java.util.Objects; // Objects.equals를 사용하기 위해 (선택 사항)
import java.io.Serializable;

public class TimetableManager implements Serializable {
	//실제 데이터 저장소들
	//schedule : 어느 시간칸에 어떤 과목있는지 저장 하는 map
	//timeslot의 equals와 hashcode로 hashmap 작동
    private Map<TimeSlot, Subject> schedule = new HashMap<>(); //시간칸 + 과목 해쉬맵
    private Map<String, Color> colorMap = new HashMap<>(); //색 지정
    private List<Color> colorPool = Arrays.asList( //색 종류
        Color.PINK, Color.CYAN, Color.ORANGE, Color.MAGENTA,
        Color.LIGHT_GRAY, Color.YELLOW, Color.GREEN
    );
    private int colorIndex = 0;

    // 시간 중복 체크 
    public boolean canRegister(List<TimeSlot> slots) {
        for (TimeSlot slot : slots) {
            if (schedule.containsKey(slot)) { //slot 키가 이미 맵에 존재하는지 확인
                return false;
            }
        }
        return true;
    }

    // 과목 등록 
    public void register(List<TimeSlot> slots, Subject subject) {
        if (subject == null || slots == null || slots.isEmpty()) 
        	return; // Null 넘기기

        if (!colorMap.containsKey(subject.getTitle())) { //색상이 없으면 색상 가져와 할당
            colorMap.put(subject.getTitle(), colorPool.get(colorIndex++ % colorPool.size()));
        }
        for (TimeSlot slot : slots) {
            schedule.put(slot, subject); //각 slot에 subject 등록
        }
    }

    // 특정 시간대의 과목 정보 
    public Subject getSubject(TimeSlot slot) {
        return schedule.get(slot);
    }
    // 과목명으로 색상 정보 
    public Color getColor(String subjectName) {
        return colorMap.getOrDefault(subjectName, Color.GRAY); // 이름이 없을 경우 기본색(GRAY) 반환
    }

     //특정 과목 객체에 해당하는 모든 TimeSlot들을 찾아 리스트로 반환
    public List<TimeSlot> getSlotsForSubject(Subject subjectToFind) {
        List<TimeSlot> foundSlots = new ArrayList<>();
        if (subjectToFind == null) {
            return foundSlots;
        }
        // Subject 클래스에 equals() 및 hashCode()가 이름과 메모 기준으로 구현되어 있다고 가정,
        // 또는 고유 ID가 있다면 그것으로 비교하는 것이 가장 정확합니다.
        // 여기서는 Subject 객체의 참조 또는 내용(equals)이 같은 경우를 찾습니다.
        for (Map.Entry<TimeSlot, Subject> entry : schedule.entrySet()) {
            // Subject 객체를 직접 비교 (Subject 클래스에 equals, hashCode 구현 권장)
            // 또는 subjectToFind의 고유한 ID (예: subjectToFind.getId())가 있다면 그것으로 비교
            if (Objects.equals(subjectToFind, entry.getValue()) || 
                (subjectToFind.getTitle().equals(entry.getValue().getTitle()) ) ) {
                // 위 조건은 Subject.equals()가 잘 구현되어 있다면 Objects.equals(subjectToFind, entry.getValue()) 만으로 충분합니다.
                // 임시로 이름과 메모가 같은 경우 동일 과목으로 판단합니다.
                foundSlots.add(entry.getKey());
            }
        }
        return foundSlots;
    }

    /**
     * 특정 과목과 관련된 모든 시간표 항목을 삭제합니다.
     * @param subjectToRemove 삭제할 Subject 객체
     */
    public void unregisterSubject(Subject subjectToRemove) {
        if (subjectToRemove == null) {
            return;
        }
        List<TimeSlot> slotsToRemove = new ArrayList<>();
        // getSlotsForSubject 메소드를 사용하여 삭제할 슬롯들을 가져옵니다.
        slotsToRemove.addAll(getSlotsForSubject(subjectToRemove));

        if (!slotsToRemove.isEmpty()) {
            for (TimeSlot slot : slotsToRemove) {
                schedule.remove(slot);
            }
        }
        
        // 선택 사항: 더 이상 이 과목명으로 등록된 시간이 없다면 colorMap에서 색상 정보도 제거할 수 있습니다.
        // 하지만, 동일한 이름으로 다른 메모를 가진 과목이 다시 추가될 수 있으므로,
        // 색상 정보는 유지하는 것이 사용자 경험상 더 일관적일 수 있습니다.
        // (또는, Subject 객체 자체를 colorMap의 키로 사용하는 방안도 고려 가능 - Subject의 hashCode/equals 중요)
    }
    // <<< 추가된/수정된 메소드 끝 >>>
}