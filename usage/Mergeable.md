# Mergeable Objects

Do you ever have an API that returns partial objects in one response, and then a full object in another? Or have an object that is parsed from two different sources of data?

`@Mergeable` objects help bridge that gap by allowing fields that return `null` from JSON using their existing values as the fallback.

## Example

When a user logs in, we get a `User` response that contains a small set of fields and some junk data:

```JSON

{
  user_name: "agrosner",
  access_token: "eubnfenfnfuen03094885",
  first_name: null,
  last_name: null
}

```

Parsing this object is simple:

```java
User partialUser = Translator.getTranslator(User.class).parse(jsonString);

```

Then we want to parse _another_ response from the API for a `User` that contains other fields, but also returns `null` for the `access_token`, which causes us to invalidate our existing token:

```JSON
{
  user_name: "agrosner",
  access_token: null,
  first_name: "Andrew",
  last_name: "Grosner"
}
```

This is no good since we keep overwriting this value and have to keep track of the token outside of normal parsing.

### Mergeable Objects

We can define `@Translatable` objects as `@Mergeable` on a _class-level_ or single _field-level_.

```java

@Mergeable
@Translatable(keyNameRule = KeyNameRule.UNDERSCORE_AND_LOWERCASE)
public class User {

  @Key
  String userName;

  @Key
  String accessToken;

  @Key
  String firstName;

  @Key
  String lastName;
}

```
By adding that annotation, previously a missing `access_token` now keeps its existing value when missing.

### Non-Mergeable

Any field that you want treated normally within a `@Mergeable` object, just add the `@NotMergeable` annotation to the field:

```java

@Translatable
@Mergeable
public class User {

  //...

  @NotMergeable
  @Key
  Date updateTime;
}

```
