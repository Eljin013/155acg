package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class MoveCircular extends AbstractAction {
	private Model mdl;
	
	public MoveCircular(Model model) {
		super("Circular");
		this.mdl = model;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		mdl.moveCircular();
		
	}

}
