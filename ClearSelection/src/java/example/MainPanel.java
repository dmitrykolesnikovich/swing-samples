package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    public MainPanel() {
        super(new GridLayout(1,2));
        JList list = new JList(makeModel()) {
            private MouseAdapter listener;
            @Override public void updateUI() {
                removeMouseListener(listener);
                removeMouseMotionListener(listener);
                setForeground(null);
                setBackground(null);
                setSelectionForeground(null);
                setSelectionBackground(null);
                super.updateUI();
                if(listener==null) listener = new ClearSelectionListener();
                addMouseListener(listener);
                addMouseMotionListener(listener);
            }
        };
        //list.putClientProperty("List.isFileList", Boolean.TRUE);
//     list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
//     list.setFixedCellWidth(64);
//     list.setFixedCellHeight(64);
//     list.setVisibleRowCount(0);

        add(makeTitledPanel("Default", new JList(makeModel())));
        add(makeTitledPanel("clearSelection", list));
        setPreferredSize(new Dimension(320, 240));
    }
    private DefaultListModel makeModel() {
        DefaultListModel model = new DefaultListModel();
        model.addElement("aaaaaaa");
        model.addElement("bbbbbbbbbbbbb");
        model.addElement("cccccccccc");
        model.addElement("ddddddddd");
        model.addElement("eeeeeeeeee");
        return model;
    }
    private static JComponent makeTitledPanel(String title, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(new JScrollPane(c));
        return p;
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class ClearSelectionListener extends MouseAdapter {
    private static void clearSelectionAndFocus(JList list) {
        list.clearSelection();
        list.getSelectionModel().setAnchorSelectionIndex(-1);
        list.getSelectionModel().setLeadSelectionIndex(-1);
    }
    private static boolean contains(JList list, Point pt) {
        for(int i=0;i<list.getModel().getSize();i++) {
            Rectangle r = list.getCellBounds(i, i);
            if(r.contains(pt)) return true;
        }
        return false;
    }
    private boolean startOutside = false;
    @Override public void mousePressed(MouseEvent e) {
        JList list = (JList)e.getSource();
        startOutside = !contains(list, e.getPoint());
        if(startOutside) {
            clearSelectionAndFocus(list);
        }
    }
    @Override public void mouseReleased(MouseEvent e) {
        startOutside = false;
    }
    @Override public void mouseDragged(MouseEvent e) {
        JList list = (JList)e.getSource();
        if(contains(list, e.getPoint())) {
            startOutside = false;
        }else if(startOutside) {
            clearSelectionAndFocus(list);
        }
    }
}