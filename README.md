# Portal Location
[![Build Status](https://server.stijnhooft.be/jenkins/buildStatus/icon?job=portal-location/master)](https://server.stijnhooft.be/jenkins/job/portal-location/job/master/)

A REST service providing location functionality. It gets its information from both external location APIs as from predownloaded data sets.

## Supported external location APIs
* LocationIQ

## Pre-downloaded data sets
* OpenWeatherMap city id list


## Docker environment variables
| Name | Example value | Description | Required? |
| ---- | ------------- | ----------- | -------- |
| JAVA_OPTS_LOCATION | -Xmx400m -Xms400m | Java opts you want to pass to the JVM | optional
| CACHE_PATH | /opt/cache/ | Path of the cache in the Docker container. Map this to your volume. | optional
| CACHE_GEOCODE_MAX_MB | 100 | [See property documentation](#General framework) | optional
| CACHE_GEOCODE_MAX_NO_OF_ENTRIES | 1000 | [See property documentation](#General framework) | optional
| LOCATION_IQ_ENABLED | true | Should LocationIQ be used? Default: true | optional
| LOCATION_IQ_ORDER | 1 | Order of usage of LocationIQ in comparison with other services. See [property documentation](#order). Default: 1 | optional
| LOCATION_IQ_API_KEY | secret | API key for LocationIQ | required when LocationIQ is enabled
| OPEN_WEATHER_MAP_ENABLED | true | Should the pre-downloaded data set from OpenWeatherMap be used? Default: true | optional
| OPEN_WEATHER_MAP_ORDER | 2 | Order of usage of OpenWeatherMap in comparison with other services. See [property documentation](#order). Default: 2 | optional

## Configuration
Since the plugin-like nature of this application, we have 2 types of configuration:
* General framework configuration
* External API configuration

### General framework
```
be.stijnhooft.portal.location.cache.path = /Users/stijnhooft/app/portal/weather/
be.stijnhooft.portal.location.cache.geocode.max-mb = 100
be.stijnhooft.portal.location.cache.geocode.max-no-of-entries = 1000
```

#### Path
When provided, the cache will be saved in that directory. 

When not provided, the cache is kept in-memory only.

#### Max-mb
How many megabytes may the cache take on the disk?

#### Max-no-of-entries
How many entries can the cache keep? Default is 1000.


### Location services
```
be.stijnhooft.portal.location.service.geocode.LocationIQ.enabled = true
be.stijnhooft.portal.location.service.geocode.LocationIQ.order = 1
be.stijnhooft.portal.location.service.geocode.LocationIQ.api-key = your-api-key
```

```
be.stijnhooft.portal.location.service.geocode.OpenWeatherMap.enabled = true
be.stijnhooft.portal.location.service.geocode.OpenWeatherMap.order = 2
```

#### Enabled
When not providing these properties for a certain service, it will assume it is enabled.
 
#### Order
APIs are called in a certain order, just until all requests have been fulfilled. In a perfect world, only the API with order = 1 is called, because it can serve all calls; the other API's shouldn't be bothered.
When not providing an order for a certain service, it will assume an order of 1.
When multiple services have the same order number, the order of these specific services cannot be guaranteed.


#### API key
If you don't disable the LocationIQ service, it is required to provide an API key.
When running in Docker, you can provide this as an environment variable "LOCATION_IQ_API_KEY".


## Release
### How to release
To release a module, this project makes use of the JGitflow plugin and the Dockerfile-maven-plugin.

1. Make sure all changes have been committed and pushed to Github.
1. Switch to the dev branch.
1. Make sure that the dev branch has at least all commits that were made to the master branch
1. Make sure that your Maven has been set up correctly (see below)
1. Run `mvn jgitflow:release-start -Pproduction`.
1. Run `mvn jgitflow:release-finish -Pproduction`.
1. In Github, mark the release as latest release.
1. Congratulations, you have released both a Maven and a Docker build!

More information about the JGitflow plugin can be found [here](https://gist.github.com/lemiorhan/97b4f827c08aed58a9d8).

#### Maven configuration
At the moment, releases are made on a local machine. No Jenkins job has been made (yet).
Therefore, make sure you have the following config in your Maven `settings.xml`;

````$xml
<servers>
    <server>
        <id>docker.io</id>
        <username>your_username</username>
        <password>*************</password>
    </server>
    <server>
        <id>portal-nexus-releases</id>
        <username>your_username</username>
        <password>*************</password>
    </server>
</servers>
````
* docker.io points to the Docker Hub.
* portal-nexus-releases points to my personal Nexus (see `<distributionManagement>` in the project's `pom.xml`)
