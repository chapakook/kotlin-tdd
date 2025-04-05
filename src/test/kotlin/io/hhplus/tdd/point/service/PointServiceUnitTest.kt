package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.entity.UserPoint
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PointServiceUnitTest {
    @Mock
    private lateinit var pointHistoryTable: PointHistoryTable

    @Mock
    private lateinit var userPointTable: UserPointTable

    private lateinit var pointService: PointServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        pointService = PointServiceImpl(userPointTable, pointHistoryTable)
    }

    @Nested
    inner class `포인트 충전 테스트` {
        @Test
        fun `failed - 0 포인트 충전`(){
            // given
            val userId = 1L
            val amount = 0L

            // when & then
            assertThatThrownBy { pointService.chargePoint(userId, amount)}
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("amount must be positive")
        }

        @Test
        fun `failed - 음수 포인트 충전`() {
            // given
            val userId = 1L
            val amount = -1L

            // when & then
            assertThatThrownBy { pointService.chargePoint(userId, amount) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("amount must be positive")
        }

        @Test
        fun `failed - 1회 충전 최대 100만 포인트 초과`() {
            // given
            val userId = 1L
            val amount = 2000000L

            // when & then
            assertThatThrownBy { pointService.chargePoint(userId, amount) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("over the maximum single charge limit 1 million")
        }

        @Test
        fun `failed - 최대 잔고 100만포인트 초과 충전`() {
            // given
            val userId = 1L
            val amount = 100000L

            // when
            `when`(userPointTable.selectById(userId)).thenReturn(UserPoint(userId, 1000000L,0))

            // then
            assertThatThrownBy { pointService.chargePoint(userId, amount) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("the maximum balance is 1 million: current point")
        }
    }

    @Nested
    inner class `포인트 사용 테스트` {
        @Test
        fun `failed - 0 포인트 사용`() {
            // given
            val userId = 1L
            val amount = 0L

            // when & then
            assertThatThrownBy { pointService.usePoint(userId, amount) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("amount must be positive")
        }

        @Test
        fun `failed - 음수 포인트 사용`() {
            // given
            val userId = 1L
            val amount = -1L

            // when & then
            assertThatThrownBy { pointService.usePoint(userId, amount) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("amount must be positive")
        }

        @Test
        fun `failed - 포인트 잔고 초과 사용`() {
            // given
            val userId = 1L
            val point = 100L
            val amount = 200L

            // when
            `when`(userPointTable.selectById(userId)).thenReturn(UserPoint(userId, point,0))

            // then
            assertThatThrownBy { pointService.usePoint(userId, amount) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageMatching("point must be more than or equal to amount: point=$point amount=$amount")
        }
    }
}