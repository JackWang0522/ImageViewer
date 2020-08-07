import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created by Zixi Wang on 5/3/2017.
 */
public class Viewer {
    public static void main(String[] args){
        PhotoViewerFrame frame = new PhotoViewerFrame();
        frame.setTitle("Pic Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.fitImage();
    }

    /**
     * Created by Zixi Wang on 5/3/2017.
     */
    public static class PhotoViewerFrame extends JFrame{

        // initial frame size
        private final int FRAME_WIDTH = 1000;
        private final int FRAME_HEIGHT = 600;

        // create frame layout elements
        private JPanel _upperPanel;
        private JPanel _controlPanel;
        private JButton _leftButton;
        private JButton _rightButton;
        private JButton _enlargeButton;
        private JButton _narrowButton;
        private JButton _fitPanelButton;
        private JLabel _photoLabel;
        private JScrollPane _scrollPane;
        private JCheckBox _autoPlayCheckBox;
        private JSlider _autoPlaySpeedSlider;
        private JLabel _autoPlaySliderLabel;
        private BufferedImage _img = null;
        private Timer timer = new Timer(1, new timerListener());

        // During testing, next picture
        private int timerCounter = 0;

        // create a new filter for file input
        private FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                // over ride filter, only files with .jpg ending can be passed.
                return file.getName().toLowerCase().endsWith(".jpg");
            }
        };

        // create variables for file inputs
        private String folderPath = System.getProperty("user.home") + "/Pictures";
        private File filePath = new File(folderPath);
        private File[] files = filePath.listFiles(filter);
        private int fileCount = files.length;
        private int currentIndex;

        /**
         * Frame constructor
         */
        public PhotoViewerFrame(){
            setSize(FRAME_WIDTH,FRAME_HEIGHT);
            createComponents();
        }

        /**
         * create components in the frame
         */
        private void createComponents(){
            // Upper panel of the frame, include panel display pictures, next picture button and previous picture button
            _upperPanel = new JPanel();
            _upperPanel.setLayout(new BorderLayout());
            _leftButton = new JButton("<---");
            _rightButton = new JButton("--->");
            _photoLabel = new JLabel();
            _scrollPane = new JScrollPane(_photoLabel);
            _rightButton.addActionListener(new rightButtonListener());
            _leftButton.addActionListener(new leftButtonListener());
            _upperPanel.add(_leftButton, BorderLayout.WEST);
            _upperPanel.add(_scrollPane,BorderLayout.CENTER);
            _upperPanel.add(_rightButton, BorderLayout.EAST);

            // Lower panel with control settings, auto play checkbox, auto play speed slider and zoom in, zoom out, fit
            _autoPlayCheckBox = new JCheckBox("Autoplay");
            _autoPlayCheckBox.addActionListener(new autoPlayCheckBoxListener());
            _autoPlaySpeedSlider = new JSlider(0,10,5);
            _autoPlaySpeedSlider.setMajorTickSpacing(5);
            _autoPlaySpeedSlider.setMinorTickSpacing(1);
            _autoPlaySpeedSlider.setPaintTicks(true);
            _autoPlaySpeedSlider.setPaintLabels(true);
            _autoPlaySpeedSlider.addChangeListener(new sliderListener());
            timer.setDelay(_autoPlaySpeedSlider.getValue() * 1000);
            _autoPlaySliderLabel = new JLabel("sec");
            _enlargeButton = new JButton("larger");
            _narrowButton = new JButton("narrow");
            _fitPanelButton = new JButton("fit");
            _enlargeButton.addActionListener(new enlargeButtonListener());
            _narrowButton.addActionListener(new narrowButtonListener());
            _fitPanelButton.addActionListener(new fitButtonListener());
            _controlPanel = new JPanel();
            _controlPanel.add(_autoPlayCheckBox);
            _controlPanel.add(_autoPlaySpeedSlider);
            _controlPanel.add(_autoPlaySliderLabel);
            _controlPanel.add(_enlargeButton);
            _controlPanel.add(_narrowButton);
            _controlPanel.add(_fitPanelButton);
            add(_upperPanel, BorderLayout.CENTER);
            add(_controlPanel, BorderLayout.SOUTH);

            // If there are picture files, set image to label, if not set a text saying no image can be displayed
            if(files.length != 0) {
                currentIndex = 0;
                setPhotoLabel(files[currentIndex]);
            }
            else {
                _photoLabel = new JLabel("No image in the path\n");
                _leftButton.setEnabled(false);
                _rightButton.setEnabled(false);
            }
        }

