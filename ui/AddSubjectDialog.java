package ui;

import timetable.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // 추가 (JList 더블클릭용)
import java.awt.event.MouseEvent;  // 추가 (JList 더블클릭용)
import javax.swing.*;

public class AddSubjectDialog extends JDialog {
    private JTextField nameField; 
    private JComboBox<String> dayBox, startBox, endBox;
    private DefaultListModel<String> displayListModel;
    private JList<String> timeDisplayList;
    private java.util.List<TimeSlot> timeSlots = new ArrayList<>();
    private Set<String> addedRanges = new HashSet<>();
    private boolean submitted = false;

    private final String[] DAYS = {"월", "화", "수", "목", "금"};
    private final String[] TIMES = {
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"
    };

    // 기본 생성자
    public AddSubjectDialog(JFrame parent) {
        super(parent, "수업 추가", true);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(420, 360); // 높이 조절 (메모 필드 제거로 인해)
        setLocationRelativeTo(parent);

        // --- UI 컴포넌트 생성 ---
        // 과목명 패널
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("과목명:"));
        nameField = new JTextField(20);
        namePanel.add(nameField);
        add(namePanel);

        // 요일 + 시간 선택 + 시간추가 버튼 패널
        JPanel timeInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeInputPanel.add(new JLabel("요일:"));
        dayBox = new JComboBox<>(DAYS);
        timeInputPanel.add(dayBox);

        timeInputPanel.add(new JLabel("시간:"));
        startBox = new JComboBox<>(TIMES);
        endBox = new JComboBox<>(TIMES);
        timeInputPanel.add(startBox);
        timeInputPanel.add(new JLabel("~"));
        timeInputPanel.add(endBox);

        JButton addTimeBtn = new JButton("+ 시간추가");
        timeInputPanel.add(addTimeBtn);
        add(timeInputPanel);

