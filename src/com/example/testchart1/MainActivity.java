package com.example.testchart1;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	MYChart chart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		chart = (MYChart) findViewById(R.id.chart);

		String[] xvalues = new String[] { "��һ", "�ܶ�", "����", "����", "����" };
		float[] yvalues = new float[] { 1.6f, 2.5f, 1.3f, 1.9f, 1.6f };
		chart.setValues(xvalues, yvalues);

	}
}
