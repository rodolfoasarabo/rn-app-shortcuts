var RnAppShortcuts =
  require('react-native').NativeAppEventEmitter.RnAppShortcuts;

module.exports = {
  /**
   * An initial action will be available if the app was cold-launched
   * from an action.
   *
   * The first caller of `popInitialAction` will get the initial
   * action object, or `null`. Subsequent invocations will return null.
   */
  popInitialAction: function () {
    return RnAppShortcuts.popInitialAction();
  },

  /**
   * Adds shortcut items to application
   */
  setShortcutItems: function (items) {
    RnAppShortcuts.setShortcutItems(items);
  },

  /**
   * Clears all previously set dynamic icons
   */
  clearShortcutItems: function () {
    RnAppShortcuts.clearShortcutItems();
  },

  /**
   * Check if quick actions are supported
   */
  isSupported: function (callback) {
    RnAppShortcuts.isSupported(callback);
  },
};
