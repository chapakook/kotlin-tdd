package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.entity.PointHistory
import io.hhplus.tdd.point.entity.UserPoint

interface PointService {
    fun findUserPointByUserId(userId: Long): UserPoint
    fun findPointHistoryByUserId(userId: Long): List<PointHistory>
    fun chargePoint(userId: Long, amount: Long ): UserPoint
    fun usePoint(userId: Long, amount: Long): UserPoint
}