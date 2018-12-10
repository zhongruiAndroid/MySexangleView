package com.github.sexangle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @createBy r-zhong
 * @time 2018-12-06 10:07
 */
public class SexangleView extends View {

    private OnProgressChangeInter onProgressChangeInter;
    private ValueAnimator valueAnimator;
    private Interpolator interpolator;
    public interface OnProgressChangeInter {
        void progress(float scaleProgress, float progress, float max);
    }


    public SexangleView(Context context) {
        super(context);
        init(null);
    }

    public SexangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SexangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private float[] pathLength = new float[13];
    /********************六边形进度条*********************/

    private Path progressPath;
    private Paint sexanglePaint;
    /*进度条是否圆角*/
    private boolean isRound = true;

    /*圆角半径*/
    private int radius = 1;
    /*六边形进度颜色*/
    private int sexangleColor;
    /*六边形线条宽度*/
    private int sexangleWidth = 30;
    /*六边形进度*/
    private float progress = 35;
    /*六边形总进度*/
    private float max = 100;
    /*动画执行期间进度*/
    private float scaleProgress;

    /*1：垂直方向 2：水平方向(暂时不实现了)*/
    private int direction = 1;

    /*进度渲染器*/
    private Shader shader;

    /*绘制六边形区域宽度*/
    private float viewWidth;
    /*绘制六边形区域高度*/
    private float viewHeight;

    /*绘制六边形边框区域宽度*/
    private float viewBorderWidth;
    /*绘制六边形边框区域高度*/
    private float viewBorderHeight;

    /*六边形宽度*/
    private float sw;
    /*六边形高度*/
    private float sh;

    /*六边形边长*/
    private float sideLength;



    /*六边形border宽度*/
    private float borderW;
    /*六边形border高度*/
    private float borderH;

    /*六边形border边长*/
    private float borderSideLength;

    /*是否使用动画*/
    private boolean useAnimation = false;
    /*动画执行时间：毫秒*/
    private int duration = 1200;


    /******************六边形****************/

    private Path sexangleSecondPath;
    private Paint sexangleSecondPaint;
    private int sexangleSecondColor;


    /*********************六边形border*******************/
    private Paint borderPaint;
    private Path borderPath;
    /*边框颜色*/
    private int borderColor;
    /*边框宽度*/
    private int borderWidth = 0;
    /*虚线长度*/
    private int borderDashLength = 0;
    /*虚线间隔*/
    private int borderDashGap = 0;
    /*边框圆角*/
    private float borderRadius = radius;
    /*虚线phase*/
    private float borderPhase;
    private PathEffect borderPathEffect;

    /*border 边距*/
    private int borderMarginLeft;
    private int borderMarginTop;
    private int borderMarginRight;
    private int borderMarginBottom;


    /*记录圆弧外切矩形坐标*/

    private List<Float[]> lineList;
    private RectF[] arcRect;

    private float cos30 = (float) Math.cos(Math.toRadians(30));
    private float sin30 = (float) Math.sin(Math.toRadians(30));
    private float tan30 = (float) Math.tan(Math.toRadians(30));


    private void init(AttributeSet attrs) {
        sexangleColor = Color.parseColor("#20D18C");


        sexangleSecondColor = Color.parseColor("#DDDDDD");


        borderColor = Color.parseColor("#666666");


        if (attrs == null) {
            return;
        }


        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SexangleView);

