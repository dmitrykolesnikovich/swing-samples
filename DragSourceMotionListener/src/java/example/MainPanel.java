package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.activation.*;
import javax.swing.*;
import javax.swing.text.*;

public class MainPanel{
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
        JFrame f1 = new JFrame("@title@");
        JFrame f2 = new JFrame();
        f1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DragPanel p1 = new DragPanel();
        DragPanel p2 = new DragPanel();

        p1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        p2.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        p1.add(new JLabel(UIManager.getIcon("OptionPane.warningIcon")));
        p1.add(new JLabel(UIManager.getIcon("OptionPane.questionIcon")));
        p1.add(new JLabel(UIManager.getIcon("OptionPane.informationIcon")));
        p1.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon")));
        p1.add(new JLabel("Text"));

        MouseListener handler = new Handler();
        p1.addMouseListener(handler);
        p2.addMouseListener(handler);

        LabelTransferHandler th = new LabelTransferHandler();
        p1.setTransferHandler(th);
        p2.setTransferHandler(th);

        JPanel p = new JPanel(new GridLayout(2,1));
        p.add(new JScrollPane(new JTextArea()));
        p.add(p2);
        f1.getContentPane().add(p1);
        f2.getContentPane().add(p);
        f1.setSize(320, 240);
        f2.setSize(320, 240);
        f1.setLocationRelativeTo(null);
        Point pt = f1.getLocation();
        pt.translate(340, 0);
        f2.setLocation(pt);
        f1.setVisible(true);
        f2.setVisible(true);
    }
}

class DragPanel extends JPanel {
    public DragPanel() {
        super();
    }
    public JLabel draggingLabel;
}
class Handler extends MouseAdapter {
    @Override public void mousePressed(MouseEvent e) {
        DragPanel p = (DragPanel)e.getSource();
        Component c = SwingUtilities.getDeepestComponentAt(p, e.getX(), e.getY());
        if(c!=null && c instanceof JLabel) {
            p.draggingLabel = (JLabel)c;
            p.getTransferHandler().exportAsDrag(p, e, TransferHandler.MOVE);
        }
    }
}
class LabelTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    private final JLabel label = new JLabel() {
        @Override public boolean contains(int x, int y) {
            return false;
        }
    };
    private final JWindow window = new JWindow();
    public LabelTransferHandler() {
        super("Text");
        //System.out.println("LabelTransferHandler");
        localObjectFlavor = new ActivationDataFlavor(DragPanel.class, DataFlavor.javaJVMLocalObjectMimeType, "JLabel");
        window.add(label);
        window.setAlwaysOnTop(true);
        com.sun.awt.AWTUtilities.setWindowOpaque(window, false); // JDK 1.6.0
        //window.setBackground(new Color(0,true)); // JDK 1.7.0
        DragSource.getDefaultDragSource().addDragSourceMotionListener(
            new DragSourceMotionListener() {
                @Override public void dragMouseMoved(DragSourceDragEvent dsde) {
                    Point pt = dsde.getLocation();
                    //pt.translate(5, 5); // offset
                    window.setLocation(pt);
                    window.setVisible(true);
                }
            });
    }
    @Override protected Transferable createTransferable(JComponent c) {
        System.out.println("createTransferable"+localObjectFlavor.getMimeType());
        DragPanel p = (DragPanel)c;
        JLabel l = p.draggingLabel;
        final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
        String text = l.getText();
        if(text==null) return dh;
        final StringSelection ss = new StringSelection(text+"\n");
        return new Transferable() {
            @Override public DataFlavor[] getTransferDataFlavors() {
                ArrayList<DataFlavor> list = new ArrayList<DataFlavor>();
                for(DataFlavor f:ss.getTransferDataFlavors()) {
                    list.add(f);
                }
                for(DataFlavor f:dh.getTransferDataFlavors()) {
                    list.add(f);
                }
                return list.toArray(dh.getTransferDataFlavors());
            }
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                for (DataFlavor f: getTransferDataFlavors()) {
                    if(flavor.equals(f)) {
                        return true;
                    }
                }
                return false;
            }
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if(flavor.equals(DataFlavor.stringFlavor)) {
                    return ss.getTransferData(flavor);
                }else if(flavor.equals(DataFlavor.plainTextFlavor)) {
                    return ss.getTransferData(flavor);
                }else if(flavor.equals(localObjectFlavor)) {
                    return dh.getTransferData(flavor);
                }else{
                    throw new UnsupportedFlavorException(flavor);
                }
            }
        };
    }
    @Override public boolean canImport(TransferSupport support) {
        if(!support.isDrop()) {
            return false;
        }
        return true;
    }
    @Override public int getSourceActions(JComponent c) {
        System.out.println("getSourceActions");
        DragPanel p = (DragPanel)c;
        label.setIcon(p.draggingLabel.getIcon());
        label.setText(p.draggingLabel.getText());
        window.add(label);
        window.pack();
        return MOVE;
    }
    @Override public boolean importData(TransferSupport support) {
        System.out.println("importData");
        if(!canImport(support)) return false;
        DragPanel target = (DragPanel)support.getComponent();
        try{
            DragPanel src = (DragPanel)support.getTransferable().getTransferData(localObjectFlavor);
            JLabel l = new JLabel();
            l.setIcon(src.draggingLabel.getIcon());
            l.setText(src.draggingLabel.getText());
            target.add(l);
            target.revalidate();
            return true;
        }catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
    @Override protected void exportDone(JComponent c, Transferable data, int action) {
        System.out.println("exportDone");
        DragPanel src = (DragPanel)c;
        if(action == TransferHandler.MOVE) {
            src.remove(src.draggingLabel);
            src.revalidate();
            src.repaint();
        }
        src.draggingLabel = null;
        window.setVisible(false);
    }
}