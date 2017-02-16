package com.empty.systempiccrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class CropAct extends AppCompatActivity {

	private static final String FILE_NAME = "car_number_plate_identify_take.jpg";
	private static final String CUT_NAME  = "car_number_plate_identify_cut.jpg";
	private static final String ZOOM_NAME = "car_number_plate_identify_zoom.jpg";
	private static final String TEMP_NAME = "car_number_plate_identify_temp.jpg";

	private final int TAKE_PHOTO_REQUEST_CODE = 1;
	private final int CUT_PHOTO_REQUEST_CODE  = 2;
	private ImageView iv_photo;

	private final String inputPath = "file://"+Environment.getExternalStorageDirectory() + "/" + CUT_NAME;
	private Uri    outputUri = Uri.parse( inputPath );

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_crop );

		iv_photo = ( ImageView ) findViewById( R.id.iv );

		openCamera();
	}

	private void openCamera() {
		Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
		intent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile( new File( Environment.getExternalStorageDirectory(), FILE_NAME )
		) );
		startActivityForResult( intent, TAKE_PHOTO_REQUEST_CODE );
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult( requestCode, resultCode, data );
		Log.e( "ddd", "requestCode == " + requestCode + " , resultCode == " + resultCode + " , data == " + data );
		switch ( requestCode ) {
			case TAKE_PHOTO_REQUEST_CODE:
				if ( resultCode == Activity.RESULT_CANCELED )
					return;
				FileUtil.compressFile( Environment.getExternalStorageDirectory() + "/" + FILE_NAME,
				                       Environment.getExternalStorageDirectory() + "/" + TEMP_NAME );
				File picture = new File( Environment.getExternalStorageDirectory() + "/" + TEMP_NAME );
				Uri uri = Uri.fromFile( picture );
				startPhotoCut( uri );
				break;
			case CUT_PHOTO_REQUEST_CODE:
				Log.e( "ddd", "data == null ? " + ( data == null ) );
				Log.e( "ddd", "parExtra == null ? " + data.getParcelableExtra( "data" ) );
				Log.e( "ddd", "extra == null ? " + data.getExtras() );

				if(data == null){
					return;
				}

				if ( data.getExtras() == null ) {
					openCamera();
					return;
				}

				Bitmap bitmap = decodeUriAsBitmap(outputUri);//decode bitmap
				iv_photo.setImageBitmap(bitmap);

				if ( data != null ) {



//					Bundle extras = data.getExtras();
//					if ( extras != null ) {
//						Bitmap cutPhoto = data.getParcelableExtra( "data" );
//						if ( cutPhoto != null ) {
//							try {
//								File cutPhotoFile = FileUtil.saveFile( cutPhoto, Environment.getExternalStorageDirectory().getAbsolutePath
//									(), CUT_NAME );
//								//								FileUtil.compressFile( cutPhotoFile.getAbsolutePath(),
//								//								                       Environment.getExternalStorageDirectory() + "/" +
//								// ZOOM_NAME );
//								Bitmap bitmap = BitmapFactory.decodeFile( Environment.getExternalStorageDirectory() + "/" + CUT_NAME );
//								iv_photo.setImageBitmap( bitmap );
//								//								identify();
//								return;
//							} catch ( Exception e ) {
//								// TODO: 2017/2/13
//								e.printStackTrace();
//							}
//						}
//					}

					//				try {
					////					FileUtil.compressFile( Environment.getExternalStorageDirectory() + "/" + TEMP_NAME,
					////					                       Environment.getExternalStorageDirectory() + "/" + ZOOM_NAME );
					//					Bitmap bitmap = BitmapFactory.decodeFile( Environment.getExternalStorageDirectory() + "/" +
					// ZOOM_NAME );
					//					iv_photo.setImageBitmap( bitmap );
					//					identify();
					//					return;
					//				} catch ( Exception e ) {
					//					// TODO: 2017/2/13
					//					e.printStackTrace();
					//				}
					break;
				}
		}
	}

	/**
	 * 裁剪图片
	 *
	 * @param uri
	 */
	public void startPhotoCut(Uri uri) {



		Intent intent = new Intent( "com.android.camera.action.CROP" );
		intent.setDataAndType( uri, "image/*" );

		intent.putExtra( "crop", "true" );
		intent.putExtra( "aspectX", 2 );
		intent.putExtra( "aspectY", 1 );
		intent.putExtra( "outputX", 600 );
		intent.putExtra( "outputY", 300 );
		intent.putExtra( "scale", true );
		intent.putExtra( "return-data", false );
		intent.putExtra( MediaStore.EXTRA_OUTPUT, outputUri );
		intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString() );
		intent.putExtra( "noFaceDetection", true );// no face detection

		startActivityForResult( intent, CUT_PHOTO_REQUEST_CODE );
	}

	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try{
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
