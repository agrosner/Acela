# Getting Started

In this section, we run through how to get started and set up how JSON gets parsed and serialized.

## Baseball Example

We love baseball teams and we want to parse and convert player data in JSON into objects that our application can use.

### Set up the Translatables

Our JSON data that is returned is in the format that resembles (for simplicity, we leave out other kinds of data that may be returned):

```JSON
[

  {
    name: "New York Yankees",
    coach: "Joe Girardi",
    players: [
      {
        name: "Mark Teixeira",
        position: "First Base"
      }
    ]
  }

]

```

First step is to create our `Player` object to represent the objects that are included by the `players` object.

```java

@Translatable
public class Player {

  @Key
  String name;

  @Key
  String position;

  // there may be more fields, for this example we won't
  // list them all.

}
```

Next, we create the `Team` object which contains many players that are returned from our JSON response:

```java

@Translatable
public class Team {

  @Key
  String name;

  @Key
  String coach;

  @Key(name = "players")
  List<Player> playersList;
}

```

### Deserialize JSON and then Serialize Back Again

Call the `Translator` class to perform most operations:

```java

// we support String, byte[], File, InputStream, char[] and more!
List<Team> teams = Acela.getTranslator(Team.class).parseList(jsonString);

// to serialize back to JSON
String json = Acela.getTranslator(Team.class).serializeList(teams);

```
