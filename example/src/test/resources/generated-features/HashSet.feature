@List
@Collection
Feature: How a hash set functioned
  Background:
    Given a hash set

  Scenario: some hash set scenario
    Given step 1
    When step 2
    Then step 3

  Scenario: when the collection is empty
    Given it has "abc"
    Then it contains "abc"

