package com.github.kyleroush;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import java.util.Collection;

public class Steps {

  Collection a;

  /**
   * this a step 1
   */
  @Given("step 1")
  public void step1() {

  }

  @Given("^it has \"([^\"]*)\"$")
  public void itHas(String arg0) throws Throwable {
    //a.add(arg0);
  }

  @Then("^it contains \"([^\"]*)\"$")
  public void itContains(String arg0) throws Throwable {
    //a.contains(arg0);
  }
}
