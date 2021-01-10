package com.rommellaranjo.letsmultiply.models

import android.os.Parcel
import android.os.Parcelable

data class QuestionWithOptions (
    val id: Long,
    val val1: Int,
    val val2: Int,
    val levelId: Long,
    val options: ArrayList<Option>
)