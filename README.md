# textdsl
An experimental combinator library for manipulating text.

## Examples

Given some text delimited by |

    ---|--
    ---|--|----|--
    -|--
    -|||||

We can create a function to align the columns by using some combinators:

```scala
    columnise("|") andThen
    normaliseColumnWidth andThen
    transposeColumns andThen
    padColumns andThen
    transposeColumns andThen
    joinColumns
````
So

```scala
alignColumns("|")(theAboveText)
```

Gives

    ---|--          
    ---|--|----|--  
    -  |--          
    -  |  |    |  ||