package io.weichao.model;

/**
 * Created by Administrator on 2017/12/13.
 */

public class orbitSixElement {
    public float a; //轨道半长轴
    public float e; //轨道偏心率
    public float i; //轨道倾角
    public float omega; //升交点赤经
    public float w; //近地点幅角
    public float m0; //平近点角

    public orbitSixElement(float a, float e, float i, float omega, float w, float m0) {
        this.a = a;
        this.e = e;
        this.i = i;
        this.omega = omega;
        this.w = w;
        this.m0 = m0;
    }
}
