package io.weichao.view;


import android.content.Context;
import android.opengl.GLES30;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import io.weichao.earth_moon_system.R;
import io.weichao.util.GLES30Util;
import io.weichao.util.LoggerConfig;
import io.weichao.util.MatrixStateUtil;
import io.weichao.util.Point;
import io.weichao.util.ShaderHelper;
import io.weichao.util.TextResourceReader;

/**
 * Created by Administrator on 2017/12/8.
 */

public class Circle {

    protected FloatBuffer mPositionBuffer;//顶点坐标数据缓冲
    protected FloatBuffer mTexCoordBuffer;//顶点纹理坐标数据缓冲

    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用
    private int muMMatrixHandle;//位置、旋转变换矩阵
    private int mPositionHandle;
    private int mColorHandle;
    private int maNormalHandle; //顶点法向量属性引用
    private int maTexCoordHandle; //顶点纹理坐标属性引用
    private int maSunLightLocationHandle;//光源位置属性引用
    private int maCameraHandle; //摄像机位置属性引用

    private static final String U_MATRIX = "uMVPMatrix";
    private static final String A_POSITION = "aPosition";
    private static final String A_COLOR = "a_Color";

    private float radiu = 0;
    private Point centerPoint;
    private int mVertexCount;
    private Context mContext;

    public float[] mVertexData;
    public static int POSITION_COMPONENT_COUNT = 3;
    public static int COLOR_COMPONENT_COUNT    = 3;
    public static int BYTES_PER_FLOAT          = 4;

    public static int mStride = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT; ;
//    protected ShaderProgram mPrimaryShader   = null;

    public Circle(Context mContext, float radiu, Point centerPoint, int splitCount) {
        this.mContext = mContext;
        this.radiu = radiu;
        this.centerPoint = centerPoint;
        initVertexData(splitCount);
//        initShader();
        initScript(mContext);
    }


