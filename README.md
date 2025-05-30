# Configured ⚙️

Configured is a format-independent configuration library for Java that allows you to generate
versioned and documented configuration files directly from code in multiple formats such as YAML,
JSON, and more.

It provides a simple and intuitive API to define configuration options, their default values,
and descriptions, which can be used to generate configuration files with comments and versioning.

## Features ✨

- **🕊️ Format Independence**: Structure your configuration without worrying about the file format.
  Change it whenever you want with a simple line of code.
- **⚙️ Auto-Generated Files**: Write your configuration files directly in code instead of manually
  creating and maintaining it. They will be generated automatically with comments and descriptions.
- **🕒 Versioning**: Easily manage configuration versions with automatic migrations.
- **📝 Documentation**: Automatically generates comments, headers and descriptions for configuration
  options from your code.
- **🔧 Default Values**: Define default values for options that can be overridden.
- **🔒 Type Safety**: Strongly typed configuration options for better safety and clarity.
  If the given value of a configuration option is invalid, it will use the default value instead.
- **🌐 Localization API**: Simple and easy to use **enum-based** localization API to manage
  internationalization. Enjoy **autocompletion** in your IDE and no more hassle with mistyped
  keys or missing translations.
    - See [Getting Started: Localization 🌍](#getting-started-localization-) for more details.

## Adding to your Project 📦
To add Configured to your project, you can use Maven or Gradle.
Configured is divided into multiple modules:
- `configured`: The core library that provides the main functionality.
- `configured-localization`: The Localization API.

And each data format has its own module:
- `configured-yaml`: The YAML data format module.
- `configured-json`: The JSON data format module.

Make sure to include the modules you need in your project.


## Getting Started: Configuration 🛠️

You can specify a `.yml` config and register its options like this:

```java
public static final Config CONFIG =
        Config.of("config.yml") // Data format inferred from the file extension
                .version(1)
                .header("""
                        Example configuration file
                        Generated using Configured!
                        """);

public static final ConfigOption<String> NAME =
        CONFIG.optionOf("name", "John Smith")
                .description("Name of the user")
                .appendDefaultValue();

public static final ConfigOption<Integer> AGE =
        CONFIG.optionOf("age", 18)
                .description("Age of the user")
                .appendDefaultValue();

public static final ConfigOption<Boolean> STUDENT =
        CONFIG.optionOf("student", true)
                .description("Whether the user is a student or not")
                .appendDefaultValue();

public static void main(String[] args) {
    CONFIG.load();
}
```

Which will generate a `config.yml` file like this:

```yaml
# Example configuration file
# Using "Configured"!

_version: 1

# Name of the user
# Default: John Smith
name: John Smith

# Age of the user
# Default: 18
age: 18

# Whether the user is a student or not
# Default: false
student: false
```

You can change the config format to JSONC (JSON with comments)
just by changing the file extension to `.jsonc`:

```java
Config.of("config.jsonc")
...
```

Which will generate a `config.jsonc` file like this:

```json5
{
  // Example configuration file
  // Using "Configured"!

  "_version": 1,
  // Name of the user
  // Default: John Smith
  "name": "John Smith",
  // Age of the user
  // Default: 18
  "age": 18,
  // Whether the user is a student or not
  // Default: false
  "student": false
}
```

You can then access/overwrite config values like this:

```java
public static void main(String[] args) {
    CONFIG.load();
    String name = CONFIG.get(NAME);
    int age = CONFIG.get(AGE);
    boolean student = CONFIG.get(STUDENT);
    // Or overwrite them like:
    CONFIG.set(NAME, "Jane Doe");
    CONFIG.set(AGE, 20);
    CONFIG.reset(STUDENT);
    CONFIG.save(); // Don't forget to save afterward
}
```

## Getting Started: Localization 🌍

Localization is a powerful feature of Configured that allows you to manage translations
and internationalization in a simple way.

It is based on **enums**, which makes it easy to work with and allows you to avoid common pitfalls
like mistyped keys or missing translations. Thanks to enums, you will also catch errors in compile-time
and have **autocompletion** in your IDE.

It also comes with **annotation** based parameter support, which allows you to pass parameters
to your translations, making it easy to create dynamic messages.

You can define a localization enum by implementing the `LocalizationKey` interface:

```java
enum Message implements LocalizationKey {
    USER_NOT_FOUND,
    CONFIGURATION_ERROR,
    INVALID_INPUT,
    OPERATION_SUCCESS,
    OPERATION_FAILED,
}
```

You can then create a `Localization` instance like this:

```java
public static final Localization LOCALIZATION =
        Localization.of(lang -> lang + ".json") // Data format inferred from the file extension
                .resourceProvider(Configured.class, lang -> "/" + lang + ".json")
                .fallbackLanguage("en_US")
                .version(1)
                .load();
```

And receive translations like this:

```java
public static void main(String[] args) {
    LOCALIZATION.get(Message.USER_NOT_FOUND); // Returns the translation for USER_NOT_FOUND
    LOCALIZATION.get(Message.INVALID_INPUT); // Returns the translation for INVALID_INPUT
}
```

The `resourceProvider` method lets you "deploy" your localization files directly from the classpath,
(i.E. from the `resources/` folder, as done here), making it easy to bundle localization files
with your application.

In this example, the localization file `en_US.json` will be loaded from the classpath's
`resources/` directory, and it will be used as the fallback language, and be copied as
`en_US.json` in the working directory.

### Parameterized Translations

Parameterized translations allow you to create dynamic messages by passing parameters
to your translations. You can use the `@Parameters(...)` annotation to define parameters in
your translations:

```java
enum Message implements LocalizationKey {
    @Parameters("username")
    USER_NOT_FOUND,
    CONFIGURATION_ERROR,
    INVALID_INPUT,
    @Parameters({"player", "action"})
    OPERATION_SUCCESS,
    @Parameters({"reason", "details"})
    OPERATION_FAILED,
}
```

You can then pass parameters to your translations like this, in the **exact order** they were 
declared in the annotation. This allows you to easily pass parameters to your translations without 
worrying about the parameter name or key:

```java
LOCALIZATION.get(Message.USER_NOT_FOUND, "Clickism");
LOCALIZATION.get(Message.OPERATION_SUCCESS, "Clickism", "Creating a new config");
LOCALIZATION.get(Message.OPERATION_FAILED, "Invalid data", "Data does not match expected format");
```

This will return the translation for `USER_NOT_FOUND` with the placeholder `{username}` replaced
with `"Clickism"`.

And for `OPERATION_SUCCESS` with the placeholders `{player}`
and `{action}` replaced with `"Clickism"` and `"created a new config"`, respectively.

And for `OPERATION_FAILED` with the placeholders `{reason}` and `{details}`
replaced with `"Invalid data"` and `"Data does not match expected format"`, respectively.

i.E, for a given localization file:

```json
{
  "user_not_found": "User {username} not found.",
  "operation_success": "{player} successfully performed an operation: {action}.",
  "operation_failed": "Operation failed, reason: {reason}, details: {details}."
}
```
This will return the following messages:

```
User Clickism not found.
Clickism successfully performed an operation: Creating a new config.
Operation failed, reason: Invalid data, details: Data does not match expected format.
```