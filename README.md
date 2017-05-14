[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--DebugPort-green.svg?style=true)](https://android-arsenal.com/details/1/3540) [![License: Apache2.0](https://img.shields.io/badge/style-apache%202.0-blue.svg?style=flat&label=license)](http://www.apache.org/licenses/LICENSE-2.0)

# Android DebugPort

Android DebugPort is a drop-in utility which allows you to write and execute code within your app's context, at runtime, and from the comfort of your computer's terminal. Think of it as a window into your application through which you can both inspect _and_ modify its state.

You can connect to one of two REPL servers running within your app:

* Debug REPL - Run java-like code and inspect/modify the state of your android application.
* SQLite REPL - Execute queries against your app's SQLite databaases.

## Getting Started - Drop-in

### Configure Your Dependencies

Add the jitpack.io repository to your root `build.gradle`:

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

In your application's `build.gradle` file, add a dependency for Android DebugPort:

```groovy
debugCompile 'com.github.jasonwyatt:Android-DebugPort:library:2.0.0'
releaseCompile 'com.github.jasonwyatt:Android-DebugPort:library-noop:2.0.0'
```

**Note:** The final line above will use a [no-op version of the DebugPort library](https://github.com/jasonwyatt/Android-DebugPort-NOOP) in production builds. This makes it impossible for people to run the DebugPort server on a production build.
    
### Run Your App

When you start your app after building for debug, you will see a low-priority notification in your system tray which will allow you to start the debugport servers.
    
### Connecting to the Debug Server

    $ telnet 192.168.2.83 8562
    Trying 192.168.2.83...
    Connected to 192.168.2.83.
    Escape character is '^]'.

    Android DebugPort v1.0
    Report issues at https://github.com/jasonwyatt/Android-DebugPort/issues
    
    BeanShell 2.0b6 - by Pat Niemeyer (pat@pat.net)
    bsh %

There are a few built in commands, to see what they are, run `help();`
  
    bsh % help();
    Available Commands:
      Access:
          call(Object obj, String method, Object... params)
              Call a method, regardless of access modifiers, on the provided object.
          get(Object obj, String fieldName)
              Get the value of a field, regardless of access modifiers, on the provided object.
          set(Object obj, String fieldName, Object value)
              Set the value of a field on the provided object to the given value, regardless of access modifiers.
    
      Field Inspection:
          fields(Class class)
              List all of the fields available for a particular class.
          fields(Object obj)
              List all of the fields available for a particular object.
          fieldsLocal(Class class)
              List all of the fields defined locally for a particular class.
          fieldsLocal(Object obj)
              List all of the fields defined locally for an object.
    
      Method Inspection:
          methods(Class class)
              Get the available methods for the provided class.
          methods(Object obj)
              Get the available methods for the provided object.
          methodsLocal(Class class)
              Show all of the locally-declared methods for the provided class.
          methodsLocal(Object obj)
              Show all of the locally-declared methods for the provided object.
    
      Other:
          exit()
              Exit this interpreter.
          help()
              Show this help message.
          source(String scriptPath)
              Load and run a Beanshell script within your app's assets folder.
    
    bsh %

Also, your application variable is automatically included as a global variable in the interpreter. It's called `app`. Try running `methodsLocal(app);`:

    bsh % methodsLocal(app);
    declared methods: {
      public void onCreate()
    }
    bsh %

Don't forget that you can execute whatever code you wish within the DebugPort. See the [beanshell documentation](http://beanshell.org/manual/contents.html) for the full rundown.

You can exit at any time by running the `exit();` command.

### Connecting to the SQLite Server

    $ telnet 192.168.0.100 8563
    Trying 192.168.0.100...
    Connected to 192.168.0.100.
    Escape character is '^]'.

    Android DebugPort v1.0
    Report issues at https://github.com/jasonwyatt/Android-DebugPort/issues

    SQLite Database REPL

    sqlite>
    
As with the Debug server, there is a help command for the SQLite server:

    sqlite> help;
    Help:
      As you'd expect, you can execute any valid SQLite statements against the database to which you're
      currently connected (see: `USE [database name];` below).
    
      In addition to regular SQLite commands, Android DebugPort provides additional functionality via several
      additional commands.
    
      Available non-SQLite commands (case insensitive):
        Databases:
            CREATE DATABASE [database name];
                Create a new database called [database name].
            DROP DATABASE [database name];
                Drop the database named [database name] from the app's collection of databases.
            USE [database name];
                Connect to the database called [database name]. All SQL commands will be executed against
                this database until USE is called again.
    
        Inspection:
            SHOW CREATE TABLE [table name];
                Show the CREATE TABLE command used to create [table name].
            SHOW DATABASES;
                Show all available databases for the app, including temporary databases.
            SHOW TABLES;
                Show all of the tables defined for the database to which you are currently connected.
    
        Other:
            exit; or quit;
                Exit this interpreter.
            help;
                Show this help message.
    
    sqlite>
    
Try running `show databases;` to see the available databases for your app:

    sqlite> show databases;
    +----------+
    | Database |
    +----------+
    | blog     |
    | projects |
    +----------+

    sqlite>

Run `use [database name];` to connect to a database, and once you're connected, you can run any SQLite command you want.  You can quit at any time by running the `exit;` command.

## Advanced Configuration

You can configure Android-DebugPort by setting any of the following `<meta-data>` values in your Application's `AndroidManifest.xml`.

```xml
<application 
    name=".MyApplication"
    label="@string/app_name"
    >
    
    <!-- Customize the port on which the BeanShell REPL will be exposed. -->
    <meta-data android:name="jwf.debugport.METADATA_DEBUG_PORT" android:value="8000"/>
    <!-- Customize the port on which the SQLite REPL will be exposed. -->
    <meta-data android:name="jwf.debugport.METADATA_SQLITE_PORT" android:value="9000"/>
    <!-- Provide any startup commands for the BeanShell REPL by referencing a string array resource. -->
    <meta-data android:name="jwf.debugport.METADATA_STARTUP_COMMANDS" android:resource="@array/startup_commands"/>
    
    <!-- ... -->
</application>
```

**Note:** It is recommended that if you wish to supply these meta-data values, you should consider setting them within an `AndroidManifest.xml` file for the `debug` build variant.

## License
This library is released under the [Apache 2.0 License](https://github.com/jasonwyatt/Android-DebugPort/blob/master/LICENCE).

