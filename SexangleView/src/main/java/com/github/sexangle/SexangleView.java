package com.github.sexangle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @createBy r-zhong
 * @time 2018-12-06 10:07
 */
public class SexangleView extends View {


    private Path path1=new Path();
    private Path oldPath;

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


    /********************六边形进度条*********************/
    private int radius=60;

    private Paint sexanglePaint;
    private Path sexanglePath;
    private Path newPath;
    /*六边形颜色*/
    private int sexangleColor;
    /*六边形宽度*/
    private int sexangleWidth = 60;
    /*六边形进度*/
    private float progress = 30;
    /*六边形总进度*/
    private float max = 100;

    /*是否顺时针*/
    private boolean isClockwise = true;
    /*1：垂直方向 2：水平方向*/
    private int direction = 1;

    /*进度渲染器*/
    private Shader shader;

    /*绘制六边形区域宽度*/
    private float viewWidth;
    /*绘制六边形区域高度*/
    private float viewHeight;

    /*六边形宽度*/
    private float sw;
    /*六边形高度*/
    private float sh;

    /*六边形边长*/
    private float sideLength;

    /*进度起始位置，和时钟位置1-12一致*/
    private int startPoint=12;



    /******************六边形second进度条****************/
    private Paint sexangleSecondPaint;
    private Path sexangleSecondPath;
    private int sexangleSecondColor;
//    private float secondProgress;


    /*********************六边形border*******************/
    private Paint borderPaint;
    private Path borderPath;
    /*边框颜色*/
    private int borderColor;
    /*边框宽度*/
    private int borderWidth = 2;
    /*虚线长度*/
    private int borderDashLength = 0;
    /*虚线间隔*/
    private int borderDashGap = 0;
    private PathEffect pathEffect;


    /*记录圆弧外切矩形坐标*/

    private List<Float[]> lineList;
    private RectF[]arcRect;

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
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if(direction==1){
            /*使六边形完全在view内部绘制，减去线条宽度*/
            viewWidth =viewWidth-sexangleWidth;
            viewHeight= (float) (viewHeight-sexangleWidth/Math.cos(Math.toRadians(30)));

            //垂直方向
            if(viewWidth/viewHeight>Math.cos(Math.toRadians(30))){
                //绘制区域宽度过长
                sideLength=viewHeight/2;
                sw = (float) (2*viewHeight/2*Math.cos(Math.toRadians(30)));
                sh =2*sideLength;
            }else{
                //绘制区域高度过长
                sideLength= (float) (viewWidth/2/Math.cos(Math.toRadians(30)));
                sw =viewWidth;
                sh =2*sideLength;
            }
        }else{
            /*使六边形完全在view内部绘制*/
            viewWidth = (float) (viewWidth-sexangleWidth/Math.cos(Math.toRadians(30)));;
            viewHeight = viewHeight-sexangleWidth;

            //水平方向
            if(viewWidth/2f/viewHeight>Math.tan(Math.toRadians(30))){
                //绘制区域宽度过长
                sideLength= (float) (viewHeight/2/Math.cos(Math.toRadians(30)));
                sh =viewHeight;
                sw =2*sideLength;
            }else{
                //绘制区域高度过长
                sideLength=viewWidth/2;
                sw =viewWidth;
                sh = (float) (2*sideLength/2/Math.tan(Math.toRadians(30)));
            }
        }

        initPaint();

        initPath();

