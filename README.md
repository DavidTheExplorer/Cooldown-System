# Overview
Cooldowns in games are commonly implemented inefficiently, with boilerplate, and magic numbers(to represent durations).\
This library encapsulates all of that behind a clean and future-rich interface.

Beyond the usual cooldown operations, this library allows you to:
- Schedule an action to run when a cooldown is over(e.g. notify the player).
- Define rejection logic for the scenario when a player tries to do something he shouldn't while on cooldown.
- Put offline players on cooldown(using their `UUID`s).

# Getting started
The core is platform independent, and the platforms with built-in support are:
- Bukkit

How to import, as well as an in-depth tutorials can be found on the [wiki](https://github.com/DavidTheExplorer/Calmdown/wiki/How-to-import).
