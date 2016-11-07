package com.dev.cardioid.ps.cardiodroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * This fragment manages its view state accordingly with the
 * current state of exhaustion or bluetooth connectivity.
 *
 */
public class FacesFragment extends Fragment {

  public static final String TAG = Utils.makeLogTag(FacesFragment.class);
  private ImageView theFace;
  private RelativeLayout theFaceLayout;

  private Button mTryAgainButton;
  private TextView mTryAgainTextBox;

  public FacesFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    theFaceLayout =  (RelativeLayout) inflater.inflate(R.layout.fragment_faces_layout, container, false);
    theFace = (ImageView)theFaceLayout.findViewById(R.id.the_face_imgview);

    theFaceLayout.setBackgroundColor(getResources().getColor(R.color.colorLinen));

    mTryAgainButton = (Button) theFaceLayout.findViewById(R.id.tentar_novamente_btn);
    mTryAgainTextBox = (TextView) theFaceLayout.findViewById(R.id.tentar_novamente_texto);
    //set text
    mTryAgainButton.setText(getResources().getString(R.string.tentar_novamente_button));
    mTryAgainTextBox.setText(getResources().getString(R.string.tentar_novamente_textview));
    //not visible
    mTryAgainButton.setVisibility(View.GONE);
    mTryAgainTextBox.setVisibility(View.GONE);

    return theFaceLayout;
  }

  /**
   * Changes the fragment's layout to represent the most
   * danger state of exhaustion.
   */
  public void setRedFace(){
    Log.d("Faces", "Red Face");
    setWarningsVisible(false);
    changeFragmentFace(true, R.drawable.simle_red_face, R.color.red_environment);
  }

  /**
   * Changes the fragment's layout to represent the most
   * light state of exhaustion.
   */
  public void setGreenFace(){
    Log.d(TAG, "Green Face");
    setWarningsVisible(false);
    changeFragmentFace(true, R.drawable.smile_green_face, R.color.green_environment);
  }

  /**
   * Changes the fragment's layout to represent the
   * intermediate state of exhaustion.
   */
  public void setYellowFace(){
    Log.d(TAG, "Yellow Face");
    setWarningsVisible(false);
    changeFragmentFace(true, R.drawable.smile_yellow_face, R.color.yellow_environment);
  }


  public void setUndefinedFace() {
    Log.d(TAG, "Setting undefined face");
    setWarningsVisible(true);
    theFace.setVisibility(View.GONE);
    theFaceLayout.setBackgroundColor(getResources().getColor(R.color.colorLinen));
  }

  /**
   * Modifies the face in the middle of the view.
   *
   * @param faceIsVisible True will make the face to be visible.
   * @param faceDrawable The drawable to apply the face widget.
   * @param backgroundColor The background color to apply to the view.
   */
  private void changeFragmentFace(boolean faceIsVisible, int faceDrawable, int backgroundColor){
    theFace.setVisibility( faceIsVisible ? View.VISIBLE : View.GONE);
    theFace.setImageDrawable(getResources().getDrawable(faceDrawable));
    theFaceLayout.setBackgroundColor(getResources().getColor(backgroundColor));
  }

  private void setWarningsVisible(boolean isVisible){
    mTryAgainButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    mTryAgainTextBox.setVisibility(isVisible ? View.VISIBLE : View.GONE);
  }

}
