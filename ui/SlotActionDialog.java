// 파일명: SlotActionDialog.java
// 위치: ui 패키지 내부

//package ui;
package ui;

import javax.swing.*;

import timetable.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SlotActionDialog extends JDialog {

    private JLabel subjectNameLabel;

    private JButton goToFolderButton;
    private JButton editButton;
    private JButton deleteButton;
    // "취소" 버튼은 사용자 요청에 따라 이전 단계에서 제거했습니다.

    private String chosenAction = null;

    public SlotActionDialog(JFrame parent, Subject subject, TimeSlot slot) {
        super(parent, "과목 정보 및 작업", true);

        // 정보 표시용 라벨
        subjectNameLabel = new JLabel("과목: " + subject.getTitle());
        
        // 액션 버튼
        goToFolderButton = new JButton("폴더로 이동");
        editButton = new JButton("수업 수정");
        deleteButton = new JButton("수업 삭제");

        // --- UI 레이아웃 설정 ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.add(subjectNameLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10)); // (행, 열, 가로갭, 세로갭)
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15)); // 버튼 패널 여백 조정

        // 버튼들 사이에 간격 추가 및 가운데 정렬 효과를 위한 처리
        Dimension buttonSize = new Dimension(200, 40); // 버튼 크기 통일 (선택 사항)
        goToFolderButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        
        goToFolderButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);     // 가운데 정렬
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);   // 가운데 정렬

        buttonPanel.add(goToFolderButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(infoPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // --- 각 버튼에 명명된 내부 클래스 리스너 연결 ---
        goToFolderButton.addActionListener(new FolderButtonListener());
        editButton.addActionListener(new EditButtonListener());
        deleteButton.addActionListener(new DeleteButtonListener());

        pack();
        setLocationRelativeTo(parent);
    }

    // --- 명명된 내부 클래스 리스너 정의 ---
    private class FolderButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            chosenAction = "FOLDER";
            dispose();
        }
    }

    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            chosenAction = "EDIT";
            dispose();
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            chosenAction = "DELETE";
            dispose();
        }
    }
    // "취소" 버튼 리스너는 버튼 자체가 제거되었으므로 필요 없습니다.

    public String getChosenAction() {
        return chosenAction;
    }
}