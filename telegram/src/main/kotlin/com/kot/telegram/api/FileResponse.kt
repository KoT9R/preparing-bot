package com.kot.telegram.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileResponse(
    val ok: Boolean,
    val result: FileResult? = null
) {
    @Serializable
    data class FileResult(
        @SerialName("file_id") val fileId: String,
        @SerialName("file_unique_id") val fileUniqueId: String,
        @SerialName("file_path") val filePath: String? = null,
        @SerialName("file_size") val fileSize: Int? = null
    )
}


