# Android DebugPort

## Getting Started

### Configure Your Dependencies

Add the jitpack.io repository to your root `build.gradle`:

    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
    }

In your application's `build.gradle`, add a dependency for Android DebugPort:

    compile 'com.github.jasonwyatt:Android-DebugPort:-SNAPSHOT'
    
### Modify Your Manifest

You'll need to make sure the following permissions are configured in your app's `ApplicationManifest.xml`

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    
Also, you need to declare the `DebugPortService` as a `<service>` in the manifest's `<application>` element:

    <application
        ...
        >
        
        <!-- Your App's stuff... -->

        <service android:name="jwf.debugport.DebugPortService" android:exported="false" />
    </application>
    
### Running the Server

Starting the DebugPort server is easy!

    DebugPortService.start(context);

Once started, you should see in the logs some information about where you can point your telnet client:
 
    04-24 21:59:37.279 6835-6859/jwf.debugport.app I/TelnetServer: Server running at 192.168.2.83:8562
    
### Connecting to the DebugPort

    $ telnet 192.168.2.83 8562
    Trying 192.168.2.83...
    Connected to 192.168.2.83.
    Escape character is '^]'.
    BeanShell 1.3.0 - by Pat Niemeyer (pat@pat.net)
    bsh %

There are a few built in commands, to see what they are, run `help();`.  

Also, your application variable is automatically included as a global variable in the interpreter. It's called `app`. Try running `methodsLocal(app);`:

    bsh % methodsLocal(app);
    declared methods: {
      onCreate()
    }
    bsh %

You can exit at any time by running the `exit();` command.

# Open Source Disclaimers

> Jason Feinstein, hereby disclaims all copyright interest in the library `BeanShell' (a library for parsing and evaluating expressions at runtime in Java) written by beanshell.org.
>
> signature of Jason Feinstein, 24 April 2016
> Jason Feinstein