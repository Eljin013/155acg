#version 430

uniform uint cType;
in vec4 colorOne;
in vec4 colorTwo;

out vec4 finalColor;
void main(void)
{
	if(cType == 0)
		finalColor = colorOne;
	else
		finalColor = colorTwo;
}