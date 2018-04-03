# Simulation Maps
Simulation maps are included in a run by adding the `-simulation 
path/to/simulation/file.json` to the args in the 'runCore' gradle command.

## File Structure
```
{
  "bounds": [
    {
      "x": x_1,
      "y": y_1,
    },
    {
      "x": x_2,
      "y": y_2,
    },
    ...
  ],
  "buoys":[
    {
      "x": x_1,
      "y": y_1,
      "color": "#XXXXXX"
    },
    {
      "x": x_2,
      "y": y_2,
      "color": "#XXXXXX"
    },
    ...
  ],
  "boat": {
    "x": x_location,
    "y": y_location,
    "angle": angle
  }
}
```

## Properties

### `x`
"x" is the __longitude__ of the item. _When clicking a location on google maps, 
the longitude is the second number_. In Iowa, it's about in the -90's.

### `y`
"y" is the __latitude__ of the item. _When clicking a location on google maps, 
the latitude is the second number_. In Iowa, it's about in the 43's.

### `color`
"color" is represented by a hexadecimal color value. It can be in the 
following formats:

- `"#rgb"`
- `"#argb"`
- `"#rrggbb"`
- `"#aarrggbb"`

See [Java Color](https://docs.oracle.com/javase/8/docs/api/java/awt/Color.html#decode-java.lang.String-) 
for more details.

### `angle`
"angle" is the angle the boat starts out facing in __degrees__, where the 
following angles point in the given direction:

| Angle (`deg`) | Unit Vector |
|---------------|-------------|
| 0             | <1, 0>      |
| 90            | <0, 1>      |
| 180           | <-1, 0>     |
| 270           | <0, -1>     |
| -90           | <0, -1>     |

## Arrays

### `bounds`
A list of points that make up the 'shore' of the lake. These points form a 
closed loop. We need to know if we physical hit the shore, so including bounds is important.

Including bounds are __not required__.

### `buoys`
A buoys that are located on the map. They're color is used to simulate CV input.

Including buoys are __not required__, but should be.