# Configured ⚙️
Configured is a work-in-progress, format-independent configuration library for Java that allows you to generate 
versioned and documented configuration files directly from code in multiple formats such as YAML, JSON, and more.

### How to Use
You can specify a config and register its options like this:
```java
public static final Config CONFIG = Config.ofYaml("config.yml")
        .version(1)
        .header("""
                Example configuration file
                Generated using Configured!
                """);

public static final ConfigOption<String> NAME = CONFIG.register(
        ConfigOption.of("name", "John Smith")
                .description("Name of the user")
                .appendDefaultValue()
);

public static final ConfigOption<Integer> AGE = CONFIG.register(
        ConfigOption.of("age", 18)
                .description("Age of the user")
                .appendDefaultValue()
);

public static final ConfigOption<Boolean> STUDENT = CONFIG.register(
        ConfigOption.of("student", false)
                .description("Whether the user is a student or not")
                .appendDefaultValue()
);

public static void main(String[] args) {
    CONFIG.load();
}
```
Which will generate a `config.yml` file like this:
```yaml
# Example configuration file
# Generated using Configured!

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