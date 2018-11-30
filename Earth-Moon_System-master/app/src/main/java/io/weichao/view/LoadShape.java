package io.weichao.view;

import android.content.Context;
import android.opengl.GLES30;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import io.weichao.model.orbitSixElement;
import io.weichao.obj_loader.Obj2OpenJL;
import io.weichao.obj_loader.model.OpenGLModelData;
import io.weichao.obj_loader.model.RawOpenGLModel;
import io.weichao.util.GLES30Util;
import io.weichao.util.MatrixStateUtil;
import io.weichao.util.Point;

/**
 * Created by Administrator on 2017/11/29.
 */

public class LoadShape {
    protected FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    protected FloatBuffer mTexCoordBuffer;//顶点纹理坐标数据缓冲
    protected FloatBuffer mNormalBuffer;    //
    protected int mVertexCount;


    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用
    private int muMMatrixHandle;//位置、旋转变换矩阵
    private int maCameraHandle; //摄像机位置属性引用
    private int maPositionHandle; //顶点位置属性引用
    private int maNormalHandle; //顶点法向量属性引用
    private int maTexCoordHandle; //顶点纹理坐标属性引用
    private int maSunLightLocationHandle;//光源位置属性引用
    private int mTextureId;

    protected Circle orbit; //卫星轨道
    protected orbitSixElement sixElement;

    private Context mContext;
    private float vertices[];
    private float texures[];
    private float normals[];

    public LoadShape(Context context, String mObjFileName, orbitSixElement sixElement) {
        mContext = context;
        //初始化顶点数据
        initVertexData(mObjFileName);
        //初始化着色器
        initScript(context);
        this.sixElement = sixElement;
        orbit = new Circle(context, sixElement.a, new Point(0.0f,0.0f,0.0f), 50);
    }

    /**
     * 初始化顶点数据
     *
     *
     */
    public void initVertexData(String mObjFileName) {
        InputStream is = null;
        try {
            is = mContext.getAssets().open(mObjFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RawOpenGLModel openGLModel = new Obj2OpenJL().convert(is);
        OpenGLModelData openGLModelData = openGLModel.normalize().center().getDataForGLDrawArrays();

        vertices = openGLModelData.getVertices();
        mVertexCount = vertices.length / 3;
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        texures = openGLModelData.getTextureCoordinates();
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(texures.length * 4);
        vbb2.order(ByteOrder.nativeOrder());
        mTexCoordBuffer = vbb2.asFloatBuffer();
        mTexCoordBuffer.put(texures);
        mTexCoordBuffer.position(0);

        normals = openGLModelData.getNormals();
        ByteBuffer vbb3 = ByteBuffer.allocateDirect(normals.length * 4);
        vbb3.order(ByteOrder.nativeOrder());
        mNormalBuffer = vbb3.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);
    }

    /**
     * 初始化着色器
     *
     * @param context
     */
    public void initScript(Context context) {
        mProgram = GLES30Util.loadProgram(context, "model/sphere/script/vertex_shader.sh", "model/sphere/script/fragment_shader.sh");
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点经纬度属性引用
        maTexCoordHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoord");
        //获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用
        maSunLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocationSun");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    public void DrawALL(float mRotAngle)
    {
        //恢复现场
        MatrixStateUtil.popMatrix();
        //保护现场
        MatrixStateUtil.pushMatrix();

        MatrixStateUtil.rotate(mRotAngle + sixElement.w, -1,(float) Math.atan(Math.toRadians(90 - sixElement.i)), 0);
        MatrixStateUtil.translate(0, 0, sixElement.a);
        MatrixStateUtil.rotate(120, 1,0, 0);
        this.draw();

        //恢复现场
        MatrixStateUtil.popMatrix();
        //保护现场
        MatrixStateUtil.pushMatrix();
        MatrixStateUtil.rotate(sixElement.omega, 0,1, 0);
        MatrixStateUtil.rotate(sixElement.i + 6, 0,1, 1);
        orbit.draw();

    }

    public void draw() {
        //指定使用某套着色器程序（必须每次都指定）
        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixStateUtil.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixStateUtil.getMMatrix(), 0);
        // TODO 下面这句有时报错：java.lang.IllegalArgumentException: remaining() < count*3 < needed
        //将摄像机位置传入渲染管线
//        GLES30.glUniform3fv(maCameraHandle, 1, MatrixStateUtil.cameraFB);
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maSunLightLocationHandle, 1, MatrixStateUtil.lightPositionFBSun);

        //将顶点位置数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //将顶点纹理数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(maTexCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoordBuffer);
        //将顶点法向量数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        //启用顶点纹理数据数组
        GLES30.glEnableVertexAttribArray(maTexCoordHandle);
        //启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle);

        //激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        //绘制图形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCount);
    }
}
