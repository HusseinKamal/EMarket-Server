package com.example.security.token

import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg tokenClaim: TokenClaim
    ):String
}