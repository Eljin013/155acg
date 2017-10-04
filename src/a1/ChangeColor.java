package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import a1.Model;

@SuppressWarnings("serial")
public class ChangeColor extends AbstractAction {
	private Model mdl;
	
	public ChangeColor(Model model) {
		super("ChangeColor");
		this.mdl = model;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		mdl.changeColor();
		
	}

}