        initPoint();
    }

    private void initPoint() {
        lineList=new ArrayList<>();

        arcRect =new RectF[6];

        oldPath = new Path();

        if(direction==1){

            /*第一个外切圆的矩形坐标*/
            RectF firstRect=new RectF(-radius, -1f*(sh/2f-(radius/cos30-radius)),radius,  -1f*(sh/2f-(radius/cos30-radius))+2f*radius);

            arcRect[0]=firstRect;

            //位移长度
            float xieLength=sideLength-2*radius*tan30;
            //x位移距离
            float translateX=cos30*xieLength;
            //y位移距离
            float translateY=sin30*xieLength;


            RectF rectF = new RectF();

            Matrix matrix=new Matrix();
            matrix.postTranslate(translateX,translateY);
            matrix.mapRect(rectF,firstRect);
            arcRect[1]=rectF;


            rectF = new RectF();
            matrix.postTranslate(0,xieLength);
            matrix.mapRect(rectF,firstRect);
            arcRect[2]=rectF;


            rectF = new RectF();
            matrix.postTranslate(-translateX,translateY);
            matrix.mapRect(rectF,firstRect);
            arcRect[3]=rectF;


            rectF = new RectF();
            matrix.postTranslate(-translateX,-translateY);
            matrix.mapRect(rectF,firstRect);
            arcRect[4]=rectF;

            rectF = new RectF();
            matrix.postTranslate(0,-xieLength);
            matrix.mapRect(rectF,firstRect);
            arcRect[5]=rectF;



           /*
            三角函数计算出第二个矩形坐标，上面一律用矩阵计算
            arcPoint[2]=new PointView(sw/2f-2f*radius,-1f*(sh/2f-(sideLength*sin30+radius*tan30-radius)));
            arcPoint[3]=new PointView(sw/2f,-1f*(sh/2f-(sideLength*sin30+radius*tan30-radius))+2f*radius);
            */



            /*第一条线段 起始坐标,共有6条线段*/


            /*第一条线段start*/
            float[] firstStartPoint=new float[2];
            firstStartPoint[0]=radius*sin30;
            firstStartPoint[1]=-1f*(sh/2f-(radius/cos30-radius*cos30));



            /*第一条线段end*/
            float[]firstEndPoint=new float[2];

            matrix=new Matrix();
            matrix.postTranslate(translateX,translateY);
            matrix.mapPoints(firstEndPoint,firstStartPoint);


            float[] firstLine=new float[]{firstStartPoint[0],firstStartPoint[1],firstEndPoint[0],firstEndPoint[1]};
            /*添加第1条线段*/
            lineList.add(new Float[]{firstStartPoint[0],firstStartPoint[1],firstEndPoint[0],firstEndPoint[1]});



            matrix=new Matrix();

            float[]linePoint=new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint,firstLine);
            /*添加第2条线段*/
            lineList.add(new Float[]{linePoint[0],linePoint[1],linePoint[2],linePoint[3]});


            linePoint=new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint,firstLine);
            /*添加第3条线段*/
            lineList.add(new Float[]{linePoint[0],linePoint[1],linePoint[2],linePoint[3]});


            linePoint=new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint,firstLine);
            /*添加第4条线段*/
            lineList.add(new Float[]{linePoint[0],linePoint[1],linePoint[2],linePoint[3]});


            linePoint=new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint,firstLine);
            /*添加第5条线段*/
            lineList.add(new Float[]{linePoint[0],linePoint[1],linePoint[2],linePoint[3]});

            linePoint=new float[4];
            matrix.postRotate(60);
            matrix.mapPoints(linePoint,firstLine);
            /*添加第6条线段*/
            lineList.add(new Float[]{linePoint[0],linePoint[1],linePoint[2],linePoint[3]});






            /*添加第1条弧的右半部分*/
            Path path=new Path();
            path.addArc(arcRect[0],-90,30);
            oldPath.addPath(path);
            /*添加第1条线段*/
            path.reset();
            path.moveTo(lineList.get(0)[0],lineList.get(0)[1]);
            path.lineTo(lineList.get(0)[2],lineList.get(0)[3]);
            oldPath.addPath(path);

            /*添加第2条弧*/
            path.reset();
            path.addArc(arcRect[1],-60,60);
            oldPath.addPath(path);
            /*添加第2条线段*/
            path.reset();
            path.moveTo(lineList.get(1)[0],lineList.get(1)[1]);
            path.lineTo(lineList.get(1)[2],lineList.get(1)[3]);
            oldPath.addPath(path);

            /*添加第3条弧*/
            path.reset();
            path.addArc(arcRect[2],0,60);
            oldPath.addPath(path);
            /*添加第3条线段*/
            path.reset();
            path.moveTo(lineList.get(2)[0],lineList.get(2)[1]);
            path.lineTo(lineList.get(2)[2],lineList.get(2)[3]);
            oldPath.addPath(path);

            /*添加第4条弧*/
            path.reset();
            path.addArc(arcRect[3],60,60);
            oldPath.addPath(path);
            /*添加第4条线段*/
            path.reset();
            path.moveTo(lineList.get(3)[0],lineList.get(3)[1]);
            path.lineTo(lineList.get(3)[2],lineList.get(3)[3]);
            oldPath.addPath(path);

            /*添加第5条弧*/
            path.reset();
            path.addArc(arcRect[4],120,60);
            oldPath.addPath(path);
            /*添加第5条线段*/
            path.reset();
            path.moveTo(lineList.get(4)[0],lineList.get(4)[1]);
            path.lineTo(lineList.get(4)[2],lineList.get(4)[3]);
            oldPath.addPath(path);

            /*添加第6条弧*/
            path.reset();
            path.addArc(arcRect[5],180,60);
            oldPath.addPath(path);
            /*添加第6条线段*/
            path.reset();
            path.moveTo(lineList.get(5)[0],lineList.get(5)[1]);
            path.lineTo(lineList.get(5)[2],lineList.get(5)[3]);
            oldPath.addPath(path);

            /*添加第1条弧的左半部分*/
            path.reset();
            path.addArc(arcRect[0],-120,30);
            oldPath.addPath(path);



            PathMeasure pathMeasure=new PathMeasure(newPath,true);
            Log("==PathMeasure="+pathMeasure.getLength());
            pathMeasure.getSegment(0,pathMeasure.getLength(), path,true);

            oldPath.rLineTo(0,0);
              pathMeasure=new PathMeasure(oldPath,true);
              Log("==="+pathMeasure.nextContour());

            for (int i = 0; i < 13; i++) {
                Log(pathMeasure.nextContour()+"==PathMeasureoldPath="+pathMeasure.getLength());
            }


            pathMeasure.getSegment(0,pathMeasure.getLength()*0.8f, path1,true);

        }else{

        }
    }


    private void initPaint() {
        sexanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sexanglePaint.setStrokeWidth(sexangleWidth);
        sexanglePaint.setStrokeCap(Paint.Cap.ROUND);
        sexanglePaint.setStyle(Paint.Style.STROKE);
        sexanglePaint.setColor(sexangleColor);


        sexangleSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sexangleSecondPaint.setStrokeWidth(sexangleWidth);
        sexangleSecondPaint.setStyle(Paint.Style.STROKE);
        sexangleSecondPaint.setColor(sexangleSecondColor);


        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);


        ////

        pathEffect=new CornerPathEffect(radius);


        if (borderDashLength > 0 && borderDashGap > 0) {
            if (pathEffect != null) {
                sexanglePaint.setPathEffect(pathEffect);
                sexangleSecondPaint.setPathEffect(pathEffect);
                borderPaint.setPathEffect(pathEffect);
            } else {
                DashPathEffect dashPathEffect = new DashPathEffect(new float[]{borderDashLength, borderDashGap}, 0);
                sexanglePaint.setPathEffect(dashPathEffect);
                sexangleSecondPaint.setPathEffect(dashPathEffect);
                borderPaint.setPathEffect(dashPathEffect);
            }
        }else{
            if (pathEffect != null) {
                sexanglePaint.setPathEffect(pathEffect);
                sexangleSecondPaint.setPathEffect(pathEffect);
                borderPaint.setPathEffect(pathEffect);
            }
        }

    }


    private void initPath() {
        newPath = new Path();

        sexanglePath = new Path();
        sexangleSecondPath = new Path();
        borderPath = new Path();

        if(direction==1){
            //垂直方向
            sexanglePath.moveTo(0,-sideLength);
            sexanglePath.lineTo(sw /2,-sideLength/2);
            sexanglePath.lineTo(sw /2,sideLength/2);
            sexanglePath.lineTo(0,sideLength);
            sexanglePath.lineTo(-sw /2,sideLength/2);
            sexanglePath.lineTo(-sw /2,-sideLength/2);
            sexanglePath.close();




        }else{
            //水平方向

            sexanglePath.moveTo(0,sh/2);
            sexanglePath.lineTo(-sideLength /2,sh/2);
            sexanglePath.lineTo(-sideLength,0);
            sexanglePath.lineTo(-sideLength /2,-sh/2);
            sexanglePath.lineTo(sideLength /2,-sh/2);
            sexanglePath.lineTo(sideLength,0);
            sexanglePath.lineTo(sideLength /2,sh/2);

            sexanglePath.close();


        }



//        sexangleSecondPath.rLineTo(0,0);
        PathMeasure pathMeasure=new PathMeasure(sexanglePath,true);
        pathMeasure.getSegment(0,pathMeasure.getLength()*0.3f,sexangleSecondPath,true);
//        sexangleSecondPath.close();


        Shader shader=new SweepGradient(0,0,new int[]{Color.parseColor("#34e8a6"),Color.parseColor("#06C1AE")},new float[]{0.5f,1});
        Shader shader1=new LinearGradient(0,-getHeight()/2,0,getHeight()/2,new int[]{Color.parseColor("#42C370"),Color.parseColor("#0BAD83")},null,Shader.TileMode.CLAMP);
        sexanglePaint.setShader(shader1);

       /* sexangleSecondPath.reset();

        sexangleSecondPath.moveTo(0,-sideLength);
        sexangleSecondPath.lineTo(sw /2,-sideLength/2);
        sexangleSecondPath.lineTo(sw /2,sideLength/2);
        sexangleSecondPath.lineTo(0,sideLength);*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.translate((getWidth() -getPaddingLeft()-getPaddingRight())/2+getPaddingLeft(), (getHeight() -getPaddingTop()-getPaddingBottom())/ 2+getPaddingTop());





        canvas.drawPath(sexanglePath,sexangleSecondPaint);
        canvas.drawPath(sexangleSecondPath,sexanglePaint);


        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(sexangleWidth);
        paint.setColor(Color.BLACK);

        canvas.drawRect(arcRect[0],paint);
        canvas.drawRect(arcRect[1],paint);
        canvas.drawRect(arcRect[2],paint);
        canvas.drawRect(arcRect[3],paint);
        canvas.drawRect(arcRect[4],paint);
        canvas.drawRect(arcRect[5],paint);



        paint.setStrokeWidth(5);
/*
        for (int i = 0; i < lineList.size(); i++) {
            canvas.drawPoint(lineList.get(i)[0],lineList.get(i)[1],paint);
            canvas.drawPoint(lineList.get(i)[2],lineList.get(i)[3],paint);
        }*/



        paint.setStrokeWidth(sexangleWidth/2);

        canvas.drawPath(path1,paint);

        canvas.restore();

    }
    public void Log(String string){
        Log.i("===","==="+string);
    }
}
