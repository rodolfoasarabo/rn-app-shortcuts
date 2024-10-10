import { NativeModules, Platform } from 'react-native';
import type { ShortcutItem } from 'rn-app-shortcuts';

const LINKING_ERROR =
  `The package 'rn-app-shortcuts' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RNAppShortcutsManager = NativeModules.RNAppShortcutsManager
  ? NativeModules.RNAppShortcutsManager
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

var _initialAction =
  RNAppShortcutsManager && RNAppShortcutsManager.initialAction;

/**
 * An initial action will be available if the app was cold-launched
 * from an action.
 *
 * The first caller of `popInitialAction` will get the initial
 * action object, or `null`. Subsequent invocations will return null.
 */
export function popInitialAction() {
  return new Promise((resolve) => {
    var initialAction = _initialAction;
    _initialAction = null;
    resolve(initialAction);
  });
}

/**
 * Adds shortcut items to application
 */
export function setShortcutItems(items: ShortcutItem[]) {
  RNAppShortcutsManager.setShortcutItems(items);
}

/**
 * Clears all previously set dynamic icons
 */
export function clearShortcutItems() {
  RNAppShortcutsManager.clearShortcutItems();
}

/**
 * Check if quick actions are supported
 */
export function isSupported(callback: any) {
  RNAppShortcutsManager.isSupported(callback);
}
