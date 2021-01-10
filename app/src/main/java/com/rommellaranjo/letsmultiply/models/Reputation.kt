package com.rommellaranjo.letsmultiply.models

import android.os.Parcel
import android.os.Parcelable

data class Reputation (
    val id: Long,
    val name: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reputation> {
        override fun createFromParcel(parcel: Parcel): Reputation {
            return Reputation(parcel)
        }

        override fun newArray(size: Int): Array<Reputation?> {
            return arrayOfNulls(size)
        }
    }
}