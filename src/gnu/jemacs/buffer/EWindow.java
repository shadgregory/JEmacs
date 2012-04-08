// Copyright (c) 2002  Per M.A. Bothner.
// This is free software;  for terms and warranty disclaimer see ./COPYING.

package gnu.jemacs.buffer;
import gnu.lists.*;
import gnu.mapping.*;
import java.util.*;

public abstract class EWindow
{
  public EFrame frame;
  public Buffer buffer;

  protected int[] pendingKeys = null;
  protected int pendingLength = 0;

 /** Previous window in cyclic window ordering. */
  protected EWindow prevWindow;

  /** Next window in cyclic window ordering. */
  protected EWindow nextWindow;
  
  /** Nominal height in pixels of a character, if non-zero. */
  protected int charHeight;

  /** Nominal width in pixels of a character, if non-zero. */
  protected int charWidth;

  public EWindow (Buffer buffer)
  {
    this.buffer = buffer;
    this.nextWindow = this;
    this.prevWindow = this;
  }

  public static EWindow getSelected()
  {
    return EFrame.selectedFrame == null ? null
      : EFrame.selectedFrame.selectedWindow;
  }

  public void setSelected()
  {
    EWindow selected = getSelected();
    if (selected != null && selected.buffer != buffer)
      selected.unselect();

    if (frame != null)
      frame.selectedWindow = this;
    EFrame.selectedFrame = frame;
    Buffer.setCurrent(buffer);

  }

  public void saveBuffersKillEmacs ()
    {
	java.util.Hashtable buffers = gnu.jemacs.buffer.Buffer.getFileBuffers();
	if (buffers.size() == 0)
	    java.lang.System.exit(0);
	Enumeration k = buffers.keys();
	boolean promptOnExit = false;
	boolean cancelExit = false;
	boolean finished = false;
	boolean saveAll = false;
	boolean noAll = false;
	while (k.hasMoreElements())
	  {
	    String key = (String) k.nextElement();
	    Buffer theBuffer = (Buffer)buffers.get(key);
	    if (noAll)
	      break;
	    if (saveAll)
	      {
		theBuffer.save();
		continue;
	      }
	    try
	      {
		if (theBuffer.upToDate())
		  continue;
		else
		  {
		    int selection =
		      frame.showCancelQuestionMessage(
						      " Save file " +
						      theBuffer.getFileName() +
						      "?");
		    switch (selection)
		      {
		      case 0: theBuffer.save();break;
		      case 1: promptOnExit = true;break;
		      case 2: break;
		      case 3:
			String diff = new String();
			try
			  {
			    diff = theBuffer.getDiff();
			  }catch (Exception e) {}
			Buffer buffer = EToolkit.getInstance().newBuffer("*Diff*");
			buffer.insert(diff,null,0);
			Buffer.setCurrent(buffer);
			EWindow selectedWindow = getSelected();
			selectedWindow.split(buffer, 50, false);
			cancelExit = true;
			break;
		      case 4: theBuffer.save();finished = true;break;
		      case 5: theBuffer.save();saveAll = true;break;
		      case 6: noAll = true;break;
		      }
		    if (finished)
		      {
			if (k.hasMoreElements())
			  promptOnExit = true;
			break;
		      }
		  }
	      }
	    catch (Exception e){}
	  }
	if (promptOnExit)
	  {
	    if (frame.showQuestionMessage("Modified buffers exist; exit anyway?") == 0)
	      java.lang.System.exit(0);
	  }
	else if (cancelExit)
	  {
	    return;
	  }
	else
	  {
	    java.lang.System.exit(0);
	  }
    }

  public abstract void unselect();
  
  public static void setSelected(EWindow window)
  {
    window.setSelected();
    window.requestFocus();
  }

  public void requestFocus()
  {
  }

  public EFrame getFrame()
  {
    return frame;
  }

  public final void setFrame(EFrame frame) { this.frame = frame; }

  public Buffer getBuffer()
  {
    return buffer;
  }

  public void setBuffer (Buffer buffer)
  {
    this.buffer = buffer;
  }

  /** Returns the "Emacs value" (1-origin) of point. */
  public abstract int getPoint();

  public final void setPoint(int point)
  {
    setDot(point - 1);
  }

  public abstract void setDot(int offset);

  /** Split this window into two, showing this buffer in both windows.
   * @return the new wndow.
   */
  public final EWindow split (int lines, boolean horizontal)
  {
    return split(buffer, lines, horizontal);
  }

  /** Split this window into two.
   * Display Var>buffer</var> in the new window.
   * @return the new window.
   */
  public abstract EWindow split (Buffer buffer, int lines, boolean horizontal);

  /** Link a new window after this. */
  protected final void linkSibling (EWindow window, boolean horizontal)
  {
    EWindow next = nextWindow;
    this.nextWindow = window;
    window.prevWindow = this;
    window.nextWindow = next;
    // next is non-null, since the order is cyclic.
    next.prevWindow = window;
  }

