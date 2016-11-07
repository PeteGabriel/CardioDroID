package com.dev.cardioid.ps.cardiodroid.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.network.dtos.WeatherObservation;
import com.dev.cardioid.ps.cardiodroid.services.ble.BleDefinedUuid;
import com.dev.cardioid.ps.cardiodroid.utils.LocationUtils;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

/**
 * The fragment that controls the dashboard's view elements.
 */
public class DashboardFragment extends Fragment {

    public static final String KM_H_UNIT = "km/h";
    public static final String ADDRESS_STATE_VALUE_KEY = "context.monitor.fragment.address.state.value.key";
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private final long UPDATE_CHART_CONST = 4000;
    private final String EXHAUSTION_STATE_VALUE_KEY = "context.monitor.fragment.state.value.key";
    private LineChart mChart;
    private Thread mThreadHelper;
    private volatile float mCurrentValue;
    /*UI Elements*/
    private TextView mLocationContextView;
    private TextView mWeatherContextView;
    private Button mBtnTryAgain;
    private TextView mTxtTryAgain;
    private RelativeLayout mLayoutParent;
    private TextView mDashContextLabel;//dashboard_context_info_label
    private TextView mDashChartTitleLabel;//dashboard_graph_title
    private String mPrimaryColor;
    private String mSecondaryColor;
    private String mAccentColor;
    private String mLinenColor;
    private IRetryAction mListener;


    public DashboardFragment() {
        // Required empty public constructor
        mCurrentValue = 0f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mPrimaryColor = parseColor(R.color.colorPrimary);
        mSecondaryColor = parseColor(R.color.colorPrimaryDark);
        mAccentColor = parseColor(R.color.colorAccent);
        mLinenColor = parseColor(R.color.colorLinen);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(EXHAUSTION_STATE_VALUE_KEY, mCurrentValue);
        outState.putString(ADDRESS_STATE_VALUE_KEY, mLocationContextView.getText().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_layout, container, false);

        setupLayoutWidgets(view);

        return view;
    }

    private void setupLayoutWidgets(View view) {
        mLocationContextView = (TextView) view.findViewById(R.id.location_context_info_view);
        mWeatherContextView = (TextView) view.findViewById(R.id.weather_context_info_view);

        mDashContextLabel = (TextView) view.findViewById(R.id.dashboard_context_info_label);
        mDashChartTitleLabel = (TextView) view.findViewById(R.id.dashboard_graph_title);
        //set text
        mDashChartTitleLabel.setText(getResources().getString(R.string.chart_title_label));
        mDashContextLabel.setText(getResources().getString(R.string.dashboard_context_label));

        mLayoutParent = (RelativeLayout) view.findViewById(R.id.context_frag_container);

        mBtnTryAgain = (Button) view.findViewById(R.id.tentar_novamente_btn);
        mBtnTryAgain.setText(getResources().getString(R.string.tentar_novamente_button));
        mTxtTryAgain = (TextView) view.findViewById(R.id.tentar_novamente_texto);
        mTxtTryAgain.setText(getResources().getString(R.string.tentar_novamente_textview));

        //CardioDroidApplication app = (CardioDroidApplication) getActivity().getApplicationContext();
        boolean isConnected = NetworkUtils.isConnected(getActivity().getApplicationContext());
        updateViewAccordinglyWithConn(isConnected);

        setupListeners();
    }

