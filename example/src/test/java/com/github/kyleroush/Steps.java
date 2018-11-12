package com.github.kyleroush;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.junit.Assertions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import org.junit.Assert;

public class Steps {

  Collection<String> collection;

  @When("step 2")
  public void step2() {

  }

  @Then("step 3")
  public void step3() {

  }

  @Given("an array list")
  public void createArrayList() {
    collection = new ArrayList();
  }

  @Given("a hash set")
  public void createHashSet() {
    collection = new HashSet();
  }

  @Given("a linked list")
  public void createLinkedList() {
    collection = new LinkedList();
  }

  @Then("it contains \"([^\"]*)\"")
  public void contains(String value) {
    Assert.assertEquals(true, collection.contains(value));
  }

  @Given("it has \"([^\"]*)\"")
  public void add(String value) {
    collection.add(value);
  }

}
