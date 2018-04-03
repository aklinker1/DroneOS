# DroneOS
This is the 2017-2018 IMARC Software Repository/Project for our DroneOS boat.

<br/><br/>

## Building the Project

#### Running the core on a single device:
1. Create a new Gradle Run Configuration
2. Name: `Run Core`
3. Gradle Project: `DroneOS`
4. Tasks: `runCore`
5. Arguments: `-PappArgs="['-manifest', 
'src/main/resources/manifests/manifest.json', '-device', 'Rpi-Zero']"`

#### Running the core as a simulation:
1. Create a new Gradle Run Configuration
2. Name: `Run Simulation`
3. Gradle Project: `DroneOS`
4. Tasks: `runCore`
5. Arguments: `-PappArgs="['-manifest', 
'src/main/resources/manifests/manifest.json', '-device', 'RPi-Zero', 
'-simulation', 'src/main/resources/simulation-maps/map-name.json']"`

#### Build:
1. Create a new Gradle Run Configuration
2. Name: `Build`
3. Gradle Project: `DroneOS`
4. Tasks: `build`