        /**
         * Actionlistener that display next image
         */
        private class rightButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent event){
                nextImage();
            }
        }

        /**
         * Actionlistener that display previous image
         */
        private class leftButtonListener implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent event){
                previousImage();
            }
        }

        /**
         * Actionlistener that enlarge the image
         */
        private class enlargeButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                enlargeImage();
            }
        }

        /**
         * Actionlistener that narrow the image
         */
        private class narrowButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                narrowImage();
            }
        }

        /**
         * Actionlistener that fit the image to the panel
         */
        private class fitButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                fitImage();
            }
        }

        /**
         * Actionlistener that start or stop auto play
         */
        private class autoPlayCheckBoxListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                if(_autoPlayCheckBox.isSelected()) {
                    timer.start();
                }
                else {
                    timer.stop();
                }
            }
        }

        /**
         * Changelistener that change the speed of auto-play
         */
        private class sliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent changeEvent){
                timerCounter = 0;
                timer.setDelay(_autoPlaySpeedSlider.getValue() * 1000);
                if(_autoPlayCheckBox.isSelected()) {
                    timer.restart();
                }
            }
        }

        /**
         * Actionlistener that bounded with timer, will be called once every time interval
         */
        private class timerListener implements ActionListener{
            public void actionPerformed(ActionEvent event){
                timer.setDelay(_autoPlaySpeedSlider.getValue() * 1000);
                if(timerCounter == 0) {
                    timerCounter = 1;
                    timer.setDelay(1);
                }
                else {
                    nextImage();
                }
            }
        }

        /**
         * display the next image, if the last image reached, display the first image
         */
        private void nextImage(){
            if(currentIndex == fileCount - 1){
                currentIndex = 0;
                setPhotoLabel(files[currentIndex]);
            }
            else {
                currentIndex++;
                setPhotoLabel(files[currentIndex]);
            }
        }

        /**
         * display the previous image, if the first image reached, display the last image
         */
        private void previousImage(){
            if(currentIndex == 0){
                currentIndex = fileCount - 1;
                setPhotoLabel(files[currentIndex]);
            }
            else {
                currentIndex--;
                setPhotoLabel(files[currentIndex]);
            }
        }

        /**
         * make a image larger
         */
        private void enlargeImage(){
            int height = (int)(_photoLabel.getIcon().getIconHeight()*1.1);
            int width = (int)(_photoLabel.getIcon().getIconWidth()*1.1);
            _photoLabel.setIcon(new ImageIcon(_img.getScaledInstance(width,
                    height, Image.SCALE_DEFAULT)));
        }

        /**
         * make a image smaller
         */
        private void narrowImage(){
            int height = (int)(_photoLabel.getIcon().getIconHeight()*0.9);
            int width = (int)(_photoLabel.getIcon().getIconWidth()*0.9);
            _photoLabel.setIcon(new ImageIcon(_img.getScaledInstance(width,
                    height, Image.SCALE_DEFAULT)));
        }

        /**
         * fit the image to the panel
         * this function is static because when the frame is created but not displayed, photo label does not have a size, so
         * the size of the picture will be set right after display the frame from main function, directly called.
         */
        public void fitImage(){
            int width = _img.getWidth();
            int height = _img.getHeight();
            int scroll_panel_width = _scrollPane.getWidth();
            int scroll_panel_height = _scrollPane.getHeight();
            double height_ratio = ((double)height) / scroll_panel_height;
            double width_ratio = (double)width / scroll_panel_width;
            double ratio = (double)width / height;
            if (height_ratio == width_ratio){
                setPhotoLabelIcon(scroll_panel_width,scroll_panel_height);
            }
            else if(height_ratio > width_ratio) {
                setPhotoLabelIcon((int)(scroll_panel_height * ratio), scroll_panel_height);
            }
            else {
                setPhotoLabelIcon(scroll_panel_width, (int)(scroll_panel_width / ratio));
            }
        }

        /**
         * set image in the photo label
         * @param width
         * @param height
         */
        private void setPhotoLabelIcon(int width, int height){
            _photoLabel.setIcon(new ImageIcon(_img.getScaledInstance(width, height, Image.SCALE_DEFAULT)));
        }

        /**
         * read a photo and set it into panel
         * @param imgFile
         */
        private void setPhotoLabel(File imgFile){
            try {
                _img = ImageIO.read(new File(imgFile.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(_scrollPane.getWidth()!=0) {
                fitImage();
            }
        }
    }

}

