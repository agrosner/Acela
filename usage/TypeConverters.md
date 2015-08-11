# Type Converters

`TypeConverter` enable you to convert JSON-formatted data of one type, into a type that your class in application code represents as another kind. A classic example is:

```JSON
date: 2015-07-06
```

can be represented as:

```java
Date date;
```

## Creating a Custom Type Converter

In order to register a custom type converter, you have two different ways:
1. Define a class and register it globally
2. Or, register one for a specific field.

A great example of (2) is when an API returns different kinds of date formats.

```java

@TypeConverter // registers it globally.
public class LocationConverter implements TypeConverter<Location, String> {

  @Override
  public Location parse(JsonParserWrapper jsonParser, String defValue) {
      String value = jsonParser.getValueAsString(defValue);
      if (value != null && value.length() > 0) {
          String[] values = value.split(",");
          return new Location(Double.valueOf(values[0]), Double.valueOf(values[1]));
      } else {
          return null;
      }
  }

  @Override
  public void serialize(JsonGeneratorWrapper wrapper, String fieldName, Location value, String defValue) {
      if (value != null) {
          wrapper.writeStringField(fieldName, value.getLatitude() + "," + value.getLongitude());
      } else {
          wrapper.writeStringField(fieldName, defValue);
      }
  }
}


```

To register it to a specific field:

```java

@Key(typeConverter = LocationConverter.class)
Location location;

```
