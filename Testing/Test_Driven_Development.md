# Test Driven Development

Test Driven Development is writing tests first before writing the feature.

The benefits are:

- Helps with specification, prevents divinations from the spec.
- Documents the code in an updated way.
- Maintainable, Allows for refactoring and encourages clean architecture that is maintainable.
- Establish confidence in the code, know your code didn't break anything.
- Write code faster. Code is well documented and easy to change and has less technical debt.
- Higher test covered, every feature has an existing test.

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

Steps:

- _Red_ - Start with a failing test by writing the bare minimum for the code to compile. Asserts that test actually fails and checks the correctness of the expected behavior.
- _Green_ - Write the minimum code for the test to pass. Add additional requirements and add additional functionality make the tests pass again.
- _Refactor_ - Clean up the code and refactor.