        radius = (int) typedArray.getDimension(R.styleable.SexangleView_radius, radius);
        sexangleColor = typedArray.getColor(R.styleable.SexangleView_sexangleColor, sexangleColor);
        sexangleSecondColor = typedArray.getColor(R.styleable.SexangleView_sexangleSecondColor, sexangleSecondColor);
        sexangleWidth = (int) typedArray.getDimension(R.styleable.SexangleView_sexangleWidth, sexangleWidth);
        progress = typedArray.getFloat(R.styleable.SexangleView_progress, 35);
        max = typedArray.getFloat(R.styleable.SexangleView_max, 100);
        isRound = typedArray.getBoolean(R.styleable.SexangleView_isRound, true);
        borderColor = typedArray.getColor(R.styleable.SexangleView_borderColor, borderColor);
        borderWidth = (int) typedArray.getDimension(R.styleable.SexangleView_borderWidth, borderWidth);
        borderDashLength = (int) typedArray.getDimension(R.styleable.SexangleView_borderDashLength, borderDashLength);
        borderDashGap = (int) typedArray.getDimension(R.styleable.SexangleView_borderDashGap, borderDashGap);
        borderRadius = typedArray.getDimension(R.styleable.SexangleView_borderRadius, radius);
        borderPhase = typedArray.getFloat(R.styleable.SexangleView_borderPhase, borderPhase);
        useAnimation = typedArray.getBoolean(R.styleable.SexangleView_useAnimation, true);
        duration = typedArray.getInteger(R.styleable.SexangleView_duration, duration);

        int  borderMargin = (int) typedArray.getDimension(R.styleable.SexangleView_borderMargin, 0);
        if (borderMargin > 0) {
            borderMarginLeft = borderMargin;
            borderMarginTop = borderMargin;
            borderMarginRight = borderMargin;
            borderMarginBottom = borderMargin;
        } else {
            borderMarginLeft = (int) typedArray.getDimension(R.styleable.SexangleView_borderMarginLeft, 0);
            borderMarginTop =  (int) typedArray.getDimension(R.styleable.SexangleView_borderMarginTop, 0);
            borderMarginRight =  (int) typedArray.getDimension(R.styleable.SexangleView_borderMarginRight, 0);
            borderMarginBottom =  (int) typedArray.getDimension(R.styleable.SexangleView_borderMarginBottom, 0);
        }

        typedArray.recycle();

        if (progress < 0) {
            progress = 0;
        }
        if (max < 0) {
            max = 100;
        }
        if (progress > max) {
            progress = max;
        }
        this.scaleProgress=progress;

        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth =MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight=MeasureSpec.getSize(heightMeasureSpec);

