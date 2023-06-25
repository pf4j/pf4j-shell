<img src="pf4j-logo.svg" width="250"/>

Shell for PF4J
=====================
[![Join the chat at https://gitter.im/decebals/pf4j](https://badges.gitter.im/decebals/pf4j.svg)](https://gitter.im/decebals/pf4j?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![GitHub Actions Status](https://github.com/pf4j/pf4j-shell/actions/workflows/build.yml/badge.svg)](https://github.com/pf4j/pf4j-shell/actions/workflows/build.yml)
[![Maven Central](http://img.shields.io/maven-central/v/org.pf4j/pf4j-shell.svg)](http://search.maven.org/#search|ga|1|pf4j=shell)

Interactive shell for [PF4J](http://pf4j.org).

Features/Benefits
-------------------
Use the shell to interact with PF4j.

You can interact with the `PluginManager` (see `manager` command) and execute the following actions:
- display `info` about manager (_plugins_roots_, _system_version_, _runtime_mode_)
- see `unresolved` plugins
- see all the `extensions` added by the all plugins
- `which` plugin loaded the given class

You can interact with plugins (see `plugins` command) and execute the following actions:
- display `info` about all plugins (_id_, _version_, _state_)
- `load` plugins
- `reload` plugins
- `unload` plugins
- `start` plugins
- `stop` plugins

You can interact with a specific plugin (see `plugin` command) and execute the following actions:
- display `info` about plugin (_id_, _version_, _state_, _path_, _provider_, _description_, _dependencies_, _classloader_, ..)
- `load` plugin
- `unload` plugins
- `start` plugins
- `stop` plugins

You can interact with `ClassLoader`s (see `classloader` command)  and execute the following actions:
- display `info` about all classloaders
- display the `plugins` classloaders
- display the `urls` for a specific plugin classloader (what jars, directories the plugin adds)
- display the `loadedClasses` for a specific plugin classloader

How to use
-------------------
It's very simple to add pf4j-shell in your application.

Define an extension point in your application/plugin using **ExtensionPoint** interface marker:

```java
public class Boot {

    public static void main(String[] args) {
        // create the plugin manager
        PluginManager pluginManager = new DemoPluginManager();

        // create shell
        Shell shell = new Shell(pluginManager);

        // start shell
        shell.start();
    }
}
```

For more details see `Boot.java` that comes with demo. 

The output is:
```
22:23:25 [main] INFO org.pf4j.demo.Boot - ########################################
22:23:25 [main] INFO org.pf4j.demo.Boot -                PF4J-DEMO                
22:23:25 [main] INFO org.pf4j.demo.Boot - ########################################
22:23:25 [main] INFO org.pf4j.DefaultPluginStatusProvider - Enabled plugins: []
22:23:25 [main] INFO org.pf4j.DefaultPluginStatusProvider - Disabled plugins: []
22:23:25 [main] INFO org.pf4j.DefaultPluginManager - PF4J version 3.9.0 in 'deployment' mode
pf4j-shell> 
```

Now you can start to run commands

See all commands available:
```
pf4j-shell> help
 -  PicocliCommands registry
Summary: classloader 
         clear       Clears the screen
         cls         Clears the screen
         help        
         manager     
         plugin      
         plugins     
```

Get information about manager:
```
pf4j-shell> manager 
Plugins roots: [plugins]
System version: 0.0.0
Runtime mode: deployment
```

Load all plugins:
```
pf4j-shell> plugins load
pf4j-shell> plugins 
            Id|Version|   State
--------------+-------+--------
welcome-plugin|  0.0.1|RESOLVED
  hello-plugin|  0.0.1|RESOLVED
```

Start all plugins:
```
pf4j-shell> plugins start 
Start plugins ...
22:31:56 [main] INFO org.pf4j.AbstractPluginManager - Start plugin 'welcome-plugin@0.0.1'
22:31:56 [main] DEBUG org.pf4j.DefaultPluginFactory - Create instance for plugin 'org.pf4j.demo.welcome.WelcomePlugin'
22:31:56 [main] INFO org.pf4j.demo.welcome.WelcomePlugin - WelcomePlugin.start()
22:31:56 [main] INFO org.pf4j.AbstractPluginManager - Start plugin 'hello-plugin@0.0.1'
22:31:56 [main] DEBUG org.pf4j.DefaultPluginFactory - Create instance for plugin 'org.pf4j.demo.hello.HelloPlugin'
22:31:56 [main] INFO org.pf4j.demo.hello.HelloPlugin - HelloPlugin.start()
pf4j-shell> plugins 
            Id|Version|  State
--------------+-------+-------
welcome-plugin|  0.0.1|STARTED
  hello-plugin|  0.0.1|STARTED
```

Stop all plugins:
```
pf4j-shell> plugins stop 
Stop plugins ..
22:32:53 [main] INFO org.pf4j.AbstractPluginManager - Stop plugin 'hello-plugin@0.0.1'
22:32:53 [main] INFO org.pf4j.demo.hello.HelloPlugin - HelloPlugin.stop()
22:32:53 [main] INFO org.pf4j.AbstractPluginManager - Stop plugin 'welcome-plugin@0.0.1'
22:32:53 [main] INFO org.pf4j.demo.welcome.WelcomePlugin - WelcomePlugin.stop()
pf4j-shell> plugins
            Id|Version|  State
--------------+-------+-------
welcome-plugin|  0.0.1|STOPPED
  hello-plugin|  0.0.1|STOPPED
```

Get information about a plugin:
```
pf4j-shell> plugin hello-plugin
Id : hello-plugin
Version : 0.0.1
State : STOPPED
Path : plugins/pf4j-demo-plugin2-0.1.0-SNAPSHOT-all.jar
PluginClass : org.pf4j.demo.hello.HelloPlugin
Description : 
Provider : Decebal Suiu
License : 
Requires : *
Dependencies : []
ClassLoader : org.pf4j.PluginClassLoader@44ebcd03
```

Get plugins classloaders:
```
pf4j-shell> classloader plugins 
welcome-plugin -> org.pf4j.PluginClassLoader@661972b0
hello-plugin -> org.pf4j.PluginClassLoader@44ebcd03
```

Get plugin classloader information:
```
pf4j-shell> classloader urls hello-plugin 
file:/home/decebal/work/pf4j-shell/demo-dist/plugins/pf4j-demo-plugin2-0.1.0-SNAPSHOT-all.jar
```
```
pf4j-shell> classloader loadedClasses hello-plugin
[class org.pf4j.demo.hello.HelloPlugin]
```

Use `TAB` for command auto-competition. 
Exit from shell with `Ctrl-D`.

Demo
---------------
Demo applications are available in [demo](https://github.com/pf4j/pf4j-shell/tree/master/demo) folder.
It's the same demo application available in PF4J but with the `Boot.java` modified (shell injected).  
Run demo using `run-demo` scripts (linux/windows).

**Note:** In demo application, by default the plugins are **not loaded** on startup, so you need to call `plugins load` to load the plugins. 
