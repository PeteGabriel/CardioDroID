package com.dev.cardioid.ps.cardiodroid.rules.parser;

/**
 * Thrown to indicate a problem with the construction of the JSON Rule Object
 * */
public final class JsonRuleException extends Exception{
  public JsonRuleException(String detailMessage) {
    super(detailMessage);
  }

  public JsonRuleException() {
    super("The Json Object was malformed.");
  }
}
