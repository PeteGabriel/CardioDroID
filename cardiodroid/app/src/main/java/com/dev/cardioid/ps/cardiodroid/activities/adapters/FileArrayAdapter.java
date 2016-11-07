package com.dev.cardioid.ps.cardiodroid.activities.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev.cardioid.ps.cardiodroid.R;
import java.io.File;

/**
 * TODO 
 */
public class FileArrayAdapter extends ArrayAdapter<File> {
  
  /**
   * Colors retrieved from the color.xml file.
   */
  private int mFolderColor, mFileColor;

  /**
   * Icons retrieved from the drawable.xml file.
   */
  private Drawable mFolderIcon, mFileIcon;

  /**
   * Ctor
   */
  public FileArrayAdapter(Context context){
    super(context, R.layout.file_list_item);

    Resources resources = context.getResources();
    mFolderColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null);
    mFolderIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_folder_black_48dp, null);
    mFileColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null);
    mFileIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_description_black_48dp, null);
  }

  /**
   * Ctor
   */
  public FileArrayAdapter(Context context, File[] files) {
    super(context, R.layout.file_list_item, files);
  }


  /**
   * Transform an icon's color into a certain color 
   */
  private Drawable setCustomDrawableColor(Drawable d, int color){
    Drawable tmp = null;
    if (d != null) {
      tmp = d.getCurrent();
      ColorFilter filter = new LightingColorFilter(Color.BLACK, color);
      tmp.setColorFilter(filter);
    }
    return tmp;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    File file = getItem(position);

    if(convertView == null)
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_list_item, parent, false);

    ((TextView)convertView.findViewById(R.id.file_name)).setText(file.getName());

    ImageView image = (ImageView) convertView.findViewById(R.id.file_type_imageview);

    Drawable tmp = (file.isDirectory()) ?
        setCustomDrawableColor(mFolderIcon, mFolderColor) :
        setCustomDrawableColor(mFileIcon, mFileColor);

    image.setImageDrawable(tmp);

    return convertView;
  }
}