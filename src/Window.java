import javax.swing.*;
import java.awt.*;

/**
 * Created by jakub on 15.11.15.
 */
public class Window extends JFrame {

    // Playback toolbar
    JToolBar playbackToolbar;
    JButton playButton;
    JButton pauseButton;
    JButton stopButton;
    JButton nextButton;
    JButton prevButton;
    JButton randomButton;
    JButton loadButton;



    // Switch-to-edit button
    JButton editButton;
    JList playList;

    public Window(){
        setupPlaybackButtons();


    }

    private void setupPlaybackButtons(){
        playButton = new JButton("Play");
        playButton.setToolTipText("Play");

        pauseButton = new JButton("Pause");
        pauseButton.setToolTipText("Pause");

        stopButton = new JButton("Stop");
        stopButton.setToolTipText("Stop");

        nextButton = new JButton("Next");
        nextButton.setToolTipText("Next track");;

        prevButton = new JButton("Prev");
        prevButton.setToolTipText("Previous track");

        randomButton = new JButton("Random");
        randomButton.setToolTipText("Play random track");

        loadButton = new JButton("Load");
        loadButton.setToolTipText("Load new track from filesystem");

        editButton = new JButton("Edit mode");
        editButton.setToolTipText("Switch to edit mode");

        playbackToolbar = new JToolBar("Playback toolbar", JToolBar.HORIZONTAL);

        playbackToolbar.add(playButton);
        playbackToolbar.add(pauseButton);
        playbackToolbar.add(stopButton);
        playbackToolbar.add(nextButton);
        playbackToolbar.add(prevButton);
        playbackToolbar.addSeparator();
        playbackToolbar.add(randomButton);
        playbackToolbar.addSeparator();
        playbackToolbar.add(loadButton);
        playbackToolbar.addSeparator();
        playbackToolbar.add(editButton);

    }


}
