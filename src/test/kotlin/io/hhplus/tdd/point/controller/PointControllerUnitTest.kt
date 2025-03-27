package io.hhplus.tdd.point.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.tdd.point.entity.PointHistory
import io.hhplus.tdd.point.entity.TransactionType
import io.hhplus.tdd.point.entity.UserPoint
import io.hhplus.tdd.point.service.PointServiceImpl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PointController::class)
class PointControllerUnitTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var pointService: PointServiceImpl

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // given
    private val positiveId = 1L
    private val zeroId = 0L
    private val negativeId = -1L
    private val amount = 100L

    @Nested
    inner class `포인트 조회 테스트`{
        @Test
        fun `succeed - 포인트 조회`(){
            // given
            `when`(pointService.findUserPointByUserId(anyLong())).thenReturn(UserPoint(positiveId,0,0))

            // when & then
            mockMvc.perform(get("/point/{id}", positiveId))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(positiveId))
        }

        @Test
        fun `failed - 0 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(get("/point/{id}", zeroId))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }

        @Test
        fun `failed - 음수 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(get("/point/{id}", negativeId))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }
    }

    @Nested
    inner class `포인트 내역 조회 테스트`{
        @Test
        fun `succeed - 포인트 내역 조회`(){
            // given
            val mockData = listOf( PointHistory(1L,positiveId, TransactionType.CHARGE,amount,System.currentTimeMillis()) )
            `when`(pointService.findPointHistoryByUserId(positiveId)).thenReturn(mockData)

            // when & then
            mockMvc.perform(get("/point/{id}/histories", positiveId))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].userId").value(positiveId))
        }

        @Test
        fun `failed - 0 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(get("/point/{id}/histories", zeroId))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }

        @Test
        fun `failed - 음수 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(get("/point/{id}/histories", negativeId))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }
    }

    @Nested
    inner class `포인트 충전 테스트`{
        @Test
        fun `succeed - 포인트 충전`(){
            // given
            `when`(pointService.chargePoint(positiveId, amount)).thenReturn(UserPoint(positiveId, amount, 0))

            // when & then
            mockMvc.perform(
                patch("/point/{id}/charge", positiveId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(amount))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(positiveId))
                .andExpect(jsonPath("$.point").value(amount))
        }

        @Test
        fun `failed - 0 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(
                patch("/point/{id}/charge", zeroId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(amount))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }

        @Test
        fun `failed - 음수 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(
                patch("/point/{id}/charge", negativeId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(amount))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }
    }

    @Nested
    inner class `포인트 사용 테스트`{
        @Test
        fun `succeed - 포인트 사용`(){
            // given
            `when`(pointService.usePoint(positiveId, amount)).thenReturn(UserPoint(positiveId, amount, 0))

            // when & then
            mockMvc.perform(patch("/point/{id}/use", positiveId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amount))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(positiveId))
                .andExpect(jsonPath("$.point").value(amount))
        }

        @Test
        fun `failed - 0 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(
                patch("/point/{id}/charge", zeroId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(amount))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }

        @Test
        fun `failed - 음수 유저 아이디 조회`(){
            // when & then
            mockMvc.perform(
                patch("/point/{id}/charge", negativeId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(amount))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("id must be positive"))
        }
    }
}