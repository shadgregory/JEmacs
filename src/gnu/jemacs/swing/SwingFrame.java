// Copyright (c) 2002  Per M.A. Bothner.
// This is free software;  for terms and warranty disclaimer see ./COPYING.

package gnu.jemacs.swing;
import gnu.jemacs.buffer.*;
import gnu.lists.LList;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

/** An Emacs frame (EFrame) implemented using the Swing toolkits. */

public class SwingFrame extends EFrame
{
  javax.swing.JFrame jframe;
  JMenuBar menuBar;
  JPanel contents;

  public SwingFrame ()
  {
    super();
  }

  public SwingFrame (Buffer buffer)
  {
    this(new SwingWindow(buffer, true));
  }

  public SwingFrame (SwingWindow window)
  {
    super(window);
    contents = window.wrap();
    jframe = new JFrame(defaultName());
    jframe.getContentPane().add(contents);
    jframe.setSize(800, 600);
    jframe.setVisible(true);
    jframe.setTitle("JEmacs");
    menuBar = new JMenuBar();
    jframe.setJMenuBar(menuBar);
    jframe.setIconImage(new ImageIcon("kawa-logo.png").getImage());
  }

  public boolean isLive()
  {
    return contents != null;
  }

  public void validate ()
  {
    jframe.validate();
  }

  public void delete()
  {
    super.delete();
    contents = null;
    jframe.dispose();
  }

  public void showAboutMessage () 
  {
    JOptionPane.showMessageDialog(jframe, aboutMessage());
  }

  public int showCancelQuestionMessage(String msg)
  {
    CloseDialog dialog = new CloseDialog(jframe,msg);
    return dialog.getAnswer();
  }

  public void showInfoMessage(String msg) 
  {
    JOptionPane.showMessageDialog(jframe, msg);
  }

  public int showQuestionMessage(String msg)
    {
	Object[] options = {"YES","NO","View This Buffer"};
	return JOptionPane.showConfirmDialog(jframe, msg, "Question", JOptionPane.YES_NO_OPTION);
    }


  public String ask(String prompt)
  {
    String result = JOptionPane.showInputDialog(jframe, prompt);
    if (result == null)
      throw new CancelledException();
    return result;
  }

  public void setMenuBar (LList menu)
  {
    menuBar.removeAll();
    java.util.Enumeration e = menu.elements();
    for (int i = 0;  e.hasMoreElements(); i++)
      {
	Object item = e.nextElement();
        if (item == null)
          menuBar.add(Box.createHorizontalGlue());
        else
          menuBar.add(new SwingMenu((LList) item));
      }
    menuBar.updateUI();
  }

  
    public String toString()
    {
	StringBuffer sbuf = new StringBuffer(100);
	sbuf.append("#<frame #");
	sbuf.append(id);
	if (jframe != null)
      {
	sbuf.append(" size: ");
	sbuf.append(jframe.getSize());
	sbuf.append(" preferred: ");
	sbuf.append(jframe.getPreferredSize());
      }
    sbuf.append('>');
    return sbuf.toString();
  }

    private class CloseDialog extends JDialog implements ActionListener 
    {
      private JPanel closePanel = null;
      private JButton yesButton = null;
      private JButton noButton = null;
      private JButton viewButton = null;
      private JButton changesButton = null;
      private JButton saveButton = null;
      private JButton saveAllButton = null;
      private JButton noAllButton = null;
      private JPanel buttonPanel = null;
      private int answer = 0;
      public int getAnswer() { return answer; }

      public CloseDialog(JFrame frame, String msg)
      {
	  super(frame, "Question", true);
	  closePanel = new JPanel();
	  buttonPanel = new JPanel();
	  closePanel.setLayout(new BorderLayout());
	  closePanel.add(new JLabel(msg), BorderLayout.NORTH);
	  closePanel.add(buttonPanel, BorderLayout.SOUTH);
	  yesButton = new JButton("Yes");
	  yesButton.addActionListener(this);
	  buttonPanel.add(yesButton);
	  noButton = new JButton("No");
	  noButton.addActionListener(this);
	  buttonPanel.add(noButton);
	  viewButton = new JButton("View This Buffer");
	  viewButton.addActionListener(this);
	  buttonPanel.add(viewButton);
	  changesButton = new JButton("View Changes In This Buffer");
	  changesButton.addActionListener(this);
	  buttonPanel.add(changesButton);
	  saveButton = new JButton("Save This But No More");
	  saveButton.addActionListener(this);
	  buttonPanel.add(saveButton);
	  saveAllButton = new JButton("Save All Buffers");
	  saveAllButton.addActionListener(this);
	  buttonPanel.add(saveAllButton);
	  noAllButton = new JButton("No For All");
	  noAllButton.addActionListener(this);
	  buttonPanel.add(noAllButton);
	  getContentPane().add(closePanel);
	  pack();
	  setLocationRelativeTo(frame);
	  setVisible(true);
      }

      public void actionPerformed(ActionEvent e)
      {
	  if(yesButton == e.getSource())
	      answer = 0;
	  else if (noButton == e.getSource())
	      answer = 1;
	  else if (viewButton == e.getSource())
	      answer = 2;
	  else if (changesButton == e.getSource())
	      answer = 3;
	  else if (saveButton == e.getSource())
	      answer = 4;
	  else if (saveAllButton == e.getSource())
	      answer = 5;
	  else if (noAllButton == e.getSource())
	      answer = 6;
	  setVisible(false);
      }
  }
}
