package com.scheduler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

public class Main extends JFrame {
  private static final long serialVersionUID = 1L;
  private static boolean admin = false;
  private UserLogin loginDialog;

  public Main(){
    loginDialog = new UserLogin(this, true);
    loginDialog.setVisible(true);
  }
  
  public static void main(String[] args) {
	try {
	  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException e1) {
	  // TODO Auto-generated catch block
	  e1.printStackTrace();
	} catch (InstantiationException e1) {
	  // TODO Auto-generated catch block
	  e1.printStackTrace();
	} catch (IllegalAccessException e1) {
	  // TODO Auto-generated catch block
	  e1.printStackTrace();
	} catch (UnsupportedLookAndFeelException e1) {
	  // TODO Auto-generated catch block
	  e1.printStackTrace();
	}
	
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // Setup Window
        Main frame = new Main();
        frame.setTitle("Logged In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(2,1));
        
        // Create Components
        ButtonActionListener buttonListener = new ButtonActionListener(frame);
        JPanel tablePanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        GridBagConstraints setup = new GridBagConstraints();
        
        // Setup Schedule Table
        Object[][] scheduleData = TableDataRetriever.getTableData("Schedule", new int[]{1, 2, 3, 6, 4 ,5});
        String[] columnNames = new String[]{"Course ID", "Department", "Course", "Start Time", "End Time", "Room"};
        SchedulerTableModel scheduleModel = new SchedulerTableModel(scheduleData, columnNames);
        JTable scheduleTable = new JTable(scheduleModel);
        //scheduleTable.setPreferredScrollableViewportSize(new Dimension(2, 1));    // TODO Fix Layout
        scheduleTable.addMouseListener(new ScheduleTableMouseListener());
        setup.fill = GridBagConstraints.BOTH;
        setup.weightx = 1;
        setup.weighty = 1;
        setup.gridx = 1;
        setup.insets = new Insets(10,5,10,10);
        // Create Scroll Pane for Table
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        scheduleScrollPane.setBorder(BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                                                      "Course Schedule", TitledBorder.CENTER, TitledBorder.TOP));
        tablePanel.add(scheduleScrollPane, setup);
        
     // Setup Course Table
        Object[][] courseData = TableDataRetriever.getTableData("Course", new int[]{2, 4});
        Toolbox.twoDArraySort(courseData, 1);
        courseData = Toolbox.twoDArrayDuplicateRemoval(courseData, 1);
        columnNames = new String[]{"Department", "Course"};
        JTable courseTable = new JTable(courseData, columnNames) {
          private static final long serialVersionUID = 1L;
          @Override
          public boolean isCellEditable(int row, int column){
            return false;
          }
        };
        courseTable.setPreferredScrollableViewportSize(new Dimension(1, 1));
        courseTable.addMouseListener(new CourseTableMouseListener(scheduleModel));
        setup.fill = GridBagConstraints.BOTH;
        setup.weightx = 0.5;
        setup.weighty = 1;
        setup.gridx = 0;
        setup.insets = new Insets(10,10,10,5);
        // Create Scroll Pane for Table
        JScrollPane courseScrollPane = new JScrollPane(courseTable);
        courseScrollPane.setBorder(BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                                                        "Course List", TitledBorder.CENTER, TitledBorder.TOP));
        tablePanel.add(courseScrollPane, setup);
        
        // Admin Only Buttons
        if (Main.isAdmin()) {
          // Requested Changes Button
          JButton requestedChanges = new JButton("Requests");
          requestedChanges.addActionListener(buttonListener);
          requestedChanges.setPreferredSize(new Dimension(100, 25));
          buttonPanel.add(requestedChanges);
          
          // Run Scheduler Button
          JButton runScheduler = new JButton("Scheduler");
          runScheduler.addActionListener(buttonListener);
          runScheduler.setPreferredSize(new Dimension(100, 25));
          buttonPanel.add(runScheduler);
          
          // Update Course List Button
          JButton updateCourses = new JButton("Courses");
          updateCourses.addActionListener(buttonListener);
          updateCourses.setPreferredSize(new Dimension(100, 25));
          buttonPanel.add(updateCourses);
          
          // Update Classroom Database Button
          JButton updateClassrooms = new JButton("Classrooms");
          updateClassrooms.addActionListener(buttonListener);
          updateClassrooms.setPreferredSize(new Dimension(100, 25));
          buttonPanel.add(updateClassrooms);
        }
        
        // Setup Close Button Button
        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 25));
        closeButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Main Close Button should exit the program
            System.exit(0);
          }
        });
        buttonPanel.add(closeButton);
        
        // Set Default Button
        // TODO: Fix this (doesn't set close as default)
        frame.getRootPane().setDefaultButton(closeButton);
        closeButton.requestFocus();
        
        // Add Labels to Window
        frame.add(tablePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
      }
    });
  }
  
  public static boolean isAdmin() {
    return admin;
  }

  // Temporary Fix, Having a public method that makes you admin is probably not wise
  public static void setAdmin(boolean admin) {
    Main.admin = admin;
  }
}
