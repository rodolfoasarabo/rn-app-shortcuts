package com.rnappshortcuts

import ShortcutItem
import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.PersistableBundle
import android.util.Log
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule

@ReactModule(name = RnAppShortcutsModule.REACT_NAME)
class RnAppShortcutsModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), ActivityEventListener {

  override fun onActivityResult(p0: Activity?, p1: Int, p2: Int, p3: Intent?) {
    // Do nothing
  }

  override fun onNewIntent(intent: Intent?) {
    intent?.let {
      sendJSEvent(it)
    }
  }

  override fun getName(): String {
    return REACT_NAME
  }

  private var actionAlreadyExecuted = false

  @ReactMethod
  fun popInitialAction(promise: Promise?) {
    try {
      val currentActivity = currentActivity
      var map: WritableMap? = null

      if (currentActivity != null && !actionAlreadyExecuted) {
        val intent = currentActivity.intent

        if (intent != null && intent.action == ACTION_SHORTCUT) {
          val bundle = intent.getParcelableExtra<PersistableBundle>(SHORTCUT_ITEM)

          if (bundle != null) {
            val item = ShortcutItem.fromPersistableBundle(bundle)
            map = item.toWritableMap()
          }
        }
      }

      actionAlreadyExecuted = true
      promise?.resolve(map)
    } catch (e: Exception) {
      promise?.reject(e)
    }
  }

  @ReactMethod
  fun setShortcutItems(items: ReadableArray) {
    if (items.size() == 0) {
      return
    }

    val currentActivity = currentActivity ?: return

    val shortcuts = mutableListOf<ShortcutInfo>()
    val context = reactApplicationContext

    for (i in 0 until items.size()) {
      val item = ShortcutItem.fromReadableMap(items.getMap(i))
      val iconResId = context.resources.getIdentifier(item.icon, "drawable", context.packageName)
      val intent = Intent(context, currentActivity::class.java).apply {
        action = ACTION_SHORTCUT
        putExtra(SHORTCUT_ITEM, item.toPersistableBundle())
      }

      shortcuts.add(
        ShortcutInfo.Builder(context, item.title).setShortLabel(item.title).setLongLabel(item.title)
          .setIcon(Icon.createWithResource(context, iconResId)).setIntent(intent).build()
      )
    }

    context.getSystemService(ShortcutManager::class.java).dynamicShortcuts = shortcuts
  }

  @ReactMethod
  fun clearShortcutItems() {
    reactApplicationContext.getSystemService(ShortcutManager::class.java)
      .removeAllDynamicShortcuts()
  }

  private fun sendJSEvent(intent: Intent) {
    Log.d("SHORTCUTS", "sendJSEvent")

    if (intent.action != ACTION_SHORTCUT) return
    Log.d("SHORTCUTS", "sendJSEvent")

    val bundle = intent.getParcelableExtra<PersistableBundle>(SHORTCUT_ITEM)

    Log.d("SHORTCUTS", "Bundle: $bundle")
    bundle?.let {
      val item = ShortcutItem.fromPersistableBundle(it)

      reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        .emit("appShortcuts", item.toWritableMap())
    }
  }

  companion object {
    const val REACT_NAME = "RnAppShortcuts"
    private const val ACTION_SHORTCUT = "ACTION_SHORTCUT"
    private const val SHORTCUT_ITEM = "SHORTCUT_ITEM"
  }
}
