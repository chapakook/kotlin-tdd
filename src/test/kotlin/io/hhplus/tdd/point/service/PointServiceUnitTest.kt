package io.hhplus.tdd.point.service

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointServiceUnitTest {

    @Nested
    inner class `포인트 충전 테스트` {
        @Test
        fun `failed - 0 포인트 충전`(){}

        @Test
        fun `failed - 음수 포인트 충전`() {}

        @Test
        fun `failed - 1회 충전 최대 100만 포인트 초과`() {}

        @Test
        fun `failed - 최대 잔고 100만포인트 초과 충전`() {}
    }

    @Nested
    inner class `포인트 사용 테스트` {
        @Test
        fun `failed - 0 포인트 사용`() {}

        @Test
        fun `failed - 음수 포인트 사용`() {}

        @Test
        fun `failed - 포인트 잔고 초과 사용`() {}
    }
}