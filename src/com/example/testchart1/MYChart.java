package com.example.testchart1;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 * @author Ѧ��
 * 
 * @date 2014-9-2����9:39:21
 */
public class MYChart extends View implements OnTouchListener {
	public static String Tag = "MYChart7";

	private boolean inited = false;// �Ƿ��ʼ��

	private int width;// �߶�
	private int height;// ���
	private Paint paint;// ����
	private Path path_bg;// ���߱���·��
	private Path path_line;// ����·��
	private Shader mShader;// ����
	private PathEffect effects;

	private float[] Yvalues;
	private PointF[] YPoints;// Y���Ӧ����
	private float max = 1000;// Y�����ֵ
	private float min;// Y����Сֵ
	private String[] Xvalues;

	private int color_line = Color.parseColor("#0f9b7c");
	private int color_goal = Color.parseColor("#3ca73b");
	private int color_inaxle = Color.GRAY;
	private int color_lable = Color.GRAY;
	private int[] color_inbg = new int[] { Color.parseColor("#0f9b7c"), Color.parseColor("#0f9b7c50") };

	private int padLeft, padRight, padTop, padBot;
	private int lable_txt_size = 30;
	private float goal;
	private float Dgoal;// Ŀ���Ӧ����
	private int itemWidth;

	private float xTuch = -1;// ��ָʾ��ʱ��x����
	private int showIndicateIndex = -1;

	public MYChart(Context context) {
		super(context);
	}

	public MYChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MYChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(0xff27a6e9);
		if (!inited) {
			init();
		}

		if (YPoints == null) {
			return;
		}

		int d = 4;// Y��̶���
		int iy = (height - padTop - padBot) / d;// Y��̶ȼ��
		paint.setColor(color_inaxle);
		paint.setStyle(Paint.Style.FILL);
		// ��X����
		canvas.drawLine(padLeft, height - padBot, width - padRight, height - padBot, paint);
		// ��������
		paint.setPathEffect(effects);
		for (int i = 0; i < d; i++) {
			canvas.drawLine(padLeft, i * iy + padTop, width - padRight, i * iy + padTop, paint);
		}
		paint.setPathEffect(null);

		// ��Y�̶�lable
		paint.setColor(color_lable);
		paint.setTextSize(lable_txt_size);
		paint.setTextAlign(Paint.Align.RIGHT);
		int[] ys = new int[d];
		for (int i = 0; i < d; i++) {
			ys[i] = toInt(max / d * (i + 1));
		}
		for (int i = 0; i < d; i++) {
			canvas.drawText(ys[d - 1 - i] + "", padLeft, i * iy + padTop + lable_txt_size / 2, paint);
		}
		// ��X�̶�lable//��X��̶�
		paint.setTextAlign(Paint.Align.CENTER);
		for (int i = 0; i < Xvalues.length; i++) {
			if (Xvalues.length == 24) {
				if (i % 6 != 0) {
					continue;
				}
			}
			if (Xvalues.length >= 28) {
				if ((i + 1) % 5 != 0) {
					continue;
				}
			}
			float textWidth = paint.measureText(Xvalues[i] + "");
			canvas.drawText(Xvalues[i] + "", YPoints[i].x, height - padBot + lable_txt_size * 4 / 3, paint);
			canvas.drawCircle(YPoints[i].x, height - padBot, 3, paint);
		}

		// ��Ŀ����
		if (goal != 0) {
			paint.setColor(color_goal);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(6);
			canvas.drawLine(padLeft, Dgoal, width - padRight, Dgoal, paint);
		}

		// �����߱���
		paint.setShader(mShader);
		canvas.drawPath(path_bg, paint);
		paint.setShader(null);

		// ������
		paint.setColor(color_line);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawPath(path_line, paint);
		paint.setStyle(Paint.Style.FILL);

		// ��ָʾ��
		if (showIndicateIndex != -1) {
			String txt = Xvalues[showIndicateIndex] + "(" + Yvalues[showIndicateIndex] + ")";
			float width = paint.measureText(txt);
			if ((YPoints[showIndicateIndex].x - width * 4 / 3) > padLeft) {
				canvas.drawText(txt, YPoints[showIndicateIndex].x - width * 4 / 3, padTop + lable_txt_size, paint);
			} else {
				canvas.drawText(txt, YPoints[showIndicateIndex].x + width / 3, padTop + lable_txt_size, paint);
			}
		}
		if (xTuch <= (width - padRight) && xTuch >= padLeft) {
			paint.setColor(color_line);
			paint.setStrokeWidth(2);
			paint.setStyle(Paint.Style.STROKE);
			paint.setTextSize(lable_txt_size);
			paint.setPathEffect(effects);
			canvas.drawLine(xTuch, padTop, xTuch, height - padBot, paint);
			paint.setPathEffect(null);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;

		init();
	}