    public void initVertexData(int splitCount) {
        // Order of coordinates: X, Y, Z, R, G, B
        float span = 360.0f/splitCount;
        ArrayList<Float> alVertix = new ArrayList<>();
        for (float hAngle = 0; hAngle <360; hAngle = hAngle + span) {
            float x = (float) (Math.cos(Math.toRadians(hAngle)) * this.radiu);
            float y = 0.0f;
            float z = (float) (Math.sin(Math.toRadians(hAngle)) * this.radiu);

            alVertix.add(x);
            alVertix.add(y);
            alVertix.add(z);
            alVertix.add(1.0f);
            alVertix.add(0.0f);
            alVertix.add(0.0f);
        }
        alVertix.add(radiu);
        alVertix.add(0.0f);
        alVertix.add(0.0f);
        alVertix.add(1.0f);
        alVertix.add(0.0f);
        alVertix.add(0.0f);

        //顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        mVertexCount = alVertix.size() / 6;

        float[] positionArray = new float[alVertix.size()];
        for (int i = 0; i < alVertix.size(); i++) {
            positionArray[i] = alVertix.get(i);
        }
        mPositionBuffer = ByteBuffer.allocateDirect(positionArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositionBuffer.put(positionArray).position(0);


//        int numPoints = 360;
//        float[] colorData = { 128.0f / 256.0f * 0.5f, 128.0f / 256.0f * 0.5f, 128.0f / 256.0f * 0.5f, 0.0f};
//        mVertexData = new float[(numPoints + 1) * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)];
//
//        for (int i = 0; i <= numPoints; i++) {
//            mVertexData[i + (7 * i) + 0] = ((float) Math.cos((double) i * Math.PI / 180.0)) * this.radiu;
//            mVertexData[i + (7 * i) + 1] = 0.0f;
//            mVertexData[i + (7 * i) + 2] = ((float) Math.sin((double) i * Math.PI / 180.0)) * this.radiu;
//            mVertexData[i + (7 * i) + 3] = 1.0f;
//            mVertexData[i + (7 * i) + 4] = colorData[0];
//            mVertexData[i + (7 * i) + 5] = colorData[1];
//            mVertexData[i + (7 * i) + 6] = colorData[2];
//            mVertexData[i + (7 * i) + 7] = colorData[3];
//        }
//
//        makeFloatBuffer();
//        mNumVertices = mVertexData.length / (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT);
    }

    protected void makeFloatBuffer()
    {
//        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexData.length * BYTES_PER_FLOAT);
//        bb.order(ByteOrder.nativeOrder());
//        mPositionBuffer = bb.asFloatBuffer();
//        mPositionBuffer.put(mVertexData);
//        mPositionBuffer.position(0);
    }

    // 初始化纹理
    public void initScript(Context context) {
        mProgram = GLES30Util.loadProgram(context, "model/sphere/script/vertex_shader.sh", "model/sphere/script/fragment_shader.sh");
        //获取程序中顶点位置属性引用
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
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

    public void initShader(){
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper
                .compileFragmentShader(fragmentShaderSource);

        mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        GLES30.glUseProgram(mProgram);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(mProgram);
        }

        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, U_MATRIX);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, A_POSITION);
        mColorHandle = GLES30.glGetAttribLocation(mProgram, A_COLOR);

//        // Bind our data, specified by the variable vertexData, to the vertex
//        // attribute at location A_POSITION_LOCATION.
//        mPositionBuffer.position(0);
//        GLES30.glVertexAttribPointer(mPositionHandle,
//                POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, mStride, mPositionBuffer);
//
//        GLES30.glEnableVertexAttribArray(mPositionHandle);
//
//        // Bind our data, specified by the variable vertexData, to the vertex
//        // attribute at location A_COLOR_LOCATION.
//        mPositionBuffer.position(POSITION_COMPONENT_COUNT);
//        GLES30.glVertexAttribPointer(mColorHandle,
//                COLOR_COMPONENT_COUNT, GLES30.GL_FLOAT, false, mStride, mPositionBuffer);
//
//        GLES30.glEnableVertexAttribArray(mColorHandle);
    }


    public void draw() {
        //指定使用某套着色器程序（必须每次都指定）
//        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixStateUtil.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixStateUtil.getMMatrix(), 0);
        // TODO 下面这句有时报错：java.lang.IllegalArgumentException: remaining() < count*3 < needed

        mPositionBuffer.position(0);
        //将顶点纹理数据送入渲染管线（必须每次都指定）
        GLES30.glVertexAttribPointer(mPositionHandle, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, mStride, mPositionBuffer);
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_COLOR_LOCATION.
        mPositionBuffer.position(POSITION_COMPONENT_COUNT);
        GLES30.glVertexAttribPointer(mColorHandle, COLOR_COMPONENT_COUNT, GLES30.GL_FLOAT, false, mStride, mPositionBuffer);

        GLES30.glEnableVertexAttribArray(mColorHandle);

        //绘制图形
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, mVertexCount);

//        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mPositionBuffer.capacity() * BYTES_PER_FLOAT, mPositionBuffer, GLES30.GL_STATIC_DRAW);
//
//        DefaultShaderProgram mDefShader = (DefaultShaderProgram) mPrimaryShader;
//
//        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//        GLES30.glUseProgram(mDefShader.getProgramOid());
//        GLES30.glUniformMatrix4fv(mDefShader.uMatrixLocation, 1, false, MatrixStateUtil.getFinalMatrix(), 0);
//        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 1);
//        GLES30.glEnableVertexAttribArray(mDefShader.aPositionLocation);
//        GLES30.glVertexAttribPointer(mDefShader.aPositionLocation, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, mStride, 0);
//        GLES30.glEnableVertexAttribArray(mDefShader.aColorLocation);
//        GLES30.glVertexAttribPointer(mDefShader.aColorLocation, COLOR_COMPONENT_COUNT, GLES30.GL_FLOAT, false, mStride, POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT);
//        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, mNumVertices);
    }
}
