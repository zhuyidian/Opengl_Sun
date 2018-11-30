attribute vec3 aPosition;
attribute vec2 aTexture;
uniform mat4 uMatrix;
varying vec2 vTexture;

void main() {
	gl_Position = uMatrix * vec4(aPosition, 1.0);
	vTexture = aTexture;
}
