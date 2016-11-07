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
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * This fragment shows the details of a simple condition.
 * Use the {@link this.newInstance} method to pass the rule
 * from which the details will be retrieved.
 */
public final class SimpleViewFragment extends Fragment {

    public static final String TAG = Utils.makeLogTag(SimpleViewFragment.class);

    private static final String RULE_KEY = "rule_extra_key";

    private Rule mRule;

    /**
     * Used to create an instance of this fragment with parameters.
     *
     * @param rule Rule to detail in view
     * @return An instance of {@link SimpleViewFragment}
     */
    public static Fragment newInstance(Rule rule){
        Log.d(TAG, "Create a new instance");
        Bundle arguments = new Bundle();
        arguments.putParcelable(RULE_KEY, rule);
        Fragment frag = new SimpleViewFragment();
        frag.setArguments(arguments);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate Call");
        mRule = getArguments().getParcelable(RULE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView Call");
        View view = inflater.inflate(R.layout.fragment_simple_condition_details, container, false);

        TextView typeDetail = (TextView) view.findViewById(R.id.type_detail_value);
        typeDetail.setText(mRule.getTypeOfRule());

        TextView smoothOperator = (TextView) view.findViewById(R.id.operator_detail_value);
        smoothOperator.setText(mRule.getOperator());

        TextView contextDetail = (TextView) view.findViewById(R.id.context_detail_value);
        contextDetail.setText(mRule.getContext());

        TextView actionDetail = (TextView) view.findViewById(R.id.action_detail_value);
        actionDetail.setText(mRule.getActionsDescription());

        return view;
    }
}
