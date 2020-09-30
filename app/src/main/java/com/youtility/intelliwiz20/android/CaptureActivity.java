package com.youtility.intelliwiz20.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.EventLogInsertion;
import com.youtility.intelliwiz20.android.camera.CameraManager;
import com.youtility.intelliwiz20.android.result.ResultHandler;
import com.youtility.intelliwiz20.android.result.ResultHandlerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author Kapil Vats
 * 
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

	private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
			ResultMetadataType.SUGGESTED_PRICE, ResultMetadataType.ERROR_CORRECTION_LEVEL, ResultMetadataType.POSSIBLE_COUNTRY);

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private TextView attendanceTypeTextView;
	private String attendanceType;
	private View resultView;
//	private Result lastResult;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
    private String fromActivity=null;
	private boolean isFromBuzzer=false;
	private SharedPreferences sharedPreferences;
	private ImageView flashOnOffImageView;
	private SharedPreferences stepCounterPref;
	

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CaptureActivity.this);
		stepCounterPref=getSharedPreferences(Constants.STEP_COUNTER_PREF, MODE_PRIVATE);

		if(getIntent().hasExtra("FROM")) {

			fromActivity=getIntent().getStringExtra("FROM");

			if (fromActivity.equalsIgnoreCase("MARK"))
				Constants.QR_CODE_SCANNER_CAMERA = 1;
			else
				Constants.QR_CODE_SCANNER_CAMERA = 0;
		}

		if(getIntent().hasExtra("BUZZER")) {
			isFromBuzzer = true;
			Constants.QR_CODE_SCANNER_CAMERA = 0;
		}

		if(getIntent().hasExtra("TYPE"))
		{
			attendanceType=getIntent().getStringExtra("TYPE");
			System.out.println("attendance Type=="+ attendanceType);
		}

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		onCreateActivity();
	}

	public void onCreateActivity() {
		/*Bundle b = getIntent().getExtras();
		task = (Task) b.getSerializable("Task");*/
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.

		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		resultView = findViewById(R.id.result_view);
		statusView = (TextView) findViewById(R.id.status_view);
		attendanceTypeTextView=(TextView)findViewById(R.id.attendanceType);

		if(attendanceType != null && fromActivity.equalsIgnoreCase("MARK") )
		{
			attendanceTypeTextView.setVisibility(View.VISIBLE);
			attendanceTypeTextView.setText(attendanceType+"");
			if(attendanceType.equalsIgnoreCase(Constants.ATTENDANCE_PUNCH_TYPE_IN)) {
				attendanceTypeTextView.setTextColor(getResources().getColor(R.color.colorGreen));
			}
			else {
				attendanceTypeTextView.setTextColor(getResources().getColor(R.color.colorRed));
			}
		}
		else
		{
			attendanceTypeTextView.setVisibility(View.GONE);
		}

		flashOnOffImageView=(ImageView)findViewById(R.id.imageView1);
		flashOnOffImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		handler = null;
	//	lastResult = null;

		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();

		inactivityTimer.onResume();

		Intent intent = getIntent();
		decodeFormats = null;
		characterSet = null;

		if (intent != null) {

			String action = intent.getAction();

			if (Intents.Scan.ACTION.equals(action)) {

				// Scan the formats the intent requested, and return the result
				// to the calling activity.
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);

				if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}

				String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				if (customPromptMessage != null) {
					statusView.setText(customPromptMessage);
				}

			}
			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

		}
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
			// Handle these events so they don't launch the Camera app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		Log.e(TAG, rawResult.getText());
		inactivityTimer.onActivity();
	//	lastResult = rawResult;
		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(CaptureActivity.this, rawResult);

		if (barcode == null) {
			// This is from history -- no saved barcode
			handleDecodeInternally(rawResult, resultHandler, null);
		} else {
			beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, rawResult);
			handleDecodeInternally(rawResult, resultHandler, barcode);
		}
	}

	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_image_border));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
			canvas.drawRect(border, paint);

			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}
	

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
		statusView.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.GONE);
		resultView.setVisibility(View.VISIBLE);
		final String res = rawResult.getText();

		System.out.println("---raw res:"+res);
		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		if (barcode == null) {
			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
		} else {
			barcodeImageView.setImageBitmap(barcode);
		}

		TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
		formatTextView.setText(rawResult.getBarcodeFormat().toString());

		TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
		typeTextView.setText(resultHandler.getType().toString());

		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		String formattedTime = formatter.format(new Date(rawResult.getTimestamp()));
		TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
		timeTextView.setText(formattedTime);

		TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
		View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
		metaTextView.setVisibility(View.GONE);
		metaTextViewLabel.setVisibility(View.GONE);
		Map<ResultMetadataType, Object> metadata = rawResult.getResultMetadata();
		if (metadata != null) {
			StringBuilder metadataText = new StringBuilder(20);
			for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
				metaTextView.setText(metadataText);
				metaTextView.setVisibility(View.VISIBLE);
				metaTextViewLabel.setVisibility(View.VISIBLE);
			}
		}

		TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
		CharSequence displayContents = resultHandler.getDisplayContents();
		contentsTextView.setText(displayContents.toString());
		// Crudely scale betweeen 22 and 32 -- bigger font for shorter text
		int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
		contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
		Log.d("Result1", " scanned result get text res "+res+" text "+displayContents.toString());
		ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
		buttonView.requestFocus();
		TextView button = (TextView) buttonView.getChildAt(0);
		button.setVisibility(View.INVISIBLE);
		button.setText("OK");
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				handleResult(res);

			}
		});
		
		handleResult(res);
	}

	public void handleResult(String res)
	{
		if(!isFromBuzzer) {
			Intent returnIntent = new Intent();
			System.out.println("result:::"+res);
			returnIntent.putExtra("SCAN_RESULT", res);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
		else
		{
			String originPoint=sharedPreferences.getString(Constants.SETTING_ORIGIN_POINT,"");
			if(originPoint.trim().length()>0)
			{
				if(res.trim().equalsIgnoreCase(originPoint))
				{
					EventLogInsertion eventLogInsertion=new EventLogInsertion(CaptureActivity.this);
					eventLogInsertion.editBuzzerStepCountEvent(stepCounterPref.getLong(Constants.STEP_COUNTER_ID,-1),res.trim());

					/*EventLogInsertion eventLogInsertion = new EventLogInsertion(CaptureActivity.this);
					eventLogInsertion.addBuzzerStepCountEvent("CAPTCHA", "Captcha", "Event Type", res.trim());*/
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setResult(RESULT_OK);
							finish();
						}
					});

				}
				else
				{
					Toast.makeText(CaptureActivity.this,"Origin point and QR Code are not matched!!", Toast.LENGTH_LONG).show();
					//startActivity(getIntent());
					setResult(RESULT_CANCELED);
					finish();
				}
			}
			else
			{
				EventLogInsertion eventLogInsertion=new EventLogInsertion(CaptureActivity.this);
				eventLogInsertion.editBuzzerStepCountEvent(stepCounterPref.getLong(Constants.STEP_COUNTER_ID,-1),"NO_ORIGIN_POINT_AVAILABLE_"+res.trim());
				/*EventLogInsertion eventLogInsertion = new EventLogInsertion(CaptureActivity.this);
				eventLogInsertion.addBuzzerStepCountEvent("STEPCOUNT", "Stepcount", "Event Type", "NO_ORIGIN_POINT_AVAILABLE_"+res.trim());*/
				setResult(RESULT_OK);
				finish();
			}
		}
	}


	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		resultView.setVisibility(View.GONE);
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		//lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}
}