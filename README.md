# Interakt
This application is intended to allow the user to create and manage environments and entities that can exist within those environments. 

# Inspiration
The inspiration for this application is [Kreatures](https://github.com/McCoy-Software-Solutions/Kreatures).

# Usage
## Modes
- Console
- Player

## Running the Project
Check out how to run the project [here](https://github.com/Stephenson-Software/Interakt/wiki/Running-the-Project).

## Usage Reporting
Each time Interakt starts it sends one `startup` event, carrying only the program name and version, to [trace](https://github.com/Stephenson-Software/trace) at `trace.danielstephenson.dev`, so that it is known whether anybody runs it. Nothing else is sent: no usernames, hostnames, actor names, world names or commands. The report is sent from a background thread and is dropped, not retried, if the service cannot be reached, so it can never slow down or stop the application.

Reporting is on by default. The first start after this was added prints a one-line notice and writes `usage-reporting.properties` next to the application's data files (the `/Interakt/` directory the application already uses). To turn it off, set

```properties
usage_reporting.enabled=false
```

in that file. `usage_reporting.endpoint` and `usage_reporting.key` in the same file are the service address and the write key issued to Interakt; the key can only add usage events and is not secret.

# Support
You can find the support discord server [here](https://discord.gg/49J4RHQxhy).

## Authors and acknowledgement
### Developers
Name | Main Contributions
------------ | -------------
Daniel Stephenson | Creator

## Project Status
This project is in active development.

# Ponder & EnvironmentLib
This project utilizes [Ponder](https://github.com/Preponderous-Software/Ponder) and [EnvironmentLib](https://github.com/Preponderous-Software/EnvironmentLib), two free and open source libraries provided by Preponderous Software.
