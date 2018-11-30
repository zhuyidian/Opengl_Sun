package com.qcwd.galaxy;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by lenovo on 2018/5/23.
 */

public class Ball extends ShapeUtil{

    private float r = 1;
    private int aPosition;
    private int aTexture;
    private int uMatrix;
    private FloatBuffer posFb, texFb;
    private int count = 0;
    private int increaseY = 5;
    private int increaseXZ = 5;

    public Ball(Resources resources, float r){
        this.r = r;
        initData();
        initShader(resources);
    }

    private void initData() {
        initPosition();
        initTexture(360 / increaseXZ + 1, 180 / increaseY + 1);
    }

    private void initPosition(){
        ArrayList<Float> posList = new ArrayList<Float>();
        for (int i = 90; i > -90 - increaseY; i -= increaseY ) {
            float y = r * sin(i);
            float rXZ = r * cos(i);
            float y1 = r * sin(i - increaseY);
            float rXZ1 = r * cos(i - increaseY);
            for (int j = 0; j < 360 + increaseXZ; j += increaseXZ) {
                float x = rXZ * cos(j);
                float z = rXZ * sin(j);
                float x1y = rXZ * cos(j + increaseXZ);
                float z1y = rXZ * sin(j + increaseXZ);
                float xy1 = rXZ1 * cos(j);
                float zy1 = rXZ1 * sin(j);
                float x1y1 = rXZ1 * cos(j + increaseXZ);
                float z1y1 = rXZ1 * sin(j + increaseXZ);
                addPoint(posList, x, y, z);
                addPoint(posList, xy1, y1, zy1);
                addPoint(posList, x1y, y, z1y);
                addPoint(posList, x1y, y, z1y);
                addPoint(posList, xy1, y1, zy1);
                addPoint(posList, x1y1, y1, z1y1);
            }
        }
        count = posList.size();
        float[] pos = new float[count];
        for (int i = 0; i < count; i++) {
            pos[i] = posList.get(i);
        }
        posFb = getBuffer(pos);
        count /= 3;
    }
    private void initTexture(int wNum, int hNum){
        float increaseS = 1f / wNum;
        float increaseT = 1f / hNum;
        float[] tex = new float[wNum * hNum * 6 * 2];
        int index = 0;
        for (int i = 0; i < hNum; i++) {
            float t = increaseT * i;
            for (int j = wNum; j > 0; j--) {
                float s = increaseS * j;
                tex[index++] = s;
                tex[index++] = t;

                tex[index++] = s;
                tex[index++] = t + increaseT;

                tex[index++] = s - increaseS;
                tex[index++] = t;

                tex[index++] = s - increaseS;
                tex[index++] = t;

                tex[index++] = s;
                tex[index++] = t + increaseT;

                tex[index++] = s - increaseS;
                tex[index++] = t + increaseT;
            }
        }
        texFb = getBuffer(tex);
    }
    private void addPoint(ArrayList<Float> list, float x, float y, float z){
        list.add(x);
        list.add(y);
        list.add(z);
    }
    private void initShader(Resources resources) {
        createProgram(resources, "ball.vert", "ball.frag");
        aPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        aTexture = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uMatrix = GLES20.glGetUniformLocation(mProgram, "uMatrix");
    }

    public void drawSelf(int textureId){
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aTexture);
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, posFb);
        GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 0, texFb);
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, MatrixUtil.getFinalMatrix(), 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aTexture);
    }

}
