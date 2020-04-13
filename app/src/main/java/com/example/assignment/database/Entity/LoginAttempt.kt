package com.example.assignment.database.Entity

import android.os.Parcel
import android.os.Parcelable

class LoginAttempt(
    val currentAttempt: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    constructor() : this(0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(currentAttempt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginAttempt> {
        override fun createFromParcel(parcel: Parcel): LoginAttempt {
            return LoginAttempt(parcel)
        }

        override fun newArray(size: Int): Array<LoginAttempt?> {
            return arrayOfNulls(size)
        }
    }
}