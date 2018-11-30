package com.qcwd.galaxy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lenovo on 2018/5/23.
 */

public class FGLView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private int earthId;
    private int moonId;
    private int sunId;
    private Ball earth;
    private Ball moon;
    private Ball sun;
    private float sunSelf = 0;
    private float earthSelf = 0;
    private float earthCom = 0;
    private float moonSelf = 0;
    private float moonCom = 0;

    public FGLView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        MatrixUtil.init();
        earth = new Ball(getResources(), 2);
        moon = new Ball(getResources(), 1);
        sun = new Ball(getResources(), 4);
        earthId = createTextureId(R.drawable.earth);
        moonId = createTextureId(R.drawable.moon);
        sunId = createTextureId(R.drawable.sun);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float r = (float) width/height;
        MatrixUtil.frustum(-r, r, -1, 1, 1, 100);
        MatrixUtil.setCamera(0, 0, 45, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        sunSelf += 0.2f;
        earthSelf += 0.3f;
        earthCom += 0.4f;
        moonSelf += 0.5f;
        moonCom += 0.6f;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        MatrixUtil.push();
        MatrixUtil.rotate(sunSelf, 0, 0, 1);
        sun.drawSelf(sunId);
        MatrixUtil.pop();
        MatrixUtil.push();
        MatrixUtil.rotate(earthSelf, 0, 0, 1);
        MatrixUtil.translate(20, 0, 0);
        MatrixUtil.rotate(earthCom, 0, 0, 1);
        earth.drawSelf(earthId);
        MatrixUtil.push();
        MatrixUtil.rotate(moonSelf, 0, 0, 1);
        MatrixUtil.translate(6, 0, 0);
        MatrixUtil.rotate(moonCom, 0, 0, 1);
        moon.drawSelf(moonId);
        MatrixUtil.pop();
        MatrixUtil.pop();
    }

    private int createTextureId(int drawableId){
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bm, 0);
        bm.recycle();
        return textureId;
    }
}
