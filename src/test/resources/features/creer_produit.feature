# language: en
Feature: Product creation
  As a catalog manager
  I want to create a new product
  So that it becomes available for sale

  Scenario: Creating a product with valid data
    Given a product named "Mechanical keyboard" with a price of 89.90 and a stock of 25
    When I submit the product creation
    Then the product is created successfully
    And the created product has the name "Mechanical keyboard"