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
import com.facebook.react.modules.core.DeviceEventManagerModule

class RnAppShortcutsModule
  (reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  private var actionAlreadyExecuted = false

  @ReactMethod
  fun popInitialAction(promise: Promise?) {
    try {
      Log.d("popInitialAction", "chegou")
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
      val intent =
        Intent(context, currentActivity::class.java).apply {
          action = ACTION_SHORTCUT
          putExtra(SHORTCUT_ITEM, item.toPersistableBundle())
        }

      shortcuts.add(
        ShortcutInfo.Builder(context, item.title)
          .setShortLabel(item.title)
          .setLongLabel(item.title)
          .setIcon(Icon.createWithResource(context, iconResId))
          .setIntent(intent)
          .build()
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
    if (intent.action != ACTION_SHORTCUT) return

    val bundle = intent.getParcelableExtra<PersistableBundle>(SHORTCUT_ITEM)
    bundle?.let {
      val item = ShortcutItem.fromPersistableBundle(it)

      reactApplicationContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        .emit("RnQuickActions", item.toWritableMap())
    }
  }

  companion object {
    const val NAME = "RnAppShortcuts"
    private const val ACTION_SHORTCUT = "ACTION_SHORTCUT"
    private const val SHORTCUT_ITEM = "SHORTCUT_ITEM"
  }

  init {
    // Ver como implementar o onNewIntent
//    reactContext.addActivityEventListener(
//      object : ActivityEventListener {
//        override fun onActivityResult(
//          activity: Activity,
//          requestCode: Int,
//          resultCode: Int,
//          data: Intent?
//        ) {
//          // Do nothing
//        }
//
//        override fun onNewIntent(intent: Intent) {
//          sendJSEvent(intent)
//        }
//      }
//    )
  }
}
