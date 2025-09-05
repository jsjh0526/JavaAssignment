package ui;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;

import subjectfolder.*;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MemoEditorUI {
    private final InsideMemo memo;
    private final JFrame frame;
    private final JTextPane textPane;
    private final JLabel updatedLabel;
    private final JTextField fontSizeField;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<Object, Boolean> styleStates = new HashMap<>();
    private final Map<Object, JButton> styleButtons = new HashMap<>();

    public MemoEditorUI(InsideMemo memo) {
        this.memo = memo;
        this.frame = new JFrame("Memo Editor");
        this.textPane = new JTextPane();
        this.updatedLabel = new JLabel();
        this.fontSizeField = new JTextField("14", 3);
        initUI();
    }

    private void initUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // 상단 툴바 패널 
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 글자 크기 조절
        JButton plusBtn = new JButton("+");
        JButton minusBtn = new JButton("-");

        plusBtn.addActionListener(e -> adjustFontSize(2));
        minusBtn.addActionListener(e -> adjustFontSize(-2));
        fontSizeField.addActionListener(e -> applyFontSize());

        toolbar.add(new JLabel("Font Size:"));
        toolbar.add(fontSizeField);
        toolbar.add(plusBtn);
        toolbar.add(minusBtn);

        // 스타일 버튼들 (굵게 B, 기울임 I, 밑줄 U, 취소선 S)
        addStyleToggleButton(toolbar, "B", StyleConstants.Bold);
        addStyleToggleButton(toolbar, "I", StyleConstants.Italic);
        addStyleToggleButton(toolbar, "U", StyleConstants.Underline);
        addStyleToggleButton(toolbar, "S", StyleConstants.StrikeThrough);

        // 저장 버튼
        JButton saveBtn = new JButton("저장");
        saveBtn.addActionListener(e -> {
            try {
                RTFEditorKit rtfKit = new RTFEditorKit();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                rtfKit.write(out, textPane.getDocument(), 0, textPane.getDocument().getLength());
                String rtfContent = out.toString("UTF-8");
                memo.setContent(rtfContent);
                updateTimestamp();
                JOptionPane.showMessageDialog(frame, "저장되었습니다.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "저장 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        toolbar.add(saveBtn);

        frame.add(toolbar, BorderLayout.NORTH);

        // 메모 텍스트 영역 (RTF 로 불러오기)
        try {
            RTFEditorKit rtfKit = new RTFEditorKit();
            textPane.setEditorKit(rtfKit);
            rtfKit.read(new StringReader(memo.getContent()), textPane.getDocument(), 0);
        } catch (Exception ex) {
            textPane.setText(""); // 초기 메모는 빈 값
        }

        textPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
        frame.add(new JScrollPane(textPane), BorderLayout.CENTER);

        // 수정 시간 표시
        updatedLabel.setText("Last modified: " + formatter.format(memo.getLastUpdatedTime()));
        updatedLabel.setForeground(new Color(0, 0, 0, 100));
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.add(updatedLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addStyleToggleButton(JPanel toolbar, String label, Object styleConstant) {
        JButton button = new JButton(label);
        styleStates.put(styleConstant, false);
        styleButtons.put(styleConstant, button);

        button.addActionListener(e -> {
            boolean enabled = !styleStates.get(styleConstant);
            styleStates.put(styleConstant, enabled);
            button.setBackground(enabled ? Color.DARK_GRAY : null);
            toggleStyle(styleConstant, enabled);
        });

        toolbar.add(button);
    }

    private void updateTimestamp() {
        updatedLabel.setText("Last modified: " + formatter.format(memo.getLastUpdatedTime()));
    }

    private void applyFontSize() {
        try {
            int size = Integer.parseInt(fontSizeField.getText());
            textPane.setFont(textPane.getFont().deriveFont((float) size));
        } catch (NumberFormatException ignored) {}
    }

    private void adjustFontSize(int delta) {
        try {
            int size = Integer.parseInt(fontSizeField.getText()) + delta;
            size = Math.max(8, size);
            fontSizeField.setText(String.valueOf(size));
            applyFontSize();
        } catch (NumberFormatException ignored) {}
    }

    private void toggleStyle(Object styleConstant, boolean enable) {
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        if (start == end) return;

        Style style = textPane.addStyle("style", null);
        if (styleConstant == StyleConstants.Bold) StyleConstants.setBold(style, enable);
        if (styleConstant == StyleConstants.Italic) StyleConstants.setItalic(style, enable);
        if (styleConstant == StyleConstants.Underline) StyleConstants.setUnderline(style, enable);
        if (styleConstant == StyleConstants.StrikeThrough) StyleConstants.setStrikeThrough(style, enable);

        doc.setCharacterAttributes(start, end - start, style, false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoEditorUI(new InsideMemo()));
    }
}

/*
 * JTextPane RTFEditorKit 사용
 * InsideMemo 객체의 내용을 편집 UI. (크기 조절, 굵게/기울임/밑줄/취소선) 
 * MainScreen 메모를 더블클릭 
 */ 

