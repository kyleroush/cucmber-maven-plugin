@List
@Collection
Feature: How an array list functioned
  Background:
    Given an array list

  Scenario: some array list scenario
    Given step 1
    When step 2
    Then step 3

  Scenario: when the collection is empty
    Given it has "abc"
    Then it contains "abc"

