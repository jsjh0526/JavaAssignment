package ui;

import subjectfolder.*;
import timetable.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

public class MainScreen extends JFrame {
    private Folder root;
    private Folder currentFolder;

    private DefaultListModel<Schedule> scheduleListModel = new DefaultListModel<>();
    private DefaultListModel<Memo> memoListModel = new DefaultListModel<>();
    private DefaultListModel<Material> materialListModel = new DefaultListModel<>();

    private JList<Schedule> scheduleList = new JList<>(scheduleListModel);
    private JList<Memo> memoList = new JList<>(memoListModel);
    private JList<Material> materialList = new JList<>(materialListModel);
    
    private JLabel infoLabel = new JLabel();

    private JTree folderTree;
    private DefaultTreeModel treeModel;

	public MainScreen(Folder folder) {
        this.root = folder;
        this.currentFolder = folder;

        setTitle("자료 관리 시스템");
        setSize(1000, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        DefaultMutableTreeNode rootNode = createTreeNode(root);
        treeModel = new DefaultTreeModel(rootNode);
        folderTree = new JTree(treeModel);
        folderTree.setRootVisible(true);
        
        folderTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
                if (selectedNode == null) return;
                Object userObj = selectedNode.getUserObject();
                if (userObj instanceof Folder) {
                    currentFolder = (Folder) userObj;
                    updateAll();
                }
            }
        });

        
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBorder(BorderFactory.createTitledBorder("일정"));
        schedulePanel.add(new JScrollPane(scheduleList), BorderLayout.CENTER);
        JPanel pane1 = new JPanel();
        JButton addScheduleBtn = new JButton("일정 추가");
        JButton deleteScheduleBtn = new JButton("일정 삭제");
        pane1.add(addScheduleBtn);
        pane1.add(deleteScheduleBtn);
        schedulePanel.add(pane1, BorderLayout.SOUTH);

        JPanel memoPanel = new JPanel(new BorderLayout());
        memoPanel.setBorder(BorderFactory.createTitledBorder("메모"));
        memoPanel.add(new JScrollPane(memoList), BorderLayout.CENTER);
        JPanel pane2 = new JPanel();
        JButton addMemoBtn = new JButton("메모 추가");
        JButton deleteMemoBtn = new JButton("메모 삭제");
        pane2.add(addMemoBtn);
        pane2.add(deleteMemoBtn);
        memoPanel.add(pane2, BorderLayout.SOUTH);

        JPanel folderPanel = new JPanel(new BorderLayout());
        folderPanel.setBorder(BorderFactory.createTitledBorder("폴더"));
        folderPanel.add(new JScrollPane(folderTree), BorderLayout.CENTER);
        JPanel pane3 = new JPanel();
        JButton addFolderBtn = new JButton("폴더 추가");
        JButton deleteFolderBtn = new JButton("폴더 삭제");
        pane3.add(addFolderBtn);
        pane3.add(deleteFolderBtn);
        folderPanel.add(pane3, BorderLayout.SOUTH);
        
        JPanel materialPanel = new JPanel(new BorderLayout());
        materialPanel.setBorder(BorderFactory.createTitledBorder("자료"));
        materialPanel.add(new JScrollPane(materialList), BorderLayout.CENTER);
        JPanel pane4 = new JPanel();
        JButton addMaterialBtn = new JButton("자료 추가");
        JButton deleteMaterialBtn = new JButton("자료 삭제");
        pane4.add(addMaterialBtn);
        pane4.add(deleteMaterialBtn);
        materialPanel.add(pane4, BorderLayout.SOUTH);
        
        addScheduleBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("일정 내용:"));
                JTextField contentField = new JTextField();
                panel.add(contentField);

                panel.add(new JLabel("날짜 선택:"));

                // 날짜 선택용 JComboBox
                JPanel datePanel = new JPanel(new FlowLayout());

                // 연도, 월, 일 콤보박스
                JComboBox<Integer> yearBox = new JComboBox<>();
                JComboBox<Integer> monthBox = new JComboBox<>();
                JComboBox<Integer> dayBox = new JComboBox<>();

                int currentYear = Calendar.getInstance().get(Calendar.YEAR);               
                for (int y = currentYear - 5; y <= currentYear + 5; y++) {
                    yearBox.addItem(y);
                }

                for (int m = 1; m <= 12; m++) {
                    monthBox.addItem(m);
                }

                // 기본 일수는 31일
                for (int d = 1; d <= 31; d++) {
                    dayBox.addItem(d);
                }

                // 월 변경 시 일수 조정
                ActionListener updateDays = evt -> {
                    int selectedYear = (int) yearBox.getSelectedItem();
                    int selectedMonth = (int) monthBox.getSelectedItem();
                    int maxDays;

                    switch (selectedMonth) {
                        case 2:
                            // 윤년 체크
                            boolean isLeap = (selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0);
                            maxDays = isLeap ? 29 : 28;
                            break;
                        case 4: case 6: case 9: case 11:
                            maxDays = 30;
                            break;
                        default:
                            maxDays = 31;
                    }

                    dayBox.removeAllItems();
                    for (int d = 1; d <= maxDays; d++) {
                        dayBox.addItem(d);
                    }
                };

                yearBox.addActionListener(updateDays);
                monthBox.addActionListener(updateDays);

                // 초기화 후 일수 맞춤
                updateDays.actionPerformed(null);

                datePanel.add(yearBox);
                datePanel.add(new JLabel("년"));
                datePanel.add(monthBox);
                datePanel.add(new JLabel("월"));
                datePanel.add(dayBox);
                datePanel.add(new JLabel("일"));
                panel.add(datePanel);

                int result = JOptionPane.showConfirmDialog(null, panel, "일정 추가", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String content = contentField.getText();
                    Integer year = (Integer) yearBox.getSelectedItem();
                    Integer month = (Integer) monthBox.getSelectedItem();
                    Integer day = (Integer) dayBox.getSelectedItem();

                    if (content != null && !content.isEmpty() && year != null && month != null && day != null) {
          
                    	LocalDate date = LocalDate.of(year, month, day);
                        Schedule s = new Schedule(content, date);
                        currentFolder.addSchedule(s);
                        updateSchedules();
                    }
                }
        	}
        });

        deleteScheduleBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		Schedule selected = scheduleList.getSelectedValue();
                if (selected != null) {
                    currentFolder.removeSchedule(selected);
                    updateSchedules();
                }
        	}
        });
        
        
        addMemoBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String memoText = JOptionPane.showInputDialog("메모를 입력하세요:");
                if (memoText != null && !memoText.isEmpty()) {
                    Memo m = new Memo(memoText);
                    currentFolder.addMemo(m);
                    updateMemos();
                }
        	}
        });
        
        deleteMemoBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		Memo selected = memoList.getSelectedValue();
                if (selected != null) {
                    currentFolder.removeMemo(selected);
                    updateMemos();
                }
        	}
        });
        
        
        addFolderBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String name = JOptionPane.showInputDialog("폴더 이름:");
                if (name != null && !name.isEmpty()) {
                    Folder f = new Folder(name, currentFolder.getSubject());
                    currentFolder.addSubfolder(f);
                    updateTree();
                }
        	}
        });
        
        deleteFolderBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		TreePath path = folderTree.getSelectionPath();
                if (path != null && path.getPathCount() > 1) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Folder parent = (Folder) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
                    Folder toRemove = (Folder) selectedNode.getUserObject();
                    parent.removeSubfolder(toRemove);
                    updateTree();
                }
        	}
        });
        
        
        addMaterialBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    Material m = new Material(file.getName(), file.getAbsolutePath());
                    currentFolder.addMaterial(m);
                    updateMaterials();
                }
        	}
        });
        
        deleteMaterialBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		Material selected = materialList.getSelectedValue();
                if (selected != null) {
                    currentFolder.removeMaterial(selected);
                    updateMaterials();
                }
        	}
        });
        
        scheduleList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Schedule selected = scheduleList.getSelectedValue();
                    if (selected != null) {
                        LocalDate today = LocalDate.now();
                        LocalDate scheduleDate = selected.getDate();
                        /*
                        LocalDate scheduleDate = selected.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
						*/
                        long daysBetween = ChronoUnit.DAYS.between(today, scheduleDate);

                        String message;
                        if (daysBetween > 0) {
                            message = daysBetween + "일 남았습니다.";
                        } else if (daysBetween == 0) {
                            message = "오늘입니다!";
                        } else {
                            message = Math.abs(daysBetween) + "일 지났습니다.";
                        }

                        JOptionPane.showMessageDialog(null,
                                String.format("[%s]\n%s", selected.getTitle(), message),
                                "일정 정보",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        memoList.addMouseListener(new MouseListener() {
        	@Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Memo selected = memoList.getSelectedValue();
                    if (selected != null) {
                        new MemoEditorUI(selected.getInsideMemo());
                    }
                }
            }

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
        });

        
        materialList.addMouseListener(new MouseListener() {
        	@Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Material m = materialList.getSelectedValue();
                    if (m != null) {
                    	try {
                            Desktop.getDesktop().open(new File(m.getContent()));
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "파일을 열 수 없습니다.");
                        }
                    }
                }
            }
            
            @Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
        });

        JPanel sec1 = new JPanel(new GridLayout(2, 1));
        sec1.add(schedulePanel);
        sec1.add(memoPanel);
        
        JPanel sec2 = new JPanel(new GridLayout(1, 2));
        sec2.add(folderPanel);
        sec2.add(materialPanel);

        updateInfoLabel();
 

        JPanel all = new JPanel(new BorderLayout());
        all.add(infoLabel, BorderLayout.NORTH);
        all.add(sec1, BorderLayout.CENTER);
        all.add(sec2, BorderLayout.SOUTH);
        
        add(all);
        updateAll();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                FileManager.save(root);
            }
        });
    }

    private DefaultMutableTreeNode createTreeNode(Folder folder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        for (Folder sub : folder.getSubfolders()) {
            node.add(createTreeNode(sub));
        }
        return node;
    }

    private void updateTree() {
        DefaultMutableTreeNode rootNode = createTreeNode(root);
        treeModel.setRoot(rootNode);
        for (int i = 0; i < folderTree.getRowCount(); i++) {
            folderTree.expandRow(i);
        }
        updateAll();
    }

    private void updateInfoLabel() {
        String info = String.format("과목명: %s",
                currentFolder.getSubject());
        infoLabel.setText(info);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
    }

    private void updateAll() {
        updateInfoLabel();
        updateSchedules();
        updateMemos();
        updateMaterials();
    }

    private void updateSchedules() {
        scheduleListModel.clear();
        for (Schedule s : currentFolder.getSchedules()) {
            scheduleListModel.addElement(s);
        }
    }
    
    private void updateMemos() {
        memoListModel.clear();
        for (Memo m : currentFolder.getMemos()) {
            memoListModel.addElement(m);
        }
    }

    private void updateMaterials() {
        materialListModel.clear();
        for (Material m : currentFolder.getMaterials()) {
            materialListModel.addElement(m);
        }
    }
    
    public static void main(String[] args) {
        Subject subject = new Subject(); 
        Folder folder = FileManager.load(subject);
        SwingUtilities.invokeLater(new Runnable() {
			    @Override
			    public void run() {
		        new MainScreen(folder).setVisible(true);
			    }
				});
    }

}