package com.youtility.intelliwiz20.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CanvasView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoAnnotationActivity extends AppCompatActivity implements View.OnClickListener {

    private Boolean isColorFabOpen = false;
    private FloatingActionButton fabColor,fabBlack,fabGray, fabWhite,fabRed, fabGreen , fabYellow, fabBlue;
    private Boolean isShapeFabOpen = false;
    private FloatingActionButton fabShape,fabFreehand,fabLine, fabRect, fabCircle, fabOvel;
    private Animation color_fab_open,color_fab_close,shape_fab_open,shape_fab_close,rotate_forward,rotate_backward;
    private CanvasView canvas = null;
    private Button drawModeButton, textModeButton;
    private Button drawUndoModeButton, drawRedoModeButton;
    private FloatingActionButton fabSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_annotation);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        canvas = (CanvasView)this.findViewById(R.id.canvas);

        color_fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        color_fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        shape_fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        shape_fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fabColor=(FloatingActionButton)findViewById(R.id.fabColors);
        fabColor.setOnClickListener(this);
        fabBlack=(FloatingActionButton)findViewById(R.id.fabBlack);
        fabBlack.setOnClickListener(this);
        fabGray=(FloatingActionButton)findViewById(R.id.fabGray);
        fabGray.setOnClickListener(this);
        fabWhite=(FloatingActionButton)findViewById(R.id.fabWhite);
        fabWhite.setOnClickListener(this);
        fabRed=(FloatingActionButton)findViewById(R.id.fabRed);
        fabRed.setOnClickListener(this);
        fabGreen=(FloatingActionButton)findViewById(R.id.fabGreen);
        fabGreen.setOnClickListener(this);
        fabYellow=(FloatingActionButton)findViewById(R.id.fabYellow);
        fabYellow.setOnClickListener(this);
        fabBlue=(FloatingActionButton)findViewById(R.id.fabBlue);
        fabBlue.setOnClickListener(this);

        fabShape=(FloatingActionButton)findViewById(R.id.fabShapes);
        fabShape.setOnClickListener(this);
        fabFreehand=(FloatingActionButton)findViewById(R.id.fabFreehand);
        fabFreehand.setOnClickListener(this);
        fabLine=(FloatingActionButton)findViewById(R.id.fabLine);
        fabLine.setOnClickListener(this);
        fabRect=(FloatingActionButton)findViewById(R.id.fabRectangle);
        fabRect.setOnClickListener(this);
        fabCircle=(FloatingActionButton)findViewById(R.id.fabCircle);
        fabCircle.setOnClickListener(this);
        fabOvel=(FloatingActionButton)findViewById(R.id.fabOvel);
        fabOvel.setOnClickListener(this);

        Bitmap reducedSizeBitmap = scaleDown(BitmapFactory.decodeFile(getIntent().getStringExtra("PATH")),2000.0f,true);

        System.out.println("Annotation Image size: width:  " +reducedSizeBitmap.getWidth() + ", height:  "+reducedSizeBitmap.getHeight());
        /*Drawable drawableImage = new BitmapDrawable(reducedSizeBitmap);
        System.out.println("Annotation drawable Image size: width:  " +drawableImage.getIntrinsicWidth() + ", height:  "+drawableImage.getIntrinsicHeight());*/


        canvas.drawBitmap(reducedSizeBitmap);
        canvas.requestFocus();

        drawModeButton=(Button)findViewById(R.id.drawMode);
        textModeButton=(Button)findViewById(R.id.textMode);

        drawRedoModeButton=(Button)findViewById(R.id.redoMode);
        drawUndoModeButton=(Button)findViewById(R.id.undoMode);

        drawModeButton.setOnClickListener(this);
        textModeButton.setOnClickListener(this);

        drawRedoModeButton.setOnClickListener(this);
        drawUndoModeButton.setOnClickListener(this);

        fabSave=(FloatingActionButton)findViewById(R.id.fabSave);
        fabSave.setOnClickListener(this);

        canvas.setMode(CanvasView.Mode.DRAW);
        canvas.setDrawer(CanvasView.Drawer.PEN);

    }

    private Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public CanvasView getCanvas() {
        return this.canvas;
    }

    public void animateColorFAB(){

        if(isColorFabOpen){

            fabColor.startAnimation(rotate_backward);
            fabBlack.startAnimation(color_fab_close);
            fabGray.startAnimation(color_fab_close);
            fabWhite.startAnimation(color_fab_close);
            fabRed.startAnimation(color_fab_close);
            fabGreen.startAnimation(color_fab_close);
            fabYellow.startAnimation(color_fab_close);
            fabBlue.startAnimation(color_fab_close);

            fabBlack.setClickable(false);
            fabGray.setClickable(false);
            fabWhite.setClickable(false);
            fabRed.setClickable(false);
            fabGreen.setClickable(false);
            fabYellow.setClickable(false);
            fabBlue.setClickable(false);
            isColorFabOpen = false;

        } else {

            fabColor.startAnimation(rotate_forward);
            fabBlack.startAnimation(color_fab_open);
            fabGray.startAnimation(color_fab_open);
            fabWhite.startAnimation(color_fab_open);
            fabRed.startAnimation(color_fab_open);
            fabGreen.startAnimation(color_fab_open);
            fabYellow.startAnimation(color_fab_open);
            fabBlue.startAnimation(color_fab_open);

            fabBlack.setClickable(true);
            fabGray.setClickable(true);
            fabWhite.setClickable(true);
            fabRed.setClickable(true);
            fabGreen.setClickable(true);
            fabYellow.setClickable(true);
            fabBlue.setClickable(true);
            isColorFabOpen = true;

        }
    }

    public void animateShapeFAB(){

        if(isShapeFabOpen){

            fabShape.startAnimation(rotate_backward);
            fabFreehand.startAnimation(shape_fab_close);
            fabLine.startAnimation(shape_fab_close);
            fabRect.startAnimation(shape_fab_close);
            fabCircle.startAnimation(shape_fab_close);
            fabOvel.startAnimation(shape_fab_close);

            fabFreehand.setClickable(false);
            fabLine.setClickable(false);
            fabRect.setClickable(false);
            fabCircle.setClickable(false);
            fabOvel.setClickable(false);
            isShapeFabOpen = false;

        } else {

            fabShape.startAnimation(rotate_forward);
            fabFreehand.startAnimation(shape_fab_open);
            fabLine.startAnimation(shape_fab_open);
            fabRect.startAnimation(shape_fab_open);
            fabCircle.startAnimation(shape_fab_open);
            fabOvel.startAnimation(shape_fab_open);

            fabFreehand.setClickable(true);
            fabLine.setClickable(true);
            fabRect.setClickable(true);
            fabCircle.setClickable(true);
            fabOvel.setClickable(true);
            isShapeFabOpen = true;

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.fabColors:
                animateColorFAB();
                break;
            case R.id.fabBlack:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorBlack));
                break;
            case R.id.fabGray:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorGray));
                break;
            case R.id.fabWhite:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.fabRed:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorRed));
                break;
            case R.id.fabGreen:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorGreen));
                break;
            case R.id.fabYellow:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorYellow));
                break;
            case R.id.fabBlue:
                canvas.setPaintStrokeColor(getResources().getColor(R.color.colorBlue));
                break;
            case R.id.fabShapes:
                animateShapeFAB();
            case R.id.fabFreehand:
                canvas.setDrawer(CanvasView.Drawer.PEN);
                break;
            case R.id.fabLine:
                canvas.setDrawer(CanvasView.Drawer.LINE);
                break;
            case R.id.fabRectangle:
                canvas.setDrawer(CanvasView.Drawer.RECTANGLE);
                break;
            case R.id.fabCircle:
                canvas.setDrawer(CanvasView.Drawer.CIRCLE);
                break;
            case R.id.fabOvel:
                canvas.setDrawer(CanvasView.Drawer.ELLIPSE);
                break;
            case R.id.drawMode:
                canvas.setMode(CanvasView.Mode.DRAW);
                break;
            case R.id.textMode:
                canvas.setMode(CanvasView.Mode.TEXT);
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(PhotoAnnotationActivity.this);
                final EditText edittext = new EditText(PhotoAnnotationActivity.this);

                alert.setTitle(getResources().getString(R.string.photoannotation_alerttitle));
                alert.setMessage(getResources().getString(R.string.photoannotation_addtexttoimage));

                alert.setView(edittext);

                alert.setPositiveButton(getResources().getString(R.string.button_add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = edittext.getText().toString();
                        canvas.setText(str);
                        canvas.invalidate();
                    }
                });

                alert.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                break;
            case R.id.undoMode:
                canvas.undo();
                break;
            case R.id.redoMode:
                canvas.redo();
                break;
            case R.id.fabSave:
                saveCanvasImage();
                break;
        }
    }


    private void saveCanvasImage()
    {
        canvas.setDrawingCacheEnabled(true);
        Bitmap bm = canvas.getDrawingCache();

        File fPath = Environment.getExternalStorageDirectory();

        File f = null;

        f = new File(getIntent().getStringExtra("PATH"));

        try {
            FileOutputStream strm = new FileOutputStream(f, false);
            bm.compress(Bitmap.CompressFormat.PNG, 100, strm);
            strm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        canvas.clear();
        canvas.setBackground(null);
        Intent ii=new Intent();
        ii.putExtra("PATH",getIntent().getStringExtra("PATH"));
        setResult(RESULT_OK,ii);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
