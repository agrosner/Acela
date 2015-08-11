# Event Listening

In this library, you have multiple mechanisms to aid you in listening to parse and serialization events. For example, you want to save a `List` of objects to a database, using [DBFlow](https://github.com/Raizlabs/DBFlow/).

## Simple Example

We receive a response from an API that returns a list of `Locations` that contain `1...many` `Reviews`. We want to parse and then save the objects to the DB, but with databases you cannot/should not associate many objects to the same column, so we have a separate table for the `Review` object.

We first define the `Review` class:

```java

@Translatable
@Table(databaseName = AppDatabase.NAME)
public class Review extends BaseModel {

  @Column
  @PrimaryKey(autoincrement = true)
  long id;

  @Column
  @Key
  String title;

  @Column
  @Key
  String review_text;

  @Column
  @ForeignKey(references = {@ForeignKeyReference(columName = "location_id", columnType = Long.class, foreignColumnName = "id"), saveForeignKeyModel = false})
  Location location;

}

```

Which associates the `Review` to the `Location` class.

Next, we need to define the `Location` class:

```java

@Translatable
@Table(databaseName = AppDatabase.NAME)
public class Location extends BaseModel {

  @Column
  @PrimaryKey
  @Key
  String id;

  @Column
  @Key
  String name;

  @Column // ERROR cannot have a field as a column in DB
  @Key
  List<Review> reviews;

}

```

When we build, we receive an error that we cannot use a `List` as a valid database column. If we remove the `@Column` annotation, we discover that we don't have a proper way of handling it so we can save it.

The solution:


```java

@Translatable
@Table(databaseName = AppDatabase.NAME)
public class Location extends BaseModel implements ParseListener {

  @Key
  List<Review> reviews;

  @Override
  public void onParse(JsonParserWrapper wrapper, String key) {
       if ("reviews".equals(key)) {
         // this method is called after any fields are filled for the key it references
           for (Review review: reviews) {
             // add reference to it.
             review.location = this;
           }
           // save all in a transaction
           new SaveModelTransaction<>(ProcessModelInfo.withModels(reviews)).onExecute();
       }
   }
}

```

## All Kinds Explained

For general parse event listening, have your class implement `ParseListener`.

For general serialization listening, have your class implement `SerializeListener`.

For more fine-grained listening you can create methods that get called during parsing/serialization. For example, you want to trigger a flag or event when a specific key is found:


```java
@ParseKeyListener(key = "manualKey")
public void onParseManualKey() {
  hasManualKey = true;
}

@SerializeKeyListener(key = "manualKey")
public void onSerializeManualKey(JsonGeneratorWrapper wrapper) {
    hasManualSerializeKey = true;
}
```

These methods __must__ be accessible and have 1 argument as a `JsonParserWrapper` for `@ParseKeyListener` or `JsonGeneratorWrapper` for `@SerializeKeyListener` or no params at all.