	public void setValues(String[] xvalues, float[] yvalues) {
		Xvalues = xvalues;
		Yvalues = yvalues;
		init();
	}

	public void setGoal(float goal) {
		this.goal = goal;
	}

	private void init() {
		if (!inited) {
			path_bg = new Path();
			path_line = new Path();
			effects = new DashPathEffect(new float[] { 16, 16, 16 }, 1);

			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(lable_txt_size);

			padLeft = (int) (paint.measureText("0000") * 6 / 5);
			padRight = lable_txt_size * 2;
			padTop = lable_txt_size * 2;
			padBot = lable_txt_size * 2;

			this.setOnTouchListener(this);
		}

		/* ���ñ��� */
		mShader = new LinearGradient(0, 0, 0, height, color_inbg, null, Shader.TileMode.CLAMP);

		/* ����Y�������Сֵ */
		if (Xvalues == null) {
			return;
		}
		max = Yvalues[0];
		min = Yvalues[0];
		for (float i : Yvalues) {
			if (max < i) {
				max = i;
			}
			if (min > i) {
				min = i;
			}
		}
		if (goal != 0 && max < goal) {
			max = (float) (goal * 1.1);
		}
		if (max == 0) {
			max = 1000;
		}
		max = tobigInt(max);

		/* ����Y�� */
		YPoints = new PointF[Yvalues.length];
		float scal = (height - padTop - padBot) / (float) max;
		itemWidth = (width - padLeft - padRight) / (YPoints.length - 1);
		for (int i = 0; i < Yvalues.length; i++) {
			float x = i * itemWidth + padLeft;
			YPoints[i] = new PointF(x, (int) (height - Yvalues[i] * scal - padBot));
		}

		/* ����Ŀ��Y�� */
		Dgoal = (int) (height - goal * scal - padBot);

		/* ���߱���·�� */
		path_bg.reset();
		path_line.reset();
		float cache = 0f;
		for (int i = 0; i < YPoints.length; i++) {
			if (i == 0) {
				path_bg.moveTo(YPoints[i].x, height - padBot);
				path_bg.lineTo(YPoints[i].x, YPoints[i].y);
				path_line.moveTo(YPoints[i].x, YPoints[i].y);
			} else {
				float baseX = (YPoints[i - 1].x + YPoints[i].x) / 2;
				path_bg.cubicTo(baseX, YPoints[i - 1].y, baseX, YPoints[i].y, YPoints[i].x, YPoints[i].y);
				path_line.cubicTo(baseX, YPoints[i - 1].y, baseX, YPoints[i].y, YPoints[i].x, YPoints[i].y);
				cache = YPoints[i].x;
			}
			// else {
			// path.lineTo(YPoints[i].x, YPoints[i].y);
			// cache = YPoints[i].x;
			// }
		}
		path_bg.lineTo(cache, height - padBot);
		path_bg.close();

		inited = true;

		invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		synchronized (this) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				if (YPoints != null) {
					xTuch = event.getX();
					if (xTuch > (width - padRight)) {
						xTuch = width - padRight;
					} else if (xTuch <= padLeft) {
						xTuch = padLeft;
					}

					for (int i = 0; i < YPoints.length; i++) {
						if (xTuch < (YPoints[i].x + itemWidth / 2) && xTuch > (YPoints[i].x - itemWidth / 2)) {
							showIndicateIndex = i;
							break;
						} else {
							showIndicateIndex = -1;
						}
					}
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			return true;
		}
	}

	/**
	 * ȥС��
	 * 
	 * @param num
	 * @return
	 */
	public int toInt(float num) {
		DecimalFormat fnum = new DecimalFormat("##0");
		String dd = fnum.format(num);
		return Integer.parseInt(dd);
	}

	/**
	 * ȡ�� ������λ
	 * 
	 * @param num
	 * @return
	 */
	public int tobigInt(float num) {
		int b = (int) Math.ceil(num);
		int l = (b + "").length();
		if (l >= 2) {
			l = l - 1;
		}
		int d = (int) Math.pow(10, (l - 1));
		num = num / d;
		b = (int) Math.ceil(num);
		b = b * d;
		return b;
	}

}
