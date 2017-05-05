# textdsl
An experimental combinator library for manipulating text.
w
## Examples

Given some text delimited by |

    ---|--
    ---|--|----|--
    -|--
    -|||||

We can create a function to align the columns by using some combinators:

```scala
    columnise(s) ∘
    normaliseColumnWidth ∘
    transposeColumns ∘
    padColumns ∘
    transposeColumns ∘
    joinColumns ∘
    trimLines
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