package io.hhplus.tdd.point.controller

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PointControllerUnitTest {
    @Nested
    inner class `포인트 조회 테스트`{
        @Test
        fun `succeed - 포인트 조회`(){}

        @Test
        fun `failed - 0 유저 아이디 조회`(){}

        @Test
        fun `failed - 음수 유저 아이디 조회`(){}
    }

    @Nested
    inner class `포인트 내역 조회 테스트`{
        @Test
        fun `succeed - 포인트 내역 조회`(){}

        @Test
        fun `failed - 0 유저 아이디 조회`(){}

        @Test
        fun `failed - 음수 유저 아이디 조회`(){}
    }

    @Nested
    inner class `포인트 충전 테스트`{
        @Test
        fun `succeed - 포인트 충전`(){}

        @Test
        fun `failed - 0 유저 아이디 조회`(){}

        @Test
        fun `failed - 음수 유저 아이디 조회`(){}
    }

    @Nested
    inner class `포인트 사용 테스트`{
        @Test
        fun `succeed - 포인트 사용`(){}

        @Test
        fun `failed - 0 유저 아이디 조회`(){}

        @Test
        fun `failed - 음수 유저 아이디 조회`(){}
    }
}