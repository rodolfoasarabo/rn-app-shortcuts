import android.os.PersistableBundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap

private const val USER_INFO_URL = "url"

data class UserInfo(
  val url: String,
) {

  companion object {

    fun fromReadableMap(map: ReadableMap): UserInfo {
      return UserInfo(url = map.getString(USER_INFO_URL)!!)
    }

    fun fromPersistableBundle(bundle: PersistableBundle): UserInfo {
      return UserInfo(url = bundle.getString(USER_INFO_URL)!!)
    }
  }

  fun toWritableMap(): WritableMap {
    val map = Arguments.createMap().apply { putString(USER_INFO_URL, url) }
    return map
  }

  fun toPersistableBundle(): PersistableBundle {
    val bundle = PersistableBundle()
    bundle.putString(USER_INFO_URL, url)
    return bundle
  }
}
