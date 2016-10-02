# JHipster Gradle Sample Application @SpringBootTest Annotation Mocked Service Issue

To demonstrate the issue I added a UserService method call in the BankAccountResource class.

 * The BankAccountResourceIntTest createEntity test fails with the @SpringBootTest annotation due to the mocked UserService method returning null.
 * The WorkingBankAccountResourceIntTest createEntity test succeeds using the deprecated Spring Boot integration test annotations.
