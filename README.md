# Android DebugPort

Android DebugPort allows you to write and execute code within your app's context, at runtime, and from the comfort of your computer's terminal. Think of it as a window into your application through which you can both inspect _and_ modify its state.

## Getting Started

### Configure Your Dependencies

Add the jitpack.io repository to your root `build.gradle`:

    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
    }

In your application's `build.gradle` file, add a dependency for Android DebugPort:

    compile 'com.github.jasonwyatt:Android-DebugPort:0.3'
    
### Modify Your Manifest

You'll need to make sure the following permissions are configured in your app's `ApplicationManifest.xml`:

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

Starting the DebugPort server is easy! Simple call the start method:

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

There are a few built in commands, to see what they are, run `help();`
  
    bsh % help();
    Available Commands:
      exit()                Exit this interpreter
      fields(Class)         List all of the fields available for a particular class.
      fields(Object)        List all of the fields available for a particular object.
      fieldsLocal(Class)    List all of the fields defined locally for a particular
                            class.
      fieldsLocal(Object)   List all of the fields defined locally for an object.
      help()                Show this help message.
      methods(Class)        Get the available methods for the provided class.
      methods(Object)       Get the available methods for the provided object.
      methodsLocal(Class)   Show all of the locally-declared methods for the provided
                            class.
      methodsLocal(Object)  Show all of the locally-declared methods for the provided
                            object.
    
    bsh %

Also, your application variable is automatically included as a global variable in the interpreter. It's called `app`. Try running `methodsLocal(app);`:

    bsh % methodsLocal(app);
    declared methods: {
      public void onCreate()
    }
    bsh %

Don't forget that you can execute whatever code you wish within the DebugPort. See the [beanshell documentation](http://beanshell.org/manual/contents.html) for the full rundown.

You can exit at any time by running the `exit();` command.

## License
This library is released under the [Apache 2.0 License](https://github.com/jasonwyatt/Android-DebugPort/blob/master/LICENCE).

# Open Source Disclaimers

> Jason Feinstein, hereby disclaims all copyright interest in the library `BeanShell' (a library for parsing and evaluating expressions at runtime in Java) written by beanshell.org.
>
> signature of Jason Feinstein, 24 April 2016
> Jason Feinstein
