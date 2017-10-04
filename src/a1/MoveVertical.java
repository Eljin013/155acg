package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class MoveVertical extends AbstractAction {
	private Model mdl;
	
	public MoveVertical(Model model) {
		super("Vertical");
		this.mdl = model;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		mdl.moveVertical();
	}

}
