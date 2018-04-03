# HTTP Request Documentation
When sending data in the body of the request, make sure that data is properly
formatted as JSON, and that there is a header set as
`"Content-Type":"application/json"`

### Tags
- [Implement]() - Need to write code to perform the request
- [Finish]() - Part of the code has been writen to provide dummy/fake data, but it still needs to be completed.
- [Bug]() - There is a bug that needs to be fixed

<a id="general"></a>
## General Requests
All requests can have the following properties in their response. It will be represented as `[generalData]` in all the responses.
```
{
  ...
  "error": [string],
  "message": {
    "type": "e"|"w"|"v"|"d",
    "content": [string]
  }
  ...
}
```

- `error` - This is an error that occured while performing the request. This should be logged.
- `message` - This is a message that should be added to the network log. This is not the log from the boat, but any general update on the request.
  - `type` - Corriesponds with the log class, and can be one of the following values:
    - `d` - Debug, white normal statement
    - `v` - Verbose, important blue statement
    - `w` - Warning, possibly problematic yellow statement
    - `e` - Error, problematic red statement
  - `content` - The string that cantains the message's content.


<br/></br>

# `GET /gps` - [Finish]()
Returns boat gps and accelerometer data. Angles (pitch, roll, compass) are all in degrees.

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| none |

## Response
```
{
  "lat": [double],
  "long": [double],
  "pitch": [double],
  "roll": [double],
  "compass": [double],
  [generalData]
}
```


<br/></br>

# `GET /logs` - [Implement]()
Returns any new logs that were output since the last request. This is the actual log from the boat. It is different from the network log.

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| none |


## Response
```
{
  "messages": [
    {
      "type": "e"|"w"|"v"|"d",
      "content": [string]
    },
    {
      "type": "e"|"w"|"v"|"d",
      "content": [string]
    },
    {
      "type": "e"|"w"|"v"|"d",
      "content": [string]
    },
    ...
  ],
  [generalData]
}
```

> See [General Requests Section](#general) for info on `type` and `content`


<br/></br>

# `POST /e-stop` - [Implement]()
There are 2 types of E-Stops for the boat. A hardware (a physical button) and a software one. __This request triggers the software one__.

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| none |

## Response
```
{
  "success": [boolean],
  "time": [long],
  [generalData]
}
```


<br/></br>

# `GET /tasks` - [Implement]()
Gets the list of tasks, their current progress, and how long each took to finish.

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| skip | An amout of tasks to skip. If `skip=2`, response would not contain the first 2 tasks | | `int` |

## Response
```
{
  "tasks": [
    {
      "name": [string],
      "completed": [boolean],
      "duration": [long/null]
    },
    {
      "name": [string],
      "completed": [boolean],
      "duration": [long/null]
    },
    ...
  ],
  [generalData]
}
```


<br/></br>

# `POST /tasks/start` - [Implement]()
Tells the boat to finish the initialization tasks and start the next one.

> This __most be called after__ `POST /connect`.

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| calledAt | The time in `ms` in which this request was sent at | `x` | `long` |

## Response
```
{
  "calledAt": [long],
  [generalData]
}
```


<br/></br>

# `POST /connect` - [Implement]()
Tells the boat that the GUI has connected.

> This __must be called before__ `POST /tasks/start`

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| calledAt | The time in `ms` in which this request was sent at | `x` | `long` |

## Response
```
{
  "calledAt": [long],
  [generalData]
}
```


<br/></br>

# `POST /ping` - [Implement]()
Tests the connection to the Boat.

## Parameters
| Name | Description | Required | Type |
|:-----|:------------|---------:|-----:|
| calledAt | The time in `ms` in which this request was sent at | `x` | `long` |

## Response
```
{
  "calledAt": [long],
  [generalData]
}
```


<br/></br>

# `GET /thrust`
This request has no query, and has the following return type:

```
{
  "leftThrust": [double],
  "rightThrust": [double],
  [generalData]
}
```

> Both of the thrust values range from `[-1, 1]`