package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.entity.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PointServiceIntegrationTest (
    private var pointService: PointServiceImpl
){
    @Nested
    inner class `포인트 충전 테스트` {
        @Test
        fun `succeed - 충전 포인트`(){
            // given
            val userId = 1L
            val amount = 50L
            pointService.findUserPointByUserId(userId)

            // when
            val result = pointService.chargePoint(userId, amount)

            // then
            assertThat(result)
                .extracting("id","point")
                .containsExactly(userId, amount)
        }

        @Test
        fun `succeed - 최대 잔고 충전`() {
            // given
            val userId = 2L
            val point = 900000L
            val amount = 100000L
            pointService.chargePoint(userId, point)

            // when
            val result = pointService.chargePoint(userId, amount)

            // then
            assertThat(result)
                .extracting("id","point")
                .containsExactly(userId, point + amount)
        }
    }

    @Nested
    inner class `포인트 충전 사용 테스트` {
        @Test
        fun `succeed - 충전 포인트 딱맞게 사용`(){
            // given
            val userId = 3L
            val beforePoint = 100L
            val amount = 100L
            pointService.chargePoint(userId, beforePoint)

            // when
            val result = pointService.usePoint(userId, amount)

            // then
            assertThat(result)
                .extracting("id","point")
                .containsExactly(userId, beforePoint - amount)
        }
    }

    @Nested
    inner class `포인트 내역 테스트` {
        @Test
        fun `succeed - 포인트 충전 내역확인`(){
            // given
            val userId = 4L
            val point = 900000L
            val amount = 100000L
            pointService.chargePoint(userId, point)
            pointService.chargePoint(userId, amount)

            // when
            val pointHistory = pointService.findPointHistoryByUserId(userId)

            // then
            assertThat(pointHistory)
                .extracting("userId","type","amount")
                .containsExactly(
                    tuple(userId, TransactionType.CHARGE, point),
                    tuple(userId, TransactionType.CHARGE, amount)
                )
        }

        @Test
        fun `succeed - 포인트 사용 내역확인`(){
            // given
            val userId = 5L
            val beforePoint = 100L
            val amount = 100L
            pointService.chargePoint(userId, beforePoint)
            pointService.usePoint(userId, amount)

            // when
            val pointHistory = pointService.findPointHistoryByUserId(userId)

            // then
            assertThat(pointHistory)
                .extracting("userId","type","amount")
                .containsExactly(
                    tuple(userId, TransactionType.CHARGE, beforePoint),
                    tuple(userId, TransactionType.USE, amount)
                )
        }
    }
}