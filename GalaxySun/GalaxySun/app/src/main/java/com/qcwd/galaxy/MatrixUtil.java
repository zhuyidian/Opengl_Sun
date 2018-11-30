package com.qcwd.galaxy;

import android.opengl.Matrix;

import java.util.Stack;

/**
 * Created by lenovo on 2018/5/23.
 */
public class MatrixUtil {

    private static float[] camera = new float[16];
    private static float[] project = new float[16];
    private static float[] change = new float[16];
    private static Stack<float[]> mStack = new Stack<float[]>();

    public static void push(){
        mStack.push(change.clone());
    }

    public static void pop(){
        change = mStack.pop();
    }

    public static void init(){
        Matrix.setIdentityM(change, 0);
    }

    public static void setCamera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        Matrix.setLookAtM(camera, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public static void frustum(float left, float right, float bottom, float top, float near, float far){
        Matrix.frustumM(project, 0, left, right, bottom, top, near, far);
    }

    public static void ortho(float left, float right, float bottom, float top, float near, float far){
        Matrix.orthoM(project, 0, left, right, bottom, top, near, far);
    }

    public static void translate(float x, float y, float z){
        Matrix.translateM(change, 0, x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z){
        Matrix.rotateM(change,0, angle, x, y, z);
    }

    //最终矩阵 = 投影 * （相机 * 变化）
    public static float[] getFinalMatrix(){
        float[] result = new float[16];
        Matrix.multiplyMM(result, 0, camera, 0, change, 0);
        Matrix.multiplyMM(result, 0, project, 0, result, 0);
        return result;
    }

}
