package io.hhplus.tdd.point.controller

import io.hhplus.tdd.point.entity.PointHistory
import io.hhplus.tdd.point.entity.UserPoint
import io.hhplus.tdd.point.service.PointServiceImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController (
    private val pointService: PointServiceImpl
){
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPoint {
        require(id > 0) { "id must be positive" }
        return pointService.findUserPointByUserId(id)
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long,
    ): List<PointHistory> {
        require(id > 0) { "id must be positive" }
        return pointService.findPointHistoryByUserId(id)
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        require(id > 0) { "id must be positive" }
        return pointService.chargePoint(id, amount)
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        require(id > 0) { "id must be positive" }
        return pointService.usePoint(id, amount)
    }
}