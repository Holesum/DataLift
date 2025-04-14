package com.example.datalift.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.datalift.model.Muser

class UserPreviewParameterProvider : PreviewParameterProvider<Muser> {
    override val values: Sequence<Muser>
        get() = sequenceOf(
            Muser(
                uname = "testUser",
                name = "John Doe",
                email = "test@example.com",
                followers = listOf("noinaboin","ubvuiabuiba","nbjiubiab"),
                following = listOf("noinaboin","ubvuiabuiba","nbjiubiab")

            )
        )
}