#version 300 es
precision mediump float;//��������Ĭ�Ͼ���
in vec2 vTextureCoord;//���մӶ�����ɫ�������Ĳ���
in vec4 vAmbient;
in vec4 vDiffuse;
in vec4 vSpecular;
out vec4 fragColor;
uniform sampler2D sTexture;//������������
void main()                         
{  //������ɫ����main����
  //����ƬԪ�������в�������ɫֵ            
  vec4 finalColor = texture(sTexture, vTextureCoord); 
  //����ƬԪ��ɫֵ 
  // fragColor = finalColor*vAmbient+finalColor*vSpecular+finalColor*vDiffuse;
   fragColor =finalColor;
}
