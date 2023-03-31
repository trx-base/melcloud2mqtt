# About melcloud2mqtt

This microservice does one thing well:

1. Simulators need tests as well!
2. Provides access to MELCloud via MQTT as a Home Assistant compatible device.

## Development Rules

1. Don't solve problems you don't have
2. Commit often
3. Build locally before push
4. Push often
5. 100% Test Coverage

# Open Issues

- Connection to MQTT broker is broken after broker experiences outage.

# MQTT Endpoints

## Subscriber

### `melcloud2mqtt/listDevices`

Provides current data about all devices available for the configured MELCloud account. The response will be deliverd to
the specified response topic.

**Payload**

```
none
```

**Meta**

```
Response Topic
```

# Docker Compose

A working example of a **docker-compose.yml** can be found in folder docker-compose.

- Rename **.env.example** to **.env** and adapt to your needs.
- Rename **application.yml.example** to **application.yml** and adapt configuration

# Build

## Basic Commands

* **Build**: `./gradlew clean build`
* **Code coverage report**: `./gradlew koverReport` - Report can be found at `build/reports/kover/html/index.html`

## Micronaut Management

The micronaut management endpoint is enabled by default. See Micronaut documentation for further
information: https://docs.micronaut.io/latest/guide/#providedEndpoints

# MELCloud API

## EffectiveFlags

The property "EffectiveFlags" must be set when updating a value. Here is a list of probable EffectiveFlag options for
specific options:

```
AtwEffectiveFlags_Power: 1,
AtwEffectiveFlags_OperationMode: 2,
AtwEffectiveFlags_EcoHotWater: 4,
AtwEffectiveFlags_OperationModeZone1: 8,
AtwEffectiveFlags_OperationModeZone2: 16,
AtwEffectiveFlags_SetTankWaterTemperature: 32,
AtwEffectiveFlags_TargetHCTemperatureZone1: 128,
AtwEffectiveFlags_TargetHCTemperatureZone2: 512,
AtwEffectiveFlags_ForcedHotWaterMode: 65536,
AtwEffectiveFlags_HolidayMode: 131072,
AtwEffectiveFlags_ProhibitHotWater: 262144,
AtwEffectiveFlags_ProhibitHeatingZone1: 524288,
AtwEffectiveFlags_ProhibitCoolingZone1: 1048576,
AtwEffectiveFlags_ProhibitHeatingZone2: 2097152,
AtwEffectiveFlags_ProhibitCoolingZone2: 4194304,
AtwEffectiveFlags_Demand: 67108864,
AtwEffectiveFlags_ThermostatTemperatureZone1: 8589934592,
AtwEffectiveFlags_ThermostatTemperatureZone2: 34359738368,
AtwEffectiveFlags_SetFlowTemperature: 281474976710656,
```

The flags have been retrieved from`doc/melcloud2mqtt.js`
> **HINT:** If you are an IntelliJ user, exclude `doc/` folder from indexing. The melcloud.js is quite large and may
> overload your machine.
>
> 1. Right click on `doc/` folder in Project explorer panel.
> 2. Select `Mark directory as...`
> 3. Select: `Exclude`
>

# Home Assitant

Entity Configuration: https://developers.home-assistant.io/docs/core/entity/sensor/