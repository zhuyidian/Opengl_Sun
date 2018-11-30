uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 a_Color;

varying vec4 v_Color;

void main()                    
{                            
    v_Color = a_Color;
	  	  
    gl_Position = uMVPMatrix * aPosition;
    gl_PointSize = 10.0;          
}          