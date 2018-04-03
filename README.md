# MARC-I
This is the 2017-2018 IMARC Software Repository/Project for our MARC-I boat.

<br/><br/>

## Building the Project

#### Running the core on a single device:
1. Create a new Gradle Run Configuration
2. Name: `Run Core`
3. Gradle Project: `MARC-I`
4. Tasks: `runCore`
5. Arguments: `-PappArgs="['-manifest', 
'src/main/resources/manifests/manifest-laptop.json', '-device', 'laptop']"`

#### Running the core as a simulation:
1. Create a new Gradle Run Configuration
2. Name: `Run Simulation`
3. Gradle Project: `MARC-I`
4. Tasks: `runCore`
5. Arguments: `-PappArgs="['-manifest', 
'src/main/resources/manifests/manifest-laptop.json', '-device', 'laptop', 
'-simulation', 'src/main/resources/simulation-maps/map-name.json']"`

#### Build:
1. Create a new Gradle Run Configuration
2. Name: `Build`
3. Gradle Project: `MARC-I`
4. Tasks: `build`

#### Perform Tests:
1. Create a new Gradle Run Configuration
2. Name: `Perform Tests`
3. Gradle Project: `MARC-I`
4. Tasks: `test`

<br/><br/>

## Contact/Help
Email [aaron-klinker@uiowa.edu](mailto:aaron-klinker@uiowa.edu) for 
questions, or come to a software meeting.
