package com.example.outdoortrack

import com.example.outdoortrack.data.repository.AuthRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 针对手机号校验逻辑的简单单元测试示例。
 */
class AuthRepositoryTest {

    @Test
    fun phoneValidator_acceptsValidChineseMobile() {
        assertTrue(AuthRepository.isPhoneValid("13812345678"))
    }

    @Test
    fun phoneValidator_rejectsInvalidMobile() {
        assertFalse(AuthRepository.isPhoneValid("123456"))
        assertFalse(AuthRepository.isPhoneValid("23812345678"))
    }
}
