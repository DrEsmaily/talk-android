/*
 * SyncMe diagnostic timings (debug builds only).
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.nextcloud.talk.utils

import android.content.Context
import android.os.SystemClock
import com.nextcloud.talk.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SyncMeDiagnostics {
    private const val FILE_NAME = "syncme-chat-timings.txt"
    private const val MAX_BYTES = 64 * 1024

    @Synchronized
    fun record(context: Context, event: String, elapsedMs: Long) {
        if (!BuildConfig.DEBUG) return
        // Event strings must be hardcoded, never usernames, URLs, room IDs or message contents.
        if (!event.matches(Regex("[a-zA-Z0-9_]{1,48}"))) return
        runCatching {
            val file = File(context.applicationContext.filesDir, FILE_NAME)
            if (file.length() > MAX_BYTES) file.writeText("")
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            file.appendText("$time | $event | ${elapsedMs.coerceAtLeast(0)} ms\n")
        }
    }

    @Synchronized
    fun report(context: Context): String = runCatching {
        val data = File(context.applicationContext.filesDir, FILE_NAME)
        "SyncMe Talk diagnostic report\nBuild: debug\nNo user IDs, room tokens or messages are logged.\n\n" +
            if (data.exists()) data.readText() else "No chat samples yet."
    }.getOrDefault("Could not read diagnostic timings.")

    fun elapsedSince(start: Long): Long = SystemClock.elapsedRealtime() - start
}