        // 추가된 시간대 표시 리스트 패널
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("추가된 시간대"));
        displayListModel = new DefaultListModel<>();
        timeDisplayList = new JList<>(displayListModel);
        listPanel.add(new JScrollPane(timeDisplayList), BorderLayout.CENTER);
        
        // <<< 선택 시간 삭제 버튼 추가 (기존 코드 유지 또는 필요시 추가) >>>
        JPanel addedTimesControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteSelectedTimeBtn = new JButton("선택 시간 삭제");
        addedTimesControlPanel.add(deleteSelectedTimeBtn);
        listPanel.add(addedTimesControlPanel, BorderLayout.SOUTH);
        add(listPanel);

        // 등록 / 취소 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitBtn = new JButton("등록");
        JButton cancelBtn = new JButton("취소");
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel);

        // --- 이벤트 리스너 연결 ---
        addTimeBtn.addActionListener(new AddTimeListener());
        submitBtn.addActionListener(new SubmitListener());
        cancelBtn.addActionListener(new CancelListener());
        deleteSelectedTimeBtn.addActionListener(new DeleteSelectedTimeListener()); // 삭제 버튼 리스너
        timeDisplayList.addMouseListener(new TimeListMouseListener()); // 더블클릭 삭제용
    }
    
    // 수정 모드용 생성자
    public AddSubjectDialog(JFrame parent, Subject subject, List<TimeSlot> existingSlots, boolean isEditMode) {
        this(parent); 

        if (isEditMode && subject != null) {
            setTitle("수업 수정"); 
            nameField.setText(subject.getTitle());

            if (existingSlots != null && !existingSlots.isEmpty()) {
                Map<String, List<String>> groupedTimesByDay = new LinkedHashMap<>();
                for (TimeSlot slot : existingSlots) {
                    groupedTimesByDay.computeIfAbsent(slot.getDay(), k -> new ArrayList<>()).add(slot.getTime());
                }

                for (Map.Entry<String, List<String>> entry : groupedTimesByDay.entrySet()) {
                    String day = entry.getKey();
                    List<String> timesOnDay = entry.getValue();
                    if (timesOnDay.isEmpty()) continue;
                    timesOnDay.sort(Comparator.naturalOrder());
                    String start = timesOnDay.get(0);
                    String endDisplay = convertToDisplayEndTime(timesOnDay.get(timesOnDay.size() - 1));
                    String displayEntry = day + " " + start + " ~ " + endDisplay;
                    String rangeKey = day + "_" + start + "~" + endDisplay; 
                    if (!addedRanges.contains(rangeKey)) {
                        displayListModel.addElement(displayEntry);
                        addedRanges.add(rangeKey);
                    }
                }
                this.timeSlots.addAll(existingSlots);
            }
        }
    }
    
    // UI 표시용 종료시간 변환 (+30분 하여 다음 슬롯의 시작 시간을 의미)
    private String convertToDisplayEndTime(String lastSlotStartTime) {
        int timeIndex = -1;
        for (int i = 0; i < TIMES.length; i++) {
            if (TIMES[i].equals(lastSlotStartTime)) {
                timeIndex = i;
                break;
            }
        }
        if (timeIndex != -1 && timeIndex + 1 < TIMES.length) {
            return TIMES[timeIndex + 1];
        } else if (timeIndex != -1) { 
            try {
                int hour = Integer.parseInt(lastSlotStartTime.substring(0, 2));
                int minute = Integer.parseInt(lastSlotStartTime.substring(3, 5));
                minute += 30;
                if (minute >= 60) { hour++; minute -= 60; }
                return String.format("%02d:%02d", hour, minute);
            } catch (Exception e) { /* ignore */ }
        }
        return lastSlotStartTime;
    }

    // --- 내부 클래스로 정의된 ActionListener들 ---
    private class AddTimeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String day = (String) dayBox.getSelectedItem();
            String start = (String) startBox.getSelectedItem();
            String end = (String) endBox.getSelectedItem();

            int startIndex = -1, endIndex = -1;
            for(int i=0; i < TIMES.length; ++i) {
                if(TIMES[i].equals(start)) startIndex = i;
                if(TIMES[i].equals(end)) endIndex = i;
            }

            if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                JOptionPane.showMessageDialog(AddSubjectDialog.this, "시간 설정이 올바르지 않습니다. 시작시간은 종료시간보다 앞서야 하며, 최소 30분 이상이어야 합니다.");
                return;
            }
            
            String displayEntry = day + " " + start + " ~ " + end;
            String rangeKey = day + "_" + start + "~" + end;

            if (addedRanges.contains(rangeKey)) {
                JOptionPane.showMessageDialog(AddSubjectDialog.this, "이미 추가된 시간대입니다.");
                return;
            }

            List<String> slotsInRange = extractRange(start, end);
            for (String t : slotsInRange) {
                timeSlots.add(new TimeSlot(day, t));
            }
            displayListModel.addElement(displayEntry);
            addedRanges.add(rangeKey);
        }
    }

    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(AddSubjectDialog.this, "과목명을 입력하세요!");
                return;
            }
            if (timeSlots.isEmpty()) {
                JOptionPane.showMessageDialog(AddSubjectDialog.this, "시간을 추가하세요!");
                return;
            }
            submitted = true;
            dispose();
        }
    }

    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
    
    private class DeleteSelectedTimeListener implements ActionListener { // "선택 시간 삭제" 버튼용
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = timeDisplayList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedDisplayEntry = displayListModel.getElementAt(selectedIndex);
                String[] parts = selectedDisplayEntry.split(" ");
                if (parts.length < 4) return;
                String day = parts[0];
                String start = parts[1];
                String end = parts[3];
                String rangeKeyToRemove = day + "_" + start + "~" + end;

                if (addedRanges.remove(rangeKeyToRemove)) {
                    displayListModel.removeElementAt(selectedIndex);
                    List<String> slotsInRemovedRange = extractRange(start, end);
                    List<TimeSlot> toRemoveFromTimeSlots = new ArrayList<>();
                    for(String timeStr : slotsInRemovedRange) {
                        toRemoveFromTimeSlots.add(new TimeSlot(day, timeStr));
                    }
                    timeSlots.removeAll(toRemoveFromTimeSlots);
                }
            } else {
                JOptionPane.showMessageDialog(AddSubjectDialog.this, "삭제할 시간대를 목록에서 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private class TimeListMouseListener extends MouseAdapter { // JList 더블클릭 삭제용
        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                int index = timeDisplayList.locationToIndex(evt.getPoint());
                if (index >= 0) { // 더블클릭된 아이템이 있다면
                    // DeleteSelectedTimeListener의 로직과 거의 동일하므로, 해당 로직을 호출하거나 재사용 가능
                    // 여기서는 간단히 하기 위해, 더블클릭 시에도 선택 후 "선택 시간 삭제" 버튼을 누르도록 유도하거나
                    // DeleteSelectedTimeListener와 동일한 로직을 여기에 복사할 수 있습니다.
                    // 지금은 "선택 시간 삭제" 버튼을 사용하도록 남겨두겠습니다.
                }
            }
        }
    }
    
    // --- Getter 메소드들 ---
    public boolean isSubmitted() { return submitted; }
    public String getSubjectName() { return nameField.getText().trim(); }
    
    public List<TimeSlot> getTimeSlots() { return timeSlots; }

    private List<String> extractRange(String start, String end) {
        List<String> result = new ArrayList<>();
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < TIMES.length; i++) {
            if (TIMES[i].equals(start)) startIndex = i;
            if (TIMES[i].equals(end)) endIndex = i;
        }
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            for (int i = startIndex; i < endIndex; i++) {
                result.add(TIMES[i]);
            }
        }
        return result;
    }
}