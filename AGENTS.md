# AGENTS.md

This file provides guidance to AI coding agents working with code in this repository.

Eclipse Workspace Mechanic: an Eclipse plug-in that keeps a workspace's preferences, key bindings and other settings in a defined state by periodically evaluating "tasks" and offering to repair the ones that fail.
It is a fork of the original Google Code project, so packages and bundle ids are `com.google.eclipse.mechanic`.

## Build

```bash
mvn clean verify                  # plug-ins, feature, p2 update site and tests
mvn clean verify -DskipTests
mvn clean verify -pl :com.google.eclipse.mechanic.tests -am -Dtest=EpfFileModelTest -DfailIfNoTests=false
```

The build is Tycho in pomless mode and needs Java 25 and Maven 3.9.9+; `.mvn/maven.config` sets the Tycho version (`tycho-version`, used by the root pom and `.mvn/extensions.xml`) and four build threads.
The only `pom.xml` is the root one; `.mvn/extensions.xml` enables `tycho-build`, which derives each module from its `MANIFEST.MF`, `feature.xml`, `category.xml` or `.target` file.
Group directories (`bundles`, `examples`, `tests`, `features`, `releng`) act as aggregators because they are listed in `tycho.pomless.aggregator.names` in `.mvn/maven.config`; a new group directory must be added there and to the root `<modules>`.
A bundle whose symbolic name ends in `.tests` becomes an `eclipse-test-plugin`.

Dependencies come only from the target platform `releng/target-platform/target-platform.target` (Eclipse release p2 repository plus Maven Central artifacts such as Gson, JUnit 5 and Mockito), never from Maven `<dependencies>`.
Consume libraries with `Import-Package` in `MANIFEST.MF`, and add a library that must ship with the plug-in to `features/mechanic/feature.xml` so the update site contains it.
The code does not use Guava.

`.github/workflows/build.yml` builds and tests every push and pull request, signing with a throwaway key; `release.yml` signs with the organization's `MAVEN_GPG_KEY` and publishes each push to `main` to the `gh-pages` composite site through `releng/update-composite-site.sh`, which keeps only the newest build.

Tests are JUnit 5 and run inside an OSGi runtime with the workbench, so they need a display; locally use `xvfb-run -a mvn ...`.

## Layout

- `bundles/com.google.eclipse.mechanic`: the only production plug-in.
- `tests/com.google.eclipse.mechanic.tests`: a fragment of the plug-in (`Fragment-Host`), so tests can access package-private members.
- `examples/...samples`: example contributions to the plug-in's extension points.
- `features/mechanic` and `releng/update`: feature and p2 update site.
- `releng/target-platform`: the target definition, also usable in the IDE.
- `config/ws-mechanic`: sample `.epf` task files, not part of the plug-in.
- `docs`: the icon and the README screenshots.

## Architecture

The core abstraction is `Task` (id, title, `Evaluator`, `RepairAction`).
`MechanicService` is the singleton that runs a background job which scans for tasks, evaluates them, publishes `MechanicStatus` to `IStatusChangeListener`s (the status bar widget in `plugin/ui`) and drives repairs through `RepairManager`.

Tasks are discovered in two layers, both wired through extension points defined in `schema/` and registered in `plugin.xml`:

1. **Resource task providers** (`resourcetaskproviders` extension point, `IResourceTaskProvider`) supply task source files.
   `ResourceTaskProviderParser` turns the "task directories" preference (path-separator list, defaults to `~/.eclipse/mechanic` and `${mechanic_configuration_path}/mechanic`) into `FileTaskProvider`s for local directories and `UriTaskProvider`s for URLs pointing at a JSON manifest (`UriTaskProviderModelParser`), with URI content cached by `UriCaches`.
2. **Scanners** (`scanners` extension point, `TaskScanner`/`ResourceTaskScanner`) turn those resources into tasks by file extension: `.epf` preference files (`PreferenceFileTaskScanner`, backed by `EpfFileModel`), `.kbd` key binding files (`core/keybinding`), and compiled `.class` tasks (`ClassFileTaskScanner`).
   `ExtensionPointScanner` additionally picks up `Task` implementations contributed directly via the `tasks` extension point.
   `RootTaskScanner` runs all registered scanners.

Package split: `com.google.eclipse.mechanic` is the only exported (public API) package; `internal` holds the implementation, `plugin.core` the activator, preferences and logging, `plugin.ui` the SWT/JFace UI, and `core.recorder` the preference recorder that generates `.epf` files from changes made in the IDE.
