package br.com.medeirosecia.analyzemail.gui;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import br.com.medeirosecia.analyzemail.domain.service.gmail.AnalyzeGmailInbox;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class GuiSwing extends JFrame {
    public GuiSwing() {
        setTitle("Analyze Email");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 3));

        var labelEmailProvider = new JLabel("Escolha o provedor de email");
        var radioGmail = new JRadioButton("Gmail");
        var radioOutlook = new JRadioButton("Outlook");
        
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(radioGmail);
        radioGroup.add(radioOutlook);

        panel.add(labelEmailProvider);
        panel.add(radioGmail);
        panel.add(radioOutlook);

        var fileChooser = new JFileChooser();
        var buttonSelectCredential = new JButton("Selecionar credencial");        
        buttonSelectCredential.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.showOpenDialog(GuiSwing.this);
            }
        });

        panel.add(buttonSelectCredential);

        JButton buttonStart = new JButton("Iniciar");
        buttonStart.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                //new AnalyzeGmailInbox();
            }
        });

        panel.add(buttonStart);

        JPanel progressBarPanel = new JPanel(new BorderLayout());
        var progressBar = new JProgressBar();
        progressBarPanel.add(progressBar, BorderLayout.CENTER);
        panel.add(progressBarPanel);


        var textArea = new JTextArea();
        textArea.setVisible(false);

        var checkBox = new JCheckBox("Mostra LOG");
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setVisible(checkBox.isSelected());
            }
        });
        
        panel.add(checkBox);
        panel.add(textArea);

        getContentPane().add(panel);

        setVisible(true);
    
    }
}
