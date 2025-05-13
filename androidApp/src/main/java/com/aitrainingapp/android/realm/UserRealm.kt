package com.aitrainingapp.android.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var username: String = ""
    var aiIdentifier: String = ""
    var profileId: Int? = null
    var active: Boolean = false
    var notificationOn: Boolean = false
}
