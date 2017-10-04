#version 430

uniform float cxInc;
uniform float cyInc;
uniform float vInc;
uniform float sInc;
out vec4 colorOne;
out vec4 colorTwo;

void main(void)
{ if (gl_VertexID == 0) {
    gl_Position = vec4( 0.25+sInc+cxInc,-0.25-sInc+vInc+cyInc, 0.0, 1.0);
	colorOne = vec4(0.0, 0.0, 1.0, 1.0);
	colorTwo = vec4(1.0, 0.0, 0.0, 1.0);
	}
  else if (gl_VertexID == 1) {
    gl_Position = vec4(-0.25-sInc+cxInc,-0.25-sInc+vInc+cyInc, 0.0, 1.0);
	colorOne = vec4(0.0, 0.0, 1.0, 1.0);
	colorTwo = vec4(0.0, 1.0, 0.0, 1.0);
  }
  else  {
    gl_Position = vec4( 0.0+cxInc, 0.25+sInc+vInc+cyInc, 0.0, 1.0);
	colorOne = vec4(0.0, 0.0, 1.0, 1.0);
	colorTwo = vec4(0.0, 0.0, 1.0, 1.0);
  }
}