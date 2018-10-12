@withA
@withB
@withExtra
Feature: Feature 1


	Scenario: Scenario1
		Given step 1
		When step 2
		Then step 3


	Scenario: with 1 extra Scenario for A
		Given a thing
		And a thing a different
		Given step 1
		When step 2
		Then step 3


	Scenario: with 2 extra Scenario for A
		Given a thing
		And a thing a different
		Given step 1
		When step 2
		Then step 3


	Scenario: with 3 extra Scenario for A
		Given a thing
		And a thing a different
		Given step 1
		When step 2
		Then step 3


	Scenario: with extra Scenario for B
		Given step 1
		When step 2
		Then step 3


	Scenario: scenarios from the extra scenarios
		Given step 1
		When step 2
		Then step 3

