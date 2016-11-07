package com.dev.cardioid.ps.cardiodroid.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * The adapter provides access to the items in your data set, creates views for items,
 * and replaces the content of some of the views with new data items when the original item
 * is no longer visible.
 */
public class RulesListAdapter extends RecyclerView.Adapter<RulesListAdapter.ViewHolder> {

  /**
   * For debug purposes.
   */
  private static final String TAG = Utils.makeLogTag(RulesListAdapter.class);

  /**
   * Used to provide events every time an item is clicked.
   */
  public interface OnItemClickListener {
    void onItemClick(Rule selectedRule);
  }

  /**
   * Used to provide events every time the checkbox is selected
   * or deselected.
   */
  public interface OnNewItemSelectedListener {
    void onNewItemSelected(int amount);
  }

  /**
   * This internal set of items to be displayed by the adapter.
   */
  private List<Rule> mDataset;

  /**
   * This list maintains the selected items by the user.
   * It helps maintaining record of how much items are selected
   * and which items are selected as well.
   */
  private List<Rule> mStackUpElements;

  /**
   * Provide a reference to the views for each data item.
   * All the views for a data item are provided inside this view holder.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private TextView nameOfRuleView;
    private TextView typeOfRuleView;
    private TextView actionsOfRuleView;
    private CheckBox selectedRuleBox;

    public ViewHolder(View view) {
      super(view);
      selectedRuleBox = (CheckBox) view.findViewById(R.id.selection_checkbox_widget);
      nameOfRuleView = (TextView) view.findViewById(R.id.rule_name_text_view_holder);
      typeOfRuleView = (TextView) view.findViewById(R.id.rule_type_text_view_holder);
      actionsOfRuleView = (TextView) view.findViewById(R.id.rule_action_text_view_holder);
    }
  }

  private OnNewItemSelectedListener onNewItemSelected;

  private OnItemClickListener onItemClickListener;


  /**
   * Ctor
   *
   * @param rules List of rules
   */
  public RulesListAdapter(List<Rule> rules, OnNewItemSelectedListener listener, OnItemClickListener onItemClick) {
    mDataset = new ArrayList<>();
    mDataset.addAll(rules);
    onNewItemSelected = listener;
    onItemClickListener = onItemClick;
    mStackUpElements = new ArrayList<>();
  }


  @Override
  public RulesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_rules_list_view, parent, false);

    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  /**
   * get element from your dataset at this position and
   * replace the contents of the view with that element
   */
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    Log.d("PEDRO", "position " + position);
    Rule r = mDataset.get(position);
    holder.nameOfRuleView.setText(r.getName());
    holder.typeOfRuleView.setText(r.getTypeOfRule());
    holder.actionsOfRuleView.setText(r.getActionsDescription());
    holder.selectedRuleBox.setChecked(false);

    final int positionWrapper = position >= mDataset.size() ? position - 1 : position; //FIX
    holder.selectedRuleBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          mStackUpElements.add(mDataset.get(positionWrapper));
        } else {
          if (mStackUpElements.contains(mDataset.get(positionWrapper)))
            mStackUpElements.remove(mDataset.get(positionWrapper));
        }
        if (onNewItemSelected != null) {
          onNewItemSelected.onNewItemSelected(mStackUpElements.size());
        }
        Log.d(TAG, "Selected Items Count: " + mStackUpElements.size());
      }
    });

    if (onItemClickListener != null) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          onItemClickListener.onItemClick(mDataset.get(positionWrapper));
        }
      });
    }
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return mDataset.size();
  }

  /**
   * Swap the list of items for a given one.
   * @param newRules
   *  new list of items
   */
  public void swapDataSet(List<Rule> newRules) {
    mDataset = new ArrayList<>();
    mDataset.addAll(newRules);
  }

  /**
   * Tell the adapter to unmark the items selected
   * by the user through the UI.
   */
  public void deselectItems() {
    mStackUpElements.clear();
    notifyDataSetChanged();
  }

  /**
   * Get a list that contains the selected elements;
   *
   * @return list of selected elements
   */
  public List<Rule> getSelectedItems() {
    return (List<Rule>) ((ArrayList<Rule>)mStackUpElements).clone();
  }

  /**
   *
   * @param r New Rule to add
   */
  public void addItem(Rule r) {
    mDataset.add(r);
    int idx = mDataset.indexOf(r);
    notifyItemInserted(idx);
    notifyItemRangeChanged(idx, mDataset.size());
  }

  /**
   * Remove the given item from the list of items holded by the adapter.
   * If the instance is not the same, it removes based on ID.
   * @param rule
   *  object to remove
   */
  public void removeItem(Rule rule) {
    final int NOT_FOUND = -1;
    int idx = mDataset.indexOf(rule);
    //if the instance was not found, remove by ID
    if (idx == NOT_FOUND){
      removeById(rule);
    }else {
      mStackUpElements.remove(mDataset.get(idx));
      mDataset.remove(rule);
    }
    notifyItemRemoved(idx);
    notifyItemRangeChanged(idx, mDataset.size());
  }

  private void removeById(Rule rule) {
    for (Rule r : mDataset) {
      if (r.getID() == rule.getID()){
        mDataset.remove(r);
        break;
      }
    }
  }
}