package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.utility_dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.activities.adapters.FileArrayAdapter;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.io.File;
import java.util.ArrayList;

/**
 * This class represents a DialogFragment specialization that provides
 * the feature of choosing a file or directory from the file system.
 *
 * The interface {@link OnFileSelectedListener}
 * provides a callback method that will be called with the selected file
 * or directory chosen by the user.
 *
 */
public class FilePickerDialogFragment extends DialogFragment
    implements AdapterView.OnItemClickListener {

  private static final String TAG = Utils.makeLogTag(FilePickerDialogFragment.class);

  public static final String BASE_DIR_ID = "base_dir_id";

  public static final String FILE_EXT_ID = "file_extension_id";

  private File currDir, electedDir;
  private File[] currDirFiles;
  private ArrayList<File> previousDirs;

  private FileArrayAdapter adapter;

  private TextView title;

  private OnFileSelectedListener mListener;

  private String fileExt;

  public static final String ROOT_DIR = "/";

  /**
   * Get a reference to the hosting activity
   * for being able to communicate events.
   *
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFileSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnFileSelectedListener!");
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    Bundle args = getArguments();

    String baseDir;

    if (args != null && args.containsKey(BASE_DIR_ID)) {
      baseDir = args.getString(BASE_DIR_ID);
    } else {
      baseDir = ROOT_DIR;
    }

    if (args != null && args.containsKey(FILE_EXT_ID)) {
      fileExt = args.getString(FILE_EXT_ID).replace(".", "");
    } else {
      fileExt = "";
    }

    previousDirs = new ArrayList<>();
    adapter = new FileArrayAdapter(getActivity());

    // Setup view
    View view = inflater.inflate(R.layout.file_picker_layout, container);

    title = (TextView) view.findViewById(R.id.file_picker_current_dir_textview);
    Button mChooseDirectoryButton = (Button) view.findViewById(R.id.file_picker_directory_button);
    mChooseDirectoryButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d(TAG, "OK button pressed");
        // give the selected file back to the caller.
        if (electedDir != null) {
          Log.d(TAG, "File selected: " + electedDir);
          mListener.onFileSelected(electedDir);
          dismiss();
        }
      }
    });

    ListView listView = ((ListView) view.findViewById(R.id.files_listView));
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);

    //"go back" button
    view.findViewById(R.id.file_picker_back_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d(TAG, "Back button pressed");

        // Check if there are previous directories in the list.
        if (previousDirs.isEmpty()) {
          dismiss();
          return;
        }
        // Get the previous directory and show it.
        int idx = previousDirs.size() - 1;
        setNewCurrentDirAndUpdateCurrElements(previousDirs.get(idx));
        // Remove the previous directory from the stack.
        previousDirs.remove(idx);
      }
    });

    setNewCurrentDirAndUpdateCurrElements(new File(baseDir));

    return view;
  }


  /**
   * Called every time an item is selected for the listview widget.
   *
   * It holds a reference to the selected element in order to be
   * used inside the "Ok button" click event listener.
   *
   * @param parent
   *  the parent layout view
   * @param view
   *  the view widget
   * @param position
   *  item's position inside the adapter's data structure
   *
   */
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    File selectedFile = currDirFiles[position];

    Log.d(TAG, "item selected: " + selectedFile.getName());

    // If the selected item is a directory, show its contents.
    if (selectedFile.isDirectory()) {
      Log.d(TAG, "Directory selected.");

      String[] selectedDirElems = selectedFile.list();
      electedDir = selectedFile;

      if (selectedDirElems != null && selectedDirElems.length > 0) {
        Log.d(TAG, "Elements Found: " + selectedDirElems.length);
        // Store the current dir in case the user wants to back track.
        previousDirs.add(currDir);
        //Set the new current directory
        setNewCurrentDirAndUpdateCurrElements(selectedFile);
      } else {
        Log.d(TAG, "Directory contains zero elements.");
      }
    } else if (selectedFile.isFile() && hasSpecifiedExtension(selectedFile)) {
      electedDir = selectedFile;
    }
  }

  private boolean hasSpecifiedExtension(File file) {
    return file.isFile() && file.getAbsolutePath().contains(fileExt);
  }

  private void setNewCurrentDirAndUpdateCurrElements(File newCurrDir) {
    currDir = newCurrDir;
    setListForCurrentDirectory();
  }

  private void setListForCurrentDirectory() {
    setListForDirectory(currDir);
  }

  private void setListForDirectory(File dir) {
    Log.d(TAG, "Setting list for directory: " + dir);
    title.setText(currDir.getAbsolutePath());

    currDirFiles = dir.listFiles();

    adapter.clear();
    adapter.addAll(currDirFiles);
  }

  /**
   * Implement this interface in order to receive
   * the selected file chosen by the user.
   */
  public interface OnFileSelectedListener {
    void onFileSelected(File file);
  }




}