  protected final void unlink()
  {
    if (frame.firstWindow == this)
      {
	if (nextWindow == this)
	  frame.firstWindow = null;
	else
	  frame.firstWindow = nextWindow;
      }
    nextWindow.prevWindow = prevWindow;
    prevWindow.nextWindow = nextWindow;
    prevWindow = this;
    nextWindow = this;
  }

  /** Return the next/previous window in the cyclic order of windows.
   * Returns null if this is the last/first window in this EFrame. */
  public EWindow getNextWindow(boolean forwards)
  {
    return nextWindow;
  }

  /** Return the next/previous EWindow in the cyclic order of windows.
   * Returns first/last if this is the last/first window in this EFrame. */
  public final EWindow getOtherWindow(boolean forwards)
  {
    EWindow win = getNextWindow(forwards); // FIXME
    /*
    if (win == null)
      win = forwards ? frame.getFirstWindow() : frame.getLastWindow();
    */
    return win;
  }

  public final EWindow getNextWindowInFrame(int count)
  {
    EWindow win = this;
    if (count > 0)
      {
        while (--count >= 0)
          win = win.getOtherWindow(true);
      }
    else
      {
        while (++count <= 0)
          win = win.getOtherWindow(false);
      }
    return win;
  }

  public void delete()
  {
    EFrame frame = this.frame;
    deleteNoValidate();
    if (frame.getFirstWindow() == null)
      frame.delete();
    else
      frame.validate();
  }

  protected void deleteNoValidate()
  {
    if (frame.selectedWindow == this)
      {
        EWindow next = getNextWindowInFrame(1);
        if (frame == EFrame.selectedFrame)
          setSelected(next);
        else
          frame.selectedWindow = next;
      }
    unlink();
    frame = null;
  }

  public void deleteOtherWindows()
  {
    for (EWindow cur = getNextWindow(true); cur != this; )
      {
        EWindow next = cur.getNextWindow(true);
        cur.deleteNoValidate();
        cur = next;
      }
    requestFocus();
    frame.validate();
  }

  protected abstract void getCharSize();

  /** Get the current width (in pixels) of this window. */
  public abstract int getWidth ();

  /** Get the current height (in pixels) of this window. */
 public abstract int getHeight ();

  public int getHeightInLines()
  {
    if (charHeight == 0)
      getCharSize();
    return getHeight() / charHeight;
  }

  public int getWidthInColumns()
  {
    if (charWidth == 0)
      getCharSize();
    return getWidth() / charWidth;
  }

  public String toString()
  {
    StringBuffer sbuf = new StringBuffer(100);
    sbuf.append("#<window on ");
    if (buffer == null)
      sbuf.append("no buffer");
    else
      {
        sbuf.append('\"');
        sbuf.append(buffer.getName());
        sbuf.append('\"');
      }
    sbuf.append(" 0x");
    sbuf.append(Integer.toHexString(System.identityHashCode(this)));
    sbuf.append('>');
    return sbuf.toString();
  }

  public void pushPrefix(int prefix)
  {
    if (pendingKeys == null)
      pendingKeys = new int[10];
    pendingKeys[pendingLength++] = prefix;
  }

  public Object lookupKey(int key)
  {
    for (int j = 0;  j < buffer.activeLength;  j++)
      {
        EKeymap actual = buffer.activeKeymaps[j];
        Object action = actual.lookupKey(pendingKeys, pendingLength,
                                  key, j < buffer.activeLength - 1);
        if (action != null)
	  return action;
      }
    return EKeymap.ignorable(key) ? null : tooLong(pendingLength); 
  }

  public abstract Object tooLong(int pendingLength);
  
  public void handleKey (int code)
  {
    Object command = lookupKey(code);

    pushPrefix(code);
    pendingLength--;
    handleCommand (command);
  }

  public void handleCommand (Object command)
  {
    if (command instanceof String || command instanceof Symbol)
      {
	Object resolved = Command.resolveSymbol(command);
	if (resolved == null)
	  throw new Error("no function defined for "+command);
	command = resolved;
      }

    if (command instanceof EKeymap)
      {
	 if (pendingKeys[pendingLength] != 0)
	   pendingLength++;
	 return;
      }

    pendingLength = 0;

    try
      {
	Procedure proc = (Procedure) command;
	Object interactive = proc.getProperty("emacs-interactive", null);
	if (interactive != null)
	  {
	    if (interactive instanceof String)
	      {
		proc.applyN(Command.processInteractionString(interactive.toString()));
	      }
            else if (interactive == LList.Empty)
              proc.apply0();
	    else
	      {
		System.err.println("not implemented: interactive not a string");
		proc.apply0();
	      }
	  }
	else
          {
            // System.err.println("procedure "+proc+" is not interactive");
            proc.apply0();
          }
      }
    catch (CancelledException ex)
      {
	// Do nothing.
      }
    catch (RuntimeException ex)
      {
	throw ex;
      }
    catch (Error ex)
      {
	throw ex;
      }
    catch (Throwable ex)
      {
	throw new WrappedException(ex);
      }

  }
}
