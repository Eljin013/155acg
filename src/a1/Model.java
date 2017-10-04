package a1;

import javax.swing.*;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;

import java.awt.BorderLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import graphicslib3D.GLSLUtils;

@SuppressWarnings("serial")
public class Model extends JFrame implements GLEventListener, MouseWheelListener {
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	//private GLSLUtils util = new GLSLUtils();
	
	//determines the movement of the triangle
	private boolean isVertical = true;
	private boolean isCircular = false;
	private boolean colorChanged = false;
	private boolean posSizeChg = false;
	private boolean negSizeChg = false;
	
	//variables for vertical movement
	private float vertY = 0.0f;
	private float vInc = 0.01f;
	
	//variables for circular movement
	private float cxInc = 0.5f;
	private float cyInc = 0.0f;
	private float theta = 0.0f;
	private float tInc = 0.1f;
	
	//variables for color change
	private int cType = 0;
	
	//variables for size change
	private float s = 0.0f;
	private float sInc = 0.1f;
	
	//instantiate commands
	private ChangeColor colorCom;
	private MoveVertical vMoveCom;
	private MoveCircular cMoveCom;
	
	
	
	public Model() {
		//belongs to the JFrame
		setTitle("Assignment 1");
		setSize(800, 800);
		
		//create the interfaces
		JPanel myPanel = new JPanel();
		this.add(myPanel, BorderLayout.NORTH);

		JButton circleButton = new JButton("Circular");
		cMoveCom = new MoveCircular(this);
		circleButton.setAction(cMoveCom);
		myPanel.add(circleButton);
		
		JButton verticalButton = new JButton("Vertical");
		vMoveCom = new MoveVertical(this);
		verticalButton.setAction(vMoveCom);
		myPanel.add(verticalButton);
		
		JPanel cPanel = new JPanel();
		this.add(cPanel, BorderLayout.CENTER);
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = cPanel.getInputMap(mapName);
		KeyStroke cKey = KeyStroke.getKeyStroke('c');
		imap.put(cKey, "color");
		ActionMap amap = cPanel.getActionMap();
		colorCom = new ChangeColor(this);
		amap.put("color", colorCom);
		this.requestFocus();
		
		this.addMouseWheelListener(this);
		
		//initialize the GLCanvas
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		setVisible(true);
		
		//responsible for the animation
		FPSAnimator animator = new FPSAnimator(myCanvas, 30);
		animator.start();
	}
	
	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		//Print out the versions
		System.out.println("OpenGL Version\t: " + gl.glGetString(GL_VERSION));
		System.out.println("JOGL Version\t: " + 
				Package.getPackage("com.jogamp.opengl").getImplementationVersion());
		System.out.println("Java Version\t: " + System.getProperty("java.version"));
		
		//creates the rendering program
		rendering_program = createShaderProgram();
		
		//
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(rendering_program);
		
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		
		//changes the size of the triangle if the size has changed
		if(posSizeChg || negSizeChg) {
			if(posSizeChg) {
				posSizeChg = false;
				s += sInc;
			}
			else {
				negSizeChg = false;
				s -= sInc;
			}
			int sOffset_loc = gl.glGetUniformLocation(rendering_program, "sInc");
			gl.glProgramUniform1f(rendering_program, sOffset_loc, s);
		}
		
		//changes between solid and gradient colors
		if(colorChanged) {
			colorChanged = false;
			int colorType = gl.glGetUniformLocation(rendering_program, "cType");
			gl.glProgramUniform1ui(rendering_program, colorType, cType);
		}
			
		//moves the triangle in a vertically if the boolean is set
		if(isVertical) {
			vertY += vInc;
			if (vertY > 1.0f) vInc = -0.01f;
			if (vertY < -1.0f) vInc = 0.01f;
			int offset_loc = gl.glGetUniformLocation(rendering_program, "vInc");
			gl.glProgramUniform1f(rendering_program, offset_loc, vertY);
		}
		
		//moves in a circular pattern if the boolean is set
		if(isCircular) {
			
			cxInc = .5f * ((float) Math.cos((double) theta));
			//System.out.println("cxInc: " + cxInc);
			cyInc = .5f * ((float) Math.sin((double) theta));
			//System.out.println("cyInc: " + cyInc);
			theta += tInc;
			//System.out.println("theta: " + theta);
			int cirXOff = gl.glGetUniformLocation(rendering_program, "cxInc");
			gl.glProgramUniform1f(rendering_program, cirXOff, cxInc);
			int cirYOff = gl.glGetUniformLocation(rendering_program, "cyInc");
			gl.glProgramUniform1f(rendering_program, cirYOff, cyInc);
		}
		
		
		
		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}

	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];
	

		String vshaderSource[] = GLSLUtils.readShaderSource("src/a1/vert.shader");
		String fshaderSource[] = GLSLUtils.readShaderSource("src/a1/frag.shader");
		int lengths[];

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);		
		
		checkOpenGLError();  // can use returned boolean if desired
		gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1)
		{	System.out.println("Vertex compilation success.");
		} else
		{	System.out.println("Vertex compilation failed.");
			printShaderLog(vShader);
		}
		
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);		
		
		checkOpenGLError();  // can use returned boolean if desired
		gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1)
		{	System.out.println("Fragment compilation success.");
		} else
		{	System.out.println("Fragment compilation failed.");
			printShaderLog(fShader);
		}
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		
		gl.glLinkProgram(vfprogram);
		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1)
		{	System.out.println("Linking succeeded.");
		} else
		{	System.out.println("Linking failed.");
			printProgramLog(vfprogram);
		}
		
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
	}
	
	private void printShaderLog(int shader)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}

	void printProgramLog(int prog)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine length of the program compilation log
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}

	boolean checkOpenGLError()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR)
		{	System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//System.out.println("Mouse wheel moved");
		if(e.getWheelRotation() < 0)
			posSizeChg = true;
		else if(e.getWheelRotation() > 0)
			negSizeChg = true;
	}
	
	public void changeColor() {
		colorChanged = true;
		if(cType == 0)
			cType = 1;
		else
			cType = 0;
	}
	
	public void moveVertical() {
		isVertical = true;
		isCircular = false;
	}
	
	public void moveCircular() {
		isCircular = true;
		isVertical = false;
	}
}
