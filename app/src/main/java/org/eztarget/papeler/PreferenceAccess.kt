package org.eztarget.papeler

class UsageStorage: SharedPreferenceStorage() {

    val getAndSetIsFirstTime: Boolean
        get() {
            val openedBefore = getBoolean(OPENED_BEFORE_KEY, false)
            if (!openedBefore) {
                edit(OPENED_BEFORE_KEY, true)
            }
            return !openedBefore
        }


//    internal fun setHasBackgroundImage(hasNewImage: Boolean) {
//        edit("HAS_IMAGE", hasNewImage)
//        if (hasNewImage) {
//            queueNewBackgroundImage()
//        }
//    }
//
//    internal fun hasBackgroundImage(): Boolean {
//        return mPrefs.getBoolean("HAS_IMAGE", false)
//    }
//
//    internal fun acknowledgeNewBackgroundImage() {
//        edit("NEW_IMAGE", false)
//    }
//
//    internal fun queueNewBackgroundImage() {
//        edit("NEW_IMAGE", true)
//    }
//
//    internal fun hasNewBackgroundImage(): Boolean {
//        return mPrefs.getBoolean("NEW_IMAGE", false)
//    }

    companion object {
        private const val OPENED_BEFORE_KEY = "OPENED"
    }
}
