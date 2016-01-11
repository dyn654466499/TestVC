package com.dyn.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
  
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

/**
 * 图片工具类
 * @author 邓耀宁
 */
public class ImageUtil {
    /**
     * 获取并过滤本地较为合理的图片。
     * @param mContext
     * @return
     */
	public static List<HashMap<String,String>> getLocalImage(Context mContext) {
		int totalPhoto = 0;
		List<HashMap<String,String>> imageList;
		long IMAGE_SIZE=120000;
		
		imageList = new ArrayList<HashMap<String,String>>();	   	
		String[] proj = {
        		MediaStore.MediaColumns.DATA,               //0		
				MediaStore.Images.ImageColumns._ID,
				MediaStore.MediaColumns.SIZE,
				
				};
		final ContentResolver resolver = mContext.getContentResolver();
		Cursor nCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
		if(nCursor!=null){
			totalPhoto = nCursor.getCount();
			HashMap<String,String> map = null;
			if (totalPhoto>0) {
				
				nCursor.moveToFirst();
				for(int i = 0;i<totalPhoto;i++)
				{   
					map = new HashMap<String, String>();
					long size=Long.parseLong(nCursor.getString(2));
					if (size>IMAGE_SIZE) {
						map.put("imageURI", nCursor.getString(0));
                        map.put("imageData", nCursor.getString(1));						
						imageList.add(map);
					}
					
					nCursor.moveToNext();
				}
				nCursor.close();
			}
		}
		return imageList;						
					
	}	
	
    //质量压缩
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>50) {   //循环判断如果压缩后图片是否大于50kb,大于继续压缩       
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
      
    //图片按比例大小压缩方法
    public static Bitmap getImageFromPath(String srcPath, float width) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > width) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        }
//      else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//          be = (int) (newOpts.outHeight / hh);
//      }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
      
    //图片按比例大小压缩方法
    public static Bitmap compBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();      
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出 
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 150f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        }
//      else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//          be = (int) (newOpts.outHeight / hh);
//      }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
      
    //图片按比例大小压缩方法
    public static Bitmap compBitmap(InputStream inputStream, float width) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
//      float ww = 150f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > width) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        }
//      else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//          be = (int) (newOpts.outHeight / hh);
//      }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeStream(inputStream, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
      
     // 把Bitmap转换成Base64
    public static String getBitmapStrBase64(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, 0);
    }
  
    // 把Base64转换成Bitmap
    public static Bitmap getBitmapFromBase64(String iconBase64) {
            byte[] bitmapArray = Base64.decode(iconBase64,  Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
    }
  
    public static Drawable convertBitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        // 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
        return bd;
    }
    
    /**
     * 指定缩放图片的宽高
     * @param bitmap 缩放的二位图
     * @param width_Ratio
     * @param height_Ratio 
     * @param degrees 进行角度转换，如0度、90度。
     * @return
     */
 	public static Bitmap scaleToFit(Bitmap bitmap, float width_Ratio,
 			float height_Ratio, int degrees) {
 		int width = bitmap.getWidth(); // 图片宽度
 		int height = bitmap.getHeight(); // 图片高度
 		Matrix matrix = new Matrix();
 		matrix.postRotate(degrees);
 		matrix.postScale((float) width_Ratio / width, (float) height_Ratio
 				/ height);// 图片等比例缩小为原来的fblRatio倍
 		Bitmap bmResult = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
 				true);// 声明位图
 		return bmResult; // 返回被缩放的图片
 	}
 	
    /**
     * 旋转bitmap
     * @param bitmap 缩放的二位图
     * @param degrees 进行角度转换，如0度、90度。
     * @return
     */
 	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
 		int width = bitmap.getWidth(); // 图片宽度
 		int height = bitmap.getHeight(); // 图片高度
 		Matrix matrix = new Matrix();
 		matrix.postRotate(degrees);
 		Bitmap bmResult = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);// 声明位图
 		return bmResult; // 返回被缩放的图片
 	}
 	
 	/**
 	 * 获取图片绝对路径
 	 * @param mContext
 	 * @param uri
 	 * @return
 	 */
 	public static String getAbsoluteImagePath(Context mContext,Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver().query(uri, proj, // Which
																	// columns
																	// to
																	// return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null); // Order-by clause (ascending by name)
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
 	
 	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * 生成二维码
	 * @param url 文本(http://apk.hiapk.com/appinfo/com.dyn.voicecontrol)
	 * @param QR_WIDTH 图像宽
	 * @param QR_HEIGHT 图像高
	 * @return
	 */
	public static Bitmap createQRImage(String url,int QR_WIDTH,int QR_HEIGHT) {
		Bitmap bitmap = null;
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return bitmap;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示到一个ImageView上面
		} catch (WriterException e) {
			e.printStackTrace();
		}

		return bitmap;
	}
	
	 /**
	  * 获得圆角图片的方法  
	  * @param bitmap
	  * @param roundPx
	  * @return
	  */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
          
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap  
                .getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
   
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
   
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
   
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
   
        return output;  
    } 
    
    /**
	 * 将bitmap存放以jpg的形式到sdcard中
	 * @param bitmap
	 * @param ImageName
	 * @param path
	 */
	public static void storeImageToSDcard(Bitmap bitmap, String ImageName,
			String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		File imagefile = new File(file, ImageName + ".png");
		try {
			String command = "chmod 777 " + imagefile.getAbsolutePath();
			Log.i("dyn", "command = " + command);
			Runtime runtime = Runtime.getRuntime();

			runtime.exec(command);
		} catch (IOException e) {
			Log.i("dyn", "chmod fail!!!!");
			e.printStackTrace();
		}
		try {
			imagefile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imagefile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成 bitmap   
    {  
              int width = drawable.getIntrinsicWidth();   // 取 drawable 的长宽   
              int height = drawable.getIntrinsicHeight();  
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;         // 取 drawable 的颜色格式   
              Bitmap bitmap = Bitmap.createBitmap(width, height, config);     // 建立对应 bitmap   
              Canvas canvas = new Canvas(bitmap);         // 建立对应 bitmap 的画布   
              drawable.setBounds(0, 0, width, height);  
              drawable.draw(canvas);      // 把 drawable 内容画到画布中   
              return bitmap;  
    }
	
	 public static Drawable zoomDrawable(Drawable drawable, int w, int h)  
     {  
               int width = drawable.getIntrinsicWidth();  
               int height= drawable.getIntrinsicHeight();  
               Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap   
               Matrix matrix = new Matrix();   // 创建操作图片用的 Matrix 对象   
               float scaleWidth = ((float)w / width);   // 计算缩放比例   
               float scaleHeight = ((float)h / height);  
               matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例   
               Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图   
               return new BitmapDrawable(newbmp);       // 把 bitmap 转换成 drawable 并返回   
     } 
}
