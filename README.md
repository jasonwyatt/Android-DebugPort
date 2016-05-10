[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--DebugPort-green.svg?style=true)](https://android-arsenal.com/details/1/3540) [![License: Apache2.0](https://img.shields.io/badge/style-apache%202.0-blue.svg?style=flat&label=license)](http://www.apache.org/licenses/LICENSE-2.0)

# Android DebugPort

Android DebugPort allows you to write and execute code within your app's context, at runtime, and from the comfort of your computer's terminal. Think of it as a window into your application through which you can both inspect _and_ modify its state.

## Getting Started

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
debugCompile 'com.github.jasonwyatt:Android-DebugPort:0.5'
testCompile 'com.github.jasonwyatt:Android-DebugPort:0.5'
releaseCompile 'com.github.jasonwyatt:Android-DebugPort-NOOP:0.5'
```

**Note:** The final line above will use a [no-op version of the DebugPort library](https://github.com/jasonwyatt/Android-DebugPort-NOOP) in production builds. This makes it impossible for people to run the DebugPort server on a production build.
    
### Modify Your Manifest

You'll need to make sure the following permissions are configured in your app's `ApplicationManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```

Also, you need to declare the `DebugPortService` as a `<service>` in the manifest's `<application>` element:

```xml
<application
    ...
    >
    
    <!-- Your App's stuff... -->
    <service android:name="jwf.debugport.DebugPortService" android:exported="false" />
</application>
```

### Running the Server

Starting the DebugPort server is easy! Simply call the start method:

    DebugPortService.start(context);

Once started, you should see in the logs some information about where you can point your telnet client:
 
    05-10 23:18:19.701 16813-17035/jwf.debugport.app I/DebugTelnetServer: Server running at 192.168.0.100:8562
    05-10 23:18:19.700 16813-17036/jwf.debugport.app I/SQLiteTelnetServer: Server running at 192.168.0.100:8563
    
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
      call(Object obj, String method, Object... params)
          Call a method, regardless of access modifiers, on the provided object.
      exit()
          Exit this interpreter
      fields(Class class)
          List all of the fields available for a particular class.
      fields(Object obj)
          List all of the fields available for a particular object.
      fieldsLocal(Class class)
          List all of the fields defined locally for a particular class.
      fieldsLocal(Object obj)
          List all of the fields defined locally for an object.
      get(Object obj, String fieldName)
          Get the value of a field, regardless of access modifiers, on the provided object.
      help()
          Show this help message.
      methods(Class class)
          Get the available methods for the provided class.
      methods(Object obj)
          Get the available methods for the provided object.
      methodsLocal(Class class)
          Show all of the locally-declared methods for the provided class.
      methodsLocal(Object obj)
          Show all of the locally-declared methods for the provided object.
      set(Object obj, String fieldName, Object value)
          Set the value of a field on the provided object to the given value, regardless of access modifiers.
    
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
    
Try running `SHOW DATABASES;` to see the available databases for your app:

    sqlite> show databases;
    +----------+
    | Database |
    +----------+
    | blog     |
    | projects |
    +----------+

    sqlite>

Run `USE [database name];` to connect to a database, and once you're connected, you can run any SQLite command you want.  You can quit at any time by running the `exit;` command.

## License
This library is released under the [Apache 2.0 License](https://github.com/jasonwyatt/Android-DebugPort/blob/master/LICENCE).

# Open Source Disclaimers

> Jason Feinstein, hereby disclaims all copyright interest in the library `BeanShell' (a library for parsing and evaluating expressions at runtime in Java) written by beanshell.org.
>
> signature of Jason Feinstein, 24 April 2016
> Jason Feinstein
