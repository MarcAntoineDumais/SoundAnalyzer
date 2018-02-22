package soundanalyzer.gui;
import java.awt.BorderLayout;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import soundanalyzer.audio.AudioConnectionListener;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.audio.AudioOutput;
import soundanalyzer.config.ApplicationContextProvider;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = -4539965066131036060L;

    private AudioInput audioInput;
    private AudioOutput audioOutput;
    private MainPanel mainPanel;
    private JMenuItem mntmStartMicrophone, mntmStopInput;
    private JMenuItem mntmChooseInputDevice;
    private JMenu mnAudioOutput;
    private JMenuItem mntmStartSpeakers;
    private JMenuItem mntmChooseOutputDevice;
    private JMenuItem mntmStopOutput;

    public MainWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnAudioInput = new JMenu("Input");
        menuBar.add(mnAudioInput);

        mntmStartMicrophone = new JMenuItem("Use Microphone");
        mntmStartMicrophone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        audioInput.setMixerInfo(null);
                        audioInput.start();
                    }
                }.start();
            }
        });
        mnAudioInput.add(mntmStartMicrophone);

        mntmChooseInputDevice = new JMenuItem("Choose Device");
        mntmChooseInputDevice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
                Mixer.Info result = (Mixer.Info)JOptionPane.showInputDialog(
                        null,
                        "Please select the audio input source:",
                        "Input Source Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null, mixerInfo, null);

                new Thread() {
                    @Override
                    public void run() {
                        if (result != null) {
                            audioInput.setMixerInfo(result);
                            audioInput.start();
                        }						
                    }
                }.start();
            }
        });
        mnAudioInput.add(mntmChooseInputDevice);

        mntmStopInput = new JMenuItem("Stop Input");
        mntmStopInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        audioInput.stop();
                    }
                }.start();
            }
        });
        mntmStopInput.setEnabled(false);
        mnAudioInput.add(mntmStopInput);
        
        mnAudioOutput = new JMenu("Output");
        menuBar.add(mnAudioOutput);
        
        mntmStartSpeakers = new JMenuItem("Use Speakers");
        mntmStartSpeakers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                audioOutput.setMixerInfo(null);
                audioOutput.start();
            }
        });
        mnAudioOutput.add(mntmStartSpeakers);
        
        mntmChooseOutputDevice = new JMenuItem("Choose Device");
        mntmChooseOutputDevice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
                Mixer.Info result = (Mixer.Info)JOptionPane.showInputDialog(
                        null,
                        "Please select the audio output source:",
                        "Output Source Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null, mixerInfo, null);

                audioOutput.setMixerInfo(result);
                audioOutput.start();
            }
        });
        mnAudioOutput.add(mntmChooseOutputDevice);
        
        mntmStopOutput = new JMenuItem("Stop Output");
        mntmStopOutput.setEnabled(false);
        mntmStopOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                audioOutput.stop();
            }
        });
        mnAudioOutput.add(mntmStopOutput);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new BorderLayout(0, 0));

        mainPanel = new MainPanel();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        setContentPane(contentPane);

        validate();
        pack();
        setLocationRelativeTo(null);
        
        audioInput = ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class);
        audioInput.subscribeConnection(new AudioConnectionListener() {
            @Override
            public void lineOpened() {
                inputLineOpened();
            }

            @Override
            public void lineClosed() {
                inputLineClosed();
            }
        });
        
        audioOutput = ApplicationContextProvider.getApplicationContext().getBean(AudioOutput.class);
        audioOutput.subscribe(new AudioConnectionListener() {
            @Override
            public void lineOpened() {
                outputLineOpened();
            }

            @Override
            public void lineClosed() {
                outputLineClosed();
            }
        });
    }

    public void inputLineOpened() {
        mntmStartMicrophone.setEnabled(false);
        mntmChooseInputDevice.setEnabled(false);
        mntmStopInput.setEnabled(true);
    }

    public void inputLineClosed() {
        mntmStartMicrophone.setEnabled(true);
        mntmChooseInputDevice.setEnabled(true);
        mntmStopInput.setEnabled(false);
    }
    
    public void outputLineOpened() {
        mntmStartSpeakers.setEnabled(false);
        mntmChooseOutputDevice.setEnabled(false);
        mntmStopOutput.setEnabled(true);
    }

    public void outputLineClosed() {
        mntmStartSpeakers.setEnabled(true);
        mntmChooseOutputDevice.setEnabled(true);
        mntmStopOutput.setEnabled(false);
    }
}
