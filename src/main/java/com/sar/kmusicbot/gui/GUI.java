/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sar.kmusicbot.gui;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import javax.swing.*;

import com.sar.kmusicbot.Bot;
import com.sar.kmusicbot.gui.panels.ConsolePanel;


/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class GUI extends JFrame 
{
    private final ConsolePanel console;
    private final Bot bot;
    
    public GUI(Bot bot) 
    {
        super();
        this.bot = bot;
        console = new ConsolePanel();
    }
    
    public void init()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("KMusicBot");

        // console output
        JTextArea text = new JTextArea();
        text.setForeground(Color.white);
        text.setBackground(Color.decode("#23272A"));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setBorder(null);
        PrintStream con=new PrintStream(new TextAreaOutputStream(text));
        System.setOut(con);
        System.setErr(con);

        JScrollPane pane = new JScrollPane();
        pane.setViewportView(text);

        super.setLayout(new GridLayout(1,1));
        super.add(pane);
        super.setPreferredSize(new Dimension(400,300));
        //end

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowListener() 
        {
            @Override public void windowOpened(WindowEvent e) { /* unused */ }
            @Override public void windowClosing(WindowEvent e) 
            {
                try
                {
                    bot.shutdown();
                }
                catch(Exception ex)
                {
                    System.exit(0);
                }
            }
            @Override public void windowClosed(WindowEvent e) { /* unused */ }
            @Override public void windowIconified(WindowEvent e) { /* unused */ }
            @Override public void windowDeiconified(WindowEvent e) { /* unused */ }
            @Override public void windowActivated(WindowEvent e) { /* unused */ }
            @Override public void windowDeactivated(WindowEvent e) { /* unused */ }
        });
    }
}
