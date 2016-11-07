package com.dev.cardioid.ps.cardiodroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import org.json.JSONException;

/**
 * This fragment shows the details of a composed condition.
 * Use the {@link this.newInstance} method to pass the rule
 * from which the details will be retrieved.
 */
public final class ComposedViewFragment extends Fragment {

    public static final String TAG = Utils.makeLogTag(ComposedViewFragment.class);

    private static final String RULE_KEY = "rule_extra_key";

    private Rule mRule;

    //UI elements
    private TextView condOneTypeDetail;
    private  TextView condOneContextDetail;

    private TextView condTwoTypeDetail;
    private TextView condTwoContextDetail;

    /**
     * Builds an instance of the fragment with a complex type
     * as parameter.
     */
    public static Fragment newInstance(Rule ruleJson){
        Bundle arguments = new Bundle();
        arguments.putParcelable(RULE_KEY, ruleJson);
        Fragment frag = new ComposedViewFragment();
        frag.setArguments(arguments);
        return frag;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate Call");
        mRule = getArguments().getParcelable(RULE_KEY);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_composed_condition_details, container, false);

        TextView typeDetail = (TextView)view.findViewById(R.id.rule_type_detail_value);
        String typeOfRule = mRule.getTypeOfRule();
        typeDetail.setText(typeOfRule);

        condOneTypeDetail = (TextView)view.findViewById(R.id.cond_1_condition_type_detail_value);
        condOneContextDetail = (TextView)view.findViewById(R.id.cond_1_context_type_detail_value);

        try {
            setupDataCondOne(condOneContextDetail, condOneTypeDetail);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        condTwoTypeDetail = (TextView)view.findViewById(R.id.cond_2_condition_type_detail_value);
        condTwoContextDetail = (TextView)view.findViewById(R.id.cond_2_condition_context_detail_value);

        try {
            setupDataCondTwo(condTwoContextDetail, condTwoTypeDetail);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        TextView actionDetail = (TextView) view.findViewById(R.id.action_detail_value);
        actionDetail.setText(mRule.getActionsDescription());

        return view;
    }

    private void setupDataCondTwo(TextView contextDetailView, TextView typeDetailView) throws JSONException {
        String typeOfSubConditionOne;
        try {
            typeOfSubConditionOne = mRule.getTypeOfSubConditionTwo();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            typeDetailView.setText("UNKNOWN");
            contextDetailView.setVisibility(View.GONE);
            return;
        }

        switch(typeOfSubConditionOne){
            case ComposedConditionAbstract.IDENTIFIER:
                typeDetailView.setText(typeOfSubConditionOne);
                contextDetailView.setVisibility(View.GONE);
                break;
            case SimpleConditionAbstract.IDENTIFIER:
                typeDetailView.setText(typeOfSubConditionOne);
                contextDetailView.setVisibility(View.VISIBLE);
                String context = mRule.getContextNameOfSubconditionTwo();
                contextDetailView.setText(context);
                break;
        }
    }

    private void setupDataCondOne(TextView contextDetailView, TextView typeDetailView) throws JSONException {
        String typeOfSubConditionOne;
        try {
            typeOfSubConditionOne = mRule.getTypeOfSubConditionOne();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            typeDetailView.setText("UNKNOWN");
            contextDetailView.setVisibility(View.GONE);
            return;
        }

        switch(typeOfSubConditionOne){
            case ComposedConditionAbstract.IDENTIFIER:
                typeDetailView.setText(typeOfSubConditionOne);
                contextDetailView.setVisibility(View.GONE);
                break;
            case SimpleConditionAbstract.IDENTIFIER:
                typeDetailView.setText(typeOfSubConditionOne);
                contextDetailView.setVisibility(View.VISIBLE);
                String context = mRule.getContextNameOfSubconditionOne();
                contextDetailView.setText(context);
                break;
        }
    }

}