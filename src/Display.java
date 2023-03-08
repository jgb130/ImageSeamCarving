
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.FileInputStream;
import java.io.IOException;

public class Display extends JFrame
   {
    private JLabel imageView;
    private SeamCarver seamCarver;
    private String path;

    public Display() {
        setTitle("Image Edit");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        JLabel pathDescription = new JLabel("Enter image path");
        pathDescription.setBounds(40, 30, 150, 40);

        JTextField imagePath = new JTextField();
        imagePath.setBounds(155, 30, 295, 40);

        JButton getImage = new JButton("Get Image");
        getImage.setBounds(460, 30, 100, 40);
        getImage.addActionListener(i -> {
                try {
                    seamCarver = new SeamCarver(ImageIO.read(new FileInputStream(imagePath.getText())));
                    updateImage(seamCarver.image());
                    path = imagePath.getText();
                }
                catch(IOException exc) {
                    System.out.println("Invalid path ~ " + imagePath.getText());
                    System.out.println("EX Dir ~ /Users/191bloomj/Desktop/ImageSeamCarving/images/image.jpg");
                }
            });

        JButton reset = new JButton("Reset Image");
        reset.setBounds(460, 70, 100, 40);
        reset.addActionListener(i -> {
                if(seamCarver != null) {
                    try{
                        seamCarver = new SeamCarver(ImageIO.read(new FileInputStream(path)));
                        updateImage(seamCarver.image());
                    }
                    catch(Exception exc) {
                        exc.printStackTrace();
                    }
                }
            });

        JButton removeHorizontal = new JButton("Remove Horizontal Seam");
        removeHorizontal.setBounds(202, 510, 180, 35);
        removeHorizontal.addActionListener(i -> {
                if(seamCarver != null) {
                    try{
                        seamCarver.highlightHorizontalSeam();
                        updateImage(seamCarver.highlighted());

                        seamCarver.removeHorizontalSeam();
                    }
                    catch(Exception exc) {
                        exc.printStackTrace();
                    }
                }
            });

        JButton removeVertical = new JButton("Remove Verticle Seam");
        removeVertical.setBounds(385, 510, 160, 35);
        removeVertical.addActionListener(i -> {
                if(seamCarver != null){
                    try {
                        seamCarver.highlightVerticalSeam();
                        updateImage(seamCarver.highlighted());

                        seamCarver.removeVerticalSeam();
                    }
                    catch(Exception exc) {
                        exc.printStackTrace();
                    }
                }
            });

        JTextField hoResize = new JTextField();
        hoResize.setBounds(130, 510, 35, 35);

        JTextField veResize = new JTextField();
        veResize.setBounds(165, 510, 35, 35);

        JButton resize = new JButton("Resize");
        resize.setBounds(45, 510, 85, 35);
        resize.addActionListener(i -> {
                if(seamCarver != null){
                    try {
                        int hoSize = Integer.parseInt(hoResize.getText());
                        int hoAmt = seamCarver.image().getWidth() - hoSize;
                        int veSize = Integer.parseInt(veResize.getText());
                        int veAmt = seamCarver.image().getHeight() - veSize;

                        for(int x = 0; x < hoAmt; x++)
                            seamCarver.removeVerticalSeam();
                        for(int x = 0; x < veAmt; x++)
                            seamCarver.removeHorizontalSeam();

                        updateImage(seamCarver.image());
                    }
                    catch(Exception exc) {
                        exc.printStackTrace();
                        return;
                    }
                }
            });

        add(pathDescription);

        add(imagePath);

        add(getImage);

        add(reset);

        add(removeHorizontal);

        add(removeVertical);

        add(hoResize);

        add(veResize);

        add(resize);

        repaint();
    }

    public void updateImage(BufferedImage image)
       {
        if(imageView != null)
           {
            remove(imageView);
           }

        imageView = new JLabel(new ImageIcon(image));
        imageView.setBounds((getWidth() / 2) - (image.getWidth() / 2), (getHeight() / 2) - (image.getHeight() / 2), image.getWidth(), image.getHeight());

        add(imageView);
        repaint();
       }
   }
