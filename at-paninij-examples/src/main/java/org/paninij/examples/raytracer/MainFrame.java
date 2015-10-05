package org.paninij.examples.raytracer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = -785558159000970342L;
    JLabel label;
    ImageIcon imageHolder;

    public MainFrame(String title, int width, int height) {
        super(title);
        setLayout(new BorderLayout());
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        imageHolder = new ImageIcon();
        getContentPane().add(new JLabel(imageHolder));
        setVisible(true);
    }

    public void setImage(BufferedImage img) {
        imageHolder.setImage(img);
        this.pack();
        repaint();
    }

}
