package com.rommellaranjo.letsmultiply.models

import android.os.Parcel
import android.os.Parcelable

data class PlayerModel  (
    val id: Long,
    val name: String?,
    val levelNewbieId: Long,
    val levelSageId: Long,
    val levelHackerId: Long,
    val reputationId: Long,
    val soundFx: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeLong(levelNewbieId)
        parcel.writeLong(levelSageId)
        parcel.writeLong(levelHackerId)
        parcel.writeLong(reputationId)
        parcel.writeInt(soundFx)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerModel> {
        override fun createFromParcel(parcel: Parcel): PlayerModel {
            return PlayerModel(parcel)
        }

        override fun newArray(size: Int): Array<PlayerModel?> {
            return arrayOfNulls(size)
        }
    }
}