    private void setupListeners() {
        mBtnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sent event to the hosting component. Let him know the user wants to retry the whole process
                mListener.onRetryClick();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IRetryAction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Hosting component must implement IRetroAction");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChart = (LineChart) view.findViewById(R.id.state_chart);
        configChart(mChart);

        if (savedInstanceState != null) {
            //restore state
            mCurrentValue = savedInstanceState.getFloat(EXHAUSTION_STATE_VALUE_KEY);
            mLocationContextView.setText(savedInstanceState.getString(ADDRESS_STATE_VALUE_KEY));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cleanResources(mThreadHelper)) {
            mThreadHelper = null;
        }//else its already null
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mThreadHelper != null) return;
        mThreadHelper = setupHelper();
        mThreadHelper.start();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (cleanResources(mThreadHelper)) {
            mThreadHelper = null;
        }//else its already null

    }

    /**
     * @param isConnected
     */
    public void updateViewAccordinglyWithConn(boolean isConnected) {
        Log.d(TAG, "Hide Context Widgets: " + !isConnected);
        if (isConnected) {
            mLayoutParent.setVisibility(View.VISIBLE);
            mDashChartTitleLabel.setVisibility(View.VISIBLE);
            mDashContextLabel.setVisibility(View.VISIBLE);
            mBtnTryAgain.setVisibility(View.GONE);
            mTxtTryAgain.setVisibility(View.GONE);
        } else {
            mLayoutParent.setVisibility(View.GONE);
            mDashChartTitleLabel.setVisibility(View.GONE);
            mDashContextLabel.setVisibility(View.GONE);
            mBtnTryAgain.setVisibility(View.VISIBLE);
            mTxtTryAgain.setVisibility(View.VISIBLE);
        }
    }

    private boolean cleanResources(Thread helper) {
        if (helper != null && helper.isAlive()) {
            try {
                helper.interrupt();
                helper.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Helper Thread interrupted.");
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Main configuration of the chart.
     */
    private void configChart(LineChart chart) {
        int linenCode = Color.parseColor(mLinenColor);
        chart.setBackgroundColor(linenCode);

        LineData data = new LineData();
        data.setValueTextColor(linenCode);

        // add empty data
        chart.setData(data);

        setupXAxis();
        setupYAxis();

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        mThreadHelper = setupHelper();
        mThreadHelper.start();
    }

    public void clearChartValues(){
      mCurrentValue = 0f;
    }


    /**
     * Called by the host activity to send new exhaustion states.
     * This new value can be used to update the chart.
     */
    public void newStateAvailable(String newStateValue) {
        mCurrentValue = translateState(newStateValue);
    }

    /**
     * Setup the chart's Y axis.
     */
    private void setupYAxis() {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor(mLinenColor));
        leftAxis.setAxisMaxValue(3f); // 1 - 2 - 3
        leftAxis.setAxisMinValue(1f); //0 = unknown
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor(mSecondaryColor));
    }

    /**
     * Setup the chart's X axis.
     */
    private void setupXAxis() {
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.parseColor(mLinenColor));
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(3);
        xl.setEnabled(true);
        xl.setGridColor(Color.parseColor(mSecondaryColor));
    }

    @Deprecated
    private Thread setupHelper() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(UPDATE_CHART_CONST);
                        addEntry(mCurrentValue);
                        /*if(mCurrentValue == 3){
                          mCurrentValue = 1;
                        }else{
                          mCurrentValue+=1;
                        }*/
                    }
                } catch (InterruptedException e) {
                    mThreadHelper.interrupt();
                }
            }
        });
    }


    private final float HEALTHY_SIGN_VALUE = 1f;
    private final float TIRED_SIGN_VALUE = 2f;
    private final float EXHAUSTED_SIGN_VALUE = 3f;

    /**
     * Given a certain state, translate into a coordinate.
     */
    private float translateState(String state) {
        switch (state) {
            case BleDefinedUuid.STATES.HEALTHY:
                return HEALTHY_SIGN_VALUE;
            case BleDefinedUuid.STATES.TIRED:
                return TIRED_SIGN_VALUE;
            case BleDefinedUuid.STATES.EXHAUSTED:
                return EXHAUSTED_SIGN_VALUE;


            default:
                return HEALTHY_SIGN_VALUE; //ignore this
        }
    }

    private int translateColor(float value) {
      if (value == HEALTHY_SIGN_VALUE)
        return  android.R.color.holo_green_dark;
      if (value == TIRED_SIGN_VALUE)
        return android.R.color.holo_orange_dark;
      if (value == EXHAUSTED_SIGN_VALUE)
        return android.R.color.holo_red_dark;

      return android.R.color.holo_green_dark; //TODO por enquanto
    }

    private void addEntry(float newValue) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue("");
            data.addEntry(new Entry(newValue, set.getEntryCount()), 0);

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();
            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);
            // move to the latest entry
            mChart.moveViewToX(data.getXValCount() - 11);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.parseColor(mSecondaryColor));
        set.setCircleColor(Color.parseColor(mAccentColor));
        set.setLineWidth(1f);
        set.setCircleRadius(7f);
        set.setDrawCircleHole(false);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    /**
     * Get the color value for the given id
     * defined at the app's resources xml file (color.xml);
     */
    @TargetApi(Build.VERSION_CODES.M)
    private String parseColor(int colorId) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ?
                "#" + Integer.toHexString(getResources().getColor(colorId, null) & 0x00ffffff)
                : getResources().getString(colorId);
    }

    public void displayLocation(String address) {
        if (mLocationContextView != null) {
            mLocationContextView.setText(address);
        }
    }

    public void displayWeatherInfo(WeatherObservation weather) {
        if (mWeatherContextView != null) {
            String description = String.format("%s \n %s",
                    weather.getCurrentObservation().getWeather(),
                    weather.getCurrentObservation().getTemperatureString());
            String iconUrl = weather.getCurrentObservation().getIconUrl();
            mWeatherContextView.setText(description);

            //TODO colocar image
        }
    }


    /**
     * Convert the speed depending on the Unit of Measure which was specified.
     *
     * @param currSpeed the speed of the device in meters/second as provided by
     *                  the {@link Location} getSpeed function.
     * @return returns the speed converted into the specified unit of Measure.
     */
    private float convertSpeed(float currSpeed) {
        float speed = 0;
        switch (getUnitOfMeasure()) {
            case KM_H_UNIT:
                speed = LocationUtils.convertSpeedToMetric(currSpeed);
                break;
        }
        return speed;
    }

    private String getUnitOfMeasure() {
        // FIXME - The user should be able to set the unit of measure for the speed (ei. metric (km/h)
        // or imperial (m/h))
        return KM_H_UNIT;
    }

    /**
     * Hides the UI widgets related to the information obtained for
     * context elements.
     *
     * @param hide if true, hide widgets.
     */
    public void hideContextData(boolean hide) {

        mWeatherContextView.setVisibility(hide ? View.GONE : View.VISIBLE);
        mLocationContextView.setVisibility(hide ? View.GONE : View.VISIBLE);

        if (hide) {
            ToastUtils.showMessage(getResources().getString(R.string.connectivity_problem_explanation),
                    getActivity());
        }
    }

    public void displayContextDataWarning(String warning) {
        mLocationContextView.setVisibility(View.VISIBLE);
        mLocationContextView.setText(warning);
    }

    public interface IRetryAction {
        void onRetryClick();
    }


}
