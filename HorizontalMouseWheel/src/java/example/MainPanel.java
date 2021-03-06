package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;

public final class MainPanel extends JPanel {
    private final JLabel label = new JLabel() {
        @Override public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, Color.ORANGE, w, h, Color.WHITE, true));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
        @Override public Dimension getPreferredSize() {
            return new Dimension(640, 640);
        }
    };
    private final JScrollPane scroll = new JScrollPane(label);

    private MainPanel() {
        super(new BorderLayout());

        label.setBorder(BorderFactory.createTitledBorder("Horizontal scroll: CTRL + Wheel"));
        label.addMouseWheelListener(new MouseWheelListener() {
            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                Component c = e.getComponent();
                Container s = SwingUtilities.getAncestorOfClass(JScrollPane.class, c);
                if (Objects.nonNull(s)) {
                    JScrollPane sp = (JScrollPane) s;
                    JComponent sb = e.isControlDown() ? sp.getHorizontalScrollBar() : sp.getVerticalScrollBar();
                    sb.dispatchEvent(SwingUtilities.convertMouseEvent(c, e, sb));
                }
            }
        });

        scroll.getVerticalScrollBar().setUnitIncrement(10);

        JScrollBar hsb = scroll.getHorizontalScrollBar();
        hsb.setUnitIncrement(10);
        hsb.addMouseWheelListener(new MouseWheelListener() {
            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                JScrollBar hsb = (JScrollBar) e.getComponent();
                Container p = SwingUtilities.getAncestorOfClass(JScrollPane.class, hsb);
                if (Objects.nonNull(p)) {
                    JViewport vport = ((JScrollPane) p).getViewport();
                    Point vp = vport.getViewPosition();
                    int d = hsb.getUnitIncrement() * e.getWheelRotation();
                    vp.translate(d, 0);
                    JComponent v = (JComponent) SwingUtilities.getUnwrappedView(vport);
                    v.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
                }
            }
        });

        add(scroll);
        setPreferredSize(new Dimension(320, 240));
    }
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
