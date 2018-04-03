# Manifests
Manifests must be used in every execution of `runCore`.  are included in a 
run They are included in the execution by adding `-manifest 
path/to/manifest/file.json` to the args in the 'runCore' gradle command.

## File Structure
```
{
  "devices": [
    {
      "id": 0,
      "name": "device_1_name",
      "primary": true,
      "ip_address": "127.0.0.0",
      "port": 4000,
      "nodes": [
        {
          "class": "com.klinker.droneos.node_classpath_1",
          "data": "path/to/initialization.json"
        },
        {
          "class": "com.klinker.droneos.node_classpath_2",
          "data": "path/to/initialization.json"
        },
        ...
      ]
    },
    {
      "id": 1,
      "name": "device_2_name",
      "ip_address": "127.0.0.0",
      "primary": false,
      "port": 4000,
      "nodes": [
        {
          "class": "com.klinker.droneos.node_classpath_3",
          "data": "path/to/initialization.json"
        },
        {
          "class": "com.klinker.droneos.node_classpath_4",
          "data": "path/to/initialization.json"
        }, 
        ...
      ]
    },
    ...
  ]
}
```

## `devices`
"devices" is a list of all the devices that are currently hooked up.

- `id` 
    - ___Required___
    - Unique `integer` 
    - Used as the key in a HashMap.
    - Can be any integer, but preferably positive counting from 0
- `name`
    - ___Optional___ (but should use)
    - Not unique `string`, but should be unique.
    - Used for debugging
- `primary`
    - ___Required Once___
    - `boolean`
    - Used to decide if the device should have the `MainNodeManager` for `true` 
    or a `SataliteNodeManager` if `false` or not given
- `ip_address`
    - ___Required___
    - Unique `string`
    - Local IP address used for communication.
- `port`
    - ___Required if `primary == true`___
    - Unique `int`
    - Local IP address used for communication.
- `nodes`
    - ___Required___
    - Array of `node-item` items (see next section) on the device.

## `node-item`
- `class`
    - ___Required___
    - `String`
    - Full classpath of a class that extends `Node`.
        - Example: `"com.klinker.droneos.cv.CVNode"`
- `data`
    - Optional
    - `String`
    - Path to a JSON file that contains info for the node. This JSON file can
     contain any valid JSON data however the creator of the node wants it 
     structured.
    