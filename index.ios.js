var RNAppShortcutsManager =
  require('react-native').NativeModules.RNAppShortcutsManager;
var _initialAction =
  RNAppShortcutsManager && RNAppShortcutsManager.initialAction;

module.exports = {
  /**
   * An initial action will be available if the app was cold-launched
   * from an action.
   *
   * The first caller of `popInitialAction` will get the initial
   * action object, or `null`. Subsequent invocations will return null.
   */
  popInitialAction: function () {
    return new Promise((resolve) => {
      var initialAction = _initialAction;
      _initialAction = null;
      resolve(initialAction);
    });
  },

  /**
   * Adds shortcut items to application
   */
  setShortcutItems: function (items) {
    RNAppShortcutsManager.setShortcutItems(items);
  },

  /**
   * Clears all previously set dynamic icons
   */
  clearShortcutItems: function () {
    RNAppShortcutsManager.clearShortcutItems();
  },

  /**
   * Check if quick actions are supported
   */
  isSupported: function (callback) {
    RNAppShortcutsManager.isSupported(callback);
  },
};
