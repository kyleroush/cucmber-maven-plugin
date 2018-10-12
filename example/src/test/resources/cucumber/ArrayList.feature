@List
@Collection
Feature: How an array list functioned

  Background:
    Given an array list

  Scenario: some array list scenario
    Given doc string
    When step 2
      |a|b |
      |1|b |
      |1|b |
      |2|b |
    Then step 3

  Scenario Outline: asdf
    Given step <a>
    When step 2
    Then step 3

    @asdf
    Examples: d
      |a|b |
      |1|b |
      |1|b |
      |2|b |
    @a
    Examples: f
      |a|
      |1|
