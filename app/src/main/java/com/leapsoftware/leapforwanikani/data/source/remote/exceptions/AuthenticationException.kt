package com.leapsoftware.leapforwanikani.data.source.remote.exceptions

class AuthenticationException: Exception() {
    override val message: String?
        get() = "Failed to authenticate API key with server"
}