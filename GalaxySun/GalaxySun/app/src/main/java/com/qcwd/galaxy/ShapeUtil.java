package com.qcwd.galaxy;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by lenovo on 2018/5/23.
 */

public class ShapeUtil {
    protected int mProgram;

    public FloatBuffer getBuffer(float[] array){
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }
    public float sin(float degrees){
        return (float) Math.sin(Math.toRadians(degrees));
    }
    public float cos(float degrees){
        return (float) Math.cos(Math.toRadians(degrees));
    }
    public void createProgram(Resources resources, String verName, String fragName){
        mProgram = createProgram(getSourceFromAssets(resources, verName), getSourceFromAssets(resources, fragName));
    }
    private int createProgram(String verSource, String fragSource){
        int verShader = loadShader(GLES20.GL_VERTEX_SHADER, verSource);
        if (verShader == 0) {
            return 0;
        }
        int fragShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragSource);
        if (fragShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, verShader);
            checkError("AttachVerShader");
            GLES20.glAttachShader(program, fragShader);
            checkError("AttachFragShader");
            GLES20.glLinkProgram(program);
            int[] status = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] == 0) {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    private String getSourceFromAssets(Resources resources, String fileName){
        try {
            InputStream is = resources.getAssets().open(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ch = 0;
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            byte[] args = baos.toByteArray();
            baos.close();
            is.close();
            String result = new String(args, "UTF-8");
            return result.replaceAll("\\r\\n", "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int loadShader(int type, String source){
        int shader = GLES20.glCreateShader(type);
        if (shader != 0){
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] status = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
    private void checkError(String op){
        int error = 0;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR){
            throw new RuntimeException("checkError " + op + "  " + error);
        }
    }
}
