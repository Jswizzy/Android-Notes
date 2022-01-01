# Test Driven Development

Test Driven Development is writing tests first before writing the feature.

## Given, When, Then

One way to think about the structure of a test is to follow the Given, When, Then testing mnemonic. This divides your test into three parts:

- _Given_
  : Setup the objects and the state of the app that you need for your test. For this test, what is "given" is that you have a list of tasks where the task is active.

- _When_
  : Do the actual action on the object you're testing.

- _Then_
  : This is where you actually check what happens when you do the action where you check if the test passed or failed. This is usually a number of assert function calls.

> Note that the" Arrange, Act, Assert" (AAA) testing mnemonic is a similar concept.

## Red, Green, Refactor

Confirm test fails, write a solution, then refactor that solution.
