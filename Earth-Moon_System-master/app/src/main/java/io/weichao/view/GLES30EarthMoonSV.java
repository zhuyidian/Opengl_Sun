package io.weichao.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.model.orbitSixElement;
import io.weichao.util.GLES30Util;
import io.weichao.util.MatrixStateUtil;
import io.weichao.util.Point;


/**
 * Created by admin on 2016/11/14.
 */
@SuppressLint("ClickableViewAccessibility")
public class GLES30EarthMoonSV extends GLSurfaceView {
    public final static int FRAME = 30;
    private final static long DELAY = (long) (1000.0f / FRAME);

    public final static int TIME_ROUND_ONE_CIRCLE = 5;//s
    private final static float RADIANS_PLUS = 360 / FRAME / TIME_ROUND_ONE_CIRCLE;

    private final static float X_TOUCH_SCALE_FACTOR = 0.5f;
    private final static float Y_TOUCH_SCALE_FACTOR = 0.5f;

    public boolean isThreadRun = true;//旋转线程的工作标志位

    private BaseFragmentActivity mActivity;

    private float mXozAngle;//摄像机绕Y轴旋转的角度
    private float mYozAngle;//摄像机绕X轴旋转的角度
    private float mRotAngle;//自转角度

//    private int mScrollDistanceWidthLimit = (int) (BaseFragmentActivity.width * ConstantUtil.ACTIVITY_SCROLL_DISTANCE_PERCENT);
//    private int mScrollDistanceHeightLimit = (int) (BaseFragmentActivity.height * ConstantUtil.ACTIVITY_SCROLL_DISTANCE_PERCENT);
//    private int mDownX;
//    private int mDownY;
//    private int mOffsetX;
//    private int mOffsetY;
    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置Y坐标
//    private long mDownTime;

    public GLES30EarthMoonSV(BaseFragmentActivity activity) {
        super(activity);

        mActivity = activity;

        setEGLContextClientVersion(3);//设置使用OPENGL ES3.0
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        SceneRenderer mRenderer = new SceneRenderer(activity);//创建场景渲染器
        setRenderer(mRenderer);//设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为持续渲染
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = (int) event.getX();
//                mDownY = (int) event.getY();
//                mOffsetX = 0;
//                mOffsetY = 0;
//                mDownTime = SystemClock.elapsedRealtime();
//                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                int currentY = (int) event.getY();
//                int offset = Math.abs(currentX - mDownX);
//                if (offset > mOffsetX) {
//                    mOffsetX = offset;
//                }
//                offset = Math.abs(currentY - mDownY);
//                if (offset > mOffsetY) {
//                    mOffsetY = offset;
//                }

                float dx = currentX - mPreviousX;//计算触控X位移
                float dy = currentY - mPreviousY;//计算触控Y位移

                //将X位移折算成绕Y轴旋转的角度(角度变化为反方向)
                mXozAngle -= dx * X_TOUCH_SCALE_FACTOR;

                //将Y位移折算成绕X轴旋转的角度，触控纵向位移摄像机绕x轴旋转 -90～+90
                mYozAngle += dy * Y_TOUCH_SCALE_FACTOR;

                if (mYozAngle > 90) {
                    mYozAngle = 90;
                } else if (mYozAngle < -90) {
                    mYozAngle = -90;
                }

                double xAngle = Math.toRadians(mXozAngle);
                double yAngle = Math.toRadians(mYozAngle);

                float cx = (float)(7.2 * Math.cos(yAngle) * Math.sin(xAngle));
                float cy = (float) (7.2 * Math.sin(yAngle));
                float cz = (float)(7.2 * Math.cos(yAngle) * Math.cos(xAngle));
//                float cy = (float) (7.2 * Math.sin(Math.toRadians(mYozAngle)));
//                float cz = (float) (7.2 * Math.cos(Math.toRadians(mYozAngle)));
                float upx = -(float) (Math.sin(Math.toRadians(mYozAngle)) * Math.cos(xAngle));
                float upy = (float) Math.cos(Math.toRadians(mYozAngle));
                float upz = -(float) (Math.sin(Math.toRadians(mYozAngle)) * Math.sin(xAngle));
                //改变绕X轴旋转角度
                MatrixStateUtil.setCamera(cx, cy, cz, 0, 0, 0, upx, upy, upz);

//            case MotionEvent.ACTION_UP:
//                // TODO 持续渲染自动调用 ACTION_UP?
//                if (SystemClock.elapsedRealtime() - mDownTime < ConstantUtil.SINGLE_TAP_TIMEOUT && mOffsetX < (mScrollDistanceWidthLimit >> 4) && mOffsetY < (mScrollDistanceHeightLimit >> 4)) {
//                    mActivity.onSingleTap();
//                }
//                break;
        }
        mPreviousX = (int) event.getX();
        mPreviousY = (int) event.getY();
        return true;
    }

    private class SceneRenderer implements Renderer {
        private Context mContext;
        private DoubleHemiSphere mEarth;//地球
        private Sphere mMoon;//月球
        private Circle mCircle; //satellite orbit


        //new add
//        private Shape sat;
//        float yAngle;
//        float xAngle;

        private LoadShape[] loadShapeList;

        public SceneRenderer(Context context) {
            mContext = context;
            loadShapeList = new LoadShape[8];
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景颜色RGBA
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            //new add
//            sat = new Shape(mContext, "sat3.obj", R.mipmap.t3);

            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //设置剔除三角形的面
            GLES30.glCullFace(GLES30.GL_BACK);
            //启用剔除
            GLES30.glEnable(GLES30.GL_CULL_FACE);

            //创建对象
            mEarth = new DoubleHemiSphere(mContext, 1, 50);
            mMoon = new Sphere(mContext, 0.2f, 50);

            for (int i = 0; i < loadShapeList.length; i++){
                orbitSixElement sixElement = new orbitSixElement(1, 1, 55, 0, i*40, 0);
                loadShapeList[i] = new LoadShape(mContext, "sat3.obj", sixElement);
            }

//            loadShapeList = new LoadShape(mContext, "sat3.obj");
//            mCircle = new Circle(mContext, 1.5f, new Point(0.0f,0.0f,0.0f), 50);
            //设置纹理
            int textureIdEarth = GLES30Util.loadTexture(mContext, "model/double_hemi_sphere/texture/earth.png");//加载地球白天纹理，对应纹理单元0
            int textureIdEarthNight = GLES30Util.loadTexture(mContext, "model/double_hemi_sphere/texture/earthn.png");//加载地球黑夜纹理，对应纹理单元1
            mEarth.setTextureId(textureIdEarth, textureIdEarthNight);
            int textureIdMoon = GLES30Util.loadTexture(mContext, "model/sphere/texture/moon.png");//加载月球纹理，对应纹理单元0
            mMoon.setTextureId(textureIdMoon);

            //new add
//            int textureIdLoadShape = GLES30Util.loadTexture(mContext, "model/t1.png");
//            loadShapeList.setTextureId(textureIdLoadShape);

//            mCircle.mPrimaryShader.onSurfaceCreated();

            //初始化变换矩阵
            MatrixStateUtil.setInitStack();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算GLSurfaeVIew的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixStateUtil.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);

            //设置相机9参数
//            float cy = (float) (7.2 * Math.sin(Math.toRadians(mYozAngle)));
//            float cz = (float) (7.2 * Math.cos(Math.toRadians(mYozAngle)));
//            float upy = (float) Math.cos(Math.toRadians(mYozAngle));
//            float upz = -(float) Math.sin(Math.toRadians(mYozAngle));
//            MatrixStateUtil.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);
            double xAngle = Math.toRadians(mXozAngle);
            double yAngle = Math.toRadians(mYozAngle);

            float cx = (float)(7.2 * Math.cos(yAngle) * Math.sin(xAngle));
            float cy = (float) (7.2 * Math.sin(yAngle));
            float cz = (float)(7.2 * Math.cos(yAngle) * Math.cos(xAngle));
            float upx = -(float) (Math.sin(Math.toRadians(mYozAngle)) * Math.cos(xAngle));
            float upy = (float) Math.cos(Math.toRadians(mYozAngle));
            float upz = -(float) (Math.sin(Math.toRadians(mYozAngle)) * Math.sin(xAngle));
            //改变绕X轴旋转角度
            MatrixStateUtil.setCamera(cx, cy, cz, 0, 0, 0, upx, upy, upz);

            //设置太阳灯光的初始位置
//            float sunx = (float) (Math.cos(Math.toRadians(mXozAngle)) * 100);
//            float sunz = -(float) (Math.sin(Math.toRadians(mYozAngle)) * 100);
//            MatrixStateUtil.setLightLocationSun(sunx, 5, sunz);

            //启动一个线程定时旋转地球、月球
            new Thread() {
                public void run() {
                    while (isThreadRun) {
                        //地球自转角度
                        mRotAngle += RADIANS_PLUS;
                        try {
                            Thread.sleep(DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            //new add
//            Matrix.perspectiveM(mProjectionMatrix, 0, 45, (float)width/height, 2, 5);
//            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3,  0, 0, 0, 0, 1, 0);
        }

        //new add
//        private final float[] mProjectionMatrix = new float[16];
//        private final float[] mViewMatrix = new float[16];
//        private final float[] mModuleMatrix = new float[16];
//        private final float[] mViewProjectionMatrix = new float[16];
//        private final float[] mMVPMatrix = new float[16];

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除屏幕深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            //保护现场
            MatrixStateUtil.pushMatrix();

            //设置太阳灯光的位置
            float sunX = (float) (Math.sin(Math.toRadians(mRotAngle)) * 100);
            float sunZ = (float) (Math.cos(Math.toRadians(mRotAngle)) * 100);
            MatrixStateUtil.setLightLocationSun(sunX, 50, sunZ);

            //earth自转
            MatrixStateUtil.rotate(mRotAngle/4, 0, 1, 0);
            //绘制earth
            mEarth.draw();

            //恢复现场
            MatrixStateUtil.popMatrix();
            //保护现场
            MatrixStateUtil.pushMatrix();

            //卫星自转
//            MatrixStateUtil.rotate(mRotAngle, 0, 1, 0);

//            MatrixStateUtil.rotate(mRotAngle, -1,(float) Math.atan(Math.toRadians(35)), 0);
//            MatrixStateUtil.translate(0, 0, 1.5f);
//            MatrixStateUtil.rotate(120, 1,0, 0);
//
//            loadShapeList.draw();

            //恢复现场
            MatrixStateUtil.popMatrix();
            //保护现场
            MatrixStateUtil.pushMatrix();

            //推坐标系到moon位置
            MatrixStateUtil.translate(2, 0, 0);
            //moon自转
            MatrixStateUtil.rotate(mRotAngle, 0, 1, 0);
            //绘制moon
            mMoon.draw();

            //恢复现场
            MatrixStateUtil.popMatrix();
            //保护现场
            MatrixStateUtil.pushMatrix();
            //circle draw
//            MatrixStateUtil.rotate(90, 1, 0, 0);
//            MatrixStateUtil.rotate(60, 0, 1, 0);
            //推坐标系到moon位置
//            MatrixStateUtil.translate(-2, 0, 0);
//            MatrixStateUtil.rotate(61, 0, 0, 1);
//            mCircle.draw();
            for (int i = 0; i < loadShapeList.length; i++){
                loadShapeList[i].DrawALL(mRotAngle);
            }

            //new add
            //推坐标系到moon位置
//            MatrixStateUtil.translate(0, -1, 0);
//            loadShapeList.draw();

//            Matrix.setIdentityM(mModuleMatrix, 0);
//            Matrix.translateM(mModuleMatrix, 0, 1, 0.5f, 0);
//            Matrix.rotateM(mModuleMatrix, 0, xAngle, 1, 0, 0);
//            Matrix.rotateM(mModuleMatrix, 0, yAngle, 0, 1, 0);
//            Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//            Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModuleMatrix, 0);
//            sat.setValue(mMVPMatrix, mModuleMatrix);
//            sat.draw();

            //恢复现场
            MatrixStateUtil.popMatrix();
        }
    }
}
