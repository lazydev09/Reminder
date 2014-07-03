package com.vf.reminder.adapter;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.vf.reminder.R;

public class ContactSimpleCursorAdapter extends SimpleCursorAdapter {

	public ContactSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		// 
	}
	
	@Override
	  public void bindView(View view, Context context, Cursor cursor) {
	      ImageView imageView = (ImageView) view.findViewById(R.id.imgPlayVideo);
	      String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
	      int photoId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
	      Log.i("TEST PHOTO", "PHOTO ID"+photoId);
	      
	      //cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
	      if(photoId<0){
	      imageView.setImageResource(R.drawable.ic_contact_picture_holo_light);
	      
	      
	      }
	      else{
	    	  Bitmap photoBitmap = loadContactPhoto(context, photoId);
	    	  if (photoBitmap != null) {
	    		  imageView.setImageBitmap(photoBitmap);
	    		  //imageView.setTag("photoId"+photoId);
	    	    }
	    	  else{
	    		  imageView.setImageResource(R.drawable.ic_contact_picture_holo_light);
	    		  imageView.setTag(name);
	    	  }
	      }
	      

	    
	    //  imageView.setImageBitmap(bitmap);
	      super.bindView(view, context, cursor);
	  }

	public Bitmap loadContactPhoto(Context context,long id) {
	    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, id);
	    byte[] data = null;
	    Cursor cursor = context.getContentResolver().query(
	        contactUri, // Uri
	        new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO }, // projection, the contact photo
	        ContactsContract.Contacts.PHOTO_ID + "!= 0", // where statement, only if the contact has a photo
	        null, null);
	    Log.i("ContactSimpleCrsor", "cursorCount: " + cursor.getCount()); // returns 1
	    if (cursor == null || !cursor.moveToNext()) {           
	        return null;
	    }
	    data = cursor.getBlob(0);
	    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	    return bitmap;
	}
	
	

}