        int mWidth =dip2px(getContext(),130);
        int mHeight=dip2px(getContext(),130);
        if(getLayoutParams().width==ViewGroup.LayoutParams.WRAP_CONTENT&&getLayoutParams().height==ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,mHeight);
        }else if(getLayoutParams().width==ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,viewWidth);
        }else if(getLayoutParams().height==ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mHeight,viewHeight);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /*计算六边形所需要的属性值*/
        calculateSize();

        /*计算border各个属性值*/
        calculateBorderSize();


        initPath();

        initPoint();
    }

    private void calculateBorderSize() {
        viewBorderWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        viewBorderHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (direction == 1) {
            /*使六边形完全在view内部绘制，减去线条宽度*/
            viewBorderWidth = viewBorderWidth - borderWidth;
            viewBorderHeight = (float) (viewBorderHeight - borderWidth / Math.cos(Math.toRadians(30)));


            viewBorderWidth = viewBorderWidth - borderWidth;
            viewBorderHeight = (float) (viewBorderHeight - borderWidth / Math.cos(Math.toRadians(30)));

            //垂直方向
            if (viewBorderWidth / viewBorderHeight > Math.cos(Math.toRadians(30))) {
                //绘制区域宽度过长
                borderSideLength = viewBorderHeight / 2;
                borderW = (float) (2 * viewBorderHeight / 2 * Math.cos(Math.toRadians(30)));
                borderH = 2 * borderSideLength;
            } else {
                //绘制区域高度过长
                borderSideLength = (float) (viewBorderWidth / 2 / Math.cos(Math.toRadians(30)));
                borderW = viewBorderWidth;
                borderH = 2 * borderSideLength;
            }
        } else {
            /*使六边形完全在view内部绘制*/
            viewBorderWidth = (float) (viewBorderWidth - borderWidth / Math.cos(Math.toRadians(30)));
            viewBorderHeight = viewBorderHeight - borderWidth;

            viewBorderWidth = (float) (viewBorderWidth - borderWidth / Math.cos(Math.toRadians(30)));
            viewBorderHeight = viewBorderHeight - borderWidth;

            //水平方向
            if (viewBorderWidth / 2f / viewBorderHeight > Math.tan(Math.toRadians(30))) {
                //绘制区域宽度过长
                borderSideLength = (float) (viewBorderHeight / 2 / Math.cos(Math.toRadians(30)));
                borderH = viewBorderHeight;
                borderW = 2 * borderSideLength;
            } else {
                //绘制区域高度过长
                borderSideLength = viewBorderWidth / 2;
                borderW = viewBorderWidth;
                borderH = (float) (2 * borderSideLength / 2 / Math.tan(Math.toRadians(30)));
            }
        }
    }

    private void calculateSize() {

        viewWidth = getWidth() - getPaddingLeft() - getPaddingRight()-getBorderMarginLeft()-getBorderMarginRight();
        viewHeight = getHeight() - getPaddingTop() - getPaddingBottom()-getBorderMarginTop()-getBorderMarginBottom();

        if (direction == 1) {
            /*使六边形完全在view内部绘制，减去线条宽度*/
            viewWidth = viewWidth - sexangleWidth;
            viewHeight = (float) (viewHeight - sexangleWidth / Math.cos(Math.toRadians(30)));


            //垂直方向
            if (viewWidth / viewHeight > Math.cos(Math.toRadians(30))) {
                //绘制区域宽度过长
                sideLength = viewHeight / 2;
                sw = (float) (2 * viewHeight / 2 * Math.cos(Math.toRadians(30)));
                sh = 2 * sideLength;
            } else {
                //绘制区域高度过长
                sideLength = (float) (viewWidth / 2 / Math.cos(Math.toRadians(30)));
                sw = viewWidth;
                sh = 2 * sideLength;
            }
        } else {
            /*使六边形完全在view内部绘制*/
            viewWidth = (float) (viewWidth - sexangleWidth / Math.cos(Math.toRadians(30)));
            viewHeight = viewHeight - sexangleWidth;

            //水平方向
            if (viewWidth / 2f / viewHeight > Math.tan(Math.toRadians(30))) {
                //绘制区域宽度过长
                sideLength = (float) (viewHeight / 2 / Math.cos(Math.toRadians(30)));
                sh = viewHeight;
                sw = 2 * sideLength;
            } else {
                //绘制区域高度过长
                sideLength = viewWidth / 2;
                sw = viewWidth;
                sh = (float) (2 * sideLength / 2 / Math.tan(Math.toRadians(30)));
            }
        }
    }

    private void initPaint() {
        sexanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sexanglePaint.setStrokeWidth(sexangleWidth);
        sexanglePaint.setStrokeCap(isRound ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        sexanglePaint.setStyle(Paint.Style.STROKE);
        sexanglePaint.setColor(sexangleColor);
        shader = new SweepGradient(0, 0, new int[]{Color.parseColor("#34e8a6"), Color.parseColor("#06C1AE"), Color.parseColor("#34e8a6")}, null);
//        Shader shader1 = new LinearGradient(0, -getHeight() / 2, 0, getHeight() / 2, new int[]{Color.parseColor("#42C370"), Color.parseColor("#0BAD83")}, null, Shader.TileMode.CLAMP);
        sexanglePaint.setShader(shader);


        sexangleSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sexangleSecondPaint.setStrokeWidth(sexangleWidth);
        sexangleSecondPaint.setStyle(Paint.Style.STROKE);
        sexangleSecondPaint.setColor(sexangleSecondColor);


        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);



        if(borderPathEffect!=null){
            borderPaint.setPathEffect(borderPathEffect);
        }else{
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{borderDashLength, borderDashGap}, borderPhase);
            CornerPathEffect cornerPathEffect=new CornerPathEffect(borderRadius);
            ComposePathEffect composePathEffect=new ComposePathEffect(dashPathEffect,cornerPathEffect);
            borderPaint.setPathEffect(composePathEffect);
        }

    }

    private void initPath() {
        progressPath = new Path();
        sexangleSecondPath = new Path();

        borderPath=new Path();


        borderPath.moveTo(0, -borderSideLength);
        borderPath.lineTo(borderW / 2, -borderSideLength / 2);
        borderPath.lineTo(borderW / 2, borderSideLength / 2);
        borderPath.lineTo(0, borderSideLength);
        borderPath.lineTo(-borderW / 2, borderSideLength / 2);
        borderPath.lineTo(-borderW / 2, -borderSideLength / 2);
        borderPath.close();

       /* if (direction == 1) {
       //6个坐标点绘制六边形，放弃这种方案，主要是带圆角的时候进度条不好控制
            //垂直方向
            sexangleSecondPath.moveTo(0, -sideLength);
            sexangleSecondPath.lineTo(sw / 2, -sideLength / 2);
            sexangleSecondPath.lineTo(sw / 2, sideLength / 2);
            sexangleSecondPath.lineTo(0, sideLength);
            sexangleSecondPath.lineTo(-sw / 2, sideLength / 2);
            sexangleSecondPath.lineTo(-sw / 2, -sideLength / 2);
            sexangleSecondPath.close();

        } else {
            //水平方向

            sexangleSecondPath.moveTo(0, sh / 2);
            sexangleSecondPath.lineTo(-sideLength / 2, sh / 2);
            sexangleSecondPath.lineTo(-sideLength, 0);
            sexangleSecondPath.lineTo(-sideLength / 2, -sh / 2);
            sexangleSecondPath.lineTo(sideLength / 2, -sh / 2);
            sexangleSecondPath.lineTo(sideLength, 0);
            sexangleSecondPath.lineTo(sideLength / 2, sh / 2);

            sexangleSecondPath.close();


        }*/





    }

    private void initPoint() {
        lineList = new ArrayList<>();

        arcRect = new RectF[6];

        if(sexangleSecondPath!=null){
            sexangleSecondPath.reset();
        }else{
            sexangleSecondPath=new Path();
        }

        if (direction == 1) {

            /*第一个外切圆的矩形坐标*/
            RectF firstRect = new RectF(-radius, -1f * (sh / 2f - (radius / cos30 - radius)), radius, -1f * (sh / 2f - (radius / cos30 - radius)) + 2f * radius);

            arcRect[0] = firstRect;

            //位移长度
            float xieLength = sideLength - 2 * radius * tan30;
            //x位移距离
            float translateX = cos30 * xieLength;
            //y位移距离
            float translateY = sin30 * xieLength;


            RectF rectF = new RectF();

            Matrix matrix = new Matrix();
            matrix.postTranslate(translateX, translateY);
            matrix.mapRect(rectF, firstRect);
            arcRect[1] = rectF;


            rectF = new RectF();
            matrix.postTranslate(0, xieLength);
            matrix.mapRect(rectF, firstRect);
            arcRect[2] = rectF;


            rectF = new RectF();
            matrix.postTranslate(-translateX, translateY);
            matrix.mapRect(rectF, firstRect);
            arcRect[3] = rectF;


            rectF = new RectF();
            matrix.postTranslate(-translateX, -translateY);
            matrix.mapRect(rectF, firstRect);
            arcRect[4] = rectF;

            rectF = new RectF();
            matrix.postTranslate(0, -xieLength);
            matrix.mapRect(rectF, firstRect);
            arcRect[5] = rectF;



           /*
            三角函数计算出第二个矩形坐标，上面一律用矩阵计算
            arcPoint[2]=new PointView(sw/2f-2f*radius,-1f*(sh/2f-(sideLength*sin30+radius*tan30-radius)));
            arcPoint[3]=new PointView(sw/2f,-1f*(sh/2f-(sideLength*sin30+radius*tan30-radius))+2f*radius);
            */



            /*第一条线段 起始坐标,共有6条线段*/


            /*第一条线段start*/
            float[] firstStartPoint = new float[2];
            firstStartPoint[0] = radius * sin30;
            firstStartPoint[1] = -1f * (sh / 2f - (radius / cos30 - radius * cos30));



            /*第一条线段end*/
            float[] firstEndPoint = new float[2];

            matrix = new Matrix();
            matrix.postTranslate(translateX, translateY);
            matrix.mapPoints(firstEndPoint, firstStartPoint);


            float[] firstLine = new float[]{firstStartPoint[0], firstStartPoint[1], firstEndPoint[0], firstEndPoint[1]};
            /*添加第1条线段*/
            lineList.add(new Float[]{firstStartPoint[0], firstStartPoint[1], firstEndPoint[0], firstEndPoint[1]});


            matrix = new Matrix();

            float[] linePoint = new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint, firstLine);
            /*添加第2条线段*/
            lineList.add(new Float[]{linePoint[0], linePoint[1], linePoint[2], linePoint[3]});


            linePoint = new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint, firstLine);
            /*添加第3条线段*/
            lineList.add(new Float[]{linePoint[0], linePoint[1], linePoint[2], linePoint[3]});


            linePoint = new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint, firstLine);
            /*添加第4条线段*/
            lineList.add(new Float[]{linePoint[0], linePoint[1], linePoint[2], linePoint[3]});


            linePoint = new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint, firstLine);
            /*添加第5条线段*/
            lineList.add(new Float[]{linePoint[0], linePoint[1], linePoint[2], linePoint[3]});

            linePoint = new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint, firstLine);
            /*添加第6条线段*/
            lineList.add(new Float[]{linePoint[0], linePoint[1], linePoint[2], linePoint[3]});





            /*添加第1条弧的右半部分*/
            sexangleSecondPath.addArc(arcRect[0], -90, 30);
            /*添加第1条线段*/
            sexangleSecondPath.moveTo(lineList.get(0)[0], lineList.get(0)[1]);
            sexangleSecondPath.lineTo(lineList.get(0)[2], lineList.get(0)[3]);


            /*添加第2条弧*/
            sexangleSecondPath.addArc(arcRect[1], -60, 60);

            /*添加第2条线段*/
            sexangleSecondPath.moveTo(lineList.get(1)[0], lineList.get(1)[1]);
            sexangleSecondPath.lineTo(lineList.get(1)[2], lineList.get(1)[3]);


            /*添加第3条弧*/
            sexangleSecondPath.addArc(arcRect[2], 0, 60);

            /*添加第3条线段*/
            sexangleSecondPath.moveTo(lineList.get(2)[0], lineList.get(2)[1]);
            sexangleSecondPath.lineTo(lineList.get(2)[2], lineList.get(2)[3]);


            /*添加第4条弧*/
            sexangleSecondPath.addArc(arcRect[3], 60, 60);

            /*添加第4条线段*/
            sexangleSecondPath.moveTo(lineList.get(3)[0], lineList.get(3)[1]);
            sexangleSecondPath.lineTo(lineList.get(3)[2], lineList.get(3)[3]);


            /*添加第5条弧*/
            sexangleSecondPath.addArc(arcRect[4], 120, 60);

            /*添加第5条线段*/
            sexangleSecondPath.moveTo(lineList.get(4)[0], lineList.get(4)[1]);
            sexangleSecondPath.lineTo(lineList.get(4)[2], lineList.get(4)[3]);


            /*添加第6条弧*/
            sexangleSecondPath.addArc(arcRect[5], 180, 60);

            /*添加第6条线段*/
            sexangleSecondPath.moveTo(lineList.get(5)[0], lineList.get(5)[1]);
            sexangleSecondPath.lineTo(lineList.get(5)[2], lineList.get(5)[3]);


            /*添加第1条弧的左半部分*/
            sexangleSecondPath.addArc(arcRect[0], -120, 30);

            recalculateViewprogress();
        } else {

        }
    }

    private void recalculateViewprogress() {
        if(progressPath!=null){
            progressPath.reset();
        }else{
            progressPath=new Path();
        }
        PathMeasure pathMeasure = new PathMeasure(sexangleSecondPath, false);
        for (int i = 0; i < 13; i++) {
            float pathMeasureLength = pathMeasure.getLength();
            if (i == 0) {
                pathLength[i] = pathMeasureLength;
            } else {
                pathLength[i] = pathLength[i - 1] + pathMeasureLength;
            }
            pathMeasure.nextContour();
        }


        pathMeasure = new PathMeasure(sexangleSecondPath, false);
        for (int i = 0; i < 13; i++) {
            if (scaleProgress == 0) {
                break;
            } else if (scaleProgress >= max) {
                pathMeasure.getSegment(0, pathMeasure.getLength(), progressPath, true);
            } else {
                if (scaleProgress / max >= pathLength[i] / pathLength[12]) {
                    pathMeasure.getSegment(0, pathMeasure.getLength(), progressPath, true);
                } else {
                    if (i == 0) {
                        pathMeasure.getSegment(0, scaleProgress / max * pathLength[12], progressPath, true);
                    } else {
                        pathMeasure.getSegment(0, (scaleProgress / max - pathLength[i - 1] / pathLength[12]) * pathLength[12], progressPath, true);
                    }
                    break;
                }
            }
            pathMeasure.nextContour();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.translate((getWidth() - getPaddingLeft() - getPaddingRight()-getBorderMarginLeft()-getBorderMarginRight()) / 2 + getPaddingLeft()+getBorderMarginLeft(), (getHeight() - getPaddingTop() - getPaddingBottom()-getBorderMarginTop()-getBorderMarginBottom()) / 2 + getPaddingTop()+getBorderMarginTop());

        /*绘制六边形边框*/
        if(borderWidth>0){
            canvas.drawPath(borderPath,borderPaint);
        }
        /*六边形*/
        canvas.drawPath(sexangleSecondPath, sexangleSecondPaint);

        /*六边形进度*/
        canvas.drawPath(progressPath, sexanglePaint);

        canvas.restore();

    }

    public boolean isRound() {
        return isRound;
    }

    public SexangleView setRound(boolean round) {
        isRound = round;
        sexanglePaint.setStrokeCap(isRound ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        invalidate();
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public SexangleView setRadius(int radius) {
        if(radius<0){
            radius=1;
        }
        this.radius = radius;
        initPoint();
        invalidate();
        return this;
    }

    public int getSexangleColor() {
        return sexangleColor;
    }

    public SexangleView setSexangleColor(int sexangleColor) {
        this.sexangleColor = sexangleColor;
        sexanglePaint.setShader(null);
        sexanglePaint.setColor(sexangleColor);
        invalidate();
        return this;
    }

    public int getSexangleWidth() {
        return sexangleWidth;
    }

    public SexangleView setSexangleWidth(int sexangleWidth) {
        this.sexangleWidth = sexangleWidth;
        calculateSize();

        sexanglePaint.setStrokeWidth(sexangleWidth);

        sexangleSecondPaint.setStrokeWidth(sexangleWidth);
        invalidate();
        return this;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        setProgress(progress, useAnimation);
    }

    public void setProgress(float progress, boolean useAnimation) {
        float beforeProgress = SexangleView.this.scaleProgress;
        if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;


        if (useAnimation) {
            valueAnimator = ValueAnimator.ofFloat(beforeProgress, progress);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    SexangleView.this.scaleProgress = (float) animation.getAnimatedValue();
                    recalculateViewprogress();
                    invalidate();
                    if (onProgressChangeInter != null) {
                        onProgressChangeInter.progress(scaleProgress, SexangleView.this.progress, max);
                    }
                }
            });
            valueAnimator.setInterpolator(interpolator==null?new DecelerateInterpolator():interpolator);
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        } else {
            SexangleView.this.scaleProgress = this.progress;
            recalculateViewprogress();
            invalidate();
            if (onProgressChangeInter != null) {
                onProgressChangeInter.progress(scaleProgress, SexangleView.this.progress, max);
            }
        }
    }

    public float getMax() {
        return max;
    }

    public SexangleView setMax(float max) {
        if (max < 0) {
            max = 100;
        }
        if (progress > max) {
            progress = max;
        }
        this.max = max;
        recalculateViewprogress();
        invalidate();
        return this;
    }

    public int getSexangleSecondColor() {
        return sexangleSecondColor;
    }

    public SexangleView setSexangleSecondColor(int sexangleSecondColor) {
        this.sexangleSecondColor = sexangleSecondColor;
        sexangleSecondPaint.setColor(sexangleSecondColor);
        invalidate();
        return this;
    }

    public Shader getShader() {
        return shader;
    }

    public SexangleView setShader(Shader shader) {
        this.shader = shader;
        sexanglePaint.setShader(shader);
        invalidate();
        return this;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public SexangleView setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        borderPaint.setColor(borderColor);
        invalidate();
        return this;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public SexangleView setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        borderPaint.setStrokeWidth(borderWidth);
        invalidate();
        return this;
    }

    private void setDashPath(int borderDashLength, int borderDashGap, float borderPhase) {
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{borderDashLength, borderDashGap}, borderPhase);
        CornerPathEffect cornerPathEffect=new CornerPathEffect(borderRadius);
        ComposePathEffect composePathEffect=new ComposePathEffect(dashPathEffect,cornerPathEffect);
        borderPaint.setPathEffect(composePathEffect);
    }
    public int getBorderDashLength() {
        return borderDashLength;
    }

    public SexangleView setBorderDashLength(int borderDashLength) {
        this.borderDashLength = borderDashLength;
        setDashPath(borderDashLength, borderDashGap, borderPhase);
        invalidate();
        return this;
    }

    public int getBorderDashGap() {
        return borderDashGap;
    }

    public SexangleView setBorderDashGap(int borderDashGap) {
        this.borderDashGap = borderDashGap;
        setDashPath(borderDashLength, borderDashGap, borderPhase);
        invalidate();
        return this;
    }

    public float getBorderRadius() {
        return borderRadius;
    }

    public SexangleView setBorderRadius(float borderRadius) {
        this.borderRadius = borderRadius;
        setDashPath(borderDashLength, borderDashGap, borderPhase);
        invalidate();
        return this;
    }

    public float getBorderPhase() {
        return borderPhase;
    }

    public SexangleView setBorderPhase(float borderPhase) {
        this.borderPhase = borderPhase;
        setDashPath(borderDashLength, borderDashGap, borderPhase);
        invalidate();
        return this;
    }

    public boolean isUseAnimation() {
        return useAnimation;
    }

    public SexangleView setUseAnimation(boolean useAnimation) {
        this.useAnimation = useAnimation;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public SexangleView setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getBorderMarginLeft() {
        return borderMarginLeft;
    }

    public int getBorderMarginTop() {
        return borderMarginTop;
    }

    public int getBorderMarginRight() {
        return borderMarginRight;
    }

    public int getBorderMarginBottom() {
        return borderMarginBottom;
    }

    public SexangleView setBorderMargin(int borderMargin) {
        return setBorderMargin(borderMargin,borderMargin,borderMargin,borderMargin);
    }
    public SexangleView setBorderMargin(int borderMarginLeft,int borderMarginTop,int borderMarginRight,int borderMarginBottom) {
        this.borderMarginLeft = borderMarginLeft;
        this.borderMarginTop = borderMarginTop;
        this.borderMarginRight = borderMarginRight;
        this.borderMarginBottom = borderMarginBottom;
        invalidate();
        return this;
    }

    public OnProgressChangeInter getOnProgressChangeInter() {
        return onProgressChangeInter;
    }

    public void setOnProgressChangeInter(OnProgressChangeInter onProgressChangeInter) {
        this.onProgressChangeInter = onProgressChangeInter;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    private int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }
}
