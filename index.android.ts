import { NativeModules, Platform } from 'react-native';
import type { ShortcutItem } from 'rn-app-shortcuts';

const LINKING_ERROR =
  `The package 'rn-app-shortcuts' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RnAppShortcuts = NativeModules.RnAppShortcuts
  ? NativeModules.RnAppShortcuts
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * An initial action will be available if the app was cold-launched
 * from an action.
 *
 * The first caller of `popInitialAction` will get the initial
 * action object, or `null`. Subsequent invocations will return null.
 */
export function popInitialAction() {
  return RnAppShortcuts.popInitialAction();
}

/**
 * Adds shortcut items to application
 */
export function setShortcutItems(items: ShortcutItem[]) {
  RnAppShortcuts.setShortcutItems(items);
}

/**
 * Clears all previously set dynamic icons
 */
export function clearShortcutItems() {
  RnAppShortcuts.clearShortcutItems();
}

/**
 * Check if quick actions are supported
 */
export function isSupported(callback: any) {
  RnAppShortcuts.isSupported(callback);
}
