package com.marginallyclever.makelangelo.plotter.plottercontrols;

/**
 * Anyone who implements MarlinInterfaceListener is listening to events coming from {@link MarlinPanel}.
 */
public interface MarlinPanelListener {
	void actionPerformed(MarlinPanelEvent e);
}
