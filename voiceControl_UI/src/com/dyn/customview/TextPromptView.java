package com.dyn.customview;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.dyn.utils.CommonUtil;


public class TextPromptView extends TextView {

	public TextPromptView(Context context) {
		super(context);
		
		int left = (int)CommonUtil.dipToPixels(context, 3);//left
		int top = (int)CommonUtil.dipToPixels(context, 8);//top
		int right = (int)CommonUtil.dipToPixels(context, 3);//right
		int bottom = (int)CommonUtil.dipToPixels(context, 3);//bottom
		this.setPadding(left, top, right, bottom);
		
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		this.setTextColor(Color.rgb(0xFF,0xFF,0xFF));
		setGravity(Gravity.CENTER);
	}

	public TextPromptView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setText("您要查询的天气.");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setColor(Color.rgb(204, 146, 70));
		paint.setStyle(Paint.Style.FILL);
		
		// 绘制这个三角形,你可以绘制任意多边形  
        Path path = new Path();
        int triTop_x = this.getWidth()/2;
        int triHight = 10;
        int triHafWidth = 10;
        path.moveTo(triTop_x, 0);// 此点为多边形的起点  
        path.lineTo(triTop_x + triHafWidth, triHight);  
        path.lineTo(triTop_x - triHafWidth, triHight);
        path.close(); // 使这些点构成封闭的多边形  
        canvas.drawPath(path, paint);  
        
		canvas.drawRect(0, triHight, canvas.getWidth(), canvas.getHeight(), paint);
        
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = 200;
		int heightSize = 100;
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		
		switch(widthSpecMode){
			case MeasureSpec.UNSPECIFIED:
				//widthSize = 100;
				break;
			case MeasureSpec.EXACTLY:
				widthSize = widthSpecSize;
				break;
			case MeasureSpec.AT_MOST:
				//widthSize = 100;
				break;
		}
		
		heightSize = this.getSuggestedMinimumHeight();
		
		this.setMeasuredDimension(widthSize, heightSize);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
}
