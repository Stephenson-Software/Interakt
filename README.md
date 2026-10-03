# Interakt

[![Play in your browser](https://img.shields.io/badge/Play-in%20your%20browser-2ea44f)](https://danielstephenson.dev/play/interakt)

This application is intended to allow the user to create and manage environments and entities that can exist within those environments. 

# Inspiration
The inspiration for this application is [Kreatures](https://github.com/McCoy-Software-Solutions/Kreatures).

# Usage
## Modes
- Console
- Player

## Running the Project
Check out how to run the project [here](https://github.com/Stephenson-Software/Interakt/wiki/Running-the-Project).

## Play in your browser
Interakt's console mode also runs in the browser, with [CheerpJ](https://cheerpj.com) (a Java runtime compiled to WebAssembly, by Leaning Technologies), at https://interakt.play.danielstephenson.dev, alongside the other games at https://danielstephenson.dev/play. Nothing needs to be installed. The page explains a first session (`help`, `generatetestdata`, `elapse`, `list`) and offers those commands as buttons, so it can be played on a phone without much typing. The data directory is `/files/interakt/`, CheerpJ's writable filesystem, which the browser keeps, so saved actors and worlds are still there after a reload. Usage reporting is off in the browser build: `web/java/BrowserMain.java` writes `usage_reporting.enabled=false` before Interakt starts.

To build and serve it locally (Java 17 and Maven):
```
./web/build.sh
python3 -m http.server 8000 --directory build/web
```
then open http://localhost:8000. `web/build.sh` compiles the application with Maven, unpacks the vendored Ponder, EnvironmentLib and Gson jars next to it, adds the browser entry point `web/java/BrowserMain.java` (which connects `System.in` and `System.out` to the page) and packs one `build/web/interakt.jar`, which `web/index.html` downloads and runs in console mode. EnvironmentLib is compiled for Java 16, so the page uses CheerpJ's Java 17 runtime. The build output in `build/` is not committed.

`.github/workflows/browser.yml` runs on every pull request and every push to `main`: it runs `mvn test`, builds the site, checks the jar's contents and the page's CheerpJ credit, and runs a short console session from the built jar. On a manual run (`workflow_dispatch`), or on a push to `main` once the repository variable `ARCADE_ENABLED` is `true`, it deploys the site to [arcade](https://github.com/Stephenson-Software/arcade) with [arcade-deploy](https://github.com/Stephenson-Software/arcade-deploy) as version `<version.txt>+g<short commit>`; the upload token is the `ARCADE_TOKEN` secret.

## Data directory
Save files (`actors.json`, `worlds.json`, and so on), `log.txt` and `usage-reporting.properties` are all kept in one data directory. By default that is `/Interakt/` - a rooted path, so `C:\Interakt\` on Windows and a directory at the filesystem root on Linux and macOS, which normally needs elevated privileges to create. To keep the data somewhere else, set either of these before starting the application (the system property wins if both are set):

- `-Dinterakt.data.dir=<directory>` on the `java` command line
- `INTERAKT_DATA_DIR=<directory>` in the environment

The directory is created if it does not exist. Existing data is not moved: point the setting at a directory and copy the files there yourself if you want to keep them.

The test run (`mvn test`) sets `interakt.data.dir` to `target/interakt-test-data`, so running the tests never touches the real data directory.

## Usage reporting
Usage reporting is on by default: each time Interakt starts it sends one `startup` event, carrying only its name, its version and a random installation ID, to [trace](https://github.com/Stephenson-Software/trace) at `https://trace.danielstephenson.dev`, so that it is known whether anybody runs it, and on how many installations. Nothing else is sent: no usernames, hostnames, IP addresses, actor names, world names, or anything typed at the console. The report is sent from a background thread and is dropped, not retried, if the service cannot be reached, so it can never slow down or stop the application. The first start after this was added prints a one-line notice and writes `usage-reporting.properties` next to the application's data files (the [data directory](#data-directory), `/Interakt/` by default).

The installation ID (the tag `install`) is a random UUID written to `trace-install-id` in the same data directory the first time reporting runs, and reused after that (in the browser build that directory is CheerpJ's `/files/interakt/`, kept by the browser). It identifies no person, account or address. Delete the file to get a new one, or set `TRACE_INSTALL_ID` in the environment to pin one. Every opt-out below also stops it: when reporting is off, no ID is made up and the file is neither read nor written.

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
