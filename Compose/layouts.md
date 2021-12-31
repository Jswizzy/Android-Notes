## Compose Layouts

3 Stages of Compose:
1. composition - emits UI tree
2. layout - UI is measures and placed on the screen 
3. drawing - UI is rendered on the screen

2 phases of the layout stage:
1. measure
2. place 

3 step process:
1. measure children
2. decide own size
3. place children

Layout Composable: Fundamental element of Compose UI

- emits layoutNodes
- slot for any composable

```kotlin
@Composable
fun Layout(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    measurePolicy: MeasurePolicy
) {
    ...
}
```

```kotlin
@Composable
fun myCustomLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measure: List<Measurable>,
        constraints: Constraints ->
        
        val width = // calculate from placeables
        val height = // calculate from placeables
        layout(width, height) {
            placeables.forEach { placeable ->
                placeable.place(
                    x = ...
                    y = ...
                )
            }
        }
    }
}
```

measurables: children
constraints: How big something can be

```koltin
@Composable
fun MyColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        val height = placeables.sumOf(Placeable::height)
        val width = placeables.maxOf(Placeable::width)
        layout(width, height) {
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = y)
                y += placeable.height
            }
        }
    }
}
```

Modifiers: modify a single constraint

```kotlin
fun Modifier.layout(
    measure: MeasureScope.(Measurable, Constraints) -> MeasurableResult
) {}

Box(Modifier
 .background(Color.Gray)
 .layout { measurable, constraints ->
    // adds 50 pixels of vertical padding
    val padding = 50
    val placable = measurable.measure(constraints.offset(vertical = -padding))
    layout(placable.width, placable.height + padding) {
        placeable.placeRelative(0, padding)
    }
}
) {
 Box(Modifier.fillMaxSize().background(Color.DarkGray))
}
```

Advanced Features

intrinsic sizes

2 pass measurements 

IntrinsicSize: use children sizes
- Min
- Max

Alignment

alignBy

BoxsWithConstraints: receiver scope that exposes determined constraints


