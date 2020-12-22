package com.rommellaranjo.letsmultiply.models

import android.os.Parcel
import android.os.Parcelable

data class Record (
    val id: Long,
    val playerId: Long,
    val levelId: Long,
    val score: String?,
    val reputationId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(playerId)
        parcel.writeLong(levelId)
        parcel.writeString(score)
        parcel.writeInt(reputationId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }
}