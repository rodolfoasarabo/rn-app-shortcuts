import android.os.PersistableBundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap

private const val SHORTCUT_TYPE = "type"
private const val SHORTCUT_TITLE = "title"
private const val SHORTCUT_ICON = "icon"
private const val SHORTCUT_USER_INFO = "userInfo"

data class ShortcutItem(
  val type: String,
  val title: String,
  val icon: String,
  val userInfo: UserInfo
) {

  companion object {
    fun fromReadableMap(map: ReadableMap): ShortcutItem {
      val item =
        ShortcutItem(
          type = map.getString(SHORTCUT_TYPE)!!,
          title = map.getString(SHORTCUT_TITLE)!!,
          icon = map.getString(SHORTCUT_ICON)!!,
          userInfo = UserInfo.fromReadableMap(map.getMap(SHORTCUT_USER_INFO)!!)
        )

      return item
    }

    fun fromPersistableBundle(bundle: PersistableBundle): ShortcutItem {
      val item =
        ShortcutItem(
          type = bundle.getString(SHORTCUT_TYPE)!!,
          title = bundle.getString(SHORTCUT_TITLE)!!,
          icon = bundle.getString(SHORTCUT_ICON)!!,
          userInfo =
          UserInfo.fromPersistableBundle(
            bundle.getPersistableBundle(SHORTCUT_USER_INFO)!!
          )
        )

      return item
    }
  }

  fun toWritableMap(): WritableMap {
    val map =
      Arguments.createMap().apply {
        putString(SHORTCUT_TYPE, type)
        putString(SHORTCUT_TITLE, title)
        putString(SHORTCUT_ICON, icon)
        putMap(SHORTCUT_USER_INFO, userInfo.toWritableMap())
      }
    return map
  }

  fun toPersistableBundle(): PersistableBundle {
    val bundle = PersistableBundle()
    bundle.putString(SHORTCUT_TYPE, type)
    bundle.putString(SHORTCUT_TITLE, title)
    bundle.putString(SHORTCUT_ICON, icon)
    bundle.putPersistableBundle(SHORTCUT_USER_INFO, userInfo.toPersistableBundle())
    return bundle
  }
}
