# Inheriting Fields

You have existing data classes with fields that are not `@Translatable` and you want to include them in the parse / serialization mechanism.

We have this class from another class that comes from a dependency:

```java

public class Point {

  public double latitude;

  public double longitude;

}


```

We want to subclass it and add some extra fields. Also our API returns JSON data that aligns with this class and the fields from `Point`.

```java

@Translatable(inheritedKeys =
  {@InheritedField(keyDefinition = @Key, fieldName = "latitude"),
  {@InheritedField(keyDefinition = @Key, fieldName = "longitude")}})
public class XPoint extends Point {

  @Key
  String locationName;

}

```

As long as the fields are `public` or package-private in relation to the subclass, then we can reference the fields.

The `keyDefinition` parameter in `@InheritedField` mirrors what you would put as if it was an actual definition within the class. 
