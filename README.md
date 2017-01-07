# Brick Pop Solver

A Java-powered solver for the *Brick Pop* facebook messenger game.

[![License](https://img.shields.io/github/license/neocotic/brick-pop-solver.svg?style=flat-square)](https://github.com/neocotic/brick-pop-solver/blob/master/LICENSE.md)

* [Install](#install)
* [Usage](#usage)
* [Device Support](#device-support)
* [Bugs](#bugs)
* [License](#license)

## Install

In order to install this tool you will need the following prerequisites:

* [Git](https://git-scm.com)
* [Java 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org)

Once you have all of them installed you just need to do the following:

``` bash
$ git checkout https://github.com/neocotic/brick-pop-solver.git
$ cd brick-pop-solver
$ mvn package
```

## Usage

This tool can either be used via the CLI or via its API.

Your device **must** be connected (with USB debug mode enabled) and *Brick Pop* should be open and a game open.

### CLI

``` bash
$ java -jar target/brick-pop-solver-0.1.0-SNAPSHOT-bundle.jar
```

It can be further customized by specifying Java properties. Take a look at the source code for `BrickPopSolver.java`.

### API

``` java
BrickPopSolver solver = new BrickPopSolver();
Solution solution = solver.solve();

solution.play();
```

A `Configuration` instance can be passed to the constructor for further customization. Take a look at the source code
for `Configuration.java`.

Other methods are also available so please explore the API.

## Device Support

Currently, only Android devices are supported and you will need to have the
[Android SDK](https://developer.android.com/studio/index.html) installed as this tool depends on the Android Debug
Bridge (`adb`) to communicate with the device. This means that you'll need to have `/path/to/android-sdk/platform-tools`
in your `PATH` environment variable so that it is discoverable.

This tool has only been tested on a Google Nexus 6P so the default configurations match this. The offset and starting
point may need to be changed to match the screen resolution of your device.

## Bugs

If you have any problems with this tool or would like to see changes currently in development you can do so
[here](https://github.com/neocotic/brick-pop-solver/issues).

## License

See [LICENSE.md](https://github.com/neocotic/brick-pop-solver/raw/master/LICENSE.md) for more information on our MIT
license.
