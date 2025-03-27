package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.entity.PointHistory
import io.hhplus.tdd.point.entity.TransactionType
import io.hhplus.tdd.point.entity.UserPoint
import org.springframework.stereotype.Service

interface PointService {
    fun findUserPointByUserId(userId: Long): UserPoint
    fun findPointHistoryByUserId(userId: Long): List<PointHistory>
    fun chargePoint(userId: Long, amount: Long ): UserPoint
    fun usePoint(userId: Long, amount: Long): UserPoint
}

@Service
class PointServiceImpl(
    private val userPointTable: UserPointTable,
    private val pointHistoryTable: PointHistoryTable,
) : PointService {

    /**
     * 포인트 조회
     * WC.
     */
    override fun findUserPointByUserId(userId: Long): UserPoint = userPointTable.selectById(userId)

    /**
     * 포인트 내역 조회
     * WC.
     * - 사용자 ID는 음수면 안된다.
     */
    override fun findPointHistoryByUserId(userId: Long): List<PointHistory> = pointHistoryTable.selectAllByUserId(userId)

    /**
     * 포인트 충전
     * WC.
     * - 사용자 ID는 음수면 안된다.
     * - 0 이하의 포인트는 충전이 불가능하다.
     * - 포인트는 한번에 100만 이상 충전이 불가하다.
     * - 최대 잔고 100만 포인트
     */
    override fun chargePoint(userId: Long, amount: Long): UserPoint {
        require(userId > 0) { "userId must be positive: userId=$userId" }
        require(amount > 0) { "amount must be positive: amount=$amount" }
        require(amount <= 1000000){ "over the maximum single charge limit 1 million: amount=$amount" }
        val target = userPointTable.selectById(userId)
        require(target.point + amount <= 1000000){ "the maximum balance is 1 million: current point=${target.point}, charge amount=$amount" }
        val result = userPointTable.insertOrUpdate(userId, target.point + amount)
        pointHistoryTable.insert(result.id, amount, TransactionType.CHARGE, result.updateMillis)
        return result
    }

    /**
     * 포인트 사용
     * WC.
     * - 사용자 ID는 음수면 안된다.
     * - 0 이하의 포인트는 사용이 불가능하다.
     * - 충전된 포인트를 초과하는 포인트는 사용이 불가능하다.
     */
    override fun usePoint(userId: Long, amount: Long): UserPoint {
        require(userId > 0) { "userId must be positive: userId=$userId" }
        require(amount > 0) { "amount must be positive: amount=$amount" }
        val target = userPointTable.selectById(userId)
        require(target.point > 0) { "point must be positive: point=${target.point}" }
        require(target.point >= amount) { "point must be more than or equal to amount: point=${target.point} amount=$amount" }
        val result = userPointTable.insertOrUpdate(userId, target.point - amount)
        pointHistoryTable.insert(result.id, amount, TransactionType.USE, result.updateMillis)
        return result
    }
}