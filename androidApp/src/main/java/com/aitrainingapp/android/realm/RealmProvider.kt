package com.aitrainingapp.android.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RealmProvider {
    val realm: Realm by lazy {
        Realm.open(
            RealmConfiguration.create(
                schema = setOf(UserRealm::class)
            )
        )
    }
}
