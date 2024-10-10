# React Native App Shortcuts

Support for Android App Shortcuts and iOS Quick Actions

**This project currently supports iOS 12+ and Android 7**

![](/assets/example.png)

## Installing

```bash
$ yarn add rn-app-shortcus
$ react-native link rn-app-shortcus
```

### Additional steps on iOS

Add the following lines to your `AppDelegate.m` file:

```obj-c
#import "RNAppShortcutsManager.h"

// @implementation AppDelegate

- (void)application:(UIApplication *)application performActionForShortcutItem:(UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^)(BOOL succeeded))completionHandler {
  [RNAppShortcutsManager onShortcutItemPress:shortcutItem completionHandler:completionHandler];
}

// @end
```

### Manual Linking on Android

Add the following to `app/build.gradle` within the `dependencies { ... }` section

```
implementation project(':rn-app-shortcuts')
````

Add `import com.rnappshortcuts.RnAppShortcutsPackage;` to your `MainApplication.java`

Also add `new RnAppShortcutsPackage()` within the 

```java
public List<ReactPackage> createAdditionalReactPackages() {
  return Arrays.<ReactPackage>asList(
    ...
  );
}
```
section of `MainApplication.java`

## Usage

### Adding static quick actions - iOS only

Add these entries into to your `Info.plist` file and replace the generic stuff (Action Title, .action, etc):

```xml
<key>UIApplicationShortcutItems</key>
<array>
  <dict>
    <key>UIApplicationShortcutItemIconType</key>
    <string>UIApplicationShortcutIconTypeLocation</string>
    <key>UIApplicationShortcutItemTitle</key>
    <string>Action Title</string>
    <key>UIApplicationShortcutItemType</key>
    <string>$(PRODUCT_BUNDLE_IDENTIFIER).action</string>
  </dict>
</array>
```

A full list of available icons can be found here:

<https://developer.apple.com/design/human-interface-guidelines/ios/icons-and-images/system-icons/#quick-action-icons>

### Adding dynamic shortcuts

In order to add / remove dynamic actions during application lifecycle, you need to import `rn-app-shortcuts` and call either `setShortcutItems` to set actions or `clearShortcutItems` to clear.

```js
import RnAppShortcuts from "rn-app-shortcuts";

RnAppShortcuts.setShortcutItems([
  {
    type: "Orders", // Required
    title: "See your orders", // Optional, if empty, `type` will be used instead
    subtitle: "See orders you've made",
    icon: "Compose", // Icons instructions below
    userInfo: {
      url: "app://orders" // Provide any custom data like deep linking URL
    }
  }
]);
```

To clear actions:

```js
RnAppShortcuts.clearShortcutItems();
```

#### Icons

##### iOS

On iOS you can use the default icons provided by Apple, they're listed here: https://developer.apple.com/design/human-interface-guidelines/ios/icons-and-images/system-icons/#quick-action-icons

You can also use custom icons creating new Image set inside Image.xcassets on XCode. You'll need to define the 1x, 2x and 3x sizes.

![](/assets/ios.png)

##### Android

On Android you'll need to create an image file (use PNG) inside `android/app/src/main/res/drawable`.

Name the image with underscores, don't use hyphens.

### Listening for shortcut actions

First, you'll need to make sure `DeviceEventEmitter` is added to the list of
requires for React Native.

```js
import { DeviceEventEmitter } from "react-native";
```

Use `DeviceEventEmitter` to listen for `appShortcuts` messages.

```js
DeviceEventEmitter.addListener("appShortcuts", data => {
  console.log(data.title);
  console.log(data.type);
  console.log(data.userInfo);
});
```

To get any actions sent when the app is cold-launched using the following code:

```js
import RnAppShortcuts from "rn-app-shortcuts";

function doSomethingWithTheAction(data) {
  console.log(data.title);
  console.log(data.type);
  console.log(data.userInfo);
}

RnAppShortcuts.popInitialAction()
  .then(doSomethingWithTheAction)
  .catch(console.error);
```

Please note that on Android if android:launchMode is set to default value standard in AndroidManifest.xml, the app will be re-created each time when app is being brought back from background and it won't receive appShortcuts event from DeviceEventEmitter, instead popInitialAction will be receiving the app shortcut event.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
