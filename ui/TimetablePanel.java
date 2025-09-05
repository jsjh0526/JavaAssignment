
package ui;

import javax.swing.*;
import timetable.*;
import subjectfolder.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class TimetablePanel extends JPanel implements Serializable {
    private TimetableManager manager;
    private static final String DATA_FILE = "timetable.ser";
    private final String[] DAYS = {"월", "화", "수", "목", "금"};
    private final String[] TIMES = {
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    };
    private Map<String, JButton> cellMap = new HashMap<>();
    private JFrame parentFrame;

    public TimetablePanel(JFrame parent) {
        this.parentFrame = parent;
        this.manager = loadManager();

        setLayout(new BorderLayout(10, 10));

        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("시간표 :");
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 시간표 격자 생성
        JPanel timetableBox = new JPanel(new BorderLayout());
        timetableBox.setPreferredSize(new Dimension(800, 500));
        timetableBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel grid = new JPanel(new GridLayout(TIMES.length + 1, DAYS.length + 1, 1, 1));
        grid.setBackground(Color.LIGHT_GRAY);

        grid.add(new JLabel("")); // 좌측 상단 빈칸
        for (String day : DAYS) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            grid.add(label);
        }

        for (String time : TIMES) {
            grid.add(new JLabel(time, SwingConstants.CENTER));
            for (String day : DAYS) {
                String key = day + "_" + time;
                JButton cell = new JButton();
                cell.setEnabled(true);
                cell.setMargin(new Insets(1, 1, 1, 1));
                cell.setFont(new Font("SansSerif", Font.PLAIN, 10));
                cell.addActionListener(new CellClickListener(day, time));
                cellMap.put(key, cell);
                grid.add(cell);
            }
        }
        timetableBox.add(grid, BorderLayout.CENTER);
        add(timetableBox, BorderLayout.CENTER);

        // 하단 "+ 수업추가" 버튼
        JButton addBtn = new JButton("+ 수업추가");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(addBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(new AddSubjectButtonListener());

        updateTimetableDisplay();
    }

    // ---------------- 저장 및 불러오기 ----------------

    private TimetableManager loadManager() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (TimetableManager) ois.readObject();
            } catch (Exception e) {
            }
        }
        return new TimetableManager();
    }

    private void saveManager() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(manager);
        } catch (Exception e) {
        }
    }

    // ---------------- 셀 클릭 리스너 ----------------

    private class CellClickListener implements ActionListener {
        private String day;
        private String time;

        public CellClickListener(String day, String time) {
            this.day = day;
            this.time = time;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TimeSlot clickedSlot = new TimeSlot(this.day, this.time);
            Subject subject = manager.getSubject(clickedSlot);

            if (subject != null) {
                SlotActionDialog dialog = new SlotActionDialog(parentFrame, subject, clickedSlot);
                dialog.setVisible(true);

                String action = dialog.getChosenAction();

                if (action != null) {
                    switch (action) {
                        case "FOLDER":
                            Folder folder = FileManager.load(subject);
                            SwingUtilities.invokeLater(new Runnable() {
									            @Override
										          public void run() {
										            new MainScreen(folder).setVisible(true);
									            }
										        });
                            break;
                        case "EDIT":
                            handleEditSubject(subject);
                            break;
                        case "DELETE":
                            handleDeleteSubject(subject);
                            break;
                        case "CANCEL":
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    // ---------------- 수업 추가 ----------------

    private class AddSubjectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddSubjectDialog dialog = new AddSubjectDialog(parentFrame);
            dialog.setVisible(true);
            if (dialog.isSubmitted()) {
                registerSubjectAndUpdateUI(dialog);
            }
        }
    }

    private void registerSubjectAndUpdateUI(AddSubjectDialog dialog) {
        String name = dialog.getSubjectName();
        List<TimeSlot> slots = dialog.getTimeSlots();

        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "과목명을 입력해야 합니다.");
            return;
        }
        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "하나 이상의 시간대를 추가해야 합니다.");
            return;
        }
        if (!manager.canRegister(slots)) {
            JOptionPane.showMessageDialog(this, "시간이 중복됩니다.");
            return;
        }

        Subject subject = new Subject(name);
        manager.register(slots, subject);
        saveManager();
        updateTimetableDisplay();
    }

    // ---------------- 수업 수정 ----------------

    private void handleEditSubject(Subject subjectToEdit) {
        List<TimeSlot> existingSlots = manager.getSlotsForSubject(subjectToEdit);
        AddSubjectDialog editDialog = new AddSubjectDialog(this.parentFrame, subjectToEdit, existingSlots, true);
        editDialog.setVisible(true);

        if (editDialog.isSubmitted()) {
            manager.unregisterSubject(subjectToEdit);

            String newName = editDialog.getSubjectName();
            List<TimeSlot> newSlots = editDialog.getTimeSlots();

            if (!manager.canRegister(newSlots)) {
                JOptionPane.showMessageDialog(this, "중복된 시간이 존재합니다. 변경을 취소합니다.");
                manager.register(existingSlots, subjectToEdit);
            } else {
                Subject updatedSubject = new Subject(newName);
                manager.register(newSlots, updatedSubject);
                saveManager();
            }
            updateTimetableDisplay();
        }
    }

    // ---------------- 수업 삭제 ----------------

    private void handleDeleteSubject(Subject subjectToDelete) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "'" + subjectToDelete.getTitle() + "' 수업의 모든 시간표를 삭제하시겠습니까?",
                "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.unregisterSubject(subjectToDelete);

            // 관련 저장 파일 삭제
            File file = new File(subjectToDelete.getTitle() + "_data.ser");
            if (file.exists()) {
                file.delete();
            }

            saveManager();
            updateTimetableDisplay();
        }
    }

    // ---------------- UI 업데이트 ----------------

    private void updateTimetableDisplay() {
        for (String time : TIMES) {
            for (String day : DAYS) {
                String key = day + "_" + time;
                JButton cell = cellMap.get(key);
                if (cell == null) continue;

                Subject subject = manager.getSubject(new TimeSlot(day, time));
                if (subject != null) {
                    cell.setText(subject.getTitle());
                    cell.setBackground(manager.getColor(subject.getTitle()));
                    cell.setOpaque(true);
                    cell.setEnabled(true);
                } else {
                    cell.setText("");
                    cell.setBackground(UIManager.getColor("Button.background"));
                    cell.setOpaque(true);
                    cell.setEnabled(true);
                }
            }
        }
    }
}
