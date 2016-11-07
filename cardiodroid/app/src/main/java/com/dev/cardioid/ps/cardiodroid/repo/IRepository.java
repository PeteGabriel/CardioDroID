package com.dev.cardioid.ps.cardiodroid.repo;

import android.net.Uri;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import java.util.List;

/**
 * This interface represents the protocol that classes must use in order
 * to access and/or store data in the application's database storage.
 *
 * {@see com.dev.cardioid.ps.cardiodroid.network.async.process.Completion}
 */
public interface IRepository {



  /**
   * This method returns all rules present inside
   * the DB.
   * @param cb
   *  callback method to be invoked when the result is available.
   */
  void getRules(final Uri resourceUri, Completion<List<Rule>> cb);


  /**
   * Insert the given rule into the respective table.
   *
   * @param jsonRuleBody
   *  The rule to insert in json notation
   * @param completion
   */
  void insertRule(String jsonRuleBody, Completion<Uri> completion);

  /**
   * Delete a given rule from its respective table.
   *
   * @param resourceUri
   *    resource uri
   * @param record
   *    the actual rule object
   * @param cb
   *    callback to be invoked after result is available
   */
  void deleteRule(Uri resourceUri, Rule record, Completion<Void> cb);

  void updateRule(final Rule infoToUpdate, Completion<Void> cb);

  void getRuleByID(final Uri resourceUri, final long idOfRule, final Completion<List<Rule>> cb);
}
