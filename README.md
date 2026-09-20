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

## Data directory
Save files (`actors.json`, `worlds.json`, and so on), `log.txt` and `usage-reporting.properties` are all kept in one data directory. By default that is `/Interakt/` - a rooted path, so `C:\Interakt\` on Windows and a directory at the filesystem root on Linux and macOS, which normally needs elevated privileges to create. To keep the data somewhere else, set either of these before starting the application (the system property wins if both are set):

- `-Dinterakt.data.dir=<directory>` on the `java` command line
- `INTERAKT_DATA_DIR=<directory>` in the environment

The directory is created if it does not exist. Existing data is not moved: point the setting at a directory and copy the files there yourself if you want to keep them.

The test run (`mvn test`) sets `interakt.data.dir` to `target/interakt-test-data`, so running the tests never touches the real data directory.

## Usage reporting
Usage reporting is on by default: each time Interakt starts it sends one `startup` event, carrying only its name and version, to [trace](https://github.com/Stephenson-Software/trace) at `https://trace.danielstephenson.dev`, so that it is known whether anybody runs it. Nothing else is sent: no usernames, hostnames, IP addresses, actor names, world names, or anything typed at the console. The report is sent from a background thread and is dropped, not retried, if the service cannot be reached, so it can never slow down or stop the application. The first start after this was added prints a one-line notice and writes `usage-reporting.properties` next to the application's data files (the [data directory](#data-directory), `/Interakt/` by default).

To turn it off, any one of these is enough:

- `usage_reporting.enabled=false` in `usage-reporting.properties` in the data directory
- `TRACE_USAGE_REPORTING=off` (also `false`, `0`, `no`) in the environment — the switch every trace client honours, checked before the settings file
- `DO_NOT_TRACK=1` (also `true`, `yes`) in the environment, per [consoledonottrack.com](https://consoledonottrack.com)

`usage_reporting.endpoint` and `usage_reporting.key` in the same file are the service address and the write key issued to Interakt; the key can only add usage events and is not secret.

Details on what trace collects and why: https://github.com/Stephenson-Software/trace#usage-reporting

